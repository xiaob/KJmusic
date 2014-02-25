package org.kymjs.music.utils;

import org.kymjs.music.AppManager;
import org.kymjs.music.Config;
import org.kymjs.music.R;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;
import android.widget.Toast;

/**
 * 应用程序UI相关工具类
 * 
 * @author kymjs
 * 
 */
public class UIHelper {

    private UIHelper() {
    }

    private static class UIHelperHolder {
        private static final UIHelper instance = new UIHelper();
    }

    public static UIHelper getUIHelper() {
        return UIHelperHolder.instance;
    }

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

    PopupWindow popupWindow = null;

    /**
     * @param context
     *            应用上下文对象
     * @param whichMusic
     *            要操作的歌曲
     * @return 返回一个操作收藏歌曲的PopupWindow
     */
    public PopupWindow getPopupWindow(final Context context) {
        View popView = View.inflate(context, R.layout.pop_collect, null);
        final int maxW = DensityUtils.dip2px(context, Config.Width);
        final int maxH = DensityUtils.dip2px(context, Config.Height);
        popupWindow = new PopupWindow(popView, maxW, maxH);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setTouchInterceptor(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                int utilX = maxW / 4;
                int utilY = maxH;
                if (x > 0 && x <= utilX && y > 0 && y < utilY) { // collect
                } else if (x > utilX && x <= utilX * 2 && y > 0 && y < utilY) { // share
                } else if (x > utilX * 2 && x <= utilX * 3 && y > 0
                        && y < utilY) { // bell
                } else if (x > utilX * 3 && x < utilX * 4 && y > 0 && y < utilY) { // delete
                }
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                return true;
            }
        });
        return popupWindow;
    }
}
