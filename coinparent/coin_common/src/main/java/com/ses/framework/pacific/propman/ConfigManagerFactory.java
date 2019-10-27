/*
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

package com.ses.framework.pacific.propman;

import com.ses.framework.pacific.logger.Logger;
import com.ses.framework.pacific.propman.Config.FileType;

public class ConfigManagerFactory {
	private static final String TAG = ConfigManagerFactory.class.getSimpleName();
	
	// Users can make a ConfigManager instance using by this method().
	public static ConfigManager getConfigManager(
			FileType configType, String targetFilePath) {
		
		ParametersOfConfigManager parameters 
			= getParameters_(configType, targetFilePath);
		if (parameters.getGetter() != null 
			&& parameters.getSetter() != null 
			&& parameters.getMonitoring() != null) {						
			return new ConfigManager(
					parameters.getGetter(), 
					parameters.getSetter(), 
					parameters.getMonitoring(),
					configType,
					targetFilePath);			
		} else {
			Logger.error(TAG,"ConfigManagerFactory was met the failure of creating getter/setter.");
			return null;
		}		
	}
	
	private static ParametersOfConfigManager getParameters_(FileType configType, String targetFilePath) {
		switch (configType) {
		case PROPERTIES_TYPE :
			return newParametersOfConfigManagerForProperties_(targetFilePath);
			
		case PREFERENCE_TYPE :
			return newParametersOfConfigManagerForPreference_(targetFilePath);
		
		case XML_TYPE :
			return newParametersOfConfigManagerForXml_(targetFilePath);
		
		default :
			Logger.error(TAG,"There was abnormal config type. Please check the config type.");
			return null;
		}
	}	
	
	private static ParametersOfConfigManager newParametersOfConfigManagerForProperties_(String targetFilePath) {		
		return new ParametersOfConfigManager(
				new PropertiesGetter(),
				new PropertiesSetter(),
				new MonitorUsingWatch(targetFilePath, Config.FileType.PROPERTIES_TYPE));
	}
	
	private static ParametersOfConfigManager newParametersOfConfigManagerForPreference_(String targetFilePath) {		
		return new ParametersOfConfigManager(
				new PreferenceGetter(),
				new PreferenceSetter(),
				new MonitorUsingWatch(targetFilePath, Config.FileType.PREFERENCE_TYPE));
	}
	
	private static ParametersOfConfigManager newParametersOfConfigManagerForXml_(String targetFilePath) {			
		return new ParametersOfConfigManager(
				new XmlGetter(),
				new XmlSetter(),
				new MonitorUsingWatch(targetFilePath, Config.FileType.XML_TYPE));
	}	
}

