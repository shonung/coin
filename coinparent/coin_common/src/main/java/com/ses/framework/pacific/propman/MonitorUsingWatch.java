/*
 * Copyright (C) 2017 LG Electronics Inc. All Rights Reserved.
 * Though every care has been taken to ensure the accuracy of this document,
 * LG Electronics Inc. cannot accept responsibility for any errors or
 * omissions or for any loss occurred to any person, whether legal or natural,
 * from acting, or refraining from action, as a result of the information
 * contained herein. Information in this document is subject to change at any
 * time without obligation to notify any person of such changes.
 * LG Electronics Inc. may have patents or patent pending applications,
 * trademarks copyrights or other intellectual property rights covering subject
 * matter in this document. The furnishing of this document does not give the
 * recipient or reader any license to these patents, trademarks copyrights or
 * other intellectual property rights.
 * No part of this document may be communicated, distributed, reproduced or
 * transmitted in any form or by any means, electronic or mechanical or
 * otherwise, for any purpose, without the prior written permission of
 * LG Electronics Inc.
 * The document is subject to revision without further notice.
 * All brand names and product names mentioned in this document are trademarks
 * or registered trademarks of their respective owners
 *
 * Author: eungsuk.shon
 */

package com.ses.framework.pacific.propman;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.ses.framework.pacific.logger.Logger;
import com.ses.framework.pacific.propman.inf.IChangedConfigValueListener;
import com.ses.framework.pacific.propman.inf.IMonitoringStrategy;

public class MonitorUsingWatch implements IMonitoringStrategy {
  private static final String TAG = MonitorUsingWatch.class.getSimpleName();

  private WatchService watchService_;
  @SuppressWarnings("unused")
  private WatchKey watchKey_;
  private ThreadPoolExecutor threadPoolExecutor_;
  private IChangedConfigValueListener reporterToNotify_;
  private Config.FileType fileType = null;

  public MonitorUsingWatch(String targetFilePath, Config.FileType fileType) {
    reporterToNotify_ = null;
    if (initialize_(targetFilePath, fileType)) {
      startMonitoring_();
    }
  }

  @Override
  public void stopMonitor() {
    if (threadPoolExecutor_ != null) {
      threadPoolExecutor_.purge();
      threadPoolExecutor_ = null;
    }
  }

  @Override
  public void registerListener(IChangedConfigValueListener listener) {
    reporterToNotify_ = listener;
  }

  @Override
  public void releaseListener() {
    reporterToNotify_ = null;
  }

  private boolean initialize_(String targetFilePath, Config.FileType fileType) {
    try {
      File targetFile = new File(targetFilePath);
      if (targetFile == null || !targetFile.exists()) {
        return false;
      }

      if (watchService_ == null) {
        watchService_ = FileSystems.getDefault().newWatchService();
      }

      Path configFolderPath = Paths.get(targetFile.getAbsolutePath());

      watchKey_ = configFolderPath.register(watchService_, StandardWatchEventKinds.ENTRY_CREATE,
          StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

      threadPoolExecutor_ = new ThreadPoolExecutor(Constants.DEFAULT_THREAD_POOL_SIZE_FOR_MONITORING,
          Constants.DEFAULT_THREAD_POOL_SIZE_FOR_MONITORING + 1, // it was not
                                                                 // used in
                                                                 // LinkedBlockingQueue
          0L, // Setting timeout, but I don't use a future
          TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

      this.fileType = fileType;

      return true;
    } catch (IOException e) {
      Logger.error(TAG, "The exception was occurred in FileSystem while monitor get WatchService.");
      return false;
    }
  }

  private void startMonitoring_() {
    Thread monitoringThread = new Thread(new Runnable() {
      @SuppressWarnings("unchecked")
      public void run() {
        while (!Thread.currentThread().isInterrupted()) { // this code must be
                                                          // used instead of
                                                          // while(true) in WAS,
                                                          // reference by
                                                          // http://atin.tistory.com/438
          try {
            WatchKey changedKey = watchService_.take();
            List<WatchEvent<?>> watchEvents = changedKey.pollEvents();

            for (WatchEvent<?> watchEvent : watchEvents) {
              executeRunnablesForReporting_((WatchEvent<Path>) watchEvent);
            }

            changedKey.reset();
          } catch (InterruptedException e) {
            Logger.error(TAG, "The exception was occurred in WatchService while WatchService takes a watch key.");
          }
        }
      }
    });

    monitoringThread.start();
  }

  private void executeRunnablesForReporting_(WatchEvent<Path> pathEvent) {
    if (threadPoolExecutor_ != null) {
      threadPoolExecutor_.execute(new ReportingRunnable(pathEvent, reporterToNotify_));
    } else {
      Logger.error(TAG, "threadPoolExecutor is null. Please re-initialize threadPoolExecutor.");
    }
  }

  private class ReportingRunnable implements Runnable {
    private WatchEvent<Path> pathEvent_;
    IChangedConfigValueListener reporter_;

    public ReportingRunnable(WatchEvent<Path> pathEvent, IChangedConfigValueListener reporterToNotify) {
      pathEvent_ = pathEvent;
      reporter_ = reporterToNotify;
    }

    public void run() {
      try {
        Path pathOfChangedFile = pathEvent_.context();
        WatchEvent.Kind<Path> eventKind = pathEvent_.kind();

        if (pathOfChangedFile != null && eventKind != null) {
          String pathString = pathOfChangedFile.toString();
          if (pathString.split(Config.FileType.PROPERTIES_TYPE.getFileExtension()).length <= 1) {
            if (reporterToNotify_ != null) {
              reporterToNotify_.changedConfigValue(System.currentTimeMillis(), pathOfChangedFile.toString(),
                  pathOfChangedFile.getFileName().toString());
            } else {
              Logger.error(TAG,
                  "reporter for callback is null. Please set a reporter by referecing IChangedConfigValueListener.");
            }
          }
        }
      } catch (Exception e) {
        // do-nothing
      } finally {
        pathEvent_ = null;
        reporter_ = null;
      }
    }
  }
}
