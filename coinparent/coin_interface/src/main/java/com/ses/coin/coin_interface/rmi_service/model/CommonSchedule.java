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

public class CommonSchedule implements Serializable {
    private String scheId;

    private String locationId;

    private String roomId;

    private String scheNm;

    private String scheUserId;

    private String scheUserNm;

    private String scheDeptNm;

    private String scheSdate;

    private String scheEdate;

    private Integer scheLocalYear;

    private Integer scheLocalMonth;

    private Integer scheLocalDay;

    private Integer scheLocalDate;

    private String scheLocalStime;

    private String scheLocalEtime;

    private Integer scheLocalDuration;

    private Integer scheSensorCnt;

    private Integer scheTotalSensor;

    private Integer scheTotalDetect;

    private Integer scheChkDuration;

    private Integer scheResult;

    private String cdate;
    
    public CommonSchedule(
    	    String 	scheId,
    	    String 	locationId,
    	    String 	roomId,
    	    String 	scheNm,
    	    String 	scheUserId,
    	    String 	scheUserNm,
    	    String 	scheDeptNm,
    	    String 	scheSdate,
    	    String 	scheEdate,
    	    Integer scheLocalYear,
    	    Integer scheLocalMonth,
    	    Integer scheLocalDay,
    	    Integer scheLocalDate,
    	    String 	scheLocalStime,
    	    String 	scheLocalEtime,
    	    Integer scheLocalDuration,
    	    Integer scheSensorCnt,
    	    Integer scheTotalSensor,
    	    Integer scheTotalDetect,
    	    Integer scheChkDuration,
    	    Integer scheResult,
    	    String 	cdate
    		) {
        this.scheId				 = scheId;
        this.locationId               = locationId;
        this.roomId              = roomId;
        this.scheNm              = scheNm;
        this.scheUserId          = scheUserId;
        this.scheUserNm          = scheUserNm;
        this.scheDeptNm          = scheDeptNm;
        this.scheSdate           = scheSdate;
        this.scheEdate           = scheEdate;
        this.scheLocalYear       = scheLocalYear;
        this.scheLocalMonth      = scheLocalMonth;
        this.scheLocalDay        = scheLocalDay;
        this.scheLocalDate       = scheLocalDate;
        this.scheLocalStime      = scheLocalStime;
        this.scheLocalEtime      = scheLocalEtime;
        this.scheLocalDuration   = scheLocalDuration;
        this.scheSensorCnt       = scheSensorCnt;
        this.scheTotalSensor     = scheTotalSensor;
        this.scheTotalDetect     = scheTotalDetect;
        this.scheChkDuration     = scheChkDuration;
        this.scheResult          = scheResult;
        this.cdate               = cdate;
    }

    public String getScheduleId() {
        return scheId;
    }

    public void setScheduleId(String scheId) {
        this.scheId = scheId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return scheNm;
    }

    public void setName(String scheNm) {
        this.scheNm = scheNm;
    }

    public String getUserId() {
        return scheUserId;
    }

    public void setUserId(String scheUserId) {
        this.scheUserId = scheUserId;
    }

    public String getUserName() {
        return scheUserNm;
    }

    public void setUserName(String scheUserNm) {
        this.scheUserNm = scheUserNm;
    }

    public String getDeptName() {
        return scheDeptNm;
    }

    public void setDeptName(String scheDeptNm) {
        this.scheDeptNm = scheDeptNm;
    }

    public String getSdate() {
        return scheSdate;
    }

    public void setSdate(String scheSdate) {
        this.scheSdate = scheSdate;
    }

    public String getEdate() {
        return scheEdate;
    }

    public void setEdate(String scheEdate) {
        this.scheEdate = scheEdate;
    }

    public Integer getLocalYear() {
        return scheLocalYear;
    }

    public void setLocalYear(Integer scheLocalYear) {
        this.scheLocalYear = scheLocalYear;
    }

    public Integer getLocalMonth() {
        return scheLocalMonth;
    }

    public void setLocalMonth(Integer scheLocalMonth) {
        this.scheLocalMonth = scheLocalMonth;
    }

    public Integer getLocalDay() {
        return scheLocalDay;
    }

    public void setLocalDay(Integer scheLocalDay) {
        this.scheLocalDay = scheLocalDay;
    }

    public Integer getLocalDayOfWeek() {
        return scheLocalDate;
    }

    public void setLocalDayOfWeek(Integer scheLocalDate) {
        this.scheLocalDate = scheLocalDate;
    }

    public String getLocalShhmm() {
        return scheLocalStime;
    }

    public void setLocalShhmm(String scheLocalStime) {
        this.scheLocalStime = scheLocalStime;
    }

    public String getLocalEhhmm() {
        return scheLocalEtime;
    }

    public void setLocalEhhmm(String scheLocalEtime) {
        this.scheLocalEtime = scheLocalEtime;
    }

    public Integer getLocalDuration() {
        return scheLocalDuration;
    }

    public void setLocalDuration(Integer scheLocalDuration) {
        this.scheLocalDuration = scheLocalDuration;
    }

    public Integer getSensorCnt() {
        return scheSensorCnt;
    }

    public void setSensorCnt(Integer scheSensorCnt) {
        this.scheSensorCnt = scheSensorCnt;
    }

    public Integer getTotalSensor() {
        return scheTotalSensor;
    }

    public void setTotalSensor(Integer scheTotalSensor) {
        this.scheTotalSensor = scheTotalSensor;
    }

    public Integer getTotalDetect() {
        return scheTotalDetect;
    }

    public void setTotalDetect(Integer scheTotalDetect) {
        this.scheTotalDetect = scheTotalDetect;
    }

    public Integer getChkDuration() {
        return scheChkDuration;
    }

    public void setChkDuration(Integer scheChkDuration) {
        this.scheChkDuration = scheChkDuration;
    }

    public Integer getResult() {
        return scheResult;
    }

    public void setResult(Integer scheResult) {
        this.scheResult = scheResult;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }
}
