/**
 * Copyright (c) 2018 Eungsuk Shon <shonung83@gmail.com>
 */

package com.ses.framework.pacific.command_handler.inf;

public interface ICommandRunnable extends Runnable {
  void setCommand(ICommand command);
  ICommandRunnable makeCommandRunnableInstance();
  void stop();
}
