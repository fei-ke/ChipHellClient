
package com.fei_ke.chiphellclient.bean;

import java.util.List;

/**
 * 版块分组
 * 
 * @author fei-ke
 * @2014-6-14
 */
public class PlateGroup extends BaseBean {
    String gid;
    String title;
    List<Plate> plates;

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Plate> getPlates() {
        return plates;
    }

    public void setPlates(List<Plate> plates) {
        this.plates = plates;
    }

    @Override
    public String toString() {
        return "PlateGroup [gid=" + gid + ", title=" + title + ", plates=" + plates + "]";
    }

}
