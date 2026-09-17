/*
 * ANTIgram Configuration
 */

package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;

public class AntigramConfig {

    private static final String PREFS_NAME = "antigram_config";
    private static SharedPreferences preferences;

    public static boolean bypassRestrictedContent = true;
    public static boolean allowScreenshots = true;
    public static boolean showDeletedEdits = true;
    public static boolean aiEnabled = false;
    public static String aiApiKey = "";
    public static boolean spamFilterEnabled = false;
    public static String spamFilterKeywords = "crypto,bitcoin,airdrop,giveaway,18+,казино,ставки,заработок";

    // iOS Style Configuration
    public static boolean iosStyleEnabled = true;
    public static boolean iosTabBarFullWidth = true;
    public static boolean iosBlur = true;
    public static boolean iosSpringAnimations = true;
    public static boolean iosHaptics = true;
    public static boolean iosCupertinoSwitch = true;
    public static boolean iosBubbles = true;

    private static boolean configLoaded = false;

    public static void loadConfig() {
        if (configLoaded) {
            return;
        }
        if (ApplicationLoader.applicationContext == null) {
            return;
        }
        preferences = ApplicationLoader.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        bypassRestrictedContent = preferences.getBoolean("bypassRestrictedContent", true);
        allowScreenshots = preferences.getBoolean("allowScreenshots", true);
        showDeletedEdits = preferences.getBoolean("showDeletedEdits", true);
        aiEnabled = preferences.getBoolean("aiEnabled", false);
        aiApiKey = preferences.getString("aiApiKey", "");
        spamFilterEnabled = preferences.getBoolean("spamFilterEnabled", false);
        spamFilterKeywords = preferences.getString("spamFilterKeywords", "crypto,bitcoin,airdrop,giveaway,18+,казино,ставки,заработок");

        // Load iOS options
        iosStyleEnabled = preferences.getBoolean("iosStyleEnabled", true);
        iosTabBarFullWidth = preferences.getBoolean("iosTabBarFullWidth", true);
        iosBlur = preferences.getBoolean("iosBlur", true);
        iosSpringAnimations = preferences.getBoolean("iosSpringAnimations", true);
        iosHaptics = preferences.getBoolean("iosHaptics", true);
        iosCupertinoSwitch = preferences.getBoolean("iosCupertinoSwitch", true);
        iosBubbles = preferences.getBoolean("iosBubbles", true);
        configLoaded = true;
    }

    public static void setBypassRestrictedContent(boolean value) {
        bypassRestrictedContent = value;
        getPreferences().edit().putBoolean("bypassRestrictedContent", value).apply();
    }

    public static void setAllowScreenshots(boolean value) {
        allowScreenshots = value;
        getPreferences().edit().putBoolean("allowScreenshots", value).apply();
    }

    public static void setShowDeletedEdits(boolean value) {
        showDeletedEdits = value;
        getPreferences().edit().putBoolean("showDeletedEdits", value).apply();
    }

    public static void setAiEnabled(boolean value) {
        aiEnabled = value;
        getPreferences().edit().putBoolean("aiEnabled", value).apply();
    }

    public static void setAiApiKey(String key) {
        aiApiKey = key != null ? key : "";
        getPreferences().edit().putString("aiApiKey", aiApiKey).apply();
    }

    public static void setSpamFilterEnabled(boolean value) {
        spamFilterEnabled = value;
        getPreferences().edit().putBoolean("spamFilterEnabled", value).apply();
    }

    public static void setSpamFilterKeywords(String keywords) {
        spamFilterKeywords = keywords != null ? keywords : "";
        getPreferences().edit().putString("spamFilterKeywords", spamFilterKeywords).apply();
    }

    public static void setIosStyleEnabled(boolean value) {
        iosStyleEnabled = value;
        getPreferences().edit().putBoolean("iosStyleEnabled", value).apply();
    }

    public static void setIosTabBarFullWidth(boolean value) {
        iosTabBarFullWidth = value;
        getPreferences().edit().putBoolean("iosTabBarFullWidth", value).apply();
    }

    public static void setIosBlur(boolean value) {
        iosBlur = value;
        getPreferences().edit().putBoolean("iosBlur", value).apply();
    }

    public static void setIosSpringAnimations(boolean value) {
        iosSpringAnimations = value;
        getPreferences().edit().putBoolean("iosSpringAnimations", value).apply();
    }

    public static void setIosHaptics(boolean value) {
        iosHaptics = value;
        getPreferences().edit().putBoolean("iosHaptics", value).apply();
    }

    public static void setIosCupertinoSwitch(boolean value) {
        iosCupertinoSwitch = value;
        getPreferences().edit().putBoolean("iosCupertinoSwitch", value).apply();
    }

    public static void setIosBubbles(boolean value) {
        iosBubbles = value;
        getPreferences().edit().putBoolean("iosBubbles", value).apply();
    }

    public static boolean isSpamMessage(String text) {
        if (!spamFilterEnabled || text == null || text.isEmpty() || spamFilterKeywords.isEmpty()) {
            return false;
        }
        String lower = text.toLowerCase();
        String[] keywords = spamFilterKeywords.split(",");
        for (String kw : keywords) {
            String trimmed = kw.trim().toLowerCase();
            if (!trimmed.isEmpty() && lower.contains(trimmed)) {
                return true;
            }
        }
        return false;
    }

    private static SharedPreferences getPreferences() {
        if (preferences == null && ApplicationLoader.applicationContext != null) {
            preferences = ApplicationLoader.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
        return preferences;
    }

    static {
        loadConfig();
    }
}
