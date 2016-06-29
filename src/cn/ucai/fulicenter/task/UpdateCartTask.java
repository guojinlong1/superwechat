package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;

/**
 * Created by Administrator on 2016/6/29 0029.
 */
public class UpdateCartTask extends BaseActivity{
    private final static String TAG = UpdateCartTask.class.getName();
    Context mContext;
    CartBean cartBean;
    String path ;
    int action ;

    public UpdateCartTask(Context mContext, CartBean cartBean) {
        this.mContext = mContext;
        this.cartBean = cartBean;
        initPath();
    }

    private void initPath() {
        ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();

            try {
                if(cartList.contains(cartBean)){
                    if(cartBean.getCount()<=0){
                        action =1;
                        path = new ApiParams()
                                .with(I.Cart.ID,cartBean.getId()+"")
                                .getRequestUrl(I.REQUEST_DELETE_CART);
                    }else {
                        action =2;
                        path = new ApiParams()
                                .with(I.Cart.IS_CHECKED,cartBean.isChecked()+"")
                                .with(I.Cart.COUNT,cartBean.getCount()+"")
                                .with(I.Cart.ID,cartBean.getId()+"")
                                .getRequestUrl(I.REQUEST_UPDATE_CART);
                    }
                }else {
                    action =3;
                    path = new ApiParams()
                            .with(I.Cart.USER_NAME,FuLiCenterApplication.getInstance().getUserName())
                            .with(I.Cart.GOODS_ID,cartBean.getGoods().getGoodsId()+"")
                            .with(I.Cart.COUNT,cartBean.getCount()+"")
                            .with(I.Cart.IS_CHECKED,cartBean.isChecked()+"")
                            .getRequestUrl(I.REQUEST_ADD_CART);
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    public void exectue(){
        Log.e(TAG,"path::::::::::"+path.toString());
        executeRequest(new GsonRequest<MessageBean>(path,MessageBean.class,
                responseUpdateCartListener(),errorListener()));
    }

    private Response.Listener<MessageBean> responseUpdateCartListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
                if(messageBean.isSuccess()){
                    switch (action){
                        case 1:
                            cartList.remove(cartBean);
                            break;
                        case 2:
                            cartList.set(cartList.indexOf(cartBean),cartBean);
                            break;
                        case 3:
                            cartBean.setId(Integer.parseInt(messageBean.getMsg()));
                            cartList.add(cartBean);
                            break;
                    }
                    mContext.sendStickyBroadcast(new Intent("update_cart"));
                }
            }
        };
    }
}
