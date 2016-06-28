package cn.ucai.fulicenter.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CollectActivity;
import cn.ucai.fulicenter.activity.SettingsActivity;
import cn.ucai.fulicenter.task.DownLoadCollectionCountTask;
import cn.ucai.fulicenter.utils.UserUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalCenterFragment extends Fragment {

    Context mContext;


    //资源文件
    private int[] pic_path = {R.drawable.order_list1,
                                R.drawable.order_list2,
                                R.drawable.order_list3,
                                R.drawable.order_list4,
                                R.drawable.order_list5};
    NetworkImageView mivUserAvatar;
    TextView mtvUserName;
    TextView mtvCollectCount;
    TextView mtvSetting;
    ImageView mivMessage;
    LinearLayout mLinearLayout;
    RelativeLayout mLayoutCenterUserInfo;

    int mCollectCOunt;

    MyClickListener mMyClickListener;


    public PersonalCenterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        View view = inflater.inflate(R.layout.fragment_personal_center, container, false);
        initView(view);
        initData();
        setListener();
        return view;
    }

    private void setListener() {
        registerCollectCountChangedListener();
        registerUpdateUserReceiver();
        mMyClickListener = new MyClickListener();
        mtvSetting.setOnClickListener(mMyClickListener);
        mLayoutCenterUserInfo.setOnClickListener(mMyClickListener);
        mLinearLayout.setOnClickListener(mMyClickListener);
    }

    class MyClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_personal_center_settings:
                case R.id.rl_personal_center:
                    startActivity(new Intent(mContext, SettingsActivity.class));
                    break;
                case R.id.ll_collect:
                    startActivity(new Intent(mContext, CollectActivity.class));
                    break;
            }
        }
    }

    private void initData() {
        mCollectCOunt = FuLiCenterApplication.getInstance().getCollectCount();
        mtvCollectCount.setText(""+mCollectCOunt);
        if(FuLiCenterApplication.getInstance().getUser()!=null){
            UserUtils.setCurrentUserAvatar(mivUserAvatar);
            UserUtils.setCurrentUserBeanNick(mtvUserName);
        }
    }

    private void initView(View layout) {
        mivUserAvatar = (NetworkImageView) layout.findViewById(R.id.iv_user_avatar1);
        mtvUserName = (TextView) layout.findViewById(R.id.tv_user_name1);
        mLinearLayout = (LinearLayout) layout.findViewById(R.id.ll_collect);
        mtvCollectCount = (TextView) layout.findViewById(R.id.tvCollectCount);
        mtvSetting = (TextView) layout.findViewById(R.id.tv_personal_center_settings);
        mivMessage = (ImageView) layout.findViewById(R.id.iv_personal_center_msg);
        mLayoutCenterUserInfo = (RelativeLayout) layout.findViewById(R.id.rl_personal_center);
        
        initOrderList(layout);
    }

    private void initOrderList(View layout) {
        //显示GridView的界面
        GridView mOrderList = (GridView) layout.findViewById(R.id.center_user_order_list);
        ArrayList<HashMap<String,Object>> imageList = new ArrayList<HashMap<String,Object>>();

        //使用HasMap将图片添加到一个数组中，注意是HasMap<String,Object>类型的，因为装到map中的图片要是资源Id,而不是资源本身
        //如果是用findViewById(R.drawable.image)这样把真正的图片取出来，放到map中是无法显示的
        HashMap<String,Object> map1 = new HashMap<String,Object>();
        map1.put("image",R.drawable.order_list1);
        imageList.add(map1);
        HashMap<String,Object> map2 = new HashMap<String,Object>();
        map2.put("image",R.drawable.order_list2);
        imageList.add(map2);
        HashMap<String,Object> map3 = new HashMap<String,Object>();
        map3.put("image",R.drawable.order_list3);
        imageList.add(map3);
        HashMap<String,Object> map4 = new HashMap<String,Object>();
        map4.put("image",R.drawable.order_list4);
        imageList.add(map4);
        HashMap<String,Object> map5 = new HashMap<String,Object>();
        map5.put("image",R.drawable.order_list5);
        imageList.add(map5);

        SimpleAdapter simpleAdapter = new SimpleAdapter(mContext, imageList, R.layout.simpl_grid_item, new String[]{"image"}, new int[]{R.id.image});
        mOrderList.setAdapter(simpleAdapter);
    }

    class UpdateUserChangedReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
           // new DownLoadCollectionCountTask(mContext).execute();
            initData();
        }
    }

    UpdateUserChangedReceiver mUserReceiver;

    private void registerUpdateUserReceiver(){
        mUserReceiver = new UpdateUserChangedReceiver();
        IntentFilter filter = new IntentFilter("update_collect_count");
        mContext.registerReceiver(mUserReceiver,filter);
    }

    class CollectCountChangedReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    }
    CollectCountChangedReceiver mReciver;
    private void registerCollectCountChangedListener(){
        mReciver = new CollectCountChangedReceiver();
        IntentFilter filter = new IntentFilter("update_collect_count");
        mContext.registerReceiver(mReciver,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mReciver!=null){
            mContext.unregisterReceiver(mReciver);
        }
        if(mUserReceiver!=null){
            mContext.unregisterReceiver(mUserReceiver);
        }
    }
}
