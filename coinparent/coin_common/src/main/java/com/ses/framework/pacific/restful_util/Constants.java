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

public class Constants {
  public static final String CONTEXT = "context";  
  public static final String MESSAGE_ID = "messageId";
  public static final String CHANNEL = "channel";
  public static final String TYPE = "type";
  public static final String NAMESPACE = "namespace";
  public static final String NAME = "name";
  public static final String PAYLOAD = "payload";
  public static final String METHOD = "method";
  public static final String METHOD_TYPE = "methodType";
  public static final String HEADER = "header";
  public static final String QUERY_PARAMS = "queryParams";
  public static final String BODY = "body";
  
  public static final String KEY_HTTP_HEADER_ACCEPT = "Accept";  
  public static final String KEY_HTTP_HEADER_CONTENT_TYPE = "Content-Type";
  public static final String KEY_HTTP_HEADER_CACHE_CONTROL = "Cache-Control";
  public static final String VAL_HTTP_HEADER_ACCEPT_DEFAULT = "application/json";
  public static final String VAL_HTTP_HEADER_CONTENT_TYPE_DEFAULT = "application/json";
  public static final String VAL_HTTP_HEADER_CACHE_CONTROL_DEFAULT = "no-cache";
  
  public static final int HTTP_CONN_TIMEOUT = 30;
  public static final int HTTP_READ_TIMEOUT = 10; 
  
  
}
