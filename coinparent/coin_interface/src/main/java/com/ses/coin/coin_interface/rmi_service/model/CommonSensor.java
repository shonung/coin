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

public class CommonSensor implements Serializable {
  private String sensorId;

  private String slmId;

  private String sensorNm;

  private String cdate;
  
  public CommonSensor(String sensorId, String slmId, String sensorNm, String cdate) {
    this.sensorId = sensorId;
    this.slmId = slmId;
    this.sensorNm = sensorNm;
    this.cdate = cdate;
  }

  public String getSensorId() {
      return sensorId;
  }

  public void setSensorId(String sensorId) {
      this.sensorId = sensorId;
  }

  public String getSlmId() {
      return slmId;
  }

  public void setSlmId(String slmId) {
      this.slmId = slmId;
  }

  public String getName() {
      return sensorNm;
  }

  public void setName(String sensorNm) {
      this.sensorNm = sensorNm;
  }

  public String getCdate() {
      return cdate;
  }

  public void setCdate(String cdate) {
      this.cdate = cdate;
  }
}
