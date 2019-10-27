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

package com.ses.coin.coin_core.decision_maker;

import java.util.Queue;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import com.ses.coin.coin_core.currency_status_collector.BithumbCoinStatusCollector;
import com.ses.coin.coin_core.currency_status_collector.KorbitCoinStatusCollector;
import com.ses.coin.coin_core.currency_status_collector.inf.ICoinStatusListener;
import com.ses.coin.coin_core.model.CoinInfo;
import com.ses.framework.pacific.logger.Logger;

public class CoinDecisionMaker implements ICoinStatusListener{
  private static final String TAG = CoinDecisionMaker.class.getSimpleName();
  private static CoinDecisionMaker instance = new CoinDecisionMaker();
  
  private enum UpDown {
    UP, DOWN;
  }
  
  private CoinInfo coinInfo = null;
  private Integer pastPrice = 0;
  Queue<UpDown> rateQueue = new CircularFifoQueue<UpDown>(3); // past' - past - current
    
  public static CoinDecisionMaker getInstance() {
    return instance;
  }
  
  public void start() {
    Logger.debug(TAG, "decision maker is start.");
  }
  
  @Override
  public void notifyCurrencyPrice(String currency, String price) {
    Logger.debug(TAG, TAG + " currency : " + currency + " price : " + price);
    try {
      boolean ret = (pastPrice > Integer.parseInt(price)) ? rateQueue.add(UpDown.UP) : rateQueue.add(UpDown.DOWN);
      pastPrice = Integer.parseInt(price);
    } catch (java.lang.NumberFormatException e) {      
    }
  }
  
  public synchronized boolean isGood(String marketCoinName) {
    boolean ret = false;
    if (rateQueue.size() > 2) {
      for (Object rate : rateQueue.toArray()) {
        if ( ((UpDown)rate).equals(UpDown.DOWN) ) {
          return false;
        }
      }
      ret = true;
    }
    return ret;
  }
  
}
