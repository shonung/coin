package com.ses.coin.coin_core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import com.ses.coin.coin_core.example.Api_Client;
import com.ses.coin.coin_core.market_manager.MarketManagerFactory;
import com.ses.coin.coin_core.market_manager.inf.IMarketManager;
import com.ses.coin.coin_core.property_manager.PropertyManager;
import com.ses.framework.pacific.logger.LogLevel;
import com.ses.framework.pacific.logger.Logger;

public class App 
{
    private static final String TAG = App.class.getSimpleName();
    private static String externalPropertyPath = System.getProperty("external_property_path");
    
    public static void main( String[] args )
    {    
//      Api_Client api = new Api_Client("6f8caf27bd215b7b89f1234e3648798d",
//          "73e7918910fff318d18d3cd88a114406");
//      
//        HashMap<String, String> rgParams = new HashMap<String, String>();
//        rgParams.put("order_currency", "BTC");
//        rgParams.put("payment_currency", "KRW");
//      
//      
//        try {
//            String result = api.callApi("/info/balance", rgParams);
//            System.out.println(result);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
      Logger.init("log", LogLevel.DEBUG);

      Logger.debug(TAG, "coin_core start");
      
      initializePropertyManager();
      
      IMarketManager coinMarketManager = MarketManagerFactory.COIN.create();
      if (coinMarketManager != null) {
        if (!coinMarketManager.start()) {
          Logger.error(TAG, "The initialization of CoinMarketManager is failed.");
        }
      } else {
        Logger.error(TAG, "CoinMarketManager is null. Maybe the error was occurred while create market manager instance.");
      }     
    }
    
    private static void initializePropertyManager() {
      if (externalPropertyPath == null) {
        File resourcesDir = new File(Constants.DEFAULT_EXTERNAL_PROPERTY_PATH);
        if (!resourcesDir.exists()) {
          resourcesDir.mkdirs();
        }

        File file = new File(Constants.DEFAULT_EXTERNAL_PROPERTY_PATH + File.separator
            + Constants.CONFIG_FILE_NAME + ".properties");

        if (!file.exists()) {
          try {
            InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(Constants.CONFIG_FILE_NAME + ".properties");
            OutputStream os = new FileOutputStream(file);
            if (is != null && os != null) {
              int readBuffer = 0;
              byte[] buffer = new byte[1024];
              while ((readBuffer = is.read(buffer)) != -1) {
                os.write(buffer, 0, readBuffer);
              }
              is.close();
              os.close();
            }
          } catch (FileNotFoundException e) {
            Logger.error(TAG, "The FileNotFoundException is occurred while copying properties files." + e);
            return;
          } catch (IOException e) {
            Logger.error(TAG, "The FileNotFoundException is occurred while copying properties files." + e);
            return;
          }
        }
        externalPropertyPath = Constants.DEFAULT_EXTERNAL_PROPERTY_PATH;
      }
      PropertyManager.getPropertyManager().initialize(externalPropertyPath);
    }
}
