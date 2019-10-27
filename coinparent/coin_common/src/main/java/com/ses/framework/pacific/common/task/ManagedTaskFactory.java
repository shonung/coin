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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.ses.framework.pacific.common.Utils;
import com.ses.framework.pacific.common.thread.DefaultManagedRunnable;
import com.ses.framework.pacific.common.thread.ManagedThreadFactory;
import com.ses.framework.pacific.logger.Logger;

public class ManagedTaskFactory {
  private static final String TAG = ManagedTaskFactory.class.getSimpleName();

  private static ManagedTaskFactory mInstance_ = null;

  public static synchronized ManagedTaskFactory getInstance() {
    if (mInstance_ == null) {
      mInstance_ = new ManagedTaskFactory();
    }
    return mInstance_;
  }

  private static final int CORE_THREAD_POOL_SIZE = 10;
  private static final int MAX_THREAD_POOL_SIZE = Integer.MAX_VALUE;

  private ExecutorService mExecuteService_ = null;

  private HashMap<Integer, ManagedTask<?>> mManagedTaskMap_ = null;

  private ManagedTaskFactory() {
    mExecuteService_ = new ThreadPoolExecutor(
        CORE_THREAD_POOL_SIZE, MAX_THREAD_POOL_SIZE,
        0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>());
    mManagedTaskMap_ = new HashMap<>();
  }

  @SuppressWarnings("unchecked")
  public <T> ManagedTask<T> getTask(int id) {
    return (ManagedTask<T>)mManagedTaskMap_.get(id);
  }

  public synchronized <T> int makeTaskAndRun(BaseObjectCallable<T> callable, long timeout) {
    ManagedTask<T> task = new ManagedTask<T>(callable);
    setManagedTask(task, timeout);
    return task.getId();
  }

  public synchronized <T> int makeTaskAndRun(BaseListCallable<T> callable, long timeout) {
    ManagedTask<List<T>> task = new ManagedTask<List<T>>(callable);
    setManagedTask(task, timeout);
    return task.getId();
  }

  public synchronized <K, V> int makeTaskAndRun(BaseMapCallable<K, V> callable, long timeout) {
    ManagedTask<Map<K, V>> task = new ManagedTask<Map<K, V>>(callable);
    setManagedTask(task, timeout);
    return task.getId();
  }

  private void setManagedTask(final ManagedTask<?> managedTask, final long timeout) {
//    Logger.info(TAG, "ManagedTaskFactory.setManagedTask() id=" + managedTask.getId());
    mManagedTaskMap_.put(managedTask.getId(), managedTask);
    ManagedThreadFactory.getInstance().getThreadAndStart(new DefaultManagedRunnable() {
      @Override
      public void doRun() {
//        Logger.info(TAG, "ManagedTask.doRun()");
        mExecuteService_.execute(managedTask);
        try {
          managedTask.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
          Logger.debug(TAG, "timeout");
        } catch (InterruptedException | ExecutionException e) {
          Logger.error(TAG, Utils.getStringFromException(e));
        }
//        Logger.info(TAG, "ManagedTask.doRun() isNotFinished=" + isNotFinished());
        if (isNotFinished()) {
          // cancelled
          managedTask.cancel(true);
        }
      }
    });
  }

  public void terminateManagedTask(int id) {
    terminateManagedTask(mManagedTaskMap_.get(id), false);
  }

  public void terminateManagedTask(int id, boolean withoutInterrupt) {
    terminateManagedTask(mManagedTaskMap_.get(id), withoutInterrupt);
  }

  public void terminateManagedTask(ManagedTask<?> managedTask) {
    terminateManagedTask(managedTask, false);
  }

  public void terminateManagedTask(ManagedTask<?> managedTask, boolean withoutInterrupt) {
    if (managedTask != null) {
//      Logger.info(TAG, "ManagedTaskFactory.terminateManagedTask() id=" + managedTask.getId());
      mManagedTaskMap_.remove(managedTask.getId());
      if (!withoutInterrupt) {
        if (managedTask.isDone()) {
          managedTask.cancel(true);
        }
      }
    }
//    Logger.info(TAG, "ManagedTaskFactory.terminateManagedTask() <<<");
  }

  @SuppressWarnings("unchecked")
  public void terminateAllTask() {
    HashMap<Integer, ManagedTask<?>> temporary = null;
    synchronized (mManagedTaskMap_) {
      temporary = (HashMap<Integer, ManagedTask<?>>)mManagedTaskMap_.clone();
      mManagedTaskMap_.clear();
    }
    if (temporary != null) {
      for (int i = 0; i < temporary.size(); i++) {
        ManagedTask<?> task = temporary.get(temporary.get(i));
        if (task != null) {
          if (task.isDone()) {
            task.cancel(true);
          }
        }
      }
      temporary.clear();
    }
  }

  public class ManagedTask<RESULT> extends FutureTask<RESULT> {
    private int mId_ = 0;
    private ManagedTask<RESULT> mTimerThread_ = null;
    private ITaskCompleteListener mTaskCompleteListener_ = null;
    private BaseCallable mCallable_ = null;

    private ManagedTask(BaseObjectCallable callable) {
      super(callable);
      init(callable);
    }

    private ManagedTask(BaseListCallable callable) {
      super(callable);
      init(callable);
    }

    private ManagedTask(BaseMapCallable callable) {
      super(callable);
      init(callable);
    }

    private void init(BaseCallable baseCallable) {
      mCallable_ = baseCallable;
      this.mId_ = System.identityHashCode(this);
      mTaskCompleteListener_ = baseCallable.getTaskCompleteListener();
    }

    public int getId() {
      return mId_;
    }

    @Override
    public synchronized void run() {
//      Logger.info(TAG, "ManagedTask.run() id=" + getId());
      super.run();
    }

    @Override
    protected void done() {
//      Logger.info(TAG, "ManagedTask.done() id=" + getId());
      if (mTaskCompleteListener_ != null) {
        if (isCancelled()) {
          mTaskCompleteListener_.taskCancelled();
        } else {
          try {
            mTaskCompleteListener_.taskFinished(get());
          } catch (InterruptedException | ExecutionException e) {
            Logger.error(TAG, Utils.getStringFromException(e));
          }
        }
      }
      terminate();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
//      Logger.info(TAG, "ManagedTask.cancel() id=" + getId());
      return super.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean equals(Object obj) {
      boolean ret = false;
      if (obj instanceof ManagedTask) {
        ManagedTask<?> other = (ManagedTask<?>) obj;
        ret = getId() == other.getId();
      }
      return ret;
    }

    public void terminate() {
//      Logger.info(TAG, "ManagedThread.terminate() id=" + getId());
      terminateManagedTask(this, true);
    }

    public void wakeup() {
      try {
        synchronized (mCallable_) {
          mCallable_.notify();
        }
      } catch (Exception e) {
        // do nothing
      }
    }
  }

}
