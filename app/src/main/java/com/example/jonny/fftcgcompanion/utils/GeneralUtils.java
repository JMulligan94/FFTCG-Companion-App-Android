package com.example.jonny.fftcgcompanion.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;

import com.example.jonny.fftcgcompanion.R;

public class GeneralUtils
{
    public static Bitmap getDefaultAvatarBitmap(Resources resources)
    {
        return BitmapFactory.decodeResource(resources, R.drawable.unknown_avatar);
    }

    public static boolean isConnectedToWifi()
    {
        WifiManager wifiManager = (WifiManager)FFTCGCompanionApplication.getAppContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled())
        {
            if (wifiManager.getConnectionInfo().getNetworkId() != -1)
                return true;
        }
        return false;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static Drawable getGradientDrawable(float cornerRadius, int strokeWidth, int solidColor, int strokeColor) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(solidColor);
        gradientDrawable.setStroke(strokeWidth, strokeColor);

        gradientDrawable.setCornerRadius(cornerRadius);

        return gradientDrawable;
    }

    public static Drawable getGradientDrawable(int strokeWidth, int solidColor, int strokeColor,
                                               int topLeft, int topRight, int bottomLeft, int bottomRight) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(solidColor);
        gradientDrawable.setStroke(strokeWidth, strokeColor);

        float[] radius = {topLeft, topLeft, topRight, topRight, bottomRight,
                bottomRight, bottomLeft, bottomLeft};
        gradientDrawable.setCornerRadii(radius);

        return gradientDrawable;
    }
}
