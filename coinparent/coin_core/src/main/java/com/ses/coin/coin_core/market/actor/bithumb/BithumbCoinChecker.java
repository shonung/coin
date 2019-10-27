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

package com.ses.coin.coin_core.market.actor.bithumb;

import com.ses.coin.coin_core.Constants;
import com.ses.coin.coin_core.currency_status_collector.StatusCollectorFactory;
import com.ses.coin.coin_core.decision_maker.CoinDecisionMaker;
import com.ses.coin.coin_core.market.actor.CheckerStatus;
import com.ses.coin.coin_core.market.actor.CoinChecker;
import com.ses.coin.coin_core.market.actor.inf.ICheckerStatusListener;
import com.ses.coin.coin_core.model.CoinInfo;
import com.ses.coin.coin_core.model.PurchaseInfo;
import com.ses.framework.pacific.logger.Logger;

public class BithumbCoinChecker extends CoinChecker implements ICheckerStatusListener {
  private static final String TAG = BithumbCoinChecker.class.getSimpleName();  
  private CheckerStatus checkerStatus = CheckerStatus.CHECKING;
  
  public BithumbCoinChecker(CoinInfo coinInfo) {
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
