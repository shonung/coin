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

package com.ses.coin.coin_core.rmi_service;

import java.util.List;

import com.ses.coin.coin_interface.rmi_service.inf.ConfInfoService;
import com.ses.coin.coin_interface.rmi_service.model.CommonLocation;
import com.ses.coin.coin_interface.rmi_service.model.CommonRoom;
import com.ses.coin.coin_interface.rmi_service.model.CommonSchedule;
import com.ses.coin.coin_interface.rmi_service.model.CommonSensor;
import com.ses.coin.coin_interface.rmi_service.model.CommonSlm;

public class ConfInfoServiceImpl implements ConfInfoService {
  private static final String TAG = ConfInfoServiceImpl.class.getName();

  /* (non-Javadoc)
   * @see com.ses.coin.coin_interface.rmi_service.inf.ConfInfoService#getRoomList()
   */
  public List<CommonRoom> getRoomList() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see com.ses.coin.coin_interface.rmi_service.inf.ConfInfoService#getRoom(java.lang.String, java.lang.String)
   */
  public CommonRoom getRoom(String locationId, String roomId) {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see com.ses.coin.coin_interface.rmi_service.inf.ConfInfoService#getLocationList(java.util.List)
   */
  public List<CommonLocation> getLocationList(List<CommonRoom> roomList) {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see com.ses.coin.coin_interface.rmi_service.inf.ConfInfoService#getSlmList()
   */
  public List<CommonSlm> getSlmList() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see com.ses.coin.coin_interface.rmi_service.inf.ConfInfoService#getSlm(java.lang.String)
   */
  public CommonSlm getSlm(String slmId) {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see com.ses.coin.coin_interface.rmi_service.inf.ConfInfoService#getSensorList(java.lang.String, java.lang.String)
   */
  public List<CommonSensor> getSensorList(String locationId, String roomId) {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see com.ses.coin.coin_interface.rmi_service.inf.ConfInfoService#getSchedule(java.lang.String)
   */
  public CommonSchedule getSchedule(String scheduleId) {
    // TODO Auto-generated method stub
    return null;
  }    
  
  
}
