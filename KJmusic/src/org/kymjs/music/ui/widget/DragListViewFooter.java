package org.kymjs.music.ui.widget;

import org.kymjs.music.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 上拉加载ListView的底部
 */
public class DragListViewFooter extends LinearLayout {
    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_LOADING = 2;

    private View contentView;
    private View progressBar;
    private TextView hintView;

    public DragListViewFooter(Context context) {
        super(context);
        initView(context);
    }

    /**
     * 设置底部组件的显示状态
     * 
     * @param state
     */
    public void setState(int state) {
        hintView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        if (state == STATE_READY) {
            hintView.setVisibility(View.VISIBLE);
        } else if (state == STATE_LOADING) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            hintView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置底边距
     * 
     * @param height
     */
    public void setBottomMargin(int height) {
        if (height < 0) {
            return;
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView
                .getLayoutParams();
        params.bottomMargin = height;
        contentView.setLayoutParams(params);
    }

    /**
     * 获取底边距
     * 
     * @return
     */
    public int getBottomMargin() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView
                .getLayoutParams();
        return params.bottomMargin;
    }

    /**
     * 普通状态
     */
    public void normal() {
        hintView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * 加载状态
     */
    public void loading() {
        hintView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * 没有更多时隐藏底部
     */
    public void hide() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView
                .getLayoutParams();
        params.height = 0;
        contentView.setLayoutParams(params);
    }

    /**
     * 显示底部
     */
    public void show() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView
                .getLayoutParams();
        params.height = LayoutParams.WRAP_CONTENT;
        contentView.setLayoutParams(params);
    }

    /**
     * 初始化底部组件
     * 
     * @param context
     */
    private void initView(Context context) {
        LinearLayout moreView = (LinearLayout) LayoutInflater.from(context)
                .inflate(R.layout.pagination_listview_footer, null);
        addView(moreView);
        moreView.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        contentView = moreView.findViewById(R.id.pagination_footer_content);
        progressBar = moreView.findViewById(R.id.pagination_footer_progressbar);
        hintView = (TextView) moreView
                .findViewById(R.id.pagination_footer_hint_textview);
    }

}
