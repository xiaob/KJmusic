package net.kymjs.music.ui;

import net.kymjs.music.AppContext;
import net.kymjs.music.AppManager;
import net.kymjs.music.Config;
import net.kymjs.music.R;
import net.kymjs.music.service.PlayerService;
import net.kymjs.music.service.ScanMusic;
import net.kymjs.music.ui.fragment.LyricFragment;
import net.kymjs.music.ui.fragment.MainFragment;
import net.kymjs.music.ui.widget.ResideMenu;
import net.kymjs.music.ui.widget.ResideMenuItem;
import net.kymjs.music.utils.Player;
import net.kymjs.music.utils.UIHelper;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 应用程序主Activity
 * 
 * @author kymjs
 */
public class Main extends BaseActivity {

    private ResideMenu resideMenu;
    private ResideMenuItem itemDown;
    private ResideMenuItem itemScan;
    private ResideMenuItem itemTimer;
    private ResideMenuItem itemSettings;
    private ResideMenuItem itemQuit;

    /** 音乐播放器服务 */
    public PlayerService mPlayersService;
    private Connection conn = new Connection();
    private MusicChangeReceiver changeReceiver = new MusicChangeReceiver();

    private Button mBtnNext, mBtnPrevious, mBtnPlay;
    private ImageView mImg;
    private TextView mTvTitle, mTvArtist;

    @Override
    public void initWidget() {
        setContentView(R.layout.main);
        setUpMenu();
        changeFragment(new MainFragment());
        initBottonBar();
    }

    /**
     * 初始化侧滑菜单界面控件
     */
    private void setUpMenu() {
        // 附加到当前activity
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_background_cool);
        resideMenu.attachToActivity(this);
        // 创建菜单项
        itemDown = new ResideMenuItem(this, R.drawable.icon_down, "下载管理");
        itemScan = new ResideMenuItem(this, R.drawable.icon_scan, "扫描音乐");
        itemTimer = new ResideMenuItem(this, R.drawable.icon_timer, "定时音乐");
        itemSettings = new ResideMenuItem(this, R.drawable.icon_setup, "系统设置");
        itemQuit = new ResideMenuItem(this, R.drawable.icon_quit, "下次再来");
        MenuClickListener listener = new MenuClickListener();
        itemDown.setOnClickListener(listener);
        itemScan.setOnClickListener(listener);
        itemTimer.setOnClickListener(listener);
        itemSettings.setOnClickListener(listener);
        itemQuit.setOnClickListener(listener);
        resideMenu.addMenuItem(itemScan);
        resideMenu.addMenuItem(itemDown);
        resideMenu.addMenuItem(itemTimer);
        resideMenu.addMenuItem(itemSettings);
        resideMenu.addMenuItem(itemQuit);
    }

    /**
     * 初始化底部栏
     */
    private void initBottonBar() {
        findViewById(R.id.bottom_bar).setOnClickListener(this);
        mImg = (ImageView) findViewById(R.id.bottom_img_image);
        mImg.setBackgroundResource(R.drawable.img_noplaying);

        mTvTitle = (TextView) findViewById(R.id.bottom_tv_title);
        mTvArtist = (TextView) findViewById(R.id.bottom_tv_artist);
        mTvTitle.setText(Config.TITLE);
        mTvArtist.setText(Config.ARTIST);

        mBtnNext = (Button) findViewById(R.id.bottom_btn_next);
        mBtnPrevious = (Button) findViewById(R.id.bottom_btn_previous);
        mBtnPlay = (Button) findViewById(R.id.bottom_btn_play);
        mBtnNext.setOnClickListener(this);
        mBtnPrevious.setOnClickListener(this);
        mBtnPlay.setOnClickListener(this);
    }

    /**
     * 刷新底部栏
     */
    private void refreshBottomBar() {
        Player player = Player.getPlayer();
        switch (player.getPlaying()) {
        case Config.PLAYING_PAUSE:
            mImg.setImageResource(R.drawable.img_playing);
            mBtnPlay.setBackgroundResource(R.drawable.selector_btn_play);
            mTvTitle.setText(player.getMusic().getTitle());
            mTvArtist.setText(player.getMusic().getArtist());
            break;
        case Config.PLAYING_PLAY:
            mImg.setImageResource(R.drawable.img_playing);
            mBtnPlay.setBackgroundResource(R.drawable.selector_btn_pause);
            mTvTitle.setText(player.getMusic().getTitle());
            mTvArtist.setText(player.getMusic().getArtist());
            break;
        case Config.PLAYING_STOP:
            mImg.setImageResource(R.drawable.img_noplaying);
            mBtnPlay.setBackgroundResource(R.drawable.selector_btn_play);
            mTvTitle.setText(Config.TITLE);
            mTvArtist.setText(Config.ARTIST);
            break;
        }
    }

    @Override
    public void widgetClick(View v) {
        Player player = Player.getPlayer();
        switch (v.getId()) {
        case R.id.bottom_bar:
            changeFragment(new LyricFragment());
            break;
        case R.id.bottom_btn_next:
            mPlayersService.next();
            break;
        case R.id.bottom_btn_previous:
            mPlayersService.previous();
            break;
        case R.id.bottom_btn_play:
            if (player.getPlaying() == Config.PLAYING_PLAY) {
                mPlayersService.pause();
                v.setBackgroundResource(R.drawable.selector_btn_play);
            } else if (player.getPlaying() == Config.PLAYING_PAUSE) {
                mPlayersService.replay();
                v.setBackgroundResource(R.drawable.selector_btn_pause);
            } else {
                mPlayersService.play();
                v.setBackgroundResource(R.drawable.selector_btn_pause);
            }
            break;
        }
    }

    class MenuClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == itemDown) {
                // changeFragment(new HomeFragment());
            } else if (v == itemScan) {
                startService(new Intent(Main.this, ScanMusic.class));
            } else if (v == itemTimer) {
                // changeFragment(new CalendarFragment());
            } else if (v == itemSettings) {
                // changeFragment(new SettingsFragment());
            } else if (v == itemQuit) {
                AppManager.getAppManager().AppExit(Main.this);
            }
            resideMenu.closeMenu();
        }
    }

    private void changeFragment(Fragment targetFragment) {
        resideMenu.clearIgnoredViewList();// 清空不拦截触摸事件的控件（界面已经被替换）
        getFragmentManager().beginTransaction()
        // 使用传入的fragment替换主界面的fragment
                .replace(R.id.main_fragment, targetFragment, "fragment")
                // 设置动画样式
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                // 添加到返回栈（使用户按下返回键时可以返回上一个界面）
                // .addToBackStack(null)
                // 提交
                .commit();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.onInterceptTouchEvent(ev)
                || super.dispatchTouchEvent(ev);
    }

    public ResideMenu getResideMenu() {
        return resideMenu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent serviceIntent = new Intent(this, PlayerService.class);
        this.bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.RECEIVER_MUSIC_CHANGE);
        registerReceiver(changeReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshBottomBar();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(changeReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unbindService(conn);
    }

    /**
     * ServiceConnection实现类
     */
    class Connection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            UIHelper.toast("呀，音乐播放失败，退出再进试试");
            mPlayersService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayersService = ((PlayerService.LocalPlayer) service)
                    .getService();
            if (mPlayersService != null) {
                ((AppContext) getApplication()).mPlayersService = mPlayersService;
            }
        }
    }

    /**
     * BroadcastReceiver类
     */
    public class MusicChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Player.getPlayer().getPlaying() != Config.PLAYING_STOP) {
                refreshBottomBar();
            }
        }
    }
}
