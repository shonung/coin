package com.ses.coin.coin_core.market;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.ses.coin.coin_core.Constants;
import com.ses.coin.coin_core.api_client.ApiClient;
import com.ses.coin.coin_core.api_client.ApiClientManager;
import com.ses.coin.coin_core.currency_status_collector.StatusCollectorFactory;
import com.ses.coin.coin_core.currency_status_collector.inf.IStatusCollector;
import com.ses.coin.coin_core.market.inf.IMarket;
import com.ses.coin.coin_core.model.MarketInfo;
import com.ses.coin.coin_core.property_manager.PropertyManager;
import com.ses.coin.coin_core.util.JsonUtils;
import com.ses.framework.pacific.logger.Logger;

public class KorbitCoinMarket implements IMarket {
  private static final String TAG = KorbitCoinMarket.class.getSimpleName();

  private MarketInfo marketInfo = null;
  private KorbitCoinCheckerManager checkerManager = null;
  private IStatusCollector statusCollector = null;

  public KorbitCoinMarket(MarketInfo marketInfo) { 
    Logger.info(TAG, marketInfo.getMarketName() + " Coin Market is created. host url : " + marketInfo.getHostUrl());
    this.marketInfo = marketInfo;
    checkerManager = new KorbitCoinCheckerManager(marketInfo);
    statusCollector = StatusCollectorFactory.KORBIT_COIN.create();
    
    initialize();
    createAndRegisterApiClient();
  }

  private void initialize() {
    String supportedCoins = PropertyManager.getPropertyManager().get(Constants.CONFIG_FILE_NAME,
        Constants.COIN_TYPE_KEY, null);
    if (supportedCoins != null) {
      try {
        JSONObject allCoinsJson = JsonUtils.getJsonObjectAsString(supportedCoins);
        JSONArray supportedCoinsJson = (JSONArray) allCoinsJson.get(marketInfo.getMarketName());
        if (supportedCoinsJson != null) {
          List<Object> supportedCoinList = JsonUtils.getListFromJsonArray(supportedCoinsJson);
          Logger.info(TAG, marketInfo.getMarketName() + "'s supported coin : " + supportedCoinList);

          for (Object supportedCoin : supportedCoinList) {
            checkerManager.startChecker((String) supportedCoin, statusCollector);
          }

          statusCollector.start();          
        } else {
          Logger.error(TAG, marketInfo.getMarketName()
              + " this coin maker is not initialized because there is no coin type in CoinType Json.");
        }
      } catch (ParseException e) {
        Logger.error(TAG, marketInfo.getMarketName()
            + " this coin maker is not initialized because coin type is not abnormal in config file");
      }
    } else {
      Logger.error(TAG, marketInfo.getMarketName()
          + " this coin maker is not initialized because there is no coin type in config file.");
    }
  }

  private void createAndRegisterApiClient() {
    ApiClientManager.getInstance().createApiClient(marketInfo.getMarketName(),
        new ApiClient(marketInfo.getHostUrl(), "", ""));
  }
}
