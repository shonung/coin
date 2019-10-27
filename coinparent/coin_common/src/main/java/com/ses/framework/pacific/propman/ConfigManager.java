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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.ses.framework.pacific.logger.Logger;
import com.ses.framework.pacific.propman.Config.FileType;
import com.ses.framework.pacific.propman.inf.IChangedConfigValueListener;
import com.ses.framework.pacific.propman.inf.IConfigManagerListener;
import com.ses.framework.pacific.propman.inf.IGetter;
import com.ses.framework.pacific.propman.inf.IMonitoringStrategy;
import com.ses.framework.pacific.propman.inf.IPropertyManager;
import com.ses.framework.pacific.propman.inf.ISetter;

public class ConfigManager implements IPropertyManager, Cloneable {
  private static final String TAG = ConfigManager.class.getSimpleName();

  private FileType fileType_;
  private IMonitoringStrategy monitoring_;
  protected IGetter getter_;
  protected ISetter setter_;

  protected HashMap<String, IConfigManagerListener> registeredListenerMap_;

  protected String targetFileExtension;
  protected String targetFilePath;
  protected Map<String/* key */, String/* value */> keyValueMap;

  @SuppressWarnings("unused")
  private ConfigManager() {
    // Do nothing
  }

  @Override
  public ConfigManager clone() {
    ConfigManager configManagerReturn = null;

    try {
      configManagerReturn = (ConfigManager) super.clone();
    } catch (CloneNotSupportedException e) {
      Logger.error(TAG, e.getMessage());
      return null;
    }
    return configManagerReturn;
  }

  // This method only was called by getConfigManager() method.
  protected ConfigManager(IGetter getter, ISetter setter, IMonitoringStrategy monitoring, FileType configType,
      String targetFilePath) {
    if (getter != null && setter != null && monitoring != null) {
      this.getter_ = getter;
      this.setter_ = setter;
      this.monitoring_ = monitoring;
      monitoring_.registerListener(new ChangedValueHandler());

      this.targetFilePath = targetFilePath;
      this.targetFileExtension = configType.getFileExtension();
      
      this.fileType_ = configType;

      initialize_();

    } else {
      Logger.error(TAG, "getter or setter is null. Please check a getter and setter.");
    }
  }

  private void initialize_() {
    if (getter_ == null || setter_ == null) {
      Logger.error(TAG, "The initialization of keyValueMap wasn't possible because getter is null.");
      return;
    }

    keyValueMap = new HashMap<String, String>();
    Collections.synchronizedMap(keyValueMap);

    File targetFile = new File(targetFilePath);
    if (keyValueMap != null && targetFile != null) {
      if (targetFile.isDirectory()) {
        File[] targetFiles = targetFile.listFiles();
        if (targetFiles != null) {
          for (File file : targetFiles) {
            if (file != null && file.exists()) {
              String filePath = file.getAbsolutePath();
              if (filePath != null && filePath.contains(targetFileExtension)) {
                List<String> keys = getter_.getKeys(filePath);
                if (keys != null) {
                  String fileName = file.getName();
                  String _fileName = fileName.substring(0, fileName.lastIndexOf("."));
                  for (String key : keys) {
                    keyValueMap.put(_fileName.toLowerCase() + "." + key.toLowerCase(),
                        getter_.get(filePath, key, null));
                  }
                }
              }
            }
          }
        }
      } else { // it is file
        List<String> keys = getter_.getKeys(targetFilePath);
        if (keys != null) {
          String _fileName = targetFilePath.substring(0, targetFilePath.lastIndexOf("."));
          for (String key : keys) {
            keyValueMap.put(_fileName.toLowerCase() + "." + key.toLowerCase(), getter_.get(targetFilePath, key, null));
          }
        }
      }

    } else {
      Logger.error(TAG, "The file is wrong. Please check your configuration file.");
    }

    Logger.info(TAG, keyValueMap + "");
  }

  @Override
  public void stopPropertyManager() {
    if (monitoring_ != null) {
      monitoring_.releaseListener();
      monitoring_.stopMonitor();
      monitoring_ = null;
    }
    if (getter_ != null) {
      getter_ = null;
    }
    if (setter_ != null) {
      setter_ = null;
    }
  }

  @Override
  public void registerListener(String className, IConfigManagerListener listener) {
    if (registeredListenerMap_ == null) {
      registeredListenerMap_ = new HashMap<String, IConfigManagerListener>();
    }

    // The put method never return fail?
    registeredListenerMap_.put(className, listener);
  }

  @Override
  public void releaseListener(String className) {
    if (registeredListenerMap_ != null) {
      registeredListenerMap_.remove(className);
    } else {
      Logger.warn(TAG, "listener map is null.");
    }
  }

  private class ChangedValueHandler implements IChangedConfigValueListener {

    public void changedConfigValue(long latestModifiedTime, String changedFilePath, String changedFileName) {
      handleChangedConfigValue_(latestModifiedTime, changedFilePath, changedFileName);
    }

