/**
 * Copyright (c) 2018 Eungsuk Shon <shonung83@gmail.com>
 */

package com.ses.framework.pacific.common;

import static java.lang.Runtime.getRuntime;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.ses.framework.pacific.logger.Logger;

public class Utils {
	private static final String TAG = Utils.class.getSimpleName();
	
	public static final String DEFAULT_DATE_FORMAT = "yyyyMMddHHmm";
	
	public static String getFilePath(String directoryPath, String fileName) {    
    return (new File(directoryPath, fileName)).getAbsolutePath();
  }

	public static String[] convertToStringArray(String... array) {
		return array;
	}

	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	public static int count(String s, char c) {
		return s.length() == 0 ? 0 : (s.charAt(0) == c ? 1 : 0) + count(s.substring(1), c);
	}

	public static long getStartUpTimeMillis(long uptimeMillis) {
		return System.currentTimeMillis() - uptimeMillis;
	}

	public static long getUptimeMillisWithStartUpTimeMillis(long startupTimeMillis) {
		return System.currentTimeMillis() - startupTimeMillis;
	}

	public static String getStringFromException(Exception e) {
		return getStringFromThrowable(e);
	}

	public static String getStringFromThrowable(Throwable t) {
		String ret = "";
		if (t != null) {
			ret += t.toString();
			ret += getStackTrace(t);
		}
		return ret;
	}

	public static String getStackTrace(Throwable t) {
		String ret = "";
		if (t != null) {
			StackTraceElement[] traceElements = t.getStackTrace();
			if (traceElements != null) {
				for (StackTraceElement traceElement : traceElements) {
					ret += "\n\t" + traceElement.toString();
					// if (!App.getApp().isDebug() &&
					// traceElement.getClassName().contains(PACKAGE_NAME_PREMAN_BASE)) {
					// break;
					// }
				}
				ret += "\n";
			}
		}
		return ret;
	}

