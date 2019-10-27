package com.ses.coin.coin_core.market.actor.korbit;

import com.ses.coin.coin_core.market.actor.CheckerStatus;
import com.ses.coin.coin_core.market.actor.CoinSeller;
import com.ses.coin.coin_core.market.actor.bithumb.BithumbCoinSeller;
import com.ses.coin.coin_core.market.actor.inf.ICheckerStatusListener;
import com.ses.coin.coin_core.model.CoinInfo;
import com.ses.coin.coin_core.model.PurchaseInfo;
import com.ses.framework.pacific.logger.Logger;

public class KorbitCoinSeller extends CoinSeller {
  private static final String TAG = KorbitCoinSeller.class.getSimpleName();

  public KorbitCoinSeller(CoinInfo coinInfo, PurchaseInfo pInfo, ICheckerStatusListener listener) {
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
