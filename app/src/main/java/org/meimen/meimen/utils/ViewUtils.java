
package org.meimen.meimen.utils;

import org.meimen.meimen.MeiMenApplication;
import org.meimen.meimen.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class ViewUtils {

    /**
     * Convenience method of findViewById
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(View parent, int id) {
        return (T) parent.findViewById(id);
    }

    /**
     * Convenience method of findViewById
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(Activity activity, int id) {
        return (T) activity.findViewById(id);
    }

    /**
     * helper to show text
     *
     * @param message
     * @param duration
     * @param gravity
     * @param xOffset
     * @param yOffset
     */
    public static void showToast(final String message, final int duration, final int gravity, final int xOffset, final int yOffset) {
        final Context context = MeiMenApplication.getContext();
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            Toast toast = Toast.makeText(context, message, duration);
            toast.setGravity(gravity, xOffset, yOffset);
            toast.show();

        } else {
            Handler mainHandler = new Handler(context.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, message, duration).show();
                }
            });
        }
    }

    public static void showToast(final String message, final int duration) {
        showToast(message, duration, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    public static void showToast(String message) {
        Resources res = MeiMenApplication.getContext().getResources();
        showToast(message, Toast.LENGTH_SHORT, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
                (res.getDimensionPixelOffset(R.dimen.toast_padding_bottom)));
    }

    public static void showToast(int messageId) {
        Context context = MeiMenApplication.getContext();
        if (context != null) {
            showToast(context.getString(messageId));
        }
    }

}
