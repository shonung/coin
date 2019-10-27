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

import java.util.HashMap;

import com.ses.framework.pacific.common.Constants;
import com.ses.framework.pacific.common.Utils;
import com.ses.framework.pacific.logger.Logger;

public class ManagedThreadFactory {
  private static final String TAG = ManagedThreadFactory.class.getSimpleName();

  private static ManagedThreadFactory mInstance_ = null;

  public static synchronized ManagedThreadFactory getInstance() {
    if (mInstance_ == null) {
      mInstance_ = new ManagedThreadFactory();
    }
    return mInstance_;
  }

  private HashMap<Long, ManagedThread> mManagedThreadMap_ = null;

  private ManagedThreadFactory() {
    mManagedThreadMap_ = new HashMap<>();
  }

  public ManagedThread getThread(long id) {
    return mManagedThreadMap_.get(id);
  }

  public synchronized ManagedThread getThread(DefaultManagedRunnable runnable) {
    return getThread(runnable, 0);
  }

  public synchronized ManagedThread getThread(DefaultManagedRunnable runnable, long timeout) {
    ManagedThread ret = new ManagedThread(timeout, runnable);
    runnable.setManagedThread(ret);
    Logger.debug(TAG, "ManagedThreadFactory.getThread() id=" + ret.getId());
    mManagedThreadMap_.put(ret.getId(), ret);
    return ret;
  }

  public long getThreadAndStart(DefaultManagedRunnable runnable) {
    return getThreadAndStart(runnable, 0);
  }

  public long getThreadAndStart(DefaultManagedRunnable runnable, long timeout) {
    long ret = 0;
    ManagedThread thread = getThread(runnable, timeout);
    ret = thread.getId();
    Logger.debug(TAG, "ManagedThreadFactory.getThreadAndStart() id=" + ret);
    mManagedThreadMap_.put(ret, thread);
    thread.start();
    return ret;
  }

  public void terminateManagedThread(long id) {
    terminateManagedThread(mManagedThreadMap_.get(id), false);
  }

  public void terminateManagedThread(long id, boolean withoutInterrupt) {
    terminateManagedThread(mManagedThreadMap_.get(id), withoutInterrupt);
  }

  public void terminateManagedThread(ManagedThread managedThread) {
    terminateManagedThread(managedThread, false);
  }

  public void terminateManagedThread(ManagedThread managedThread, boolean withoutInterrupt) {
    if (managedThread != null) {
//      Logger.info(TAG, "ManagedThreadFactory.terminateManagedTask() id=" + managedThread.getId());
      mManagedThreadMap_.remove(managedThread.getId());
      if (!withoutInterrupt) {
        if (managedThread.isAlive() || managedThread.isDaemon()) {
          managedThread.interruptItself();
          try {
            managedThread.join(Constants.THREAD_JOIN_TIMEOUT);
          } catch (InterruptedException e) {
            // do nothing
          } finally {
            managedThread = null;
          }
        }
      }
    }
//    Logger.info(TAG, "ManagedThreadFactory.terminateManagedTask() <<<");
  }

  @SuppressWarnings("unchecked")
  public void terminateAllThread() {
    HashMap<Long, ManagedThread> temporary = null;
    synchronized (mManagedThreadMap_) {
      temporary = (HashMap<Long, ManagedThread>)mManagedThreadMap_.clone();
      mManagedThreadMap_.clear();
    }
    if (temporary != null) {
      for (long i = 0; i < temporary.size(); i++) {
        ManagedThread thread = temporary.get(temporary.get(i));
        if (thread != null) {
          thread.interruptItself();
        }
      }
      temporary.clear();
    }
  }

  public class ManagedThread extends Thread {
    private long mTimeout_ = 0;
    private ManagedThread mTimerThread_ = null;
    private DefaultManagedRunnable mRunnable_ = null;

    private ManagedThread(long timeout, DefaultManagedRunnable runnable) {
      super(runnable);
      mRunnable_ = runnable;
      mTimeout_ = timeout;
    }

    @Override
    public synchronized void start() {
//      Logger.info(TAG, "ManagedThread.start() id=" + getId());
      if (mTimeout_ > 0) {
        mTimerThread_ = getThread(new TimerRunnable(this, mTimeout_));
        mTimerThread_.start();
      }
      super.start();
    }

    @Override
    protected void finalize() throws Throwable {
//      Logger.info(TAG, "ManagedThread.finalize() id=" + getId());
      if (mTimerThread_ != null) {
        mTimerThread_.interruptItself();
        mTimerThread_ = null;
      }
      super.finalize();
    }

    @Override
    public boolean equals(Object obj) {
      boolean ret = false;
      if (obj instanceof ManagedThread) {
        ManagedThread other = (ManagedThread) obj;
        ret = getId() == other.getId();
      }
      return ret;
    }

    private void interruptItself() {
//      Logger.info(TAG, "ManagedThread.interruptItself() id=" + getId());
      try {
        synchronized (this) {
          if (isAlive() || isDaemon()) {
//            Logger.info(TAG, "ManagedThread.interrupt() id=" + getId());
            interrupt();
          }
        }
      } catch (Exception e) {
        // do nothing
      }
      terminate();
    }

    public void terminate() {
//      Logger.info(TAG, "ManagedThread.terminate() id=" + getId());
      terminateManagedThread(this, true);
    }

    public void wakeup() {
//      Logger.info("BaseCommandHandler", "request to wakeup");
      try {
        synchronized (mRunnable_) {
          mRunnable_.notify();
        }
      } catch (Exception e) {
        Logger.error(TAG, Utils.getStringFromException(e));
        // do nothing
      }
    }
  }

  private final class TimerRunnable extends DefaultManagedRunnable {
    private ManagedThread mTargetManagedThread_ = null;
    private long mTimeout_ = 0;

    private TimerRunnable(ManagedThread managedThread, long timeout) {
      mTargetManagedThread_ = managedThread;
      mTimeout_ = timeout;
    }

    @Override
    public void doRun() {
      try {
        synchronized (this) {
          this.wait(mTimeout_);
        }
        mTargetManagedThread_.interruptItself();
      } catch (Exception e) {
        // do nothing
      }
    }
  }
}
