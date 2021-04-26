package com.android.helper.utils.media.audio;

import com.android.helper.base.BaseEntity;

/**
 * 音频播放列表的实体类
 */
public class AudioEntity extends BaseEntity {

    private String audio;      // 音频的路径地址
    private String cover;   // 音频对用的图片
    private String name;   // 音频对应的标题
    private String id;      // 音频对应的id

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                "audio='" + audio + '\'' +
                ", cover='" + cover + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