    private void handleChangedConfigValue_(long latestModifiedTime, String changedFilePath, String changedFileName) {
      Logger.info(TAG, "handleChangedConfigValue_" + ", lastModifiedTime : " + latestModifiedTime
          + ", changedFilePath : " + changedFilePath);

      if (changedFilePath != null && changedFilePath.contains(targetFileExtension)) {
        if (targetFilePath.endsWith(File.separator)) {
          targetFilePath = targetFilePath.substring(0, targetFilePath.length()-1);
        }
        refreshAndCallback_((targetFilePath.contains(changedFilePath)) ? changedFilePath
            : targetFilePath + File.separator + changedFilePath, changedFileName);
      }
    }
  }

  protected void refreshAndCallback_(String filePath, String fileName) {
    HashMap<String, String> changedKeyValueMap = new HashMap<String, String>();
    refreshConfigValueInConfigManager_(changedKeyValueMap, filePath, fileName);
    if (changedKeyValueMap != null && !changedKeyValueMap.isEmpty() && registeredListenerMap_ != null) {
      for (IConfigManagerListener callback : registeredListenerMap_.values()) {
        callback.changedConfigValue(changedKeyValueMap);
      }
    }
  }

  protected HashMap<String, String> refreshConfigValueInConfigManager_(HashMap<String, String> changedKeyValueMap,
      String filePath, String fileName) {    
    try {
      if (getter_ != null && changedKeyValueMap != null && filePath != null && fileName != null) {
        String _fileName = fileName.substring(0, fileName.lastIndexOf("."));         
        
        List<String> keys = getter_.getKeys(filePath);
        if (keys != null) {
          List<String> lowerKeys = keys.stream().map(String::toLowerCase).collect(Collectors.toList());
          
          Map<String,String> copyMap = new HashMap<>();
          copyMap.putAll(keyValueMap);
          Set<String> oldKeySet = copyMap.keySet();
          for (String oldKey : oldKeySet) {
            String onlyKey = oldKey.substring(oldKey.lastIndexOf(".")+1, oldKey.length());
            if (!lowerKeys.contains(onlyKey)) {              
              changedKeyValueMap.put(onlyKey, "");
              keyValueMap.remove(oldKey);
              Logger.info(TAG, "remove key-value, key : " + oldKey.toLowerCase());
            }
          }          
          
          for (String key : keys) {
            String oldValue = keyValueMap.get(_fileName.toLowerCase() + "." + key.toLowerCase());
            String newValue = getter_.get(filePath, key, null);
            
            if (oldValue == null || (oldValue != null && !oldValue.equals(newValue))) {
              changedKeyValueMap.put(key, oldValue);
              keyValueMap.put(_fileName.toLowerCase() + "." + key.toLowerCase(), newValue);
              Logger.info(TAG, "put key-value, key : " + _fileName.toLowerCase() + "." + key.toLowerCase() + "value : " + newValue);
            } 
          }
        }
      }
    } catch (Exception e) {      
      Logger.error(TAG, "The exception was occured while refreshing property values.");
    }
    
    Logger.info(TAG, "" + keyValueMap.toString());
    
    return changedKeyValueMap;
  }

  @SuppressWarnings("unused") // this function just utility for test. I want to
                              // remain this function for adding more function.
  private <K, V> K getKeyFromValueInMap_(HashMap<K, V> mapParam, V value) {
    for (Map.Entry<K, V> entry : mapParam.entrySet()) {
      if (value.equals(entry.getValue())) {
        return entry.getKey();
      }
    }
    return null;
  }

  @Override
  public synchronized String get(String keyWithFileName, String defaultValue) {
    if (keyValueMap == null || keyWithFileName == null) {
      return defaultValue;
    }

    String ret = keyValueMap.get(keyWithFileName.toLowerCase());

    return (ret != null) ? ret : defaultValue;
  }

  @Override
  public synchronized String get(String fileName, String key, String defaultValue) {
    if (keyValueMap == null || key == null || fileName == null) {
      return defaultValue;
    }

    String ret = keyValueMap.get(fileName.toLowerCase() + "." + key.toLowerCase());

    return (ret != null) ? ret : defaultValue;
  }

  @Override
  public synchronized String setSync(String targetFilePath, String key, String value) {
    return setter_.setSync(targetFilePath, key, value);
  }

  @Override
  public synchronized void setAsync(String targetFilePath, String key, String value) {
    setter_.setAsync(targetFilePath, key, value);
  }

  @Override
  public Map<String, String> getsByPrefix(String prefix) {
    if (targetFilePath == null && prefix == null) {
      return null;
    }

    Map<String, String> gettedMap = new HashMap<String, String>();
    Set<String> keySet = keyValueMap.keySet();

    for (String key : keySet) {
      if (key != null && key.toLowerCase().contains(prefix.toLowerCase())) {
        gettedMap.put(key, get(key, ""));
      }
    }

    return gettedMap;
  }

  @Override
  public Map<String, String> getAllKeyValueMap() {
    return keyValueMap;
  }

  @Override
  public FileType getPropertyFileType() {    
    return this.fileType_;
  }
}
