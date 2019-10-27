package com.ses.coin.coin_core.market.actor.gopax;

import org.json.simple.JSONObject;

import com.ses.coin.coin_core.Constants;
import com.ses.coin.coin_core.api_client.ApiClientManager;
import com.ses.coin.coin_core.market.actor.CheckerStatus;
import com.ses.coin.coin_core.market.actor.CoinBuyer;
import com.ses.coin.coin_core.market.actor.inf.ICheckerStatusListener;
import com.ses.coin.coin_core.model.CoinInfo;
import com.ses.coin.coin_core.model.PurchaseInfo;
import com.ses.framework.pacific.logger.Logger;

public class GopaxCoinBuyer extends CoinBuyer {
  private static final String TAG = GopaxCoinBuyer.class.getSimpleName();

  public GopaxCoinBuyer(CoinInfo coinInfo, ICheckerStatusListener listener) {
    super(coinInfo, listener);
  }

  @Override
  public void run() {
    Logger.debug(TAG, coinInfo.returnMarketCoinName() + "'s buyer is starting.");
    Integer currentPrice = 0;
    Integer boughtCoinNum = 0;
    currentPrice = getCurrentPrice(coinInfo.getMarketInfo().getMarketName(), coinInfo.getCoinName());
    try {
      if (currentPrice > 0) {

      }
    } catch (Exception e) {

    } finally {
      if (listener != null) {
        if (currentPrice > 0 && boughtCoinNum > 0) {
          listener.changedCheckerStatus(CheckerStatus.BOUGHT, new PurchaseInfo(boughtCoinNum, currentPrice));
        } else {
          Logger.error(TAG, "this buyer is not valid.");
          listener.changedCheckerStatus(CheckerStatus.CHECKING, new PurchaseInfo(0, 0));
        }
      }
    }
  }

  private Integer buyCurrentPrice(String marketName, String coinName) {
    Integer ret = 0; // bought coin num

    Integer currentKwd = getCurrentKwd(marketName, coinName);
    if (currentKwd > 0) {

    }

    return ret;
  }

  private Integer getCurrentKwd(String marketName, String coinName) {
    Integer ret = 0; // curretn kwd

    return ret;
  }

  private Integer getCurrentPrice(String marketName, String coinName) { // continously loop
    Integer ret = 0; // current price
    for (int loop = 0; loop < Constants.BUYER_RETRY_NUM; loop++) {
      try {
        JSONObject result = ApiClientManager.getInstance().getApiClient(marketName)
            .postApiCall("/public/ticker/" + coinName, new JSONObject());
        if (result != null) {
          Logger.debug(TAG, coinName + " result : " + result);

          if (result.get(Constants.BITHUMB_STATUS).equals(Constants.BITHUMB_STATUS_OK)) {
            String buy_price = (String) ((JSONObject) result.get(Constants.BITHUMB_DATA))
                .get(Constants.BITHUMB_BUY_PRICE);
            ret = Integer.parseInt(buy_price);
            break;
          } else {
            Logger.error(TAG, "status is not ok");
          }
        } else {
        }
      } catch (Exception e) {
        Logger.debug(TAG, "exception is occurred while collecting " + marketName + "-" + coinName + "'s status. ");
        break;
      }
    }
    return ret;
  }
}
