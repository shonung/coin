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

package com.ses.coin.coin_core.api_client;

import java.util.concurrent.ConcurrentHashMap;

import com.ses.framework.pacific.logger.Logger;

public class ApiClientManager {
  private static final String TAG = ApiClientManager.class.getSimpleName();
  
  private ConcurrentHashMap<String/*marketName*/, ApiClient> clientMap = new ConcurrentHashMap<>();
  private static final ApiClientManager instance = new ApiClientManager();
  
  private ApiClientManager() {
  }
  
  protected void finalize() {
    clientMap.clear();
    clientMap = null;
  }
  
  public static ApiClientManager getInstance() {
    return instance;
  }
  
  public void createApiClient(String marketName, ApiClient apiClient) {
    if (marketName != null && apiClient != null) {
      clientMap.put(marketName, apiClient);
      Logger.debug(TAG, marketName + "'s ApiClient is created.");
    } else {
      Logger.error(TAG, "The creation of api client is failed because input instance is null.");
    }
  }
  
  public void destoryApiClient(String marketName) {
    if (marketName != null) {
      ApiClient apiClient = clientMap.remove(marketName);
      apiClient = null;
    } else {
      Logger.error(TAG, "The destruction of api client is failed because input instance is null.");
    }
  }
  
  public ApiClient getApiClient(String marketName) {
    ApiClient ret = null;
    if (marketName != null) {
      ret = clientMap.get(marketName);
    }
    return ret;
  }
  
}
