package net.kymjs.music.adapter;

import java.util.ArrayList;
import java.util.List;

import net.kymjs.music.R;
import net.kymjs.music.bean.Music;
import net.kymjs.music.utils.Player;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 歌词界面播放列表适配器
 * 
 * @author kymjs
 */
public class LrcListAdapter extends BaseAdapter {

    private Context context;
    private List<Music> datas;

    public LrcListAdapter(Context context) {
        super();
        this.context = context;
        this.datas = Player.getPlayer().getList();
        if (datas == null) {
            datas = new ArrayList<Music>();
        }
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return datas.get(position).getId();
    }

    static class ViewHolder {
        TextView tv_title;
        TextView tv_artist;
        ImageView img;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder = null;
        if (v == null) {
            v = View.inflate(context, R.layout.list_item_lrclist, null);
            holder = new ViewHolder();
            holder.img = (ImageView) v.findViewById(R.id.list_item_img);
            holder.tv_title = (TextView) v.findViewById(R.id.list_item_title);
            holder.tv_artist = (TextView) v.findViewById(R.id.list_item_artist);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.tv_title.setText(datas.get(position).getTitle());
        holder.tv_artist.setText(datas.get(position).getArtist());
        holder.img.setImageResource(R.drawable.adp_collect_click);
        return v;
    }

}
