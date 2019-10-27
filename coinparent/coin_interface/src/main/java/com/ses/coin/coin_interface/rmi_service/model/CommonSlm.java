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

package com.ses.coin.coin_interface.rmi_service.model;

import java.io.Serializable;

public class CommonSlm implements Serializable {
  private String slmId;

  private String slmProtocol;

  private String slmIp;

  private Integer slmPort;

  private Integer slmUseAuth;

  private String slmUserId;

  private String slmUserPw;

  private String cdate;

  public CommonSlm(String slmId, String slmProtocol, String slmIp, Integer slmPort, Integer slmUseAuth,
      String slmUserId, String slmUserPw, String cdate) {
    this.slmId = slmId;
    this.slmProtocol= slmProtocol;
    this.slmIp = slmIp;
    this.slmPort = slmPort;
    this.slmUseAuth = slmUseAuth;
    this.slmUserId = slmUserId;
    this.slmUserPw = slmUserPw;
    this.cdate = cdate;
  }

  public String getSlmId() {
    return slmId;
  }

  public void setSlmId(String slmId) {
    this.slmId = slmId;
  }

  public String getProtocol() {
    return slmProtocol;
  }

  public void setProtocol(String slmProtocol) {
    this.slmProtocol = slmProtocol;
  }

  public String getIp() {
    return slmIp;
  }

  public void setIp(String slmIp) {
    this.slmIp = slmIp;
  }

  public Integer getPort() {
    return slmPort;
  }

  public void setPort(Integer slmPort) {
    this.slmPort = slmPort;
  }

  public Integer getUseAuth() {
    return slmUseAuth;
  }

  public void setUseAuth(Integer slmUseAuth) {
    this.slmUseAuth = slmUseAuth;
  }

  public String getUserId() {
    return slmUserId;
  }

  public void setUserId(String slmUserId) {
    this.slmUserId = slmUserId;
  }

  public String getUserPw() {
    return slmUserPw;
  }

  public void setUserPw(String slmUserPw) {
    this.slmUserPw = slmUserPw;
  }

  public String getCdate() {
    return cdate;
  }

  public void setCdate(String cdate) {
    this.cdate = cdate;
  }
}
