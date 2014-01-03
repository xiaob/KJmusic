package net.kymjs.music.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Table;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 本地音乐文件bean类
 */
@Table(name = "music")
public class Music implements Parcelable, Serializable {
    private static final long serialVersionUID = 1L;

    // @Id(column="id")//设置自定义主键
    private int id;
    private String title;
    private String artist;
    private String path;
    private String size;
    private int collect;

    public Music(int id, String title, String artist, String path, String size,
            int collect) {
        super();
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.size = size;
        this.collect = collect;
    }

    public Music() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCollect() {
        return collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    // 重载构造函数,供内部类使用
    private Music(Parcel in) {
        Bundle bundle = in.readBundle();
        id = bundle.getInt("id");
        title = bundle.getString("title");
        artist = bundle.getString("artist");
        path = bundle.getString("path");
        size = bundle.getString("size");
        collect = bundle.getInt("collect");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putString("title", title);
        bundle.putString("artist", artist);
        bundle.putString("path", path);
        bundle.putString("size", size);
        bundle.putInt("collect", collect);
        out.writeBundle(bundle);
    }

    // 创建一个内部类用来读取数据
    public static final Parcelable.Creator<Music> CREATOR = new Parcelable.Creator<Music>() {
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };
}