	// public static boolean isAppInstalled(Context context, String packageName) {
	// PackageManager pm = context.getPackageManager();
	// boolean app_installed;
	// try {
	// pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
	// app_installed = true;
	// } catch (PackageManager.NameNotFoundException e) {
	// app_installed = false;
	// }
	// return app_installed;
	// }
	//
	// public static int getAppPid(Context context, String packageName) {
	// ActivityManager am = (ActivityManager)
	// context.getSystemService(Context.ACTIVITY_SERVICE);
	// PackageManager pm = context.getPackageManager();
	// if (am != null) {
	// List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos =
	// am.getRunningAppProcesses();
	// if (runningAppProcessInfos != null && runningAppProcessInfos.size() > 0) {
	// for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo :
	// runningAppProcessInfos) {
	// try {
	// CharSequence name =
	// pm.getApplicationLabel(pm.getApplicationInfo(runningAppProcessInfo.processName,
	// PackageManager.GET_META_DATA));
	// if (packageName.equals(name)) {
	// return runningAppProcessInfo.pid;
	// }
	// } catch (PackageManager.NameNotFoundException e) {
	// // not found.
	// }
	// }
	// }
	// }
	// return 0;
	// }
	//
	public static String encodeValueWithSha512(String value) {
		String ret = null;
		if (value != null && value.trim().length() > 0) {
			try {
				MessageDigest digester = MessageDigest.getInstance("SHA-512");
				digester.update(value.getBytes());
				byte[] digest = digester.digest();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < digest.length; i++) {
					sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
				}
				ret = sb.toString();
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}

		}
		return ret;
	}

	// public static void write(Parcel dest, String string) {
	// dest.writeByte((byte) (string == null ? 0 : 1));
	// if (string != null) {
	// dest.writeString(string);
	// }
	// }
	//
	// public static String readString(Parcel source) {
	// if (source.readByte() == 1) {
	// return source.readString();
	// }
	// return null;
	// }
	//
	// public static void write(Parcel dest, boolean bool) {
	// dest.writeByte((byte) (bool ? 1 : 0));
	// }
	//
	// public static boolean readBoolean(Parcel source) {
	// return source.readByte() == 1;
	// }

	public static String getEmptyStringIfNull(String value) {
		return (value != null ? value : "");
	}

	// public static int getCurrentNetworkStatus(Context context) {
	// int ret = Constants.NETWORK_TYPE_NONE;
	//
	// if (context != null) {
	// ConnectivityManager manager =
	// (ConnectivityManager)
	// context.getSystemService(Context.CONNECTIVITY_SERVICE);
	// if (manager != null) {
	// NetworkInfo networkInfo =
	// manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	// if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
	// return Constants.NETWORK_TYPE_MOBILE;
	// }
	// networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	// if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
	// return Constants.NETWORK_TYPE_WIFI;
	// }
	// networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
	// if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
	// return Constants.NETWORK_TYPE_ETHERNET;
	// }
	// }
	// }
	//
	// return ret;
	// }
	//
	// public static boolean isNetworkAvailable(Context context) {
	// boolean ret = false;
	// int currentNetworkStatus = getCurrentNetworkStatus(context);
	// ret = (currentNetworkStatus == Constants.NETWORK_TYPE_MOBILE ||
	// currentNetworkStatus == Constants.NETWORK_TYPE_WIFI ||
	// currentNetworkStatus == Constants.NETWORK_TYPE_ETHERNET);
	// return ret;
	// }
	//
	// public static int getProcessId(Context context, String packageName) {
	// int processId = 0;
	// if (context != null && packageName != null) {
	// ActivityManager am = (ActivityManager)
	// context.getSystemService(Context.ACTIVITY_SERVICE);
	// List<ActivityManager.RunningAppProcessInfo> processInfoList =
	// am.getRunningAppProcesses();
	// if (processInfoList != null) {
	// for (ActivityManager.RunningAppProcessInfo processInfo : processInfoList) {
	// if (processInfo.processName.equalsIgnoreCase(packageName)) {
	// processId = processInfo.pid;
	// break;
	// }
	// }
	// }
	// }
	// return processId;
	// }
	//
	// public static float getMemoryUsagePercent(Context context, int pId) {
	// ActivityManager am = (ActivityManager)
	// context.getSystemService(Context.ACTIVITY_SERVICE);
	// if (am != null) {
	// ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
	// am.getMemoryInfo(mi);
	// Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{pId});
	// if (memoryInfos != null && memoryInfos.length > 0) {
	// return ((float) memoryInfos[0].getTotalPss() * 1024 / mi.totalMem) * 100;
	// }
	// }
	// return 0;
	// }
	//
	// public static int getStateOfPackageName(Context context, String
	// packageName) {
	// int ret = Constants.APP_STATE_STOPPED;
	// if (context != null && packageName != null) {
	// ActivityManager am = (ActivityManager)
	// context.getSystemService(Context.ACTIVITY_SERVICE);
	// List<ActivityManager.RunningAppProcessInfo> processInfoList =
	// am.getRunningAppProcesses();
	// if (processInfoList != null) {
	// for (ActivityManager.RunningAppProcessInfo processInfo : processInfoList) {
	// if (processInfo.processName.equalsIgnoreCase(packageName)) {
	// if (processInfo.importance ==
	// ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
	// ret = APP_STATE_FOREGROUND;
	// } else {
	// ret = APP_STATE_BACKGROUND;
	// }
	// break;
	// }
	// }
	// }
	// }
	// return ret;
	// }
	//
	// private static final int[] VERSION_SCALES = {100000, 1000, 1};
	//
	// public static int getVersionCode(String fileName) {
	// int ret = 0;
	// if (fileName != null) {
	// try {
	// int from = fileName.lastIndexOf("-");
	// int to = fileName.lastIndexOf(".apk");
	// String version = fileName.substring(from + 1, to);
	// StringTokenizer st = new StringTokenizer(version, ".");
	// int index = 0;
	// while (st.hasMoreTokens()) {
	// ret += VERSION_SCALES[index++] * Integer.parseInt(st.nextToken());
	// }
	// } catch (Exception e) {
	// ret = 0;
	// }
	// }
	//
	// return ret;
	// }
	//
	// public static void launchApp(Context context, String packageName) {
	// PackageManager pm = context.getPackageManager();
	// Intent intent = pm.getLaunchIntentForPackage(packageName);
	// Logger.info("Utils", ">>> intent=" + intent);
	// context.startActivity(intent);
	// }

	public static boolean isRooted() {
		String output = readLineAsRoot("id");
		return (output != null && output.toLowerCase().contains("uid=0"));
	}

	// public static boolean executeCommandAsRoot(String command) {
	// return executeCommand(command, true);
	// }
	//
	// public static boolean executeCommand(String command) {
	// return executeCommand(command, false);
	// }
	//
	// private static boolean executeCommand(String command, boolean
	// rootPermission) {
	// boolean ret = false;
	//
	// if (command != null) {
	// try {
	// Process sh = getRuntime().exec((rootPermission ? "su" : "sh"), null, null);
	// OutputStream os = sh.getOutputStream();
	// os.write(command.getBytes("ASCII"));
	// os.flush();
	//// os.writeBytes("exit\n");
	//// os.flush();
	// os.close();
	// sh.waitFor();
	// sh.destroy();
	// ret = true;
	// } catch (Exception e) {
	// Logger.error(TAG, Utils.getStringFromException(e));
	// }
	// }
	//
	// return ret;
	// }

	public static String readLine(String command) {
		List<String> lineList = readLineAfterExecuteScript(command, 1, false);
		return (lineList != null && lineList.size() > 0) ? lineList.get(0) : null;
	}

	public static List<String> readLineAfterExecuteScript(String command) {
		return readLineAfterExecuteScript(command, Integer.MAX_VALUE, false);
	}

	public static String readLineAsRoot(String command) {
		List<String> lineList = readLineAfterExecuteScript(command, 1, true);
		return (lineList != null && lineList.size() > 0) ? lineList.get(0) : null;
	}

	public static List<String> readLineAfterExecuteScriptAsRoot(String command) {
		return readLineAfterExecuteScript(command, Integer.MAX_VALUE, true);
	}

	public static List<String> readLineAfterExecuteScript(String command, int lineNum, boolean rootPermission) {
		ArrayList<String> lineList = null;
		if (command != null) {
			try {
				Process sh = getRuntime().exec((rootPermission ? "su" : "sh"), null, null);
				OutputStream os = sh.getOutputStream();
				os.write(command.getBytes("ASCII"));
				os.flush();
				os.close();
				sh.waitFor();
				BufferedReader in = new BufferedReader(
				    new InputStreamReader(sh.exitValue() == 0 ? sh.getInputStream() : sh.getErrorStream()));
				lineList = new ArrayList<>();
				while (in.ready() && lineList.size() < lineNum) {
					String line = in.readLine();
					lineList.add(line);
				}
				in.close();
				sh.destroy();
			} catch (Exception e) {
				// do nothing
			}
		}
		return lineList;
	}

	public static boolean equals(Object a, Object b) {
		if (a == null && b == null) {
			return true;
		}
		return (a != null && b != null && a.equals(b));
	}

	public static boolean equalsIgnoreCase(Object a, Object b) {
		if (a == null && b == null) {
			return true;
		}
		return (a != null && b != null && a.toString().equalsIgnoreCase(b.toString()));
	}

	private static int ZIP_MAX_SIZE = 10000000;

	public static File makeZip(File[] srcFiles, String outZipFilePath) {

		if (srcFiles == null || outZipFilePath == null) {
			return null;
		}

		try {
			BufferedInputStream origin = null;
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outZipFilePath)));

			try {
				byte data[] = new byte[ZIP_MAX_SIZE];

				for (File srcFile : srcFiles) {
					if (srcFile == null) {
						continue;
					}

					FileInputStream fi = new FileInputStream(srcFile);
					origin = new BufferedInputStream(fi, ZIP_MAX_SIZE);
					try {
						ZipEntry entry = new ZipEntry(
						    srcFile.getAbsolutePath().substring(srcFile.getAbsolutePath().lastIndexOf("/") + 1));
						out.putNextEntry(entry);
						int count;
						while ((count = origin.read(data, 0, ZIP_MAX_SIZE)) != -1) {
							out.write(data, 0, count);
						}
					} finally {
						origin.close();
					}
				}

			} finally {
				out.close();
			}
		} catch (Exception ex) {
			Logger.error(TAG, Utils.getStringFromException(ex));
		}
		return new File(outZipFilePath);
	}

	public static String getSystemProperty(String key) {
		String value = null;

		try {
			value = (String) Class.forName("android.os.SystemProperties").getMethod("get", String.class).invoke(null, key);
		} catch (Exception e) {
			// do nothing.
		}

		return value;
	}

	// private static final String PORT_ACTIVATE_ADB = "62637"; // 62637P ->
	// NAMERP -> PREMAN
	// private static final String PORT_DEACTIVATE_ADB = "-1";
	//
	// private static final String PROPERTY_ADB_TCP_PORT = "service.adb.tcp.port";
	// private static final String TARGET_ADB_TCP_PORT = "#ADB_TCP_PORT#";
	// private static final String COMMAND_SET_ADB_PORT = "setprop " +
	// PROPERTY_ADB_TCP_PORT + " " +
	// TARGET_ADB_TCP_PORT + ";";
	// private static final String COMMAND_RESTART_ADB = "stop adbd;start adbd;";
	//
	//
	// public static String lastestAdbPort = null;
	//
	// public static String activateAdb(boolean active) {
	// String ret = null;
	//
	// if (active) {
	// String port = getSystemProperty(PROPERTY_ADB_TCP_PORT);
	// boolean shouldBeSet = true;
	// if (isInteger(port, 0, 65535)) {
	// if (!port.equals(PORT_DEACTIVATE_ADB)) {
	// shouldBeSet = !checkListener(port);
	// } else {
	// port = PORT_ACTIVATE_ADB;
	// }
	// } else {
	// port = PORT_ACTIVATE_ADB;
	// }
	//
	// if (shouldBeSet) {
	// Logger.info(TAG, ">>> ADB port=" + port + " RESTARTING...");
	// executeCommandAsRoot(
	// COMMAND_SET_ADB_PORT.replaceAll(TARGET_ADB_TCP_PORT, port) +
	// COMMAND_RESTART_ADB);
	// }
	// ret = "ADB PORT " + port + " ACTIVATED";
	// lastestAdbPort = port;
	//
	// } else {
	// executeCommandAsRoot(
	// COMMAND_SET_ADB_PORT.replaceAll(TARGET_ADB_TCP_PORT, PORT_DEACTIVATE_ADB) +
	// COMMAND_RESTART_ADB);
	// ret = "ADB DEACTIVATED";
	// lastestAdbPort = PORT_DEACTIVATE_ADB;
	// }
	//
	// return ret;
	// }

	public static boolean isInteger(String intString) {
		return isInteger(intString, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public static boolean isInteger(String intString, int min, int max) {
		boolean ret = false;

		if (intString != null) {
			try {
				int value = Integer.parseInt(intString.trim());
				int from, to;
				if (min < max) {
					from = min;
					to = max;
				} else {
					from = max;
					to = min;
				}
				ret = (from <= value) && (value <= to);
			} catch (Exception ex) {
				// do nothing;
			}
		}

		return ret;
	}

	private static final String COMMAND_CHECK_LISTENER = "netstat -aln | grep LISTEN | grep ";
	private static final String BIND_ALL = "0.0.0.0";

	public static boolean checkListener(String portNumber) {
		boolean ret = false;

		if (portNumber != null) {
			String line = readLineAsRoot(COMMAND_CHECK_LISTENER + portNumber);
			if (line != null) {
				ret = (line.indexOf(BIND_ALL + ":" + portNumber) >= 0);
			}
		}
		return ret;
	}

	private static final String REGEX_MAC_ADDR = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";

	public static boolean isMacFormat(String macAddress) {
		return (macAddress != null && macAddress.matches(REGEX_MAC_ADDR));
	}

	private static final String REGEX_IP_ADDR = "^(([01]?[0-9]?[0-9]|2([0-4][0-9]|5[0-5]))\\.){3}([01]?[0-9]?[0-9]|2([0-4][0-9]|5[0-5]))$";

	public static boolean isIpFormat(String ipAddress) {
		return (ipAddress != null && ipAddress.matches(REGEX_IP_ADDR));
	}

	public static String extractStringWithCommand(String command, String parseFrom, String parseTo, boolean trim) {
		return extractStringFromLine(readLine(command), parseFrom, parseTo, trim);
	}

	public static String extractStringWithCommandAsRoot(String command, String parseFrom, String parseTo, boolean trim) {
		return extractStringFromLine(readLineAsRoot(command), parseFrom, parseTo, trim);
	}

	// public static String extractStringFromLine(String line, String parseFrom,
	// String parseTo, boolean trim) {
	// String ret = null;
	// if (line != null) {
	// try {
	// int from = line.indexOf(parseFrom);
	// if (from >= 0) {
	// from += parseFrom.length();
	// int to = line.indexOf(parseTo, from);
	// if (to >= from) {
	// ret = line.substring(from, to);
	// if (ret != null && trim) {
	// ret = ret.trim();
	// }
	// }
	// }
	// } catch (Exception ex) {
	// // do nothing
	// }
	// }
	// return ret;
	// }
	//
	public static StringBuffer getStringBuffer(List<String> stringList) {
		StringBuffer ret = null;
		if (stringList != null) {
			ret = new StringBuffer();
			for (String str : stringList) {
				ret.append(str + "\n");
			}
		}
		return ret;
	}

	private static final Calendar calendar = Calendar.getInstance();

	public static int getValueOfCalendar(Date date, int field) {
		int ret = -1;
		if (date != null) {
			try {
				synchronized (calendar) {
					calendar.setTime(date);
					ret = calendar.get(field);
				}
			} catch (Exception ex) {
				// do nothing
			}
		}
		return ret;
	}

	public static Date getDateAfterAdding(Date date, int field, int value) {
		Date ret = null;
		if (date != null) {
			try {
				synchronized (calendar) {
					calendar.setTime(date);
					calendar.add(field, value);
					ret = calendar.getTime();
				}
			} catch (Exception ex) {
				// do nothing
			}
		}
		return ret;
	}

	public static Date getDateAfterSetting(Date date, int field, int value) {
		Date ret = null;
		if (date != null) {
			try {
				synchronized (calendar) {
					calendar.setTime(date);
					calendar.set(field, value);
					ret = calendar.getTime();
				}
			} catch (Exception ex) {
				// do nothing
			}
		}
		return ret;
	}

	private static final String COMMA_SEPARATOR = ",";

	public static List<String> makeListFromString(String listString) {
		return makeListFromString(listString, true);
	}

	public static List<String> makeListFromString(String listString, boolean toLower) {
		List<String> ret = null;
		if (listString != null) {
			try {
				ret = new ArrayList<>(Arrays.asList((toLower ? listString.toLowerCase() : listString).split(COMMA_SEPARATOR)));
			} catch (Exception ex) {
				// do nothing;
			}
		}
		return ret;
	}

	public static String makeStringFromList(List<String> list) {
		String ret = null;
		if (list != null) {
			Collections.sort(list);
			ret = "";
			for (String item : list) {
				ret += item + COMMA_SEPARATOR;
			}
			ret = ret.toLowerCase();
		}
		return ret;
	}

	public static String getLastWord(String str, String delim) {
		String ret = null;
		if (str != null && delim != null) {
			int offset = str.lastIndexOf(delim);
			try {
				ret = (offset >= 0 ? str.substring(offset + delim.length()) : str);
			} catch (Exception ex) {
				ret = str;
			}
		} else {
			ret = str;
		}
		return ret;
	}

	// public static HttpURLConnection getHttpURLConnection(URL url) throws
	// Exception {
	// HttpURLConnection ret = null;
	// if (url != null) {
	// ret = (HttpURLConnection) url.openConnection();
	// if (ret instanceof HttpsURLConnection) {
	// ((HttpsURLConnection) ret).setSSLSocketFactory(new TLSSocketFactory());
	// }
	// } else {
	// throw new Exception("no url");
	// }
	// return ret;
	// }

	public static String extractStringFromLine(String line, String parseFrom, String parseTo, boolean trim) {
		String ret = null;
		if (line != null) {
			try {
				int from = line.indexOf(parseFrom);
				if (from >= 0) {
					from += parseFrom.length();
					int to = -1;
					if (parseTo != null) {
						to = line.indexOf(parseTo, from);
					} else {
						to = line.length();
					}
					if (to >= from) {
						ret = line.substring(from, to);
						if (ret != null && trim) {
							ret = ret.trim();
						}
					}
				}
			} catch (Exception ex) {
				// do nothing
			}
		}
		return ret;
	}

	public static String getPathOfClass(Class<?> clazz) {
		String ret = null;
		if (clazz != null) {
			ret = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
		}
		return ret;
	}

	private static String[] JAVA_PACKAGE_EXTENSIONS = { "jar", "war" };

	public static String getDirPathOfClass(Class<?> clazz) {
		String ret = null;
		String path = getPathOfClass(clazz);
		if (isClassInPackageFile(clazz)) {
			File file = new File(path);
			ret = file.getPath().replaceAll(file.getName(), "");
		} else {
			ret = path;
		}
		return ret;
	}

	public static boolean isClassInPackageFile(Class<?> clazz) {
		boolean ret = false;
		String path = getPathOfClass(clazz);
		if (path != null) {
			File file = new File(path);
			if (file != null && file.exists() && !file.isDirectory()) {
				String extension = getExtension(file.getName());
				for (String packageExtension : JAVA_PACKAGE_EXTENSIONS) {
					if (packageExtension.equals(extension)) {
						ret = true;
						break;
					}
				}
			}
		}
		return ret;
	}

	private static String getExtension(String fileName) {
		String ret = "";
		if (fileName != null && fileName.trim().length() > 0) {
			int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
			int i = fileName.substring(p + 1).lastIndexOf('.');
			if (i >= 0) {
				ret = fileName.substring(p + 1 + i + 1).toLowerCase();
			}
		}
		return ret;
	}

	public static void closeInputStream(InputStream in) {
		if (in != null) {
			try {
				in.close();
				in = null;
			} catch (IOException ioe) {
				// do nothing
			}
		}
	}

	public static void closeOutputStream(OutputStream out) {
		if (out != null) {
			try {
				out.close();
				out = null;
			} catch (IOException ioe) {
				// do nothing
			}
		}
	}
}
