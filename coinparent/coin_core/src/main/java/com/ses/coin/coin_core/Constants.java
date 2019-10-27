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

package com.ses.coin.coin_core;

public class Constants {
  public static final String DEFAULT_EXTERNAL_PROPERTY_PATH = "resources";
  public static final String CONFIG_FILE_NAME = "configs";
  
  public static final String COLLECTION_DELAY_MILLI_KEY = "CollectionDelayMilli";
  public static final String COLLECTION_INTERVAL_MILLI_KEY = "CollectionIntervalMilli";
  public static final String COIN_MARKET_KEY = "CoinMarket";
  public static final String COIN_TYPE_KEY = "CoinType";
  public static final String MARKET_KEY = "MarketKey";  
  
  public static final String DEFAULT_COIN_NAME = "BTC";
  
  public static final String MARKET_COIN_SPLITTER = "@";
  
  public static final Integer DEFAULT_COLLECTION_DELAY_MILLI = 1 * 1000;  
  public static final Integer DEFAULT_COLLECTION_INTERVAL_MILLI = 1 * 60 * 1000;
  
  
  public static final String BITHUMB_STATUS = "status";
  public static final String BITHUMB_STATUS_OK = "0000";
  public static final String BITHUMB_DATA = "data";
  public static final String BITHUMB_BUY_PRICE = "buy_price";
  
  public static final String KORBIT_ASK = "ask";
  
  
  public static final Integer BUYER_RETRY_NUM = 10;
}
