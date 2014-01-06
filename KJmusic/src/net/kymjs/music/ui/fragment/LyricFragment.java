package net.kymjs.music.ui.fragment;

import net.kymjs.music.Config;
import net.kymjs.music.R;
import net.kymjs.music.adapter.LrcListAdapter;
import net.kymjs.music.bean.Music;
import net.kymjs.music.ui.Main;
import net.kymjs.music.ui.widget.TabLayout;
import net.kymjs.music.ui.widget.TabLayout.OnViewChangeListener;
import net.kymjs.music.utils.Player;
import net.kymjs.music.utils.PreferenceHelper;
import net.kymjs.music.utils.UIHelper;
import net.tsz.afinal.FinalDb;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * 歌词界面
 * 
 * @author kymjs
 */
public class LyricFragment extends BaseFragment {
    // 主体部分的控件
    private TabLayout mScrollLayout;
    private View bottomBar;
    private Button mBtnBack;
    private CheckBox mCboxWordImg;
    private SeekBar mSeekBarMusic;
    private SeekThread mSeekThread = new SeekThread();
    private SeekHandle mSeekHandle = new SeekHandle();
    private Player mPlayer = Player.getPlayer();
    FinalDb db = FinalDb.create(getActivity(), Config.DB_NAME, Config.isDebug);

    // 底部栏控件
    private ImageView mImgPlay;
    private ImageView mImgPrevious;
    private ImageView mImgNext;
    private int loopMode;
    private ImageView mImgLoop;

    // 播放列表部分
    private ListView mPlayList;
    private LrcListAdapter adapter;

    // 歌词main部分
    private TextView mMusicTitle;
    private TextView mMusicArtist;
    private Button mBtnCollect;
    private Button mBtnShared;

    private int[] loopModes = { R.drawable.bt_playing_mode_singlecycle,
            R.drawable.bt_playing_mode_order, R.drawable.bt_playing_mode_cycle,
            R.drawable.bt_playing_mode_shuffle };
    private String[] loopModeStr = { "单曲播放", "单曲循环", "列表播放", "随机播放" };

