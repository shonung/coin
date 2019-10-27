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

public class GopaxCoinStatusCollector implements IStatusCollector {
  private static final String TAG = BithumbCoinStatusCollector.class.getSimpleName();

  private Map<String/* composite name */, ICoinStatusListener> listenerMap = new HashMap<>();
  private Long collectThreadId = -1L;

  public GopaxCoinStatusCollector() {
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
                Map<String,String> getInput = new HashMap<>();
//                getInput.put("", "");
                JSONObject result = ApiClientManager.getInstance().getApiClient(marketName)
                    .getApiCall("/trading-pairs/" + coinName + "/ticker", getInput, new JSONObject());
                if (result != null) {
                  Logger.debug(TAG, coinName + " result : " + result);

                  if (result.get(Constants.BITHUMB_STATUS).equals(Constants.BITHUMB_STATUS_OK)) {
                    String buy_price = (String) ((JSONObject) result.get(Constants.BITHUMB_DATA))
                        .get(Constants.BITHUMB_BUY_PRICE);
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
            Thread.sleep(Constants.DEFAULT_COLLECTION_DELAY_MILLI);
          }
          Thread.sleep(Constants.DEFAULT_COLLECTION_INTERVAL_MILLI);
        }
      }
    }, 0);
  }
}
