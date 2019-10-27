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

import org.json.simple.JSONObject;

import com.ses.coin.coin_core.Constants;
import com.ses.coin.coin_core.api_client.ApiClientManager;
import com.ses.coin.coin_core.market.actor.CheckerStatus;
import com.ses.coin.coin_core.market.actor.CoinBuyer;
import com.ses.coin.coin_core.market.actor.inf.ICheckerStatusListener;
import com.ses.coin.coin_core.model.CoinInfo;
import com.ses.coin.coin_core.model.PurchaseInfo;
import com.ses.framework.pacific.logger.Logger;

public class BithumbCoinBuyer extends CoinBuyer {
  private static final String TAG = BithumbCoinBuyer.class.getSimpleName();

  public BithumbCoinBuyer(CoinInfo coinInfo, ICheckerStatusListener listener) {
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
