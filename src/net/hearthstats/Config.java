package net.hearthstats;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

import net.hearthstats.log.Log;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {

    private final static Logger debugLog = LoggerFactory.getLogger(Config.class);

    public static final OS os = _parseOperatingSystem();

	private static String _version;
	
	private static Wini _ini = null;

	private static String _userkey;

	private static boolean _checkForUpdates;

    private static boolean _useOsxNotifications;

	private static boolean _showNotifications;

	private static boolean _showHsFoundNotification;

	private static boolean _showHsClosedNotification;

	private static boolean _showScreenNotification;

	private static boolean _showModeNotification;

	private static boolean _showDeckNotification;

	private static boolean _analyticsEnabled;

	private static boolean _minToTray;

	private static boolean _startMinimized;

	private static int _x;

	private static int _y;

	private static int _width;

	private static int _height;

	private static String _defaultApiBaseUrl = "http://hearthstats.net/api/v1/";

	private static String _apiBaseUrl;
	
	public static void rebuild() {
        debugLog.debug("Building config");

		_storePreviousValues();

		_getIni().clear();

		// api
		setUserKey("your_userkey_here");
		setApiBaseUrl(_defaultApiBaseUrl );
		
		// updates
		setCheckForUpdates(true);
		
		// notifications
        setUseOsxNotifications(isOsxNotificationsSupported());
		setShowNotifications(true);
		setShowHsFoundNotification(true);
		setShowHsClosedNotification(true);
		setShowScreenNotification(true);
		setShowModeNotification(true);
		setShowDeckNotification(true);
		setShowYourTurnNotification(true);
		
		// analytics
		setAnalyticsEnabled(true);
		
		// ui
		setMinToTray(true);
		setStartMinimized(false);
		setX(0);
		setY(0);
		setWidth(600);
		setHeight(700);
		
		_restorePreviousValues();
		
		save();
		
	}

	public static String getApiBaseUrl() {
		return _getStringSetting("API", "baseurl", _defaultApiBaseUrl);
	}
	private static void setApiBaseUrl(String baseUrl) {
		_setStringValue("API", "baseurl", baseUrl);
	}

	public static String getUserKey() {
		return _getStringSetting("API", "userkey", "your_userkey_here");
	}
	
	public static int getX() {
		return _getIntegerSetting("ui", "x", 0);
	}
	
	public static int getY() {
		return _getIntegerSetting("ui", "y", 0);
	}
	
	public static int getWidth() {
		return _getIntegerSetting("ui", "width", 600);
	}
	
	public static int getHeight() {
		return _getIntegerSetting("ui", "height", 700);
	}

	public static boolean startMinimized() {
		return _getBooleanSetting("ui", "startminimized", false);
	}
	
	public static boolean analyticsEnabled() {
		return _getBooleanSetting("analytics", "enabled", true);
	}
	
	public static boolean showEventLog() {
		return _getBooleanSetting("ui", "eventlog", true);
	}
	
	public static boolean mirrorGameImage() {
		return _getBooleanSetting("ui", "mirrorgame", false);
	}
	
	public static boolean checkForUpdates() {
		return _getBooleanSetting("updates", "check", true);
	}
	
	public static boolean showDeckNotification() {
		return _getBooleanSetting("notifications", "deck", true);
	}
	
	public static boolean showScreenNotification() {
		return _getBooleanSetting("notifications", "screen", true);
	}
	
	public static boolean showHsFoundNotification() {
		return _getBooleanSetting("notifications", "hsfound", true);
	}
	
	public static boolean showModeNotification() {
		return _getBooleanSetting("notifications", "mode", true);
	}
	public static boolean showYourTurnNotification() {
		return _getBooleanSetting("notifications", "yourturn", true);
	}
	
	public static boolean showHsClosedNotification() {
		return _getBooleanSetting("notifications", "hsclosed", true);
	}
	
	public static boolean minimizeToTray() {
		return _getBooleanSetting("ui", "mintotray", true);
	}

    public static boolean useOsxNotifications() {
        return _getBooleanSetting("notifications", "osx", isOsxNotificationsSupported());
    }

	public static boolean showNotifications() {
		return _getBooleanSetting("notifications", "enabled", true);
	}
	
	public static String getVersion() {
		if(_version == null) {
			_version = "";
			String versionFile = "/version";
			if(Config.os.toString().equals("OSX")) {
				versionFile += "-osx";
			}
			InputStream in = Config.class.getResourceAsStream(versionFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            try {
				while ((strLine = br.readLine()) != null)   {
				    _version += strLine;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Exception in Config: " + e.toString());
			}
		}
		return _version;
	}
	public static String getVersionWithOs() {
		return getVersion() + '-' + os;
	}

    public static void setUseOsxNotifications(boolean val) {
        _setBooleanValue("notifications", "osx", val);
    }

    public static Boolean isOsxNotificationsSupported() {
        try {
            if (Config.os == OS.OSX) {
                String osVersion = Config.getSystemProperty("os.version");
                String osVersionSplit[] = osVersion.split("\\.");
                if (osVersionSplit[0].equals("10")) {
                    // This is OS X
                    int version = Integer.parseInt(osVersionSplit[1]);
                    if (version >= 8) {
                        // This is OS X 10.8 or later
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            debugLog.warn("Unable to determine if OS X notifications are supported, assuming false", ex);
        }
        return false;
    }

    public static void setShowNotifications(boolean val) {
		_setBooleanValue("notifications", "enabled", val);
	}
	
	public static void setAnalyticsEnabled(boolean val) {
		_setBooleanValue("analytics", "enabled", val);
	}
	public static void setShowHsFoundNotification(boolean val) {
		_setBooleanValue("notifications", "hsfound", val);
	}
	public static void setShowHsClosedNotification(boolean val) {
		_setBooleanValue("notifications", "hsclosed", val);
	}
	public static void setShowScreenNotification(boolean val) {
		_setBooleanValue("notifications", "screen", val);
	}
	public static void setShowYourTurnNotification(boolean val) {
		_setBooleanValue("notifications", "yourturn", val);
	}
	public static void setShowModeNotification(boolean val) {
		_setBooleanValue("notifications", "mode", val);
	}
	public static void setShowDeckNotification(boolean val) {
		_setBooleanValue("notifications", "deck", val);
	}
	
	public static void setCheckForUpdates(boolean val) {
		_setBooleanValue("updates", "check", val);
	}
	public static void setMinToTray(boolean val) {
		_setBooleanValue("ui", "mintotray", val);
	}
	public static void setStartMinimized(boolean val) {
		_setBooleanValue("ui", "startminimized", val);
	}
	
	public static void setUserKey(String userkey) {
		_setStringValue("API", "userkey", userkey);
	}
	
	private static void _createConfigIniIfNecessary() {
		File configFile = new File(getConfigPath());
		if (!configFile.exists()) {
            if (Config.os == OS.OSX) {
                // The location has moved on Macs, so move the old config.ini to the new location if there is one
                File oldConfigFile = new File("config.ini");
                if (oldConfigFile.exists()) {
                    debugLog.info("Found old config.ini file in {}, moving it to {}", oldConfigFile.getAbsolutePath(), configFile.getAbsolutePath());
                    boolean renameSuccessful = oldConfigFile.renameTo(configFile);
                    if (renameSuccessful) {
                        debugLog.debug("Moved successfully");
                        return;
                    } else {
                        debugLog.warn("Unable to move config.ini file to {}, creating a new file", configFile.getAbsolutePath());
                    }
                }
            }

			try {
				configFile.createNewFile();
			} catch (IOException e) {
                Log.warn("Error occurred while creating config.ini file", e);
			}
		}
	}

    private static String getConfigPath() {
        if (Config.os == OS.OSX) {
            return getSystemProperty("user.home") + "/Library/Preferences/net.hearthstats.HearthStatsUploader.ini";
        } else {
            return "config.ini";
        }
    }
	
	private static void _setStringValue(String group, String key, String val) {
		_getIni().put(group, key, val);
		try {
			_getIni().store();
		} catch (IOException e) {
            Log.warn("Error occurred while setting key " + key + " in config.ini", e);
		}
	}
	
	private static void _setBooleanValue(String group, String key, boolean val) {
		_getIni().put(group, key, val);
	}
	
	public static void setX(int val) {
		_setIntVal("ui", "x", val);
	}
	public static void setY(int val) {
		_setIntVal("ui", "y", val);
	}
	public static void setWidth(int val) {
		_setIntVal("ui", "width", val);
	}
	public static void setHeight(int val) {
		_setIntVal("ui", "height", val);
	}
	
	private static Wini _getIni() {
		if(_ini == null) {
			_createConfigIniIfNecessary();
			try {
				_ini = new Wini(new File(getConfigPath()));
			} catch (Exception e) {
                Log.warn("Error occurred while loading config.ini", e);
			}
		}
		return _ini;
	}
	
	private static boolean _getBooleanSetting(String group, String key, boolean deflt) {
		String setting = _getIni().get(group, key);
		return setting == null ? deflt : setting.equals("true");
	}
	
	private static int _getIntegerSetting(String group, String key, int deflt) {
		String setting = _getIni().get(group, key);
		return setting == null ? deflt : Integer.parseInt(setting); 
	}
	
	private static String _getStringSetting(String group, String key, String deflt) {
		String setting = _getIni().get(group, key);
		return setting == null ? deflt : setting;
	}
	
	private static void _restorePreviousValues() {
		setUserKey(_userkey);
		setApiBaseUrl(_apiBaseUrl);
		setCheckForUpdates(_checkForUpdates);
        setUseOsxNotifications(_useOsxNotifications);
		setShowNotifications(_showNotifications);
		setShowHsFoundNotification(_showHsFoundNotification);
		setShowHsClosedNotification(_showHsClosedNotification);
		setShowScreenNotification(_showScreenNotification);
		setShowModeNotification(_showModeNotification);
		setShowDeckNotification(_showDeckNotification);
		setAnalyticsEnabled(_analyticsEnabled);
		setMinToTray(_minToTray);
		setStartMinimized(_startMinimized);
		setX(_x);
		setY(_y);
		setWidth(_width);
		setHeight(_height);
	}
	
	private static void _storePreviousValues() {
		_userkey = getUserKey();
		_apiBaseUrl = getApiBaseUrl();
		_checkForUpdates = checkForUpdates();
        _useOsxNotifications = useOsxNotifications();
		_showNotifications = showNotifications();
		_showHsFoundNotification = showHsFoundNotification();
		_showHsClosedNotification = showHsClosedNotification();
		_showScreenNotification = showScreenNotification();
		_showModeNotification = showModeNotification();
		_showDeckNotification = showDeckNotification();
		_analyticsEnabled = analyticsEnabled();
		_minToTray = minimizeToTray();
		_startMinimized = startMinimized();
		_x = getX();
		_y = getY();
		_width = getWidth();
		_height = getHeight();
	}
	
	private static void _setIntVal(String group, String key, int val) {
		_getIni().put(group, key, val + "");
	}
	
	public static void save() {
		try {
			_getIni().store();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Unable to write to config.ini while trying to save settings");
		}
	}

    public static String getJavaLibraryPath() {
        return getSystemProperty("java.library.path");
    }

    public static String getSystemProperty(String property) {
        try {
            return System.getProperty(property);
        } catch (SecurityException ex) {
            // Some system properties may not be available if the user has their security settings locked down
            debugLog.warn("Caught a SecurityException reading the system property '" + property + "', defaulting to blank string.");
            return "";
        }
    }

    /**
     * Parses the os.name system property to determine what operating system we are using.
     * This method is private because you should use the cached version {@link Config#os)} which is faster.
     * @return The current OS
     */
    private static OS _parseOperatingSystem() {
        String osString = getSystemProperty("os.name");
        if (osString == null) {
            return OS.UNSUPPORTED;
        } else if (osString.startsWith("Windows")) {
            return OS.WINDOWS;
        } else if (osString.startsWith("Mac OS X")) {
            return OS.OSX;
        } else {
            return OS.UNSUPPORTED;
        }
    }

    public static enum OS {
        WINDOWS, OSX, UNSUPPORTED;
    }
}
