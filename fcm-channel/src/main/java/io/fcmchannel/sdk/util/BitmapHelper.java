package io.fcmchannel.sdk.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

/**
 * Created by John Cordeiro on 5/19/17.
 */

public class BitmapHelper {

    @NonNull
    public static RoundedBitmapDrawable getRoundedBitmap(Context context, @DrawableRes int tabBitmapRes, int radius) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), tabBitmapRes);
        RoundedBitmapDrawable roundedBitmap = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
        roundedBitmap.setCornerRadius(radius);
        return roundedBitmap;
    }

}
