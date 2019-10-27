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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import javax.activation.UnsupportedDataTypeException;

import com.ses.framework.pacific.logger.Logger;

public class CopyUtils {
  private static final String TAG = CopyUtils.class.getSimpleName();
  
  @SuppressWarnings("unchecked")
  public static <T> T deepCopy(T src){
    T dest = null;
    try {
      dest = (T)src.getClass().newInstance();
      deepCopyFrom(src, dest);
    } catch (Exception e) {
      if(e.getCause() != null) Logger.error(TAG, "deepCopy() failed : " + e.getCause().getMessage());
      else Logger.error(TAG, "deepCopy() failed");
    }
    return dest;
  }
  
  public static <T> T deepCopyFrom(T src, T dest){
    Field[] fields = src.getClass().getDeclaredFields();
    Method[] destMethods = dest.getClass().getDeclaredMethods();
    Method setter = null;

    for ( Field field : fields ) {
      field.setAccessible(true);
      for(Method method : destMethods){
        if(method.getName().equalsIgnoreCase("set" + field.getName())){
          setter = method;
          break;
        }
      }

      try {
        fieldCopy(src, dest, field, setter);
      } catch (Exception e) {
        if(e.getCause() != null) Logger.error(TAG, "deepCopyFrom() failed : " + e.getCause().getMessage());
        else Logger.error(TAG, "deepCopyFrom() failed");
      }
    }
    return dest;
  }
    
  private static void fieldCopy(Object src, Object dest, Field field, Method setter) throws UnsupportedDataTypeException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
    Object item = field.get(src);
    
    if(item == null) return;
    
    if(field.getType().isPrimitive()){
      setter.invoke(dest, item);
    }
    else if(item instanceof Number || item instanceof String || item instanceof Boolean || item instanceof BigDecimal){ // immutable
      setter.invoke(dest, item);
    }
    else if(field.getType().isArray()){
      Object array = Array.newInstance(field.getType().getComponentType(), Array.getLength(item));
      for(int i = 0; i < Array.getLength(item); i++){
        Array.set(array, i, Array.get(item, i));
      }
      
      setter.invoke(dest, array);
    }
    else throw new UnsupportedDataTypeException();
  }
}
