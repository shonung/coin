/**
 * Copyright (c) 2018 Eungsuk Shon <shonung83@gmail.com>
 */

package com.ses.framework.pacific.command_handler;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Future;

import com.ses.framework.pacific.command_handler.inf.ICommand;
import com.ses.framework.pacific.command_handler.inf.ICommandRunnable;
import com.ses.framework.pacific.common.thread.DefaultManagedRunnable;
import com.ses.framework.pacific.common.thread.ManagedThreadFactory;
import com.ses.framework.pacific.logger.Logger;

public class CommandQueue {
  private static final String TAG = CommandQueue.class.getSimpleName();
  private static final long SLEEP_DURATION = 10 * 1000;

  private static final CommandQueue instance = new CommandQueue();

  private ArrayList<ICommand> commandQueue = null;
  private CommandHandler commandHandler = null;
  private boolean isTerminated = true;
  private ManagedThreadFactory.ManagedThread pickUpThread = null;

  public static CommandQueue getInstance() {
    return instance;
  }

  CommandQueue() {  
    initialize();
  }

  private void initialize() {
    commandQueue = new ArrayList<>();
    commandHandler = new CommandHandler();
  }
  
  public CommandQueue setCorePoolSize(int size) {
    if (commandHandler != null) {
      commandHandler.setCorePoolSize(size);
    }
    
    return this;
  }
  
  public CommandQueue setMaximumPoolSize(int size) {
    if (commandHandler != null) {
      commandHandler.setMaximumPoolSize(size);
    }
    
    return this;
  }  
  
  public CommandQueue registerCommandAndRunnable(String type, ICommandRunnable runnable) {
    if (commandHandler != null) {
      commandHandler.registerCommandAndRunnable(type, runnable);
    } else {
      Logger.error(TAG, "The CommandQueue is not initialized. Please try to initialize.");
    }
    
    return this;
  }
  
  public void releaseCommandAndRunnable(String type) {
    if (commandHandler != null) {
      commandHandler.releaseCommandAndRunnable(type);
    } else {
      Logger.error(TAG, "The CommandQueue is not initialized. Please try to initialize.");
    }
  }

  public void start() {
    
    if (commandHandler == null) {
      commandHandler = new CommandHandler();
    }
    
    pickUpThread = ManagedThreadFactory.getInstance().getThread(new DefaultManagedRunnable() {
      @Override
      public void doRun() {
        isTerminated = false;
        while (!isTerminated) {
          if (commandQueue != null) {
            ICommand command = poll();
            if (command != null) {
              if (commandHandler != null) {             
                commandHandler.runCommandRunnable(command); 
              }
            } else {
              synchronized (this) {
                try {
                  this.wait(SLEEP_DURATION);
                } catch (InterruptedException e) {
                  Logger.error(TAG, "InterruptedException is occurred while waiting" + e);
                }
              }
            }
          }
        }
      }
    });

    if (pickUpThread != null) {
      pickUpThread.start();
    }
  }

  public void stop() {
    isTerminated = true;
    
    if (pickUpThread != null) {
      pickUpThread.terminate();
    }

    if (commandQueue != null) {
      commandQueue.clear();
    }
    
    if (commandHandler != null) {
      commandHandler.clear();
    }
  }

  public synchronized void injectCommand(ICommand command) {
    if (commandQueue != null) {
      commandQueue.add(command);
      if (pickUpThread != null) {
        pickUpThread.wakeup();
      } else {
        Logger.error(TAG, "pick up thread is null");
      }
    } else {
      Logger.error(TAG, "command queue array list is null");
    }
  } 
  
  public synchronized void stopAndRemoveCommandRunnable(String type, String id) {
    if (commandHandler != null) {
      commandHandler.stopAndRemoveCommandRunnable(type, id);
    }
  }
  
  public synchronized void removeCommandRunnable(String type, String id) {
    if (commandHandler != null) {
      commandHandler.removeCommandRunnable(type, id);
    }
  }
  

  private synchronized ICommand poll() {
    return commandQueue.size() > 0 ? commandQueue.remove(0) : null;
  }

}
