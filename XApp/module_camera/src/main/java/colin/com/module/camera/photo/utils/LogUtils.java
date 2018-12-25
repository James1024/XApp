package colin.com.module.camera.photo.utils;

import android.util.Log;

import colin.com.module.camera.BuildConfig;


/**
 * @author nie yunlong
 * @description
 * @date 2018/10/30
 */
public class LogUtils {

    public static void log(String hintMsg) {
        if (BuildConfig.DEBUG) {
            Log.e("Camera1Api", hintMsg);
        }
    }

}
