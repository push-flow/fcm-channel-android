package io.fcmchannel.sdk.shared;

import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

public class BindingAdapters {

    @BindingAdapter("app:layout_marginLeft")
    public static void setMarginLeft(View view, float margin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.leftMargin = Math.round(margin);
    }

    @BindingAdapter("app:layout_marginRight")
    public static void setMarginRight(View view, float margin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.rightMargin = Math.round(margin);
    }

    @BindingAdapter("app:layout_marginStart")
    public static void setMarginStart(View view, float margin) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            layoutParams.setMarginStart(Math.round(margin));
        }
    }

    @BindingAdapter("app:layout_marginEnd")
    public static void setMarginEnd(View view, float margin) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            layoutParams.setMarginEnd(Math.round(margin));
        }
    }

    @BindingAdapter("app:textColorRes")
    public static void setTextColor(TextView view, @ColorRes int colorRes) {
        view.setTextColor(ContextCompat.getColor(view.getContext(), colorRes));
    }

    @BindingAdapter("app:backgroundColorRes")
    public static void setBackgroundColor(View view, @ColorRes int colorRes) {
        final int color = ContextCompat.getColor(view.getContext(), colorRes);
        final ColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);

        view.getBackground().setColorFilter(colorFilter);
    }

    @BindingAdapter("app:srcRes")
    public static void setImageSrc(ImageView view, @DrawableRes int drawableRes) {
        view.setImageResource(drawableRes);
    }

}
