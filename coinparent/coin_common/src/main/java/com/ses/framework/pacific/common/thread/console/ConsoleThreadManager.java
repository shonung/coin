/**
 * Copyright (C) 2016 LG Electronics Inc. All Rights Reserved.
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
 * Author: zack
 */

package com.ses.framework.pacific.common.thread.console;

import static com.ses.framework.pacific.common.Constants.MILLIS_3_SECONDS;
import static java.lang.Runtime.getRuntime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import com.ses.framework.pacific.common.Utils;
import com.ses.framework.pacific.common.inf.IConsoleLineFilter;
import com.ses.framework.pacific.common.inf.IConsoleLineFormatter;
import com.ses.framework.pacific.common.task.BaseObjectCallable;
import com.ses.framework.pacific.common.task.ITaskCompleteListener;
import com.ses.framework.pacific.common.task.ManagedTaskFactory;
import com.ses.framework.pacific.common.thread.DefaultManagedRunnable;
import com.ses.framework.pacific.common.thread.ManagedThreadFactory;
import com.ses.framework.pacific.logger.Logger;

public class ConsoleThreadManager {
  private final String TAG = ConsoleThreadManager.class.getSimpleName();
  private static ConsoleThreadManager mInstance_ = null;

  public static synchronized ConsoleThreadManager getInstance() {
    if (mInstance_ == null) {
      mInstance_ = new ConsoleThreadManager();
    }
    return mInstance_;
  }

  private static final long CONSOLE_DEFAULT_SLEEP = 200;
  private static final long COMMAND_EXECUTION_COMPLETE_TIMEOUT = MILLIS_3_SECONDS;

  class ConsoleManagedRunnable extends DefaultManagedRunnable {
    private final String TAG = ConsoleManagedRunnable.class.getSimpleName();

    Process process = null;
    BufferedReader in = null;
    ArrayList<String> inputLineList = null;

    boolean isTerminated = false;
    boolean canCommand = true;

    private String initialCommand = null;
    private IConsoleLineFilter lineFilter = null;
    private IConsoleLineFormatter lineFormatter = null;

    ConsoleManagedRunnable(String initialCommand, boolean rootPermission, IConsoleLineFilter lineFilter, IConsoleLineFormatter lineFormatter) throws Exception {
      this.initialCommand = initialCommand;
      this.lineFilter = lineFilter;
      this.lineFormatter = lineFormatter;
      inputLineList = new ArrayList<>();
      try {
        process = getRuntime().exec((rootPermission ? "su" : "sh"), null, null);
      } catch (Exception e) {
        Logger.error(TAG, Utils.getStringFromException(e));
        throw e;
      }
    }

    void setTerminated() {
      synchronized (this) {
        isTerminated = true;
        try {
          this.notify();
        } catch (Exception ex) {
          //do nohting
        }
      }
    }

    ArrayList<String> getInputLineList() {
      ArrayList<String> ret = inputLineList;
      inputLineList = new ArrayList<>();
      return ret;
    }

    @Override
    public void doRun() throws InterruptedException {
      runCommand(initialCommand);
      while (!isTerminated) {
        String line = null;
        try {
          if (in != null && in.ready()) {
            line = in.readLine();
          }
        } catch (IOException e) {
          //
        }
        if (line == null) {
          synchronized (this) {
            try {
              this.wait(CONSOLE_DEFAULT_SLEEP);
            } catch (Exception ex) {
              //do nohting
            }
          }
        } else {
          if (lineFilter != null) {
            if (lineFilter.isShownToConsole(line)) {
              inputLineList.add((lineFormatter != null ? lineFormatter.format(line) : line));
            }
          } else {
            inputLineList.add((lineFormatter != null ? lineFormatter.format(line) : line));
          }
        }
      }
    }

    void closeInputStream() {
      if (in != null) {
        try {
          in.close();
          in = null;
        } catch (Exception ex) {
          // do nothing
        }
      }
    }

    @Override
    public void postRun() throws InterruptedException {
      super.postRun();
      Logger.debug(TAG, ">>> postRun");
      closeInputStream();
      if (process != null) {
        try {
          process.destroy();
          process = null;
        } catch (Exception ex) {
          // do nothing
        }
      }
    }


