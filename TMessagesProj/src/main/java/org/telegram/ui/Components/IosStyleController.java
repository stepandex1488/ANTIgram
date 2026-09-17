/*
 * Telegram iOS Edition Style Controller
 */

package org.telegram.ui.Components;

import static org.telegram.messenger.AndroidUtilities.dp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.graphics.ColorUtils;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AntigramConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.MainTabsLayout;

public class IosStyleController {

    // Apple iOS System Palette
    public static final int IOS_BLUE = 0xFF007AFF;
    public static final int IOS_GREEN = 0xFF34C759;
    public static final int IOS_RED = 0xFFFF3B30;
    public static final int IOS_GRAY_UNSELECTED = 0xFF8E8E93;
    public static final int IOS_BG_GROUPED_LIGHT = 0xFFF2F2F7;
    public static final int IOS_BG_GROUPED_DARK = 0xFF000000;
    public static final int IOS_CARD_LIGHT = 0xFFFFFFFF;
    public static final int IOS_CARD_DARK = 0xFF1C1C1E;
    public static final int IOS_HAIRLINE_LIGHT = 0x333C3C43;
    public static final int IOS_HAIRLINE_DARK = 0x44545458;

    // Dimensions
    public static final int IOS_TAB_BAR_HEIGHT_DP = 50;

    /**
     * Check if iOS style is enabled
     */
    public static boolean isIosEnabled() {
        return AntigramConfig.iosStyleEnabled;
    }

    /**
     * Trigger authentic iOS selection haptic feedback
     */
    public static void performSelectionHaptic(View view) {
        if (!AntigramConfig.iosHaptics || view == null) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Vibrator vibrator = (Vibrator) view.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null && vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));
                        return;
                    }
                    vibrator.vibrate(VibrationEffect.createOneShot(12, 60));
                    return;
                }
            }
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        } catch (Exception ignored) {
        }
    }

    /**
     * Trigger authentic iOS impact (medium) haptic feedback
     */
    public static void performImpactHaptic(View view) {
        if (!AntigramConfig.iosHaptics || view == null) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Vibrator vibrator = (Vibrator) view.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null && vibrator.hasVibrator()) {
                    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK));
                    return;
                }
            }
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        } catch (Exception ignored) {
        }
    }

    /**
     * Apply iOS docked tab bar styling to MainTabsLayout
     */
    public static void applyIosTabBarLayout(MainTabsLayout tabsView, FrameLayout tabsViewWrapper, boolean isDark) {
        if (tabsView == null || !AntigramConfig.iosStyleEnabled || !AntigramConfig.iosTabBarFullWidth) {
            return;
        }

        // Expand to full width docked at bottom edge
        tabsView.setMaxWidth(Integer.MAX_VALUE);
        tabsView.setPadding(0, dp(4), 0, dp(4));

        if (tabsViewWrapper != null) {
            tabsViewWrapper.setPadding(0, 0, 0, 0);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) tabsView.getLayoutParams();
            if (lp != null) {
                lp.width = FrameLayout.LayoutParams.MATCH_PARENT;
                lp.leftMargin = 0;
                lp.rightMargin = 0;
                lp.bottomMargin = 0;
                lp.gravity = Gravity.BOTTOM | Gravity.FILL_HORIZONTAL;
                tabsView.setLayoutParams(lp);
            }
        }
    }

    /**
     * Draw top hairline separator for iOS TabBar / NavigationBar
     */
    public static void drawIosHairline(Canvas canvas, int width, int height, boolean isDark, boolean isTop) {
        if (!AntigramConfig.iosStyleEnabled) {
            return;
        }
        Paint paint = new Paint();
        paint.setColor(isDark ? IOS_HAIRLINE_DARK : IOS_HAIRLINE_LIGHT);
        paint.setStrokeWidth(AndroidUtilities.density <= 2 ? 1f : 0.67f * AndroidUtilities.density);
        float y = isTop ? 0 : height - paint.getStrokeWidth() / 2f;
        canvas.drawLine(0, y, width, y, paint);
    }
}
