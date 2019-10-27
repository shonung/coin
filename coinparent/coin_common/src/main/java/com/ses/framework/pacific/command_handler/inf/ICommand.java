/**
 * Copyright (c) 2018 Eungsuk Shon <shonung83@gmail.com>
 */

package com.ses.framework.pacific.command_handler.inf;

public interface ICommand {
  String getType();
  String getId();
  void setId(String id);
}
