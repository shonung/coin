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

public class GopaxCoinCheckerManager {
  private static final String TAG = BithumbCoinCheckerManager.class.getSimpleName();

  private MarketInfo marketInfo = null;
  private ConcurrentHashMap<String/* coin name */, Long/* managed thread id */> managedCheckerMap = new ConcurrentHashMap<>();

  public GopaxCoinCheckerManager(MarketInfo marketInfo) {
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
