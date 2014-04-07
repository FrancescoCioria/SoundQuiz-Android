package com.mosquitolabs.soundquiz;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;


public class Utility {

    private final static String accessKey = "AKIAITISRDB2BZLF5UMA";
    private final static String secretKey = "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";
    private final static int FPS = 35;
    private static SharedPreferences sharedPreferences;
    private final static AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
    private static ClientConfiguration clientConfig = new ClientConfiguration();
    private static AmazonS3Client s3Client;

    public static void initUtility(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        clientConfig.setProtocol(Protocol.HTTP);
        s3Client = new AmazonS3Client(credentials, clientConfig);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager CManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo NInfo = CManager.getActiveNetworkInfo();
        if (NInfo != null && NInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static int getFPS(){
        return FPS;
    }

    public static int convertDpToPixels(Context context, float dp) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }

    public static float pixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    @TargetApi(14)
    public static int getWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            display.getSize(size);
            return size.x;
        }
        return display.getWidth();
    }

    @TargetApi(14)
    public static int getHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            display.getSize(size);
            return size.y;
        }

        return display.getHeight();

    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }


    @TargetApi(14)
    public static void hideActionbar(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            activity.getActionBar().hide();
        }
    }

    public static void shortToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void longToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    public static synchronized void saveImageToDisk(Context context, String ID, Bitmap image) {
        try {
            String path = new String("IMG" + ID);
            java.io.FileOutputStream out = context.openFileOutput(path,
                    Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized Bitmap readImageFromDisk(Context context, String ID, boolean blurred) {
        try {
            String path = blurred ? "IMG_" + ID + "_blur" : "IMG_" + ID;
            java.io.FileInputStream in = context.openFileInput(path);
            Bitmap image = BitmapFactory.decodeStream(in);
            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static AmazonS3Client getS3Client() {
        return s3Client;
    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }


}
