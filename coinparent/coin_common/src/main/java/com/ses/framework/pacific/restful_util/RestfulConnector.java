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

package com.ses.framework.pacific.restful_util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ses.framework.pacific.logger.Logger;

public class RestfulConnector {
  private static final String TAG = RestfulConnector.class.getSimpleName();
  
  public enum HttpMethodType {
    GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");
    private String str;
    HttpMethodType(String str) {
      this.str = str;
    }
    public String getMethodName() {
      return this.str;
    }
  }
  
  private static void acceptCookie() {
    CookieManager manager = new CookieManager();
    manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    CookieHandler.setDefault(manager);
  }
  
  @SuppressWarnings("deprecation")
  public static JSONObject getRestfulApiWithJson(String hostName, String apiPath, Map<String,String> getInput, JSONObject body) {
    JSONObject ret = null;
    URL url = null;
    HttpURLConnection conn = null;
    if (hostName != null && apiPath != null && body != null && getInput != null) {
      try {
        StringBuilder urlString = new StringBuilder(hostName);
        urlString.append(apiPath);
        urlString.append("?");
        for (String inputKey : getInput.keySet()) {
          urlString.append(inputKey + "=" + getInput.get(inputKey));
          urlString.append("&");
        }
        url = new URL(urlString.toString());

        conn = (HttpURLConnection) url.openConnection();        
        
        CookieManager manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
        
        conn.setRequestMethod(HttpMethodType.GET.getMethodName());
//        RestfulConnector.setCommonHeader(conn, HttpMethodType.GET.getMethodName(), Constants.VAL_HTTP_HEADER_CONTENT_TYPE_DEFAULT);
        conn.setRequestProperty("User-Agent", "insomnia/5.12.4");
        if (body != null && !body.isEmpty()) {
          RestfulConnector.setJSONObject(conn, body);
        }

        conn.connect();
      } catch (MalformedURLException e) {
        Logger.error(TAG, "MalformedURLException was occurred while generating url." + e);
        if (conn != null) {
          conn.disconnect();
          conn = null;
        }
        return null;
      } catch (IOException e) {
        Logger.error(TAG, "IOException was occurred while opening http connection." + e);
        if (conn != null) {
          conn.disconnect();
          conn = null;
        }
        return null;
      }
      
      if (conn != null) {
        try {
          InputStream is = null;
          int httpResultCode = conn.getResponseCode();
          
          if (httpResultCode < HttpURLConnection.HTTP_BAD_REQUEST) {
            is = conn.getInputStream();
          } else {
            is = conn.getErrorStream();
          }

          ByteArrayOutputStream os = new ByteArrayOutputStream();
          if (is != null && os != null) {
            while (true) {
              int data = is.read();
              if (data == -1) {
                break;
              }
              os.write(data);
            }
            is.close();
            os.close();
          }
          String respMessage = new String(os.toByteArray(), "UTF-8");

          Logger.debug(TAG, "response status : " + httpResultCode);
          Logger.debug(TAG, "response body : " + respMessage);
          
          if (respMessage != null) {
            JSONParser parser = new JSONParser();
            try {
              ret = (JSONObject) parser.parse(respMessage);              
            } catch (ParseException e) {
              Logger.error(TAG, "ParseException was occurred while parsing response." + e);
              return null;
            }
          }
        } catch (IOException e) {
          Logger.error(TAG, "IOException was occurred while opening http connection." + e);
        }
      }
    }
    return ret;
  }

