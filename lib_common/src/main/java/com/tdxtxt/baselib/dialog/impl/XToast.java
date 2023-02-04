package com.tdxtxt.baselib.dialog.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.tdxtxt.baselib.R;

import java.lang.ref.WeakReference;

/**
 * 功能描述:
 * @author tangdexiang
 * @since 2021/4/7
 */
@SuppressLint("InflateParams")
public class XToast {
    private static WeakReference<Toast> lastToast = null;

    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;

    private XToast() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @StringRes int message) {
        return normal(context, context.getString(message), Toast.LENGTH_SHORT, null, false);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @NonNull CharSequence message) {
        return normal(context, message, Toast.LENGTH_SHORT, null, false);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @StringRes int message, Drawable icon) {
        return normal(context, context.getString(message), Toast.LENGTH_SHORT, icon, true);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @NonNull CharSequence message, Drawable icon) {
        return normal(context, message, Toast.LENGTH_SHORT, icon, true);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @StringRes int message, int duration) {
        return normal(context, context.getString(message), duration, null, false);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @NonNull CharSequence message, int duration) {
        return normal(context, message, duration, null, false);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @StringRes int message, int duration,
                               Drawable icon) {
        return normal(context, context.getString(message), duration, icon, true);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @NonNull CharSequence message, int duration,
                               Drawable icon) {
        return normal(context, message, duration, icon, true);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @StringRes int message, int duration,
                               Drawable icon, boolean withIcon) {
        return custom(context, context.getString(message), icon, ContextCompat.getColor(context, R.color.toast_normal_tint_color),
                ContextCompat.getColor(context, R.color.toast_default_text_color), duration, withIcon, true);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @NonNull CharSequence message, int duration,
                               Drawable icon, boolean withIcon) {
        return custom(context, message, icon, ContextCompat.getColor(context, R.color.toast_normal_tint_color),
                ContextCompat.getColor(context, R.color.toast_default_text_color), duration, withIcon, true);
    }

    @CheckResult
    public static Toast warning(@NonNull Context context, @StringRes int message) {
        return warning(context, context.getString(message), Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast warning(@NonNull Context context, @NonNull CharSequence message) {
        return warning(context, message, Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast warning(@NonNull Context context, @StringRes int message, int duration) {
        return warning(context, context.getString(message), duration, true);
    }

    @CheckResult
    public static Toast warning(@NonNull Context context, @NonNull CharSequence message, int duration) {
        return warning(context, message, duration, true);
    }

    @CheckResult
    public static Toast warning(@NonNull Context context, @StringRes int message, int duration, boolean withIcon) {
        return custom(context, context.getString(message), ContextCompat.getDrawable(context, R.drawable.baselib_xtoast_ic_error_outline_white_24dp),
                ContextCompat.getColor(context, R.color.toast_warning_color), ContextCompat.getColor(context, R.color.toast_default_text_color),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast warning(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon) {
        return custom(context, message, ContextCompat.getDrawable(context, R.drawable.baselib_xtoast_ic_error_outline_white_24dp),
                ContextCompat.getColor(context, R.color.toast_warning_color), ContextCompat.getColor(context, R.color.toast_default_text_color),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast info(@NonNull Context context, @StringRes int message) {
        return info(context, context.getString(message), Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast info(@NonNull Context context, @NonNull CharSequence message) {
        return info(context, message, Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast info(@NonNull Context context, @StringRes int message, int duration) {
        return info(context, context.getString(message), duration, true);
    }

    @CheckResult
    public static Toast info(@NonNull Context context, @NonNull CharSequence message, int duration) {
        return info(context, message, duration, true);
    }

    @CheckResult
    public static Toast info(@NonNull Context context, @StringRes int message, int duration, boolean withIcon) {
        return custom(context, context.getString(message), ContextCompat.getDrawable(context, R.drawable.baselib_xtoast_ic_info_outline_white_24dp),
                ContextCompat.getColor(context, R.color.toast_info_color), ContextCompat.getColor(context, R.color.toast_default_text_color),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast info(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon) {
        return custom(context, message, ContextCompat.getDrawable(context, R.drawable.baselib_xtoast_ic_info_outline_white_24dp),
                ContextCompat.getColor(context, R.color.toast_info_color), ContextCompat.getColor(context, R.color.toast_default_text_color),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast success(@NonNull Context context, @StringRes int message) {
        return success(context, context.getString(message), Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast success(@NonNull Context context, @NonNull CharSequence message) {
        return success(context, message, Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast success(@NonNull Context context, @StringRes int message, int duration) {
        return success(context, context.getString(message), duration, true);
    }

    @CheckResult
    public static Toast success(@NonNull Context context, @NonNull CharSequence message, int duration) {
        return success(context, message, duration, true);
    }

    @CheckResult
    public static Toast success(@NonNull Context context, @StringRes int message, int duration, boolean withIcon) {
        return custom(context, context.getString(message), ContextCompat.getDrawable(context, R.drawable.baselib_xtoast_ic_check_white_24dp),
                ContextCompat.getColor(context, R.color.toast_success_color), ContextCompat.getColor(context, R.color.toast_default_text_color),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast success(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon) {
        return custom(context, message, ContextCompat.getDrawable(context, R.drawable.baselib_xtoast_ic_check_white_24dp),
                ContextCompat.getColor(context, R.color.toast_success_color), ContextCompat.getColor(context, R.color.toast_default_text_color),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast error(@NonNull Context context, @StringRes int message) {
        return error(context, context.getString(message), Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast error(@NonNull Context context, @NonNull CharSequence message) {
        return error(context, message, Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast error(@NonNull Context context, @StringRes int message, int duration) {
        return error(context, context.getString(message), duration, true);
    }

    @CheckResult
    public static Toast error(@NonNull Context context, @NonNull CharSequence message, int duration) {
        return error(context, message, duration, true);
    }

    @CheckResult
    public static Toast error(@NonNull Context context, @StringRes int message, int duration, boolean withIcon) {
        return custom(context, context.getString(message), ContextCompat.getDrawable(context, R.drawable.baselib_xtoast_ic_clear_white_24dp),
                ContextCompat.getColor(context, R.color.toast_error_color), ContextCompat.getColor(context, R.color.toast_default_text_color),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast error(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon) {
        return custom(context, message, ContextCompat.getDrawable(context, R.drawable.baselib_xtoast_ic_clear_white_24dp),
                ContextCompat.getColor(context, R.color.toast_error_color), ContextCompat.getColor(context, R.color.toast_default_text_color),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast custom(@NonNull Context context, @StringRes int message, Drawable icon,
                               int duration, boolean withIcon) {
        return custom(context, context.getString(message), icon, -1, ContextCompat.getColor(context, R.color.toast_default_text_color),
                duration, withIcon, false);
    }

    @CheckResult
    public static Toast custom(@NonNull Context context, @NonNull CharSequence message, Drawable icon,
                               int duration, boolean withIcon) {
        return custom(context, message, icon, -1, ContextCompat.getColor(context, R.color.toast_default_text_color),
                duration, withIcon, false);
    }

    @CheckResult
    public static Toast custom(@NonNull Context context, @StringRes int message, @DrawableRes int iconRes,
                               @ColorRes int tintColorRes, int duration,
                               boolean withIcon, boolean shouldTint) {
        return custom(context, context.getString(message), ContextCompat.getDrawable(context, iconRes),
                ContextCompat.getColor(context, tintColorRes), ContextCompat.getColor(context, R.color.toast_default_text_color),
                duration, withIcon, shouldTint);
    }

    @CheckResult
    public static Toast custom(@NonNull Context context, @NonNull CharSequence message, @DrawableRes int iconRes,
                               @ColorRes int tintColorRes, int duration,
                               boolean withIcon, boolean shouldTint) {
        return custom(context, message, ContextCompat.getDrawable(context, iconRes),
                ContextCompat.getColor(context, tintColorRes), ContextCompat.getColor(context, R.color.toast_default_text_color),
                duration, withIcon, shouldTint);
    }

    @CheckResult
    public static Toast custom(@NonNull Context context, @StringRes int message, Drawable icon,
                               @ColorRes int tintColorRes, int duration,
                               boolean withIcon, boolean shouldTint) {
        return custom(context, context.getString(message), icon, ContextCompat.getColor(context, tintColorRes),
                ContextCompat.getColor(context, R.color.toast_default_text_color), duration, withIcon, shouldTint);
    }

    @CheckResult
    public static Toast custom(@NonNull Context context, @StringRes int message, Drawable icon,
                               @ColorRes int tintColorRes, @ColorRes int textColorRes, int duration,
                               boolean withIcon, boolean shouldTint) {
        return custom(context, context.getString(message), icon, ContextCompat.getColor(context, tintColorRes),
                ContextCompat.getColor(context, textColorRes), duration, withIcon, shouldTint);
    }

    @SuppressLint("ShowToast")
    @CheckResult
    public static Toast custom(@NonNull Context context, @NonNull CharSequence message, Drawable icon,
                               @ColorInt int tintColor, @ColorInt int textColor, int duration,
                               boolean withIcon, boolean shouldTint) {
        final Toast currentToast = Toast.makeText(context, "", duration);
        final View toastLayout = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.baselib_xui_layout_xtoast, null);
        final ImageView toastIcon = toastLayout.findViewById(R.id.toast_icon);
        final TextView toastTextView = toastLayout.findViewById(R.id.toast_text);
        Drawable drawableFrame;

        if (shouldTint) {
            drawableFrame = tint9PatchDrawableFrame(context, tintColor);
        } else {
            drawableFrame = ContextCompat.getDrawable(context, R.drawable.baselib_xtoast_frame);
        }
        setBackground(toastLayout, drawableFrame);

        if (withIcon) {
            if (icon == null) {
                throw new IllegalArgumentException("Avoid passing 'icon' as null if 'withIcon' is set to true");
            }
            setBackground(toastIcon, Config.get().tintIcon ? tintIcon(icon, textColor) : icon);
        } else {
            toastIcon.setVisibility(View.GONE);
        }

        toastTextView.setText(message);
        toastTextView.setTextColor(textColor);
        toastTextView.setTypeface(Config.get().typeface, Typeface.NORMAL);
        if (Config.get().textSize != -1) {
            toastTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Config.get().textSize);
        }
        if (Config.get().alpha != -1) {
            toastLayout.getBackground().setAlpha(Config.get().alpha);
        }
        currentToast.setView(toastLayout);

        if (!Config.get().allowQueue) {
            if (lastToast != null) {
                Toast toast = lastToast.get();
                if(toast != null) toast.cancel();
            }
            lastToast = new WeakReference<>(currentToast);
        }
        if (Config.get().gravity != -1) {
            currentToast.setGravity(Config.get().gravity, Config.get().xOffset, Config.get().yOffset);
        }
        return currentToast;
    }

    static Drawable tintIcon(@NonNull Drawable drawable, @ColorInt int tintColor) {
        drawable.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    static Drawable tint9PatchDrawableFrame(@NonNull Context context, @ColorInt int tintColor) {
        final NinePatchDrawable toastDrawable = (NinePatchDrawable) AppCompatResources.getDrawable(context, R.drawable.baselib_xtoast_frame);
        return tintIcon(toastDrawable, tintColor);
    }

    static void setBackground(@NonNull View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static class Config {
        private static volatile Config sInstance = null;

        private static final Typeface LOADED_TOAST_TYPEFACE = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
        private Typeface typeface = LOADED_TOAST_TYPEFACE;
        private int textSize = -1; //sp
        private boolean tintIcon = true;
        private boolean allowQueue = true;
        private int alpha = -1;
        private int gravity = -1;
        private int xOffset = 0;
        private int yOffset = 0;

        private Config() {

        }

        /**
         * 获取单例
         *
         * @return
         */
        public static Config get() {
            if (sInstance == null) {
                synchronized (Config.class) {
                    if (sInstance == null) {
                        sInstance = new Config();
                    }
                }
            }
            return sInstance;
        }

        public void reset() {
            typeface = LOADED_TOAST_TYPEFACE;
            textSize = -1;
            tintIcon = true;
            allowQueue = true;
            alpha = -1;
            gravity = -1;
            xOffset = 0;
            yOffset = 0;
        }

        @CheckResult
        public Config setToastTypeface(Typeface typeface) {
            if (typeface != null) {
                this.typeface = typeface;
            }
            return this;
        }

        @CheckResult
        public Config setTextSize(int sizeInSp) {
            this.textSize = sizeInSp;
            return this;
        }

        @CheckResult
        public Config tintIcon(boolean tintIcon) {
            this.tintIcon = tintIcon;
            return this;
        }

        public Config setAlpha(@IntRange(from = 0, to = 255) int alpha) {
            this.alpha = alpha;
            return this;
        }

        @CheckResult
        public Config allowQueue(boolean allowQueue) {
            this.allowQueue = allowQueue;
            return this;
        }

        public Config setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Config setGravity(int gravity, int xOffset, int yOffset) {
            this.gravity = gravity;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            return this;
        }

        public Config resetGravity() {
            gravity = -1;
            xOffset = 0;
            yOffset = 0;
            return this;
        }

        public Config setXOffset(int xOffset) {
            this.xOffset = xOffset;
            return this;
        }

        public Config setYOffset(int yOffset) {
            this.yOffset = yOffset;
            return this;
        }
    }
}
