package com.ses.coin.coin_core.currency_status_collector;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.ses.coin.coin_core.Constants;
import com.ses.coin.coin_core.api_client.ApiClientManager;
import com.ses.coin.coin_core.currency_status_collector.inf.ICoinStatusListener;
import com.ses.coin.coin_core.currency_status_collector.inf.IStatusCollector;
import com.ses.coin.coin_core.model.CoinInfo;
import com.ses.framework.pacific.common.thread.DefaultManagedRunnable;
import com.ses.framework.pacific.common.thread.ManagedThreadFactory;
import com.ses.framework.pacific.logger.Logger;

public class KorbitCoinStatusCollector implements IStatusCollector {
  private static final String TAG = KorbitCoinStatusCollector.class.getSimpleName();

  private Map<String/* composite name */, ICoinStatusListener> listenerMap = new HashMap<>();
  private Long collectThreadId = -1L;

  public KorbitCoinStatusCollector() {
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

  public void registerListener(CoinInfo coinInfo, ICoinStatusListener listener) {
    if (coinInfo != null && listener != null) {
      listenerMap.put(coinInfo.returnMarketCoinName(), listener);
    } else {
      Logger.error(TAG, "The registration of listener is failed because input instance is null.");
    }
  }

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
                Map<String, String> getInput = new HashMap<>();
                getInput.put("currency_pair", coinName);
                
                JSONObject result = ApiClientManager.getInstance().getApiClient(marketName)
                    .getApiCall("/v1/ticker/detailed", getInput, new JSONObject());
                if (result != null) {
                  Logger.debug(TAG, coinName + " result : " + result);

                  String buy_price = (String)result.get(Constants.KORBIT_ASK);
                  if (buy_price != null) {
                    listenerMap.get(marketCoinName).notifyCurrencyPrice(coinName, buy_price);
                  }                  
                } else {
                }
              } catch (Exception e) {
                Logger.debug(TAG, "exception is occurred while collecting " + marketCoinName + "'s status. ");
              }
            }
            Thread.sleep(Constants.DEFAULT_COLLECTION_DELAY_MILLI);
          }
          Thread.sleep(Constants.DEFAULT_COLLECTION_INTERVAL_MILLI);
        }
      }
    }, 0);
  }
}
