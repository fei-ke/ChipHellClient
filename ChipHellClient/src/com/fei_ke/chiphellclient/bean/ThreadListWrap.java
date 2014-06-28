
package com.fei_ke.chiphellclient.bean;

import java.util.List;

/**
 * 帖子列表第一页可能会包含子版块
 * 
 * @author fei-ke
 *         2014-6-24 下午6:16:46
 */
public class ThreadListWrap extends BaseBean {
    List<Thread> threads;
    List<Plate> plates;

    public List<Thread> getThreads() {
        return threads;
    }

    public void setThreads(List<Thread> threads) {
        this.threads = threads;
    }

    public List<Plate> getPlates() {
        return plates;
    }

    public void setPlates(List<Plate> plates) {
        this.plates = plates;
    }

}
