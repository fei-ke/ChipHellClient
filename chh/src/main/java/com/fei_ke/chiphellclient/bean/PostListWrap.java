package com.fei_ke.chiphellclient.bean;

import java.util.List;

/**
 * Created by fei-ke on 2014/11/2.
 */
public class PostListWrap extends BaseBean {
    private String title;
    private List<Post> posts;
    private int totalPage;
    private int curPage;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int size() {
        return posts != null ? posts.size() : 0;
    }
}
