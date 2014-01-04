package net.kymjs.music.adapter;

import java.util.ArrayList;
import java.util.List;

import net.kymjs.music.Config;
import net.kymjs.music.R;
import net.kymjs.music.bean.Music;
import net.kymjs.music.utils.ListData;
import net.tsz.afinal.FinalDb;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 本地音乐ListView适配器
 * 
 * @author kymjs
 */
public class MyMusicAdapter extends AbsPlayListAdapter {
    private Context mContext;
    private int currentPager;
    private List<Music> datas = null;

    public MyMusicAdapter(Context context, int current) {
        super(current);
        mContext = context;
        this.currentPager = current;
        datas = ListData.getLocalList(context);
        if (datas == null) {
            datas = new ArrayList<Music>();
        }
    }

    @Override
    public void refresh() {
        datas = ListData.getCollectList(mContext);
        notifyDataSetChanged();
    }

    @Override
    public int getCurrentPager() {
        return currentPager;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int arg0) {
        return datas.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return datas.get(position).getId();
    }

    static class ViewHolder {
        TextView tv_title;
        TextView tv_artist;
        ImageView img_collect;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View
                    .inflate(mContext, R.layout.list_item_music, null);
            holder = new ViewHolder();
            holder.tv_title = (TextView) convertView
                    .findViewById(R.id.list_item_title);
            holder.tv_artist = (TextView) convertView
                    .findViewById(R.id.list_item_artist);
            holder.img_collect = (ImageView) convertView
                    .findViewById(R.id.list_img_button);
            holder.img_collect.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    FinalDb db = FinalDb.create(mContext, Config.DB_NAME,
                            Config.isDebug);
                    Music music = datas.get(position);
                    if (isCollect(position)) {
                        music.setCollect(0);
                        ((ImageView) v)
                                .setImageResource(R.drawable.selector_adp_notcollect);
                    } else {
                        music.setCollect(1);
                        ((ImageView) v)
                                .setImageResource(R.drawable.selector_adp_collect);
                    }
                    db.update(music, "id = '" + music.getId() + "'");
                    Config.changeCollectInfo = true;
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.img_collect.setTag(position);
        holder.tv_title.setText(datas.get(position).getTitle());
        holder.tv_artist.setText(datas.get(position).getArtist());
        if (isCollect(position)) {
            holder.img_collect
                    .setImageResource(R.drawable.selector_adp_collect);
        } else {
            holder.img_collect
                    .setImageResource(R.drawable.selector_adp_notcollect);
        }
        return convertView;
    }

    /*
     * 判断歌曲是否已经被收藏
     */
    private boolean isCollect(int position) {
        return datas.get(position).getCollect() != 0;
    }
}
