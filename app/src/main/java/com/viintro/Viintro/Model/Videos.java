package com.viintro.Viintro.Model;

/**
 * Created by hasai on 15/03/17.
 */
public class Videos {

    Boolean intro;
    String public_id;
    String video_mpegdash;//mpd
    String video_hls;//m3u8
    String video_mp4;//mp4
    String thumbnail;
    int view_count;


    public Boolean getIntro() {
        return intro;
    }

    public void setIntro(Boolean intro) {
        this.intro = intro;
    }

    public String getPublic_id() {
        return public_id;
    }

    public void setPublic_id(String public_id) {
        this.public_id = public_id;
    }

    public String getVideo_mpegdash() {
        return video_mpegdash;
    }

    public void setVideo_mpegdash(String video_mpegdash) {
        this.video_mpegdash = video_mpegdash;
    }

    public String getVideo_hls() {
        return video_hls;
    }

    public void setVideo_hls(String video_hls) {
        this.video_hls = video_hls;
    }

    public String getVideo_mp4() {
        return video_mp4;
    }

    public void setVideo_mp4(String video_mp4) {
        this.video_mp4 = video_mp4;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }
}
