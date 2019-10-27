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

import java.util.concurrent.ConcurrentHashMap;

import com.ses.coin.coin_core.currency_status_collector.inf.IStatusCollector;
import com.ses.coin.coin_core.decision_maker.CoinDecisionMaker;
import com.ses.coin.coin_core.market.actor.CoinChecker;
import com.ses.coin.coin_core.market.actor.CoinCheckerFactory;
import com.ses.coin.coin_core.model.CoinInfo;
import com.ses.coin.coin_core.model.MarketInfo;
import com.ses.framework.pacific.common.thread.ManagedThreadFactory;
import com.ses.framework.pacific.logger.Logger;

public class BithumbCoinCheckerManager {
  private static final String TAG = BithumbCoinCheckerManager.class.getSimpleName();
  
  private MarketInfo marketInfo = null;
  private ConcurrentHashMap<String/*coin name*/,Long/*managed thread id*/> managedCheckerMap = new ConcurrentHashMap<>();
  
  public BithumbCoinCheckerManager(MarketInfo marketInfo) {
    this.marketInfo = marketInfo;
  }
  
  public void startChecker(String coinName, IStatusCollector statusCollector) {    
    if (coinName != null) {
      CoinInfo coinInfo = new CoinInfo(marketInfo, coinName); 
      CoinChecker coinChecker = CoinCheckerFactory.createCoinChecker(coinInfo);
      long threadId = ManagedThreadFactory.getInstance().getThreadAndStart(coinChecker, 0);
      statusCollector.registerListener(coinInfo, CoinDecisionMaker.getInstance());
      managedCheckerMap.put(coinName, threadId);      
    } else {
      Logger.error(TAG, "coin name is null. startChecker() is failed");
    }
  }
  
  public void stopChecker(String coinName) {
    if (coinName != null) {      
      Long threadId = managedCheckerMap.remove(coinName);
      if (threadId != null) {
        ManagedThreadFactory.getInstance().terminateManagedThread(threadId);
      }
    } else {
      Logger.error(TAG, "coin name is null. stopChecker() is failed");
    }
  }
  
  public void stopAllChecker() {
    for (String coinName : managedCheckerMap.keySet()) {
      stopChecker(coinName);
    }
  }
  
}
