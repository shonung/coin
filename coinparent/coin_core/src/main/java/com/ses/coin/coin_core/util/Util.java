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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

@SuppressWarnings("restriction")
public class Util {

    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String HMAC_SHA512 = "HmacSHA512";

    public static String base64Encode(byte[] bytes) {
    String bytesEncoded = Base64.encode(bytes);
    return bytesEncoded;
    }

    public static String hashToString(String data, byte[] key) {
    String result = null;
    Mac sha512_HMAC;
  
    try {
        sha512_HMAC = Mac.getInstance("HmacSHA512");
        System.out.println("key : " + new String(key));
        SecretKeySpec secretkey = new SecretKeySpec(key, "HmacSHA512");
        sha512_HMAC.init(secretkey);
  
        byte[] mac_data = sha512_HMAC.doFinal(data.getBytes());
        System.out.println("hex : " + bin2hex(mac_data));
        result = Base64.encode(mac_data);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return result;
    }

    public static byte[] hmacSha512(String value, String key) {
    try {
        SecretKeySpec keySpec = new SecretKeySpec(
          key.getBytes(DEFAULT_ENCODING), HMAC_SHA512);
        Mac mac = Mac.getInstance(HMAC_SHA512);
        mac.init(keySpec);
        return mac.doFinal(value.getBytes(DEFAULT_ENCODING));
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
    } catch (InvalidKeyException e) {
        throw new RuntimeException(e);
    } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
    }
    }

    public static String asHex(byte[] bytes) {
      return new String(Base64.encode(bytes));
    }

    public static String bin2hex(byte[] data) {
      return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }

    public static String mapToQueryString(Map<String, String> map) {
    StringBuilder string = new StringBuilder();
  
    if (map.size() > 0) {
        string.append("?");
    }
  
    for (Entry<String, String> entry : map.entrySet()) {
        string.append(entry.getKey());
        string.append("=");
        string.append(entry.getValue());
        string.append("&");
    }
  
    return string.toString();
    }
}

