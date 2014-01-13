package net.kymjs.music.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

import net.kymjs.music.AppLog;
import net.kymjs.music.Config;
import net.kymjs.music.bean.Music;
import net.kymjs.music.parser.ParserMusicXML;
import net.kymjs.music.utils.UIHelper;
import net.tsz.afinal.FinalDb;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;

import android.app.IntentService;
import android.content.Intent;

/**
 * 用于下载歌曲的网络信息（xml文件）
 * 
 * @author kymjs
 */
public class DownMusicInfo extends IntentService {

    public DownMusicInfo() {
        super("MusicDownService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Music music = (Music) intent.getSerializableExtra("music");
        downUrlInfo(music);
    }

    /**
     * 下载歌曲相应信息的xml文件
     * 
     * @param musicName
     * @param artist
     */
    private void downUrlInfo(Music music) {
        StringBuilder xml = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        // 读取超时
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                5000);
        httpClient.getParams().setParameter(
                CoreProtocolPNames.HTTP_CONTENT_CHARSET, "utf-8");

        String url = "http://box.zhangmen.baidu.com/x?op=12&count=1&title="
                + music.getTitle().replace(" ", "") + "$$"
                + music.getArtist().replace(" ", "") + "$$$$";
        // try {
        // url = urlEncode(url.trim(), "utf-8");
        // } catch (UnsupportedEncodingException e2) {
        // e2.printStackTrace();
        // }
        url = url.trim();
        HttpGet get = new HttpGet(url);
        BufferedReader br = null;
        try {
            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                br = new BufferedReader(new InputStreamReader(
                        entity.getContent()));
                String line = null;
                while ((line = br.readLine()) != null) {
                    xml.append(line);
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 数据获取到，开始解析
        AppLog.kymjs(getClass() + "-------网络请求：" + xml.toString());
        music = ParserMusicXML.ParserMusic(music, xml.toString());
        if ("0000".equals(music.getlrcId())) {
            UIHelper.toast(this, "歌词没有找到");
        } else {
            // 下载完成，发送广播
            Intent downxml = new Intent(Config.RECEIVER_DOWNLOAD_XML);
            downxml.putExtra("music", (Serializable) music);
            sendBroadcast(downxml);
            // 更新数据库中数据
            FinalDb db = FinalDb.create(this);
            db.update(music, "id = '" + music.getId() + "'");
        }
    }
}
