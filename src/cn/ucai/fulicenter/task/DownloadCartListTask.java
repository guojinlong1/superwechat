package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.Contact;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/5/23.
 */
public class DownloadCartListTask extends BaseActivity {
    private static final String TAG = DownloadCartListTask.class.getName();
    Context mContext;
    String username;
    String path;

    int pageId;
    int pageSize;

    int listSize;
    ArrayList<CartBean> list;


    public DownloadCartListTask(Context mContext,int pageId,int pageSize) {
        this.mContext = mContext;
        this.username = FuLiCenterApplication.getInstance().getUserName();
        this.pageId = pageId;
        this.pageSize = pageSize;
        initPath();
    }

    private void initPath() {
        try {
            path = new ApiParams()
                    .with(I.Cart.USER_NAME, username)
                    .with(I.PAGE_ID,pageId+"")
                    .with(I.PAGE_SIZE,pageSize+"")
                    .getRequestUrl(I.REQUEST_FIND_CARTS);
            Log.e(TAG,path.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute(){
        executeRequest(new GsonRequest<CartBean[]>(path, CartBean[].class,
                responseDownloadCartListTaskListener(), errorListener()));

    }

    private Response.Listener<CartBean[]> responseDownloadCartListTaskListener() {
        return new Response.Listener<CartBean[]>() {
            @Override
            public void onResponse(CartBean[] cartBean) {
                Log.e(TAG, "DownloadContactList,contacts=" + cartBean);
                if (cartBean != null) {
                    Log.e(TAG, "DownloadContactList,contacts.size=" + cartBean.length);
                    list = Utils.array2List(cartBean);
                    try {
                        for(CartBean c :list){
                                path = new ApiParams()
                                        .with(D.NewGood.KEY_GOODS_ID,c.getGoodsId()+"")
                                        .getRequestUrl(I.REQUEST_FIND_GOOD_DETAILS);
                            Log.e(TAG,path.toString());
                           executeRequest(new GsonRequest<GoodDetailsBean>(path,GoodDetailsBean.class,
                                   responseDownloadDetailListener(c),errorListener()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private Response.Listener<GoodDetailsBean> responseDownloadDetailListener(final CartBean c) {
        return new Response.Listener<GoodDetailsBean>() {
            @Override
            public void onResponse(GoodDetailsBean goodDetailsBean) {
                listSize++;
                Log.e(TAG,"listSize:"+listSize);
                if (goodDetailsBean!=null){
                    ArrayList carList = FuLiCenterApplication.getInstance().getCartList();
                    c.setGoods(goodDetailsBean);
                    if(!carList.contains(c)){
                        carList.add(c);
                    }
                }
                if(listSize==list.size()){
                    mContext.sendStickyBroadcast(new Intent("update_car_list"));
                }
            }
        };
    }
}
