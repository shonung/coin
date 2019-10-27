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

package com.ses.framework.pacific.common.thread;

import com.ses.framework.pacific.common.Utils;
import com.ses.framework.pacific.logger.Logger;

public class DefaultManagedRunnable implements Runnable {
  private static final String TAG = DefaultManagedRunnable.class.getSimpleName();

  private ManagedThreadFactory.ManagedThread mOwner_ = null;

  public final void setManagedThread(ManagedThreadFactory.ManagedThread owner) {
    mOwner_ = owner;
  }

  @Override
  public final void run() {
//    Logger.info(TAG, "DefaultManagedRunnable.run() id=" + mOwner_.getId());
    try {
      doRun();
      postRun();
    } catch (InterruptedException e) {
      Logger.debug(TAG, "interrupt");
    } catch (Exception e) {
      Logger.error(TAG, Utils.getStringFromException(e));
    }
  }

  public void doRun() throws InterruptedException {
    // nothing to do
  }

//  @CallSuper
  public void postRun() throws InterruptedException {
//    Logger.info(TAG, "DefaultManagedRunnable.postRun() id=" + mOwner_.getId());
    if (mOwner_ != null) {
      mOwner_.terminate();
    }
  }

  public final boolean isNotFinished() {
    return mOwner_.isAlive() || mOwner_.isDaemon();
  }

  public final boolean isInterrupted() {
    return mOwner_.isInterrupted();
  }
}
