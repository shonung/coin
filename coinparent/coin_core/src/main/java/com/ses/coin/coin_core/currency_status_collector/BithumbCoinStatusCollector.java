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

package com.ses.coin.coin_core.currency_status_collector;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.ses.coin.coin_core.Constants;
import com.ses.coin.coin_core.api_client.ApiClientManager;
import com.ses.coin.coin_core.currency_status_collector.inf.ICoinStatusListener;
import com.ses.coin.coin_core.currency_status_collector.inf.IStatusCollector;
import com.ses.coin.coin_core.model.CoinInfo;
import com.ses.coin.coin_core.property_manager.PropertyManager;
import com.ses.framework.pacific.common.thread.DefaultManagedRunnable;
import com.ses.framework.pacific.common.thread.ManagedThreadFactory;
import com.ses.framework.pacific.logger.Logger;
import com.ses.framework.pacific.restful_util.RestfulConnector.HttpMethodType;

public class BithumbCoinStatusCollector implements IStatusCollector {
  private static final String TAG = BithumbCoinStatusCollector.class.getSimpleName();

  private Map<String/* composite name */, ICoinStatusListener> listenerMap = new HashMap<>();
  private Long collectThreadId = -1L;  

  public BithumbCoinStatusCollector() {
  }

  protected void finalize() {
    if (collectThreadId > -1L) {
      ManagedThreadFactory.getInstance().terminateManagedThread(collectThreadId);
    }
    listenerMap.clear();
    listenerMap = null;
  }

  @Override
  public void start() {
    if (collectThreadId == -1L) {
      collect();
      Logger.info(TAG, "The Coin Stauts Collector is staring");
    } else {
      Logger.error(TAG, "This Status Collector is already running.");
    }
  }

  @Override
  public void stop() {
    if (collectThreadId > -1L) {
      ManagedThreadFactory.getInstance().terminateManagedThread(collectThreadId);
      collectThreadId = -1L;
    }
  }

  @Override
  public void registerListener(CoinInfo coinInfo, ICoinStatusListener listener) {
    if (coinInfo != null && listener != null) {
      listenerMap.put(coinInfo.returnMarketCoinName(), listener);
    } else {
      Logger.error(TAG, "The registration of listener is failed because input instance is null.");
    }
  }

  @Override
  public void releaseListener(CoinInfo coinInfo) {
    if (coinInfo != null) {
      listenerMap.remove(coinInfo.returnMarketCoinName());
    } else {
      Logger.error(TAG, "The releasement of listener is failed because input instance is null.");
    }
  }

  @Override
  public void collect() {
    collectThreadId = ManagedThreadFactory.getInstance().getThreadAndStart(new DefaultManagedRunnable() {
      @Override
      public void doRun() throws InterruptedException {
        while (true) {
          for (String marketCoinName : listenerMap.keySet()) {
            if (marketCoinName != null) {
              String[] splittedName = marketCoinName.split(Constants.MARKET_COIN_SPLITTER);
              String marketName = splittedName[0];
              String coinName = splittedName[1];
              
              try {
                JSONObject result = ApiClientManager.getInstance().getApiClient(marketName)
                    .postApiCall("/public/ticker/" + coinName, new JSONObject());
                if (result != null) {
                  Logger.debug(TAG, coinName + " result : " + result);

                  if (result.get(Constants.BITHUMB_STATUS).equals(Constants.BITHUMB_STATUS_OK)) {
                    String buy_price = (String) ((JSONObject) result.get(Constants.BITHUMB_DATA)).get(Constants.BITHUMB_BUY_PRICE);
                    ICoinStatusListener listener = listenerMap.get(marketCoinName);

                    if (buy_price != null) {
                      listener.notifyCurrencyPrice(coinName, buy_price);
                    } else {
                      Logger.error(TAG, "price is null. Please check value in response ");
                    }
                  } else {
                    Logger.error(TAG, "status is not ok");
                  }
                } else {
                }
              } catch (Exception e) {
                Logger.debug(TAG, "exception is occurred while collecting " + marketCoinName + "'s status. ");
              }
            }
            String collectionDelayMilli = PropertyManager.getPropertyManager().get(Constants.CONFIG_FILE_NAME, Constants.COLLECTION_DELAY_MILLI_KEY, null);
            Thread.sleep((collectionDelayMilli != null) ? Integer.parseInt(collectionDelayMilli.trim()) : Constants.DEFAULT_COLLECTION_DELAY_MILLI);
          }
          String collectionIntervalMilli = PropertyManager.getPropertyManager().get(Constants.CONFIG_FILE_NAME, Constants.COLLECTION_INTERVAL_MILLI_KEY, null);
          Thread.sleep((collectionIntervalMilli != null) ? Integer.parseInt(collectionIntervalMilli.trim()) : Constants.DEFAULT_COLLECTION_INTERVAL_MILLI);
        }
      }
    }, 0);
  }
}
