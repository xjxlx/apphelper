package com.android.helper.utils.media.audio;

import com.android.helper.base.BaseEntity;

/**
 * 音频播放列表的实体类
 */
public class AudioEntity extends BaseEntity {

    private String url;      // 音频的路径地址
    private String image;   // 音频对用的图片
    private String title;   // 音频对应的标题
    private String id;      // 音频对应的id

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AudioEntity{" +
                "url='" + url + '\'' +
                ", image='" + image + '\'' +
                ", title='" + title + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
