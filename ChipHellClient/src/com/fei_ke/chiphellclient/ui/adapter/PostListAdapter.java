
package com.fei_ke.chiphellclient.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fei_ke.chiphellclient.bean.Post;
import com.fei_ke.chiphellclient.ui.customviews.PostItemView;

import java.util.LinkedList;
import java.util.List;

/**
 * 回帖列表适配器
 * 
 * @author fei-ke
 * @2014-6-15
 */
public class PostListAdapter extends BaseAdapter {
    private List<Post> mPosts;

    @Override
    public int getCount() {
        return mPosts == null ? 0 : mPosts.size();
    }

    @Override
    public Post getItem(int position) {
        return mPosts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PostItemView postItemView = null;
        if (convertView == null) {
            postItemView = PostItemView.newInstance(parent.getContext());
        } else {
            postItemView = (PostItemView) convertView;
        }
        Post post = getItem(position);
        postItemView.bindValue(post, position == 0);
        return postItemView;
    }

    /**
     * @param posts
     * @return 是否有新数据加载
     */
    public boolean update(List<Post> newPosts) {
        if (mPosts == null) {
            mPosts = new LinkedList<Post>();
        }
        if (mPosts.size() == 0) {
            mPosts.addAll(newPosts);
            notifyDataSetChanged();
            return true;
        }
        int oldSize = getCount();
        // i是老的，j是新的
        for (int i = 0, j = 0; j < newPosts.size(); i++) {
            Post newPost = newPosts.get(j);
            if (i < mPosts.size()) {
                Post oldPost = mPosts.get(i);
                if (oldPost.getAuthi().equals(newPost.getAuthi())) {
                    mPosts.remove(i);
                    mPosts.add(i, newPost);
                    j++;
                }
            } else {
                mPosts.add(newPost);
                j++;
            }

        }
        notifyDataSetChanged();
        return oldSize != getCount();
    }

    public List<Post> getPosts() {
        return mPosts;
    }

    public void clear() {
        if (mPosts != null) {
            mPosts.clear();
        }
    }

}
