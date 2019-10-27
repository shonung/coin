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

package com.ses.coin.coin_core.market;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.ses.coin.coin_core.Constants;
import com.ses.coin.coin_core.api_client.ApiClient;
import com.ses.coin.coin_core.api_client.ApiClientManager;
import com.ses.coin.coin_core.currency_status_collector.BithumbCoinStatusCollector;
import com.ses.coin.coin_core.currency_status_collector.StatusCollectorFactory;
import com.ses.coin.coin_core.currency_status_collector.inf.IStatusCollector;
import com.ses.coin.coin_core.market.inf.IMarket;
import com.ses.coin.coin_core.model.MarketInfo;
import com.ses.coin.coin_core.property_manager.PropertyManager;
import com.ses.coin.coin_core.util.JsonUtils;
import com.ses.framework.pacific.logger.Logger;

public class BithumbCoinMarket implements IMarket {
  private static final String TAG = BithumbCoinMarket.class.getSimpleName();
  
  private MarketInfo marketInfo = null;
  private BithumbCoinCheckerManager checkerManager = null;
  private IStatusCollector statusCollector = null;
  
  public BithumbCoinMarket(MarketInfo marketInfo) { 
    Logger.info(TAG, marketInfo.getMarketName() + " Coin Market is created. host url : " + marketInfo.getHostUrl());
    this.marketInfo = marketInfo;
    checkerManager = new BithumbCoinCheckerManager(marketInfo);    
    statusCollector = StatusCollectorFactory.BITHUMB_COIN.create();
    
    initialize();
    createAndRegisterApiClient();
  }
  
  private void initialize() {
    String supportedCoins = PropertyManager.getPropertyManager().get(Constants.CONFIG_FILE_NAME, Constants.COIN_TYPE_KEY, null);
    if (supportedCoins != null) {      
      try {
        supportedCoins = supportedCoins.trim();
        JSONObject allCoinsJson = JsonUtils.getJsonObjectAsString(supportedCoins);
        JSONArray supportedCoinsJson = (JSONArray) allCoinsJson.get(marketInfo.getMarketName());
        if (supportedCoinsJson != null) {
          List<Object> supportedCoinList = JsonUtils.getListFromJsonArray(supportedCoinsJson);                  
          Logger.info(TAG, marketInfo.getMarketName() + "'s supported coin : " + supportedCoinList);
          
          for (Object supportedCoin : supportedCoinList) {
            checkerManager.startChecker((String)supportedCoin, statusCollector);
          }     
          
          statusCollector.start();
        } else {
          Logger.error(TAG, marketInfo.getMarketName() + " this coin maker is not initialized because there is no coin type in CoinType Json.");
        }
      } catch (ParseException e) {
        Logger.error(TAG, marketInfo.getMarketName() + " this coin maker is not initialized because coin type is not abnormal in config file");
      }      
    } else {
      Logger.error(TAG, marketInfo.getMarketName() + " this coin maker is not initialized because there is no coin type in config file.");
    }
  }
  
  private void createAndRegisterApiClient() {
    ApiClientManager.getInstance().createApiClient(marketInfo.getMarketName(), new ApiClient(marketInfo.getHostUrl(), "", ""));
  }
  
}
