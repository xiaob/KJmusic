package net.kymjs.music.ui.fragment;

import net.kymjs.music.R;
import net.kymjs.music.adapter.LrcListAdapter;
import net.kymjs.music.ui.Main;
import net.kymjs.music.ui.widget.TabLayout;
import net.kymjs.music.ui.widget.TabLayout.OnViewChangeListener;
import net.kymjs.music.utils.Player;
import net.kymjs.music.utils.UIHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

    // 底部栏控件
    private ImageView mImgPlay;
    private ImageView mImgPrevious;
    private ImageView mImgNext;

    // 播放列表部分
    private ListView mPlayList;
    private LrcListAdapter adapter;

    // 歌词main部分
    private TextView mMusicTitle;
    private TextView mMusicArtist;
    private Button mBtnCollect;
    private Button mBtnShared;

    @Override
    public View setView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        View view = inflater.inflate(R.layout.frag_lyric, container, false);
        return view;
    }

    @Override
    public void initWidget(View parentView) {
        initScrollLayout(parentView);
        bottomBar = parentView.findViewById(R.id.lrc_bottom);
        bottomBar.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mImgPlay = (ImageView) parentView.findViewById(R.id.lrc_btn_play);
        mImgPrevious = (ImageView) parentView.findViewById(R.id.lrc_btn_prev);
        mImgNext = (ImageView) parentView.findViewById(R.id.lrc_btn_next);
        mImgPlay.setOnClickListener(this);
        mImgPrevious.setOnClickListener(this);
        mImgNext.setOnClickListener(this);
        ((Main) getActivity()).getResideMenu().addIgnoredView(mScrollLayout);
        initPlayList(parentView);

        mMusicTitle = (TextView) parentView.findViewById(R.id.lrc_main_title);
        mMusicTitle.setText(Player.getPlayer().getMusic().getTitle());
        mMusicArtist = (TextView) parentView.findViewById(R.id.lrc_main_artist);
        mMusicArtist.setText(Player.getPlayer().getMusic().getArtist());
        mBtnCollect = (Button) parentView.findViewById(R.id.lrc_main_collect);
        mBtnShared = (Button) parentView.findViewById(R.id.lrc_main_share);
        mBtnCollect.setOnClickListener(this);
        mBtnShared.setOnClickListener(this);
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
                ((Main) getActivity()).mPlayersService.play(Player.getPlayer()
                        .getList(), position);
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
            UIHelper.toast("点击收藏");
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
        mMusicTitle.setText(Player.getPlayer().getMusic().getTitle());
        mMusicArtist.setText(Player.getPlayer().getMusic().getArtist());
        mBtnCollect.setBackgroundResource(getBtnCollectBg(Player.getPlayer()
                .getMusic().getCollect() != 0));
    }

    // 获取收藏按钮背景
    private int getBtnCollectBg(boolean isCollect) {
        return isCollect ? R.drawable.selector_lrc_collected
                : R.drawable.selector_lrc_collect;
    }
}
