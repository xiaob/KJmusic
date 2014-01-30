package org.kymjs.music.utils;

import org.kymjs.music.AppManager;

import android.content.Context;
import android.widget.Toast;

/**
 * 应用程序UI相关工具类
 * 
 * @author kymjs
 * 
 */
public class UIHelper {
    static Toast toast = null;

    public static void toast(String msg) {
        toast = Toast.makeText(AppManager.getAppManager().currentActivity(),
                msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void toast(Context context, String msg) {
        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