  public static JSONObject postRestfulApiWithJson(String hostName, String apiPath, JSONObject body) {
    JSONObject ret = null;
    URL url = null;
    HttpURLConnection conn = null;
    if (hostName != null && apiPath != null && body != null) {
      try {
        url = new URL(hostName + apiPath);

        conn = (HttpURLConnection) url.openConnection();
        RestfulConnector.setCommonHeader(conn, HttpMethodType.POST.getMethodName(), Constants.VAL_HTTP_HEADER_CONTENT_TYPE_DEFAULT);
        if (body != null) {
          RestfulConnector.setJSONObject(conn, body);
        }

        conn.connect();
      } catch (MalformedURLException e) {
        Logger.error(TAG, "MalformedURLException was occurred while generating url." + e);
        if (conn != null) {
          conn.disconnect();
          conn = null;
        }
        return null;
      } catch (IOException e) {
        Logger.error(TAG, "IOException was occurred while opening http connection." + e);
        if (conn != null) {
          conn.disconnect();
          conn = null;
        }
        return null;
      }
      
      if (conn != null) {
        try {
          InputStream is = null;
          int httpResultCode = conn.getResponseCode();
          
          if (httpResultCode < HttpURLConnection.HTTP_BAD_REQUEST) {
            is = conn.getInputStream();
          } else {
            is = conn.getErrorStream();
          }

          ByteArrayOutputStream os = new ByteArrayOutputStream();
          if (is != null && os != null) {
            while (true) {
              int data = is.read();
              if (data == -1) {
                break;
              }
              os.write(data);
            }
            is.close();
            os.close();
          }
          String respMessage = new String(os.toByteArray(), "UTF-8");

          Logger.debug(TAG, "response status : " + httpResultCode);
          Logger.debug(TAG, "response body : " + respMessage);
          
          if (respMessage != null) {
            JSONParser parser = new JSONParser();
            try {
              ret = (JSONObject) parser.parse(respMessage);              
            } catch (ParseException e) {
              Logger.error(TAG, "ParseException was occurred while parsing response." + e);
              return null;
            }
          }
        } catch (IOException e) {
          Logger.error(TAG, "IOException was occurred while opening http connection." + e);
        }
      }
    }
        
    return ret;
  }

  public static void setCommonHeader(HttpURLConnection conn, String method) throws ProtocolException {
    setCommonHeader(conn, method, Constants.VAL_HTTP_HEADER_CACHE_CONTROL_DEFAULT,
        Constants.VAL_HTTP_HEADER_CONTENT_TYPE_DEFAULT, Constants.VAL_HTTP_HEADER_ACCEPT_DEFAULT);
  }

  public static void setCommonHeader(HttpURLConnection conn, String method, String contentType)
      throws ProtocolException {
    setCommonHeader(conn, method, Constants.VAL_HTTP_HEADER_CACHE_CONTROL_DEFAULT, contentType,
        Constants.VAL_HTTP_HEADER_ACCEPT_DEFAULT);
  }

  public static void setCommonHeader(HttpURLConnection conn, String method, String cacheControl, String contentType,
      String accept) throws ProtocolException {
    conn.setConnectTimeout((int) (Constants.HTTP_CONN_TIMEOUT * 1000));
    conn.setReadTimeout((int) (Constants.HTTP_READ_TIMEOUT * 1000));
    conn.setRequestProperty(Constants.KEY_HTTP_HEADER_CACHE_CONTROL, cacheControl);
    conn.setRequestProperty(Constants.KEY_HTTP_HEADER_CONTENT_TYPE, contentType);
    conn.setRequestProperty(Constants.KEY_HTTP_HEADER_ACCEPT, accept);

    conn.setDoInput(true);
    conn.setDoOutput(true);
    conn.setRequestMethod(method);
  }

  public static boolean setJSONObject(HttpURLConnection conn, JSONObject jsonObject) throws IOException {
    boolean ret = false;
    if (conn != null && jsonObject != null) {
      OutputStream os = null;
      os = conn.getOutputStream();
      os.write(jsonObject.toString().getBytes());
      os.flush();
      ret = true;
      closeInOutputStream_(os);
    }

    return ret;
  }

  private static void closeInOutputStream_(OutputStream out) {
    if (out != null) {
      try {
        out.close();
        // out = null;
      } catch (IOException ioe) {
        // do nothing
      }
    }
  }
}
