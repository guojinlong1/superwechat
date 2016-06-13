package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.SuperWeChatApplication;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.Group;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/5/23.
 */
public class DownloadPublicGroupTask extends BaseActivity{
    private static final String TAG = DownloadPublicGroupTask.class.getName();
    Context mcontext;
    String username;
    int page_id;
    int page_size;
    String path;
    ArrayList<Group> groupList;
    ArrayList<Group> list;

    public DownloadPublicGroupTask(Context mcontext, String username, int page_id, int page_size) {
        this.mcontext = mcontext;
        this.username = username;
        this.page_id = page_id;
        this.page_size = page_size;
        initPath();
    }

    private void initPath() {
        if(groupList!=null){
            groupList.clear();
            list.clear();
        }

        try {
            path = new ApiParams()
                    .with(I.User.USER_NAME, username)
                    .with(I.PAGE_ID, page_id+"")
                    .with(I.PAGE_SIZE, page_size+"")
                    .getRequestUrl(I.REQUEST_FIND_PUBLIC_GROUPS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void execute() {
        executeRequest(new GsonRequest<Group[]>(path,Group[].class,
                responseDownloadPublicGroupTaskListener(),errorListener()));
    }

    private Response.Listener<Group[]> responseDownloadPublicGroupTaskListener() {
        return new Response.Listener<Group[]>() {
            @Override
            public void onResponse(Group[] groups) {
                if (groups != null && groups.length > 1) {
                    Log.e("DownloadPublicGroupTask",groups.length+"");
                    groupList =
                            SuperWeChatApplication.getInstance().getPublicGroupList();
                    list = Utils.array2List(groups);
                    for (Group g : list) {
                        if (!groupList.contains(g)) {
                            groupList.add(g);
                        }
                    }
                    Log.e("PublicGroupsActivity",list.size()+"");
                    mcontext.sendStickyBroadcast(new Intent("update_public_group"));
                }
            }
        };
    }


}
