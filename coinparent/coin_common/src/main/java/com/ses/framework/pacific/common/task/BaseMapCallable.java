/**
 * Copyright (C) 2016 LG Electronics Inc. All Rights Reserved.
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
 * Author: zack
 */

package com.ses.framework.pacific.common.task;

import java.util.Map;
import java.util.concurrent.Callable;

public abstract class BaseMapCallable<K, V> extends BaseCallable<Map<K, V>> implements Callable<Map<K, V>> {
  public BaseMapCallable() {
    setTaskCompleteListener(getListener());
  }

  public abstract ITaskCompleteListener<Map<K, V>> getListener();

  @Override
  public abstract Map<K, V> call() throws Exception;
}