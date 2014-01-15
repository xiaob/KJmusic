package net.kymjs.music.ui;

import net.kymjs.music.AppManager;
import net.kymjs.music.Config;
import net.kymjs.music.R;
import net.kymjs.music.service.ScanMusic;
import net.kymjs.music.ui.widget.ScrollLayout;
import net.kymjs.music.ui.widget.ScrollLayout.OnViewChangeListener;
import net.kymjs.music.utils.PreferenceHelper;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 首次安装时进入的欢迎界面
 * 
 * @author kymjs
 * 
 */
public class FirstInstallActivity extends BaseActivity implements
        OnViewChangeListener {
    private LinearLayout pointLayout;
    private ScrollLayout scrollLayout;
    private Button mBtnStart;
    private int count;
    private ImageView[] imgs;
    private int currentItem;

    @Override
    public void initWidget() {
        setContentView(R.layout.aty_welcome_first);
        pointLayout = (LinearLayout) findViewById(R.id.pointLayout);
        scrollLayout = (ScrollLayout) findViewById(R.id.scrollLayout);
        mBtnStart = (Button) findViewById(R.id.startBtn);
        count = scrollLayout.getChildCount();
        imgs = new ImageView[count];
        for (int i = 0; i < count; i++) {
            imgs[i] = (ImageView) pointLayout.getChildAt(i);
            imgs[i].setEnabled(true);
            imgs[i].setTag(i);
        }
        currentItem = 0;
        imgs[currentItem].setEnabled(false);
        scrollLayout.setOnViewChangeLintener(this);
        mBtnStart.setOnClickListener(this);
        
        loadRes();
        writeLog();
    }

    // 设置当前点
    @Override
    public void onViewChange(int postion) {
        if (postion < 0 || postion > count - 1 || currentItem == postion) {
            return;
        }
        imgs[currentItem].setEnabled(true);
        imgs[postion].setEnabled(false);
        currentItem = postion;
    }

    @Override
    public void widgetClick(View v) {
        startActivity(new Intent(this, Main.class));
        AppManager.getAppManager().finishActivity();
    }

    /**
     * 扫描本地歌曲
     */
    private void loadRes() {
        Intent it = new Intent();
        it.setClass(this, ScanMusic.class);
        startService(it);
    }

    /**
     * 写入本地记录
     */
    private void writeLog() {
        PreferenceHelper.write(this, Config.FIRSTINSTALL_FILE,
                Config.FIRSTINSTALL_KEY, true);
    }
}
