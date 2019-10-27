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

package com.ses.coin.coin_core.market_manager;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.ses.coin.coin_core.Constants;
import com.ses.coin.coin_core.currency_status_collector.BithumbCoinStatusCollector;
import com.ses.coin.coin_core.decision_maker.CoinDecisionMaker;
import com.ses.coin.coin_core.market.BithumbCoinMarket;
import com.ses.coin.coin_core.market.GopaxCoinMarket;
import com.ses.coin.coin_core.market.KorbitCoinMarket;
import com.ses.coin.coin_core.market.inf.IMarket;
import com.ses.coin.coin_core.market_manager.inf.IMarketManager;
import com.ses.coin.coin_core.model.MarketInfo;
import com.ses.coin.coin_core.property_manager.PropertyManager;
import com.ses.coin.coin_core.util.JsonUtils;
import com.ses.framework.pacific.logger.Logger;

public class CoinMarketManager implements IMarketManager {
  private static final String TAG = CoinMarketManager.class.getSimpleName();
  
  private Map<String, IMarket> managedMarketMap = new HashMap<>(); 

  @Override
  public boolean start() {
    boolean ret = false;
    Logger.debug(TAG, "CoinMarketManager start");
    ret = initialize();
    return ret;
  }

  @Override
  public void stop() {
    
  }
  
  @SuppressWarnings("unchecked")
  private boolean initialize() {
    boolean ret = false;
    String marketsString = PropertyManager.getPropertyManager().get(Constants.CONFIG_FILE_NAME, Constants.COIN_MARKET_KEY, null);
    if (marketsString != null) {
      try {
        JSONObject marketsJson = JsonUtils.getJsonObjectAsString(marketsString);
        Logger.info(TAG, "markets = " + marketsJson);
        
        if (marketsJson != null) {
          Map<String, Object> marketsMap = JsonUtils.getMapFromJsonObject(marketsJson);
          
          if (marketsMap != null) {
            for (String market : marketsMap.keySet()) {
              MarketInfo marketInfo = new MarketInfo();
              marketInfo.setMarketName(market);
              marketInfo.setHostUrl((String)marketsMap.get(market));
              
              if (market.equals("bithumb")) {
                managedMarketMap.put(market, new BithumbCoinMarket(marketInfo)); 
              } else if (market.equals("gopax")) {
                managedMarketMap.put(market, new GopaxCoinMarket(marketInfo)); 
              } else if (market.equals("korbit")) {
                managedMarketMap.put(market, new KorbitCoinMarket(marketInfo));
              }
            }
            
            CoinDecisionMaker.getInstance().start();
            
            ret = true;
          } else {
            Logger.error(TAG, "The CoinMarket's json type is abnormal");
          }          
        }   
      } catch (ParseException e) {
        Logger.error(TAG, "The CoinMarket's config value is abnormal.");
      }      
    } else {
      Logger.error(TAG, "There is no CoinMarket key-value in config file.");
    }
    return ret;
  }

}
