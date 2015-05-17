package com.fei_ke.chiphellclient.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 杨金阳 on 2015/5/17.
 */
public class AppUpdate {
    private String name;
    private String version;//versionCode
    private String changelog;
    private String versionShort;//versionName
    private String installUrl;//apk url
    private String update_url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public int getVersionCode() {
        return Integer.valueOf(version);
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public String getVersionShort() {
        return versionShort;
    }

    public void setVersionShort(String versionShort) {
        this.versionShort = versionShort;
    }

    public String getInstallUrl() {
        return installUrl;
    }

    public void setInstallUrl(String installUrl) {
        this.installUrl = installUrl;
    }

    public String getUpdate_url() {
        return update_url;
    }

    public void setUpdate_url(String update_url) {
        this.update_url = update_url;
    }

    public AppUpdate fromJson(String json) {
        try {
            JSONObject jo = new JSONObject(json);
            this.name = jo.getString("name");
            this.version = jo.getString("version");
            this.changelog = jo.getString("changelog");
            this.versionShort = jo.getString("versionShort");
            this.installUrl = jo.getString("installUrl");
            this.update_url = jo.getString("update_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public String toString() {
        return "AppUpdate{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", changelog='" + changelog + '\'' +
                ", versionShort='" + versionShort + '\'' +
                ", installUrl='" + installUrl + '\'' +
                ", update_url='" + update_url + '\'' +
                '}';
    }
}