    @Override
    public View setView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        View view = inflater.inflate(R.layout.frag_lyric, container, false);
        return view;
    }

    @Override
    public void initWidget(View parentView) {
        initScrollLayout(parentView);
        ((Main) getActivity()).getResideMenu().addIgnoredView(mScrollLayout);
        initSeekBar(parentView);
        initBottomBar(parentView);
        initPlayList(parentView);
        initLrcMainView(parentView);

        mBtnBack = (Button) parentView.findViewById(R.id.lrc_btn_back);
        mBtnBack.setOnClickListener(this);
        mCboxWordImg = (CheckBox) parentView
                .findViewById(R.id.lrc_cbox_wordimg);
        mCboxWordImg.setOnClickListener(this);
    }

    /**
     * 初始化歌词界面中心部分
     */
    private void initLrcMainView(View parentView) {
        mMusicTitle = (TextView) parentView.findViewById(R.id.lrc_main_title);
        mMusicTitle.setText(mPlayer.getMusic().getTitle());
        mMusicArtist = (TextView) parentView.findViewById(R.id.lrc_main_artist);
        mMusicArtist.setText(mPlayer.getMusic().getArtist());
        mBtnCollect = (Button) parentView.findViewById(R.id.lrc_main_collect);
        mBtnCollect.setBackgroundResource(getBtnCollectBg(mPlayer.getMusic()
                .getCollect() != 0));
        mBtnShared = (Button) parentView.findViewById(R.id.lrc_main_share);
        mBtnCollect.setOnClickListener(this);
        mBtnShared.setOnClickListener(this);
    }

    /**
     * 初始化歌词界面底部滑动条
     */
    private void initSeekBar(View parentView) {
        mSeekHandle.post(mSeekThread);
        mSeekBarMusic = (SeekBar) parentView
                .findViewById(R.id.lrc_seekbar_music);
        mSeekBarMusic.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPlayer.seekTo(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mPlayer.seekTo(seekBar.getProgress());
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
            }
        });
    }

    /**
     * SeekBar的控制器，随歌曲播放改变
     */
    class SeekThread implements Runnable {
        @Override
        public void run() {
            Message msg = Message.obtain();
            msg.arg1 = mPlayer.getDuration(); // 最大值
            msg.arg2 = mPlayer.getCurrentPosition(); // 进度
            mSeekHandle.sendMessage(msg);
        }
    }

    @SuppressLint("HandlerLeak")
    class SeekHandle extends Handler {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mSeekBarMusic.setMax(msg.arg1);
            mSeekBarMusic.setProgress(msg.arg2);
            // mLrcView.seekLrcToTime(msg.arg2);
            // mTextProg.setText(StringUtils.timeFormat(msg.arg2));
            // mTextTotal.setText(StringUtils.timeFormat(msg.arg1));
            mSeekHandle.post(mSeekThread);
        }
    }

    /**
     * 初始化歌词界面底部栏
     */
    private void initBottomBar(View parentView) {
        bottomBar = parentView.findViewById(R.id.lrc_bottom);
        bottomBar.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mImgPlay = (ImageView) parentView.findViewById(R.id.lrc_btn_play);
        mImgPlay.setImageResource(getBtnPlayBg());
        mImgPrevious = (ImageView) parentView.findViewById(R.id.lrc_btn_prev);
        mImgNext = (ImageView) parentView.findViewById(R.id.lrc_btn_next);
        mImgPlay.setOnClickListener(this);
        mImgPrevious.setOnClickListener(this);
        mImgNext.setOnClickListener(this);
        mImgLoop = (ImageView) parentView.findViewById(R.id.lrc_btn_loop);
        mImgLoop.setImageResource(getImgLoopBg());
        mPlayer.setMode(loopMode);
        mImgLoop.setOnClickListener(this);
    }

    /**
     * 初始化播放列表控件
     */
    private void initPlayList(View parentView) {
        mPlayList = (ListView) parentView.findViewById(R.id.lrc_pager_list);
        adapter = new LrcListAdapter(getActivity());
        mPlayList.setAdapter(adapter);
        mPlayList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                ((Main) getActivity()).mPlayersService.play(mPlayer.getList(),
                        position);
            }
        });
    }

    /**
     * 初始化主体界面
     */
    private void initScrollLayout(View parentView) {
        mScrollLayout = (TabLayout) parentView.findViewById(R.id.lrc_tablayout);
        mScrollLayout.scrollToScreen(2);
        int mScrollChildCount = mScrollLayout.getChildCount();

        final RadioGroup circles = (RadioGroup) parentView
                .findViewById(R.id.lrc_circle_layout);
        for (int i = 0; i < mScrollChildCount; i++) {
            circles.addView(getCircles());
            ((RadioButton) circles.getChildAt(0)).setChecked(true);
        }
        mScrollLayout.SetOnViewChangeListener(new OnViewChangeListener() {
            @Override
            public void OnViewChange(int view) {
                RadioButton circle = (RadioButton) circles.getChildAt(view);
                circle.setChecked(true);
                if (view == 1) {
                    mCboxWordImg.setChecked(true);
                } else if (view == 2) {
                    mCboxWordImg.setChecked(false);
                }
            }
        });
    }

    /**
     * 获取一个"小点"
     */
    private RadioButton getCircles() {
        RadioButton circle = new RadioButton(getActivity());
        int dimen5 = (int) getResources().getDimension(R.dimen.space_5);
        int dimen3 = (int) getResources().getDimension(R.dimen.space_3);
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(dimen5,
                dimen5);
        params.setMargins(dimen3, 0, dimen3, 0);
        circle.setLayoutParams(params);
        circle.setBackgroundResource(R.drawable.selector_rbtn_circle);
        return circle;
    }

    @Override
    public void widgetClick(View parentView) {
        switch (parentView.getId()) {
        case R.id.lrc_main_share:
            UIHelper.toast("点击");
            break;
        case R.id.lrc_main_collect:
            Music music = mPlayer.getMusic();
            music.setCollect((music.getCollect() + 1) % 2);
            mBtnCollect.setBackgroundResource(getBtnCollectBg(music
                    .getCollect() != 0));
            db.update(music, "id = '" + music.getId() + "'");
            Config.changeCollectInfo = true;
            Config.changeMusicInfo = true;
            getActivity().sendBroadcast(
                    new Intent(Config.RECEIVER_MUSIC_SCAN_SUCCESS));
            break;
        case R.id.lrc_btn_back:
            ((Main) getActivity()).wantScroll((Main) getActivity());
            break;
        case R.id.lrc_cbox_wordimg:
            if (mCboxWordImg.isChecked()) {
                mScrollLayout.scrollToScreen(1);
            } else {
                mScrollLayout.scrollToScreen(2);
            }
            break;
        case R.id.lrc_btn_play:
            if (mPlayer.getPlaying() == Config.PLAYING_STOP) {
                ((Main) getActivity()).mPlayersService.play();
            } else if (mPlayer.getPlaying() == Config.PLAYING_PLAY) {
                ((Main) getActivity()).mPlayersService.pause();
            } else {
                ((Main) getActivity()).mPlayersService.replay();
            }
            mImgPlay.setImageResource(getBtnPlayBg());
            break;
        case R.id.lrc_btn_prev:
            ((Main) getActivity()).mPlayersService.previous();
            break;
        case R.id.lrc_btn_next:
            ((Main) getActivity()).mPlayersService.next();
            break;
        case R.id.lrc_btn_loop:
            PreferenceHelper.write(getActivity(), Config.LOOP_MODE_FILE,
                    Config.LOOP_MODE_KEY, loopMode = (loopMode + 1) % 4);
            mPlayer.setMode(loopMode);
            mImgLoop.setImageResource(getImgLoopBg());
            UIHelper.toast(loopModeStr[loopMode]);
            break;
        }
    }

    /**
     * 刷新歌词界面
     */
    public void refreshLrcView() {
        if (adapter != null) {
            adapter.refreshLrcAdapter();
        }
        mMusicTitle.setText(mPlayer.getMusic().getTitle());
        mMusicArtist.setText(mPlayer.getMusic().getArtist());
        mBtnCollect.setBackgroundResource(getBtnCollectBg(mPlayer.getMusic()
                .getCollect() != 0));
        mImgPlay.setImageResource(getBtnPlayBg());
    }

    // 获取收藏按钮背景
    private int getBtnCollectBg(boolean isCollect) {
        return isCollect ? R.drawable.selector_lrc_collected
                : R.drawable.selector_lrc_collect;
    }

    // 获取播放按钮背景
    private int getBtnPlayBg() {
        int background = 0;
        if (mPlayer.getPlaying() == Config.PLAYING_PLAY) {
            background = R.drawable.selector_radio_pause;
        } else {
            background = R.drawable.selector_radio_play;
        }
        return background;
    }

    // 获取循环播放控件背景
    private int getImgLoopBg() {
        loopMode = PreferenceHelper.readInt(getActivity(),
                Config.LOOP_MODE_FILE, Config.LOOP_MODE_KEY,
                Config.MODE_REPEAT_ALL);
        return loopModes[loopMode];
    }
}
