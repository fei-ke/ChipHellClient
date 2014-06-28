
package com.fei_ke.chiphellclient.bean;

/**
 * 用户
 * 
 * @author fei-ke
 * @2014-6-16
 */
public class User {
    private String avatarUrl = "";
    private String name = "登录";
    private String info = "";
    private String formHash;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "User [avatarUrl=" + avatarUrl + ", name=" + name + ", info=" + info + "]";
    }

}
