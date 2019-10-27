package com.ses.coin.coin_core.market.actor.korbit;

import com.ses.coin.coin_core.Constants;
import com.ses.coin.coin_core.decision_maker.CoinDecisionMaker;
import com.ses.coin.coin_core.market.actor.CheckerStatus;
import com.ses.coin.coin_core.market.actor.CoinChecker;
import com.ses.coin.coin_core.market.actor.bithumb.BithumbCoinBuyer;
import com.ses.coin.coin_core.market.actor.bithumb.BithumbCoinChecker;
import com.ses.coin.coin_core.market.actor.bithumb.BithumbCoinSeller;
import com.ses.coin.coin_core.market.actor.inf.ICheckerStatusListener;
import com.ses.coin.coin_core.model.CoinInfo;
import com.ses.coin.coin_core.model.PurchaseInfo;
import com.ses.framework.pacific.logger.Logger;

public class KorbitCoinChecker extends CoinChecker implements ICheckerStatusListener {
  private static final String TAG = KorbitCoinChecker.class.getSimpleName();
  private CheckerStatus checkerStatus = CheckerStatus.CHECKING;

  public KorbitCoinChecker(CoinInfo coinInfo) {
    super(coinInfo);    
    Logger.debug(TAG, coinInfo.returnMarketCoinName() + "'s current status is " + this.checkerStatus);
  }

  public synchronized void setCheckerStatus(CheckerStatus checkerStatus) {
    this.checkerStatus = checkerStatus;
    Logger.debug(TAG, coinInfo.returnMarketCoinName() + "'s current status is " + this.checkerStatus);
  }

  @Override
  public void doRun() throws InterruptedException {
    Logger.info(TAG, TAG + " for " + coinInfo.getCoinName() + " is running");

    while (true) {
      if (checkerStatus.equals(CheckerStatus.CHECKING)) {
        if (CoinDecisionMaker.getInstance() != null && CoinDecisionMaker.getInstance().isGood(coinInfo.returnMarketCoinName())) {
          Logger.debug(TAG, coinInfo.returnMarketCoinName() + " is good. start coinBuyer.");
          setCheckerStatus(CheckerStatus.BUYING);
          coinBuyer = new BithumbCoinBuyer(coinInfo, this);
          coinBuyer.run();
        }
      } else {
        Logger.debug(TAG, coinInfo.returnMarketCoinName() + "'s checker status is not checking.");
      }
      Thread.sleep(Constants.DEFAULT_COLLECTION_INTERVAL_MILLI);
    }
  }

  @Override
  public void changedCheckerStatus(CheckerStatus checkerStatus, PurchaseInfo pInfo) {
    setCheckerStatus(checkerStatus);
    if (this.checkerStatus.equals(CheckerStatus.BOUGHT)) {
      setCheckerStatus(CheckerStatus.SELLING);
      coinBuyer = null;
      coinSeller = new BithumbCoinSeller(coinInfo, pInfo, this);
      coinSeller.run();
    } else if (this.checkerStatus.equals(CheckerStatus.SOLD)) {
      setCheckerStatus(CheckerStatus.CHECKING);
      coinSeller = null;
    } else if (this.checkerStatus.equals(CheckerStatus.CHECKING)) {
      coinBuyer = null;
    }
  }
}
