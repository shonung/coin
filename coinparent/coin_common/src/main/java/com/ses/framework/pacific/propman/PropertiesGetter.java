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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.ses.framework.pacific.logger.Logger;
import com.ses.framework.pacific.propman.inf.IGetter;

public class PropertiesGetter implements IGetter {
	private static final String TAG = PropertiesGetter.class.getSimpleName();

	private Properties properties_;
	private ThreadPoolExecutor threadPoolExecutor_;
	
	public enum CallableType {
		BY_PREFIX("by_prefix"),
		BY_FILEPATH("by_filepath");
		private String typeStr;
		CallableType(String typeStr) {
			this.typeStr = typeStr;
		}
		public String getTypeStr() {
			return this.typeStr;
		}
	}

	public PropertiesGetter() {
		initialize_();	
	}
	
	private class GettingCallable implements Callable<String> {
		private String key_;
		private String defaultValue_;
		private String targetFilePath_;

		public GettingCallable(String targetFilePath, String key, String defaultValue) {
			if (key != null) {
				this.key_ = key;
				this.defaultValue_ = defaultValue;
				this.targetFilePath_ = targetFilePath;
			} else {
				Logger.error(TAG,"GettingCallable Constructor's key parameter are null. Please check parameters.");
			}
		}

		public String call() throws Exception {
			if (targetFilePath_ != null) {	
				try {
					// Can I consider FileInputStream or BufferedFileInputStream
					// or FileReader for performance?
					FileInputStream fis = new FileInputStream(targetFilePath_);
					if (fis != null) {
						properties_.load(fis);
						String value = properties_.getProperty(key_, defaultValue_);						
						fis.close();
						properties_.clear();
						return value;
					} else {
						Logger.error(TAG,"FileInputStream is null.");
						return null;
					} // why is this area a dead code? Can't FileInputStream
					  // return null?
				} catch (FileNotFoundException e) {
					Logger.error(TAG,"FileNotFoundException was occurred. Please check whether you use a valid file path. The file path is "+targetFilePath_);					
					return null;
				} catch (IOException e) {
					Logger.error(TAG,"IOException was occurred. Please retry get method().");
					return null;
				}
			} else {
				Logger.error(TAG,"This getter has not initialized. It must be initialized.");
				return null;
			}
		}
	}
	
	private class GetPropKeysCallable implements Callable<List<String>> {
		private String prefix = null;
		private String filePath = null;
		private CallableType type = null;
		private String targetFilePath_ = null;
		
		public GetPropKeysCallable(String targetFilePath, String prefix, CallableType type) {
			if (type == CallableType.BY_PREFIX) {
				this.prefix = prefix;
			} 
			
			this.type = type;
			this.targetFilePath_ = targetFilePath;
		}

		public List<String> call() throws Exception {			
			
			try {
				// Can I consider FileInputStream or BufferedFileInputStream
				// or FileReader for performance?
				FileInputStream fis = new FileInputStream(targetFilePath_);
				if (fis != null) {
					properties_.load(fis);
					Enumeration<?> propertyNames = properties_.propertyNames();							
					List<String> value = new ArrayList<String>();						
					if (propertyNames != null) {
					    while (propertyNames.hasMoreElements()) {
					        String key = (String) propertyNames.nextElement();
					        if (type == CallableType.BY_FILEPATH || key.contains(this.prefix)) {
					        	value.add(key);
					        }
					    }
					}
				    
					fis.close();
					properties_.clear();
					return value;
				} else {
					Logger.error(TAG,"FileInputStream is null.");
					return null;
				} // why is this area a dead code? Can't FileInputStream
				  // return null?
			} catch (FileNotFoundException e) {
				Logger.error(TAG,"FileNotFoundException was occurred. Please check whether you use a valid file path. The file path is "+targetFilePath_);					
				return null;
			} catch (IOException e) {
				Logger.error(TAG,"IOException was occurred. Please retry get method().");
				return null;
			}
		}
	}

	private void initialize_() {
		properties_ = new Properties();
		threadPoolExecutor_ = new ThreadPoolExecutor(Constants.DEFAULT_THREAD_POOL_SIZE_FOR_GETTER,
				Constants.DEFAULT_THREAD_POOL_SIZE_FOR_GETTER + 1, // Don't use
																	// //
																	// LinkedBlockingQueue
				Constants.DEFAULT_THREAD_POOL_TIMEOUT_FOR_GETTER, // Setting
																	// timeout
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	@Override
	public String get(String targetFilePath, String key, String defaultValue) {
		if (threadPoolExecutor_ != null) {
			Future<String> result = threadPoolExecutor_.submit(new GettingCallable(targetFilePath, key, defaultValue));
	
			try {
				return result.get();
			} catch (InterruptedException e) {
				Logger.error(TAG,"InterruptedException is occurred while waiting result of get");
				return null;
			} catch (ExecutionException e) {
				Logger.error(TAG,"ExecutionException is occurred while waiting result of get, key is "+key);
				return null;
			}
		} else {
			Logger.error(TAG,"ThreadPoolExcutor is null. Please initialize PropertiesGetter.");
			return null;
		}
	}

	@Override
	public List<String> getPropsByPrefix(String targetFilePath, String prefix) { // must need prefix. we can find file path through this prefix.	
		if (prefix == null) {
			Logger.error(TAG,"prefix is null. Please check argument.");
			return null;
		}
		
		if (threadPoolExecutor_ != null) {
			Future<List<String>> result = threadPoolExecutor_.submit(new GetPropKeysCallable(targetFilePath, prefix, CallableType.BY_PREFIX));
	
			try {
				return result.get();
			} catch (InterruptedException e) {
				Logger.error(TAG,"InterruptedException is occurred while waiting result of get");
				return null;
			} catch (ExecutionException e) {
				Logger.error(TAG,"ExecutionException is occurred while waiting result of get, key is "+prefix);
				return null;
			}
		} else {
			Logger.error(TAG,"ThreadPoolExcutor is null. Please initialize PropertiesGetter.");
			return null;
		}
	}
	
	@Override
	public List<String> getKeys(String filePath) {
		if (filePath == null) {
			Logger.error(TAG,"prefix is null. Please check argument.");
			return null;
		}
		
		if (threadPoolExecutor_ != null) {
			Future<List<String>> result = threadPoolExecutor_.submit(new GetPropKeysCallable(filePath, null, CallableType.BY_FILEPATH));
	
			try {
				return result.get();
			} catch (InterruptedException e) {
				Logger.error(TAG,"InterruptedException is occurred while waiting result of get");
				return null;
			} catch (ExecutionException e) {
				Logger.error(TAG,"ExecutionException is occurred while waiting result of get, key is "+filePath);
				return null;
			}
		} else {
			Logger.error(TAG,"ThreadPoolExcutor is null. Please initialize PropertiesGetter.");
			return null;
		}
	}
}

