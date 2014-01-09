package net.kymjs.music.utils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

public class MusicDownUtils {
    public static String getLrcXML(String musicName, String artist) {
        String xml = null;
        FinalHttp fh = new FinalHttp();
        AjaxParams params = new AjaxParams();
        params.put("count", 1 + "");
        params.put("op", 12 + "");
        params.put("title", musicName + "$$" + artist + "$$$$");
        fh.get("http://box.zhangmen.baidu.com/x", params,
                new AjaxCallBack<String>() {
                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                    }
                });
        return xml;
    }
}
