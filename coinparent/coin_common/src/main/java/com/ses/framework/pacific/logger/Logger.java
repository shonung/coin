/*
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
 * Author: zack, kangwon.zhang
 */

package com.ses.framework.pacific.logger;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

public class Logger {
  private static Logger mInstance_ = null;

  private static final String KEY_IS_DEBUG = "IS_DEBUG";
  private static final String KEY_LOG_DIR = "LOG_DIR";
  private static final String KEY_LOG_FILE_NAME = "LOG_FILE_NAME";
  private static final String VAL_LOG_FILE_NAME = "device_agent";
  private static final String KEY_LOG_FILE_EXTENSION = "LOG_FILE_EXTENSION";
  private static final String VAL_LOG_FILE_EXTENSION = "log";
  
  static {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    List<StackTraceElement> list = Arrays.asList(stackTrace);
    for (StackTraceElement element : list) {
        if (element.getClassName().startsWith("org.junit.")) {
        	Logger.init("unitTestLog", LogLevel.DEBUG);
        	break;
        }
    }
  }

  public static boolean init(String directory, LogLevel debug) {
    return init(directory, VAL_LOG_FILE_NAME, VAL_LOG_FILE_EXTENSION, debug);
  }

  public static boolean init(String directory, String filename, LogLevel debug) {
    return init(directory, filename, VAL_LOG_FILE_EXTENSION, debug);
  }

  public static boolean init(String directory, String filename, String extension, LogLevel debug) {
    boolean initialized = false;

    if (mInstance_ == null) {
      if (directory != null) {
        File path = new File(directory);

        if (!path.exists()) {
          path.mkdirs();
        }

        if (path.exists() && path.canWrite()) {
          System.setProperty(KEY_IS_DEBUG, String.valueOf(debug));
          System.setProperty(KEY_LOG_DIR, directory);
          System.setProperty(KEY_LOG_FILE_NAME, filename);
          System.setProperty(KEY_LOG_FILE_EXTENSION, extension);

          initialized = true;
        }
      }

      mInstance_ = new Logger();
      mInstance_.setLogLevel_(debug);
      mInstance_.mWriteToFile_ = true;
    }

    return initialized;
  }

  private static void printWarning_() {
    System.out.println("Logger should be initialized before use it!!!");
  }

  public static void debug(String tag, String msg) {
    if (mInstance_ != null) {
      mInstance_.debug_(tag + " " + msg);
    } else {
      printWarning_();
    }
  }

  public static void info(String tag, String msg) {
    if (mInstance_ != null) {
      mInstance_.info_(tag + " " + msg);
    } else {
      printWarning_();
    }
  }

  public static void warn(String tag, String msg) {
    if (mInstance_ != null) {
      mInstance_.warn_(tag + " " + msg);
    } else {
      printWarning_();
    }
  }

  public static void error(String tag, String msg) {
    if (mInstance_ != null) {
      mInstance_.error_(tag + " " + msg);
    } else {
      printWarning_();
    }
  }

  public static void fatal(String tag, String msg) {
    if (mInstance_ != null) {
      mInstance_.fatal_(tag + " " + msg);
    } else {
      printWarning_();
    }
  }

  public static void enable(boolean enabled) {
    if (mInstance_ != null) {
      mInstance_.mWriteToFile_ = enabled;
    } else {
      printWarning_();
    }
  }

  public static void enableConsole(boolean enabled) {
    if (mInstance_ != null) {
      mInstance_.mPrintToConsole_ = enabled;
    } else {
      printWarning_();
    }
  }

  public static void setLevel(LogLevel level) {
    if (mInstance_ != null) {
      mInstance_.setLogLevel_(level);
    } else {
      printWarning_();
    }
  }

  public static File getLogFile() {
    if (mInstance_ != null) {
      return mInstance_.getTargetDirectoryFile_();
    } else {
      printWarning_();
    }
    return null;
  }

  private org.slf4j.Logger mFileLogger_ = null;
  private org.slf4j.Logger mConsoleLogger_ = null;
  private LogLevel mLogLevel_ = LogLevel.ERROR;
  private boolean mWriteToFile_ = false;
  private boolean mPrintToConsole_ = true;

  private Logger() {
    ILoggerFactory factory = LoggerFactory.getILoggerFactory();

    mFileLogger_ = factory.getLogger("file");
    mConsoleLogger_ = factory.getLogger("con");
  }

  private File getTargetDirectoryFile_() {
    File ret = null;
    String directory = System.getProperty(KEY_LOG_DIR);
    String filename = System.getProperty(KEY_LOG_FILE_NAME);
    String extension = System.getProperty(KEY_LOG_FILE_EXTENSION);

    if (directory != null && filename != null && extension != null) {
      ret = new File(directory, filename + "." + extension);
    }

    return ret;
  }

  private void debug_(String msg) {
    if (mLogLevel_.compareTo(LogLevel.DEBUG) >= 0) {
      if (mWriteToFile_) {
        mFileLogger_.debug(msg);
      }
      if (mPrintToConsole_) {
        mConsoleLogger_.debug(msg);
      }
    }
  }

  private void info_(String msg) {
    if (mLogLevel_.compareTo(LogLevel.INFO) >= 0) {
      if (mWriteToFile_) {
        mFileLogger_.info(msg);
      }
      if (mPrintToConsole_) {
        mConsoleLogger_.info(msg);
      }
    }
  }

  private void warn_(String msg) {
    if (mLogLevel_.compareTo(LogLevel.WARNING) >= 0) {
      if (mWriteToFile_) {
        mFileLogger_.warn(msg);
      }
      if (mPrintToConsole_) {
        mConsoleLogger_.warn(msg);
      }
    }
  }

  private void error_(String msg) {
    if (mLogLevel_.compareTo(LogLevel.ERROR) >= 0) {
      if (mWriteToFile_) {
        mFileLogger_.error(msg);
      }
      if (mPrintToConsole_) {
        mConsoleLogger_.error(msg);
      }
    }
  }

  private void fatal_(String msg) {
    if (mWriteToFile_) {
      mFileLogger_.error(msg);
    }
    if (mPrintToConsole_) {
      mConsoleLogger_.error(msg);
    }
  }

  private void setLogLevel_(LogLevel logLevel) {
    mLogLevel_ = logLevel;
  }
}
