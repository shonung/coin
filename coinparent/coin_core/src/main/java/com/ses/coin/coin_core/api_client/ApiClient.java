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

package com.ses.coin.coin_core.api_client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;

import com.ses.coin.coin_core.util.HttpRequest;
import com.ses.coin.coin_core.util.Util;
import com.ses.framework.pacific.logger.Logger;
import com.ses.framework.pacific.restful_util.RestfulConnector;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

@SuppressWarnings("unused")
public class ApiClient {  
  private static final String TAG = ApiClient.class.getSimpleName();
  
  protected String api_url = "https://api.bithumb.com";
  protected String api_key;
  protected String api_secret;

  public ApiClient(String api_url, String api_key, String api_secret) {
    this.api_url = api_url;
    this.api_key = api_key;
    this.api_secret = api_secret;
  }
  
  public synchronized JSONObject postApiCall(String resource, JSONObject body) {
    return RestfulConnector.postRestfulApiWithJson(api_url, resource, body);
  }
  
  public synchronized JSONObject getApiCall(String resource, Map<String,String> getInput, JSONObject body) {
    return RestfulConnector.getRestfulApiWithJson(api_url, resource, getInput, body);
  }
  
 
  private String usecTime() {
    return String.valueOf(System.currentTimeMillis());
  }

  private String request(String strHost, String strMemod, HashMap<String, String> rgParams,
      HashMap<String, String> httpHeaders) {
    String response = "";

    if (strHost.startsWith("https://")) {
      HttpRequest request = HttpRequest.get(strHost);
      // Accept all certificates
      request.trustAllCerts();
      // Accept all hostnames
      request.trustAllHosts();
    }

    if (strMemod.toUpperCase().equals("HEAD")) {
    } else {
      HttpRequest request = null;
     
      if (strMemod.toUpperCase().equals("POST")) {

        request = new HttpRequest(strHost, "POST");
        request.readTimeout(10000);

        System.out.println("POST ==> " + request.url());

        if (httpHeaders != null && !httpHeaders.isEmpty()) {
          httpHeaders.put("api-client-type", "2");
          request.headers(httpHeaders);
          System.out.println(httpHeaders.toString());
        }
        if (rgParams != null && !rgParams.isEmpty()) {
          request.form(rgParams);
          System.out.println(rgParams.toString());
        }
      } else {
        request = HttpRequest.get(strHost + Util.mapToQueryString(rgParams));
        request.readTimeout(10000);

        System.out.println("Response was: " + response);
      }

      if (request.ok()) {
        response = request.body();
      } else {
        response = "error : " + request.code() + ", message : " + request.body();
      }
      request.disconnect();
    }

    return response;
  }

  public static String encodeURIComponent(String s) {
    String result = null;

    try {
      result = URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20").replaceAll("\\%21", "!").replaceAll("\\%27", "'")
          .replaceAll("\\%28", "(").replaceAll("\\%29", ")").replaceAll("\\%26", "&").replaceAll("\\%3D", "=")
          .replaceAll("\\%7E", "~");
    }

    // This exception should never occur.
    catch (UnsupportedEncodingException e) {
      result = s;
    }

    return result;
  }

  private HashMap<String, String> getHttpHeaders(String endpoint, HashMap<String, String> rgData, String apiKey,
      String apiSecret) {

    String strData = Util.mapToQueryString(rgData).replace("?", "");
    String nNonce = usecTime();

    strData = strData.substring(0, strData.length() - 1);

    System.out.println("1 : " + strData);

    strData = encodeURIComponent(strData);

    HashMap<String, String> array = new HashMap<String, String>();

    String str = endpoint + ";" + strData + ";" + nNonce;

    String encoded = asHex(hmacSha512(str, apiSecret));

    System.out.println("strData was: " + str);
    System.out.println("apiSecret was: " + apiSecret);
    array.put("Api-Key", apiKey);
    array.put("Api-Sign", encoded);
    array.put("Api-Nonce", String.valueOf(nNonce));

    return array;

  }

  private static final String DEFAULT_ENCODING = "UTF-8";
  private static final String HMAC_SHA512 = "HmacSHA512";

  public static byte[] hmacSha512(String value, String key) {
    try {
      SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(DEFAULT_ENCODING), HMAC_SHA512);

      Mac mac = Mac.getInstance(HMAC_SHA512);
      mac.init(keySpec);

      final byte[] macData = mac.doFinal(value.getBytes());
      byte[] hex = new Hex().encode(macData);

      // return mac.doFinal(value.getBytes(DEFAULT_ENCODING));
      return hex;

    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    } catch (InvalidKeyException e) {
      throw new RuntimeException(e);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  public static String asHex(byte[] bytes) {
    return new String(Base64.encodeBase64(bytes));
  }

  @SuppressWarnings("unchecked")
  public HashMap<String,String> callApi(String endpoint, String type,HashMap<String, String> params) {
    HashMap<String, String> result = null;
    String rgResultDecode = "";
    HashMap<String, String> rgParams = new HashMap<String, String>();
    rgParams.put("endpoint", endpoint);

    if (params != null) {
      rgParams.putAll(params);
    }

    String api_host = api_url + endpoint;
    HashMap<String, String> httpHeaders = getHttpHeaders(endpoint, rgParams, api_key, api_secret);

    rgResultDecode = request(api_host, type, rgParams, httpHeaders);

    if (!rgResultDecode.startsWith("error")) {      
      try {
        result = new ObjectMapper().readValue(rgResultDecode, HashMap.class);
        System.out.println(result.get("status"));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return result;
  }
}
