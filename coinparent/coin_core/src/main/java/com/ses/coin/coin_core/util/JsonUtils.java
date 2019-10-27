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

package com.ses.coin.coin_core.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonUtils {
  public static JSONObject getJsonObjectAsString(String string) throws ParseException {
    JSONObject ret = null;
    if (string != null) {
      JSONParser parser = new JSONParser();
      try {
        Object obj = parser.parse(string);
        ret = (JSONObject) obj;
      } catch (ParseException e) {
        throw e;
      } catch (Exception e) {
        throw e;
      }
    }
    return ret;
  }

  @SuppressWarnings("unchecked")
  public static JSONObject getJsonStringFromMap(Map<String, Object> map) {

    JSONObject json = new JSONObject();
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      json.put(key, value);
    }

    return json;
  }

  @SuppressWarnings("unchecked")
  public static JSONArray getJsonArrayFromList(List<Map<String, Object>> list) {

    JSONArray jsonArray = new JSONArray();
    for (Map<String, Object> map : list) {
      jsonArray.add(getJsonStringFromMap(map));
    }

    return jsonArray;
  }

  @SuppressWarnings("unchecked")
  public static String getJsonStringFromList(List<Map<String, Object>> list) {

    JSONArray jsonArray = new JSONArray();
    for (Map<String, Object> map : list) {
      jsonArray.add(getJsonStringFromMap(map));
    }

    return jsonArray.toJSONString();
  }
  
  public static List<Object> getListFromJsonArray(JSONArray jsonAry) {
    List<Object> list = new ArrayList<>();
    for (int i=0; i<jsonAry.size(); i++) {
        list.add( jsonAry.get(i) );
    }
    return list;
  }

  @SuppressWarnings("unchecked")
  public static Map<String, Object> getMapFromJsonObject(JSONObject jsonObj) {

    Map<String, Object> map = null;

    try {
      map = new ObjectMapper().readValue(jsonObj.toJSONString(), Map.class);
    } catch (JsonParseException e) {
      e.printStackTrace();
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return map;
  }

  public static List<Map<String, Object>> getListMapFromJsonArray(JSONArray jsonArray) {

    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

    if (jsonArray != null) {
      int jsonSize = jsonArray.size();
      for (int i = 0; i < jsonSize; i++) {
        Map<String, Object> map = JsonUtils.getMapFromJsonObject((JSONObject) jsonArray.get(i));
        list.add(map);
      }
    }

    return list;
  }
}
