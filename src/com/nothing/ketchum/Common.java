package com.nothing.ketchum;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;

public class Common {
 private static final String TAG = "GlyphManager";
 public static final int MAX_GLYPH_INTENSITY = 4096;
 public static final int DEFAULT_GLYPH_INTENSITY = 2500;

 public Common() {}
 
 static String getAppKey(Context context) {
     String result = null;

     try {
         result = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData.getString("NothingKey");
     } catch (PackageManager.NameNotFoundException var3) {
         PackageManager.NameNotFoundException e = var3;
         Log.e(TAG, e.getMessage());
     }

     return result;
 }

 static String getTargetDevice(Context context) {
     String result = null;

     try {
         result = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData.getString("TargetDevice");
         if(!Glyph.targetDevices.contains(result)) {
             result = null;
         }
     } catch (PackageManager.NameNotFoundException var3) {
         PackageManager.NameNotFoundException e = var3;
         Log.e(TAG, e.getMessage());
     }

     return result;
 }


 static int getTargetDeviceGlyphChannelSize(String targetDevice) {
     if(isDevice20111(targetDevice)) return Glyph.DEVICE_20111_SIZE;
     else if(isDevice22111(targetDevice)) return Glyph.DEVICE_22111_SIZE;
     else if(isDevice23111(targetDevice)) return Glyph.DEVICE_23111_SIZE;
     else if(isDevice24111(targetDevice)) return Glyph.DEVICE_24111_SIZE;
     else return -1;
 }

 static boolean isDevice20111(String targetDevice) {
     return Glyph.DEVICE_20111.equals(targetDevice);
 }

 static boolean isDevice22111(String targetDevice) {
	 return targetDevice.equals(Glyph.DEVICE_22111) || targetDevice.equals(Glyph.DEVICE_22111_IN);
 }

 static boolean isDevice23111(String targetDevice) {
	 return targetDevice.equals(Glyph.DEVICE_23111) || targetDevice.equals(Glyph.DEVICE_23113);
 }
 
 static boolean isDevice24111(String targetDevice) {
	 return targetDevice.equals(Glyph.DEVICE_24111) || targetDevice.equals(Glyph.DEVICE_24113);
 }

 public static boolean is20111() {
     return isDevice20111(Build.MODEL);
 }

 public static boolean is22111() {
     return isDevice22111(Build.MODEL);
 }

 public static boolean is23111() {
     return isDevice23111(Build.MODEL);
 }
 
 public static boolean is24111() {
     return isDevice24111(Build.MODEL);
 }
}