    boolean runCommand(String command) {
      Logger.debug(TAG, ">>> runCommand command=" + command + ", canCommand=" + canCommand);
      boolean ret = false;
      if (canCommand && command != null && process != null && isNotFinished()) {
        closeInputStream();
        try {
          OutputStream os = process.getOutputStream();
          os.write(command.getBytes("ASCII"));
          os.flush();
          os.close();
          Logger.debug(TAG, ">>> runCommand flush command=" + command);
          int id = ManagedTaskFactory.getInstance().makeTaskAndRun(new BaseObjectCallable<Boolean>() {
            @Override
            public ITaskCompleteListener<Boolean> getListener() {
              return null;
            }

            @Override
            public Boolean call() throws Exception {
              process.waitFor();
              return true;
            }
          }, COMMAND_EXECUTION_COMPLETE_TIMEOUT);
          ManagedTaskFactory.ManagedTask<Boolean> task = ManagedTaskFactory.getInstance().getTask(id);
//          Logger.debug(TAG, ">>> before get");
          task.get();
//          Logger.debug(TAG, ">>> after get");
          ret = true;
        } catch (CancellationException | InterruptedException ex) {
          // execution complete timeout
          //Logger.error(TAG, Utils.getStringFromException(ex));
        } catch (ExecutionException ex) {
          Logger.error(TAG, Utils.getStringFromException(ex));
        } catch (Exception ex) {
          Logger.error(TAG, Utils.getStringFromException(ex));
        } finally {
          try {
            if (process.exitValue() == 0) {
              in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            } else {
              in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            }
            canCommand = true;
          } catch (IllegalThreadStateException ex) {
            //Logger.error(TAG, Utils.getStringFromException(ex));
            canCommand = false;
            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
          } catch (Exception ex) {
            Logger.error(TAG, Utils.getStringFromException(ex));
          }
        }
      }
      return ret;
    }

    boolean isLive() {
      return isNotFinished();
    }

  }

  private Map<Long, ConsoleManagedRunnable> idConsoleManagedRunnableMap = null;

  private ConsoleThreadManager() {
    idConsoleManagedRunnableMap = new HashMap<>();
  }

//  public long runCommand(long id, String command) {
//    return runCommand(id, command, false);
//  }

  public long runCommand(long id, String command, boolean rootPermission, IConsoleLineFilter lineFilter, IConsoleLineFormatter lineFormatter) {
    long processId = id;

    ConsoleManagedRunnable consoleManagedRunnable = idConsoleManagedRunnableMap.get(id);

    if (consoleManagedRunnable != null) {
//      Logger.info(TAG, ">>> 1 consoleManagedRunnable=" + consoleManagedRunnable);
      if (!consoleManagedRunnable.isLive()) {
        idConsoleManagedRunnableMap.remove(id);
        consoleManagedRunnable.setTerminated();
        consoleManagedRunnable = null;
      }
    }

    if (consoleManagedRunnable == null) {
      try {
        consoleManagedRunnable = new ConsoleManagedRunnable(command, rootPermission, lineFilter, lineFormatter);
//        Logger.info(TAG, ">>> 2 consoleManagedRunnable=" + consoleManagedRunnable);
        processId = ManagedThreadFactory.getInstance().getThreadAndStart(consoleManagedRunnable);
        idConsoleManagedRunnableMap.put(processId, consoleManagedRunnable);
//        Logger.debug(TAG, ">>> new console id=" + processId + ", command=" + command + ", rootPermission=" + rootPermission);
      } catch (Exception ex) {
        Logger.error(TAG, Utils.getStringFromException(ex));
      }
    }

//    if (consoleManagedRunnable != null) {
//      Logger.info(TAG, ">>> 3 consoleManagedRunnable=" + consoleManagedRunnable);
//      consoleManagedRunnable.runCommand(command);
//    }

    return processId;
  }

  public boolean isLive(long id) {
    ConsoleManagedRunnable consoleManagedRunnable = idConsoleManagedRunnableMap.get(id);
    return (consoleManagedRunnable != null && consoleManagedRunnable.isLive());
  }


  public List<String> getInputLineList(long id) {
    ConsoleManagedRunnable consoleManagedRunnable = idConsoleManagedRunnableMap.get(id);
    return (consoleManagedRunnable != null ? consoleManagedRunnable.getInputLineList() : null);
  }

  public void setTerminate(long id) {
    ConsoleManagedRunnable consoleManagedRunnable = idConsoleManagedRunnableMap.remove(id);
    if (consoleManagedRunnable != null) {
      consoleManagedRunnable.setTerminated();
    }
  }
}
