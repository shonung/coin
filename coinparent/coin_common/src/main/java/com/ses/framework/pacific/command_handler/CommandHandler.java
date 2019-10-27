/**
 * Copyright (c) 2018 Eungsuk Shon <shonung83@gmail.com>
 */

package com.ses.framework.pacific.command_handler;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.ses.framework.pacific.command_handler.inf.ICommand;
import com.ses.framework.pacific.command_handler.inf.ICommandRunnable;
import com.ses.framework.pacific.logger.Logger;

public class CommandHandler {
  private static final String TAG = CommandHandler.class.getSimpleName();
  private static final int DEFAULT_CORE_THREAD_POOL_SIZE = 8;
  private static final int DEFAULT_MAX_THREAD_POOL_SIZE = Integer.MAX_VALUE;
  private static final int DEFAULT_THREAD_POOL_TIMEOUT = 30 * 1000;
  
  private Map<String, ICommandRunnable> commandMap = null;
  private Map<String/*type + _ + id*/, Map.Entry<Future<?>,ICommandRunnable>> threadMap = null;
  private ThreadPoolExecutor executor;
  
  CommandHandler() {
    initialize();
  }
  
  private void initialize() {
    commandMap = new HashMap<>();
    
    threadMap = new HashMap<>();
    
    executor = new ThreadPoolExecutor(DEFAULT_CORE_THREAD_POOL_SIZE, DEFAULT_MAX_THREAD_POOL_SIZE,
        DEFAULT_THREAD_POOL_TIMEOUT, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
  }
  
  protected void finalize() {
    if (commandMap != null) {
      commandMap.clear();
      commandMap = null;
    }
    
    if (executor != null) {
      executor.shutdownNow();   
      executor = null;
    }
  }
  
  public void clear() {
    if (commandMap != null) {
      commandMap.clear();
    }
    
    if (executor != null) {
      executor.shutdownNow();
      try {
        executor.awaitTermination(DEFAULT_THREAD_POOL_TIMEOUT, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
      }
      executor = null;
      executor = new ThreadPoolExecutor(DEFAULT_CORE_THREAD_POOL_SIZE, DEFAULT_MAX_THREAD_POOL_SIZE,
          DEFAULT_THREAD_POOL_TIMEOUT, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }
  }
  
  public void setCorePoolSize(int size) {
    if (executor != null) {
      executor.setCorePoolSize(size+1);
      
      Logger.debug(TAG, "The core pool size in thread pool executor is "+executor.getCorePoolSize());
    }
  }
  
  public void setMaximumPoolSize(int size) {
    if (executor != null) {
      executor.setMaximumPoolSize(size);
    }
  } 
  
  public void registerCommandAndRunnable(String type, ICommandRunnable runnable) {
    if (commandMap != null) {
      commandMap.put(type, runnable);
    } else {
      Logger.error(TAG, "CommandHandler was not initialized.. Please retry to initialize.");
    }
  }
  
  public void releaseCommandAndRunnable(String type) {
    if (type != null && commandMap != null) {
      commandMap.remove(type);
    }
  }
  
  public void runCommandRunnable(ICommand command) {
    if (command != null && commandMap != null && executor != null) {
      ICommandRunnable runnable = commandMap.get(command.getType());
      if (runnable != null) {
        ICommandRunnable myRunnable = runnable.makeCommandRunnableInstance();
        if (myRunnable != null) {
          myRunnable.setCommand(command);
//          executor.execute(myRunnable);
          Future<?> future = executor.submit(myRunnable);
          if (threadMap != null) {
            Map.Entry<Future<?>, ICommandRunnable> entry = new AbstractMap.SimpleEntry<>(future, myRunnable);
            threadMap.put(command.getType() + "_" + command.getId() , entry);
          }  
        }
      }
    }
  }
  
  public void stopAndRemoveCommandRunnable(String type, String id) {
    if (type != null && id != null) {
      String typeId = type + "_" + id;
      if (threadMap != null) {
        Map.Entry<Future<?>, ICommandRunnable> entry = threadMap.remove(typeId);
        if (entry != null) {
          Future<?> future = entry.getKey();
          ICommandRunnable runnable = entry.getValue();
          if (!future.isDone() && !future.isCancelled()) {
            runnable.stop();
            future.cancel(true);
          }        
        }
      }
    }
  }
  
  public void removeCommandRunnable(String type, String id) {
    if (type != null && id != null) {
      String typeId = type + "_" + id;
      if (threadMap != null) {
        threadMap.remove(typeId);
      }
    }
  }
  
}
