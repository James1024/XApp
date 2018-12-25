package colin.com.common.utils;

import android.os.Environment;

/**
 * @author wanglr
 * @date 2018/11/28
 */
public class SdcardUtils {

    public static String getSDcardPath(){

        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getAppLocalPath(){

        return getSDcardPath()+"/_xapp/";
    }
}
