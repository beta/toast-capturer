package net.kyouko.toastcapturer;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for capturing toasts.
 */
public class CapturerService extends AccessibilityService {

    public final static String ACTION_CATCH_TOAST = "net.kyouko.toastcapturer.CATCH_TOAST";
    public final static String ACTION_CATCH_NOTIFICATION = "net.kyouko.toastcapturer.CATCH_NOTIFICATION";
    public final static String EXTRA_PACKAGE = "EXTRA_PACKAGE";
    public final static String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public final static String EXTRA_TIME = "EXTRA_TIME";

    private final AccessibilityServiceInfo info = new AccessibilityServiceInfo();
    private final java.text.DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            final String sourcePackageName = (String) event.getPackageName();
            Parcelable parcelable = event.getParcelableData();

            List<CharSequence> messages = event.getText();
            if (!messages.isEmpty()) {
                final String message = (String) messages.get(0);
                Intent intent;
                if ((parcelable instanceof Notification)) {
                    intent = new Intent(ACTION_CATCH_NOTIFICATION);
                } else {
                    intent = new Intent(ACTION_CATCH_TOAST);
                }
                intent.putExtra(EXTRA_PACKAGE, sourcePackageName);
                intent.putExtra(EXTRA_MESSAGE, message);
                intent.putExtra(EXTRA_TIME, dateFormat.format(new Date()));
                getApplicationContext().sendBroadcast(intent);
            }
        }
    }

    @Override public void onInterrupt() {
        // Ignored
    }

    @Override protected void onServiceConnected() {
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.notificationTimeout = 100;
        this.setServiceInfo(info);
    }

    public static boolean isAccessibilityServiceEnabled(Context context) {
        int accessibilityEnabled = 0;
        final String service = "net.kyouko.toastcapturer/net.kyouko.toastcapturer.CapturerService";

        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("Toast Capturer", "Accessibility settings not found: " + e.getLocalizedMessage());
        }

        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessibilityService = splitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        } else {
            Log.e("Toast Capturer", "Accessibility service disabled.");
        }

        return false;
    }

}
