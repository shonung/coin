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
 * Author: eungsuk.shon
 */

package com.ses.framework.pacific.propman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.ses.framework.pacific.propman.inf.ISetter;

public class PropertiesSetter implements ISetter {
  private static final Logger logger = Logger.getLogger(PropertiesGetter.class.getName());

  private ThreadPoolExecutor threadPoolExecutor_;

  public PropertiesSetter() {
    initialize_();
  }

  private void initialize_() {
    threadPoolExecutor_ = new ThreadPoolExecutor(Constants.DEFAULT_THREAD_POOL_SIZE_FOR_SETTER,
        Constants.DEFAULT_THREAD_POOL_SIZE_FOR_SETTER + 1, // Don't use //
                                                           // LinkedBlockingQueue
        Constants.DEFAULT_THREAD_POOL_TIMEOUT_FOR_SETTER, // Setting timeout
        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
  }

  @Override
  public String setSync(String targetFilePath, String key, String value) {
    if (targetFilePath != null) {
      try {
        // Can I consider FileInputStream or BufferedFileInputStream or
        // FileReader for performance?
        FileInputStream fis = new FileInputStream(targetFilePath);
        Properties properties = new Properties();
        if (fis != null && properties != null) {
          properties.load(fis);
          Object returnObject = properties.setProperty(key, value);
          fis.close();
          properties.store(new FileOutputStream(targetFilePath), null);
          properties.clear();
          if (returnObject != null) {
            return returnObject.toString();
          } else {
            return null;
          }
        } else {
          logger.severe("FileInputStream is null.");
          fis.close();
          return null;
        } // why is this area a dead code? Can't FileInputStream return null?
      } catch (FileNotFoundException e) {
        logger.severe("FileNotFoundException was occurred. The setter will generate file with key-value.");
        File file = new File(targetFilePath);        
        try {
          FileWriter fw = new FileWriter(file, true);
          fw.write(key + "=" + value);
          fw.flush();
          fw.close();
        } catch (IOException e1) {
          logger.severe("The generating file with key-value is failed.");
          return null;
        }

        return null;
      } catch (IOException e) {
        logger.severe("IOException was occurred. Please retry set method().");
        return null;
      }
    } else {
      logger.severe("This setter has not initialized. It must be initialized.");
      return null;
    }
  }

  @Override
  public void setAsync(String targetFilePath, String key, String value) {
    threadPoolExecutor_.execute(new SettingCallable(targetFilePath, key, value));
  }

  private class SettingCallable implements Runnable {
    private String key_;
    private String value_;
    private String targetFilePath_;

    public SettingCallable(String targetFilePath, String key, String value) {
      if (key != null) {
        this.key_ = key;
        this.value_ = value;
        this.targetFilePath_ = targetFilePath;
      } else {
        logger.severe("SettingCallable Constructor's key parameter are null. Please check parameters.");
      }
    }

    @Override
    public void run() {
      if (targetFilePath_ != null) {
        try {
          // Can I consider FileInputStream or BufferedFileInputStream or
          // FileReader for performance?
          FileInputStream fis = new FileInputStream(targetFilePath_);
          Properties properties = new Properties();
          if (fis != null) {
            properties.load(fis);
            properties.setProperty(key_, value_);
            fis.close();
            properties.store(new FileOutputStream(targetFilePath_), null);
            properties.clear();
          } else {
            logger.severe("FileInputStream is null.");
          } // why is this area a dead code? Can't FileInputStream return null?
        } catch (FileNotFoundException e) {
          logger.severe("FileNotFoundException was occurred. The setter will generate file with key-value.");
          File file = new File(targetFilePath_);        
          try {
            FileWriter fw = new FileWriter(file, true);
            fw.write(key_ + "=" + value_);
            fw.flush();
            fw.close();
          } catch (IOException e1) {
            logger.severe("The generating file with key-value is failed.");
            return;
          }

          return;
        } catch (IOException e) {
          logger.severe("IOException was occurred. Please retry set method().");
          return;
        }
      } else {
        logger.severe("This setter has not initialized. It must be initialized.");
      }
    }
  }
}
