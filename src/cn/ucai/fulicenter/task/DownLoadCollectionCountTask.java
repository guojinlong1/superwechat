package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.activity.FuLiCenterMainActivity;
import cn.ucai.fulicenter.bean.Contact;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/5/23.
 */
public class DownLoadCollectionCountTask extends BaseActivity {
    private static final String TAG = DownLoadCollectionCountTask.class.getName();
    Context mContext;
    String username;
    String path;
    public DownLoadCollectionCountTask(Context mContext) {
        this.mContext = mContext;
        initPath();
    }

    public DownLoadCollectionCountTask(Context mContext, String username) {
        this.mContext = mContext;
        this.username = username;
        initPath();
    }

    private void initPath() {
        username = FuLiCenterApplication.getInstance().getUserName();
        Log.e(TAG, "username=" + username);
        try {
            path = new ApiParams()
                    .with(I.Collect.USER_NAME,username)
                    .getRequestUrl(I.REQUEST_FIND_COLLECT_COUNT);
            Log.e(TAG, "path=" + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void execute() {
        if(path==null || path.isEmpty()) return;
        executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class, responseDownloadCollectCountTaskListener(), errorListener()));
    }

    private Response.Listener<MessageBean> responseDownloadCollectCountTaskListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                Log.e(TAG,"messageBean="+messageBean);
                if (messageBean.isSuccess()) {
                    String count = messageBean.getMsg();
                    Log.e(TAG,count.toString());
                    FuLiCenterApplication.getInstance().setCollectCount(Integer.parseInt(messageBean.getMsg()));
                    //Log.e(TAG,messageBean.toString());
                }else {
                    Log.e(TAG,"count=0");
                    FuLiCenterApplication.getInstance().setCollectCount(0);
                }
                Intent intent = new Intent("update_collect_count");
                mContext.sendStickyBroadcast(intent);
            }


        };
    }
}
