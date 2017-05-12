package com.viintro.Viintro.Model;

import org.json.JSONObject;

/**
 * Created by hasai on 23/02/17.
 */

public class Video extends JSONObject{

    private String type;
    private String source_default;
    private String source_hls;
    private String source_mpd;
    private String thumbnail;
    private String public_id;
    private String cdn_id;
    private String created_at;
    private String updated_at;

    public String getCdn_id() {
        return cdn_id;
    }

    public void setCdn_id(String cdn_id) {
        this.cdn_id = cdn_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource_default() {
        return source_default;
    }

    public void setSource_default(String source_default) {
        this.source_default = source_default;
    }

    public String getSource_hls() {
        return source_hls;
    }

    public void setSource_hls(String source_hls) {
        this.source_hls = source_hls;
    }

    public String getSource_mpd() {
        return source_mpd;
    }

    public void setSource_mpd(String source_mpd) {
        this.source_mpd = source_mpd;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPublic_id() {
        return public_id;
    }

    public void setPublic_id(String public_id) {
        this.public_id = public_id;
    }
}