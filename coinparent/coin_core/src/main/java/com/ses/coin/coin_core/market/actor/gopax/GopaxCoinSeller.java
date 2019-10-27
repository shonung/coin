package com.ses.coin.coin_core.market.actor.gopax;

import com.ses.coin.coin_core.market.actor.CheckerStatus;
import com.ses.coin.coin_core.market.actor.CoinSeller;
import com.ses.coin.coin_core.market.actor.inf.ICheckerStatusListener;
import com.ses.coin.coin_core.model.CoinInfo;
import com.ses.coin.coin_core.model.PurchaseInfo;
import com.ses.framework.pacific.logger.Logger;

public class GopaxCoinSeller extends CoinSeller {
  private static final String TAG = GopaxCoinSeller.class.getSimpleName();

  public GopaxCoinSeller(CoinInfo coinInfo, PurchaseInfo pInfo, ICheckerStatusListener listener) {
    super(coinInfo, pInfo, listener);
  }

  @Override
  public void run() {
    Logger.debug(TAG, coinInfo.returnMarketCoinName() + "'s seller is starting.");

    try {

    } catch (Exception e) {

    } finally {
      if (listener != null) {
        listener.changedCheckerStatus(CheckerStatus.SOLD, new PurchaseInfo(0, 0));
      }
    }
  }
}
