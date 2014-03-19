package org.kymjs.music.ui.widget;

import org.kymjs.music.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

public class DragListView extends ListView implements OnScrollListener {

    private float mLastY = -1;
    private Scroller mScroller;
    private OnScrollListener mScrollListener;

    private DragListViewListener mListViewListener;

    // 头部
    private DragListViewHeader mHeaderView;
    // 依靠它计算头部的显示高度
    private RelativeLayout mHeaderViewContent;
    private TextView mHeaderTimeView;
    // 头部高度
    private int mHeaderViewHeight;
    // 可以下拉刷新
    private boolean mEnablePullRefresh = true;
    // 正在刷新的状态
    private boolean mPullRefreshing = false;

    // 底部
    private DragListViewFooter mFooterView;
    private boolean mEnablePullLoad;
    private boolean mPullLoading;
    private boolean mIsFooterReady = false;

    // item的总数
    private int mTotalItemCount;

    // 回滚底部或头部
    private int mScrollBack;
    private final static int SCROLLBACK_HEADER = 0;
    private final static int SCROLLBACK_FOOTER = 1;
    // 回滚时间
    private final static int SCROLL_DURATION = 400; // scroll back duration
    // 拉动距离大于50px 加载更多
    private final static int PULL_LOAD_MORE_DELTA = 50;
    private final static float OFFSET_RADIO = 1.8f;

    public DragListView(Context context) {
        super(context);
        initWithContext(context);
    }

    public DragListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    public DragListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
    }

    /**
     * 初始化上下文高度
     * 
     * @param context
     */
    private void initWithContext(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        // 绑定监听
        super.setOnScrollListener(this);

        // 初始化头部
        mHeaderView = new DragListViewHeader(context);
        mHeaderViewContent = (RelativeLayout) mHeaderView
                .findViewById(R.id.pagination_header_content);
        mHeaderTimeView = (TextView) mHeaderView
                .findViewById(R.id.pagination_header_time);
        addHeaderView(mHeaderView);

        // 初始化底部
        mFooterView = new DragListViewFooter(context);

        // 初始化头部高度
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        mHeaderViewHeight = mHeaderViewContent.getHeight();
                        getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        // 确保只加载一次底部
        if (mIsFooterReady == false) {
            mIsFooterReady = true;
            addFooterView(mFooterView);
        }
        super.setAdapter(adapter);
    }

    /**
     * 允许或不允许下拉刷新
     * 
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;
        if (!mEnablePullRefresh) {
            // 不允许
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        } else {
            mHeaderViewContent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 允许或不允许上拉加载更多数据
     * 
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad) {
            // 不允许
            mFooterView.hide();
            mFooterView.setOnClickListener(null);
        } else {
            mPullLoading = false;
            mFooterView.show();
            mFooterView.setState(DragListViewFooter.STATE_NORMAL);
            // 上拉或点击最后一行 都可以加载更多
            mFooterView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    /**
     * 停止刷新，重置头部
     */
    public void stopRefresh() {
        if (mPullRefreshing == true) {
            mPullRefreshing = false;
            resetHeaderHeight();
        }
    }

    /**
     * 停止加载更多，重置底部
     */
    public void stopLoadMore() {
        if (mPullLoading == true) {
            mPullLoading = false;
            mFooterView.setState(DragListViewFooter.STATE_NORMAL);
        }
    }

    /**
     * 设置刷新时间
     * 
     * @param time
     */
    public void setRefreshTime(String time) {
        mHeaderTimeView.setText(time);
    }

    /**
     * 支持滚动
     */
    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnXScrollListener) {
            OnXScrollListener l = (OnXScrollListener) mScrollListener;
            l.onXScrolling(this);
        }
    }

    /**
     * 更新头部高度
     * 
     * @param delta
     */
    private void updateHeaderHeight(float delta) {
        mHeaderView.setVisibleHeight((int) delta
                + mHeaderView.getVisibleHeight());
        if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
            if (mHeaderView.getVisibleHeight() > mHeaderViewHeight) {
                mHeaderView.setState(DragListViewHeader.STATE_READY);
            } else {
                mHeaderView.setState(DragListViewHeader.STATE_NORMAL);
            }
        }
        setSelection(0); // scroll to top each time
    }

    /**
     * 重置头部高度
     */
    private void resetHeaderHeight() {
        int height = mHeaderView.getVisibleHeight();
        if (height == 0) {
            return;
        }
        // 正在刷新，或头部没有完全显示，不做任务动作
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        // 默认高度
        int finalHeight = 0;
        // 若正在刷新，头部显示完全高度
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }
        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height,
                SCROLL_DURATION);
        invalidate();
    }

    /**
     * 更新底部高度
     * 
     * @param delta
     */
    private void updateFooterHeight(float delta) {
        int height = mFooterView.getBottomMargin() + (int) delta;
        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) {
                // 上拉足够高时，加载更多
                mFooterView.setState(DragListViewFooter.STATE_READY);
            } else {
                mFooterView.setState(DragListViewFooter.STATE_NORMAL);
            }
        }
        mFooterView.setBottomMargin(height);
    }

    /**
     * 重置底部高度
     */
    private void resetFooterHeight() {
        int bottomMargin = mFooterView.getBottomMargin();
        if (bottomMargin > 0) {
            mScrollBack = SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
                    SCROLL_DURATION);
            invalidate();
        }
    }

    /**
     * 加载更多
     */
    private void startLoadMore() {
        mPullLoading = true;
        mFooterView.setState(DragListViewFooter.STATE_LOADING);
        if (mListViewListener != null) {
            mListViewListener.onLoadMore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (getFirstVisiblePosition() == 0
                        && (mHeaderView.getVisibleHeight() > 0 || deltaY > 0)) {
                    // 第一项正在显示，标题显示或拉下来。
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                    invokeOnScrolling();
                } else if (getLastVisiblePosition() == mTotalItemCount - 1
                        && (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
                    // 最后一项，已经拉起或想拉起来。
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
                break;
            default:
                mLastY = -1; // reset
                if (getFirstVisiblePosition() == 0) {
                    // 调用刷新
                    if (mEnablePullRefresh
                            && mHeaderView.getVisibleHeight() > mHeaderViewHeight) {
                        mPullRefreshing = true;
                        mHeaderView
                                .setState(DragListViewHeader.STATE_REFRESHING);
                        if (mListViewListener != null) {
                            mListViewListener.onRefresh();
                        }
                    }
                    resetHeaderHeight();
                } else if (getLastVisiblePosition() == mTotalItemCount - 1) {
                    // 调用加载更多
                    if (mEnablePullLoad
                            && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
                        startLoadMore();
                    }
                    resetFooterHeight();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                mHeaderView.setVisibleHeight(mScroller.getCurrY());
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }
            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    /**
     * 滚动状态改变
     */
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        // 发送给自己的监听器
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }
    }

    /**
     * 设置自己的监听器
     */
    public void setDragListViewListener(DragListViewListener l) {
        mListViewListener = l;
    }

    /**
     * 它将调用onxscrolling当页眉/页脚退回。
     */
    public interface OnXScrollListener extends OnScrollListener {
        public void onXScrolling(View view);
    }

    /**
     * 包含刷新和加载更多地接口方法
     */
    public interface DragListViewListener {
        public void onRefresh();

        public void onLoadMore();
    }
}
