/**
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

package com.ses.coin.coin_core.property_manager;

import com.ses.framework.pacific.propman.ConfigManagerFactory;
import com.ses.framework.pacific.propman.inf.IConfigManagerListener;
import com.ses.framework.pacific.propman.Config.FileType;

import java.io.File;
import java.util.Map;

public class PropertyManager {
  private static final String TAG = PropertyManager.class.getSimpleName();

  private static final PropertyManager instance = new PropertyManager();

  private com.ses.framework.pacific.propman.inf.IPropertyManager propMan = null;

  public static PropertyManager getPropertyManager() {
    return instance;
  }

  private PropertyManager() {
  }

  public PropertyManager initialize(String folderPath) {
    this.propMan = ConfigManagerFactory.getConfigManager(FileType.PROPERTIES_TYPE, folderPath);
    return this;
  }

  public void close() {
    if (propMan != null) {
      this.propMan.stopPropertyManager();
      this.propMan = null;
    }
  }

  public String get(String fileName, String key, String defaultValue) {
    String ret = null;
    if (propMan != null) {
      ret = propMan.get(fileName, key, defaultValue);
    } 
    return ret;
  }

  public void setListener(IConfigManagerListener listener) {
    if (propMan != null) {
      propMan.registerListener(TAG, listener);
    }
  }

  public void set(String targetFileName, String key, String value) {    
    if (propMan != null) {
      propMan.setAsync(targetFileName + File.separator + FileType.PROPERTIES_TYPE, key, value);
    }
  }
  
  public Map<String, String> getAllKeyValueMap() {
    Map<String, String> ret = null;
    if (propMan != null) {
      ret = propMan.getAllKeyValueMap();
    }
    return ret;
  }
  
  public String getPropertyFileType() {
    String ret = null;
    if (propMan != null) {
      ret = propMan.getPropertyFileType().getFileExtension();
    }
    return ret;
  }
}
