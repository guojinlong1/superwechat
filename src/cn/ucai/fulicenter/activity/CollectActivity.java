package cn.ucai.fulicenter.activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CollectAdapter;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.User;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;


/**
 * Created by Administrator on 2016/6/20 0020.
 */
public class CollectActivity extends BaseActivity {
    final static String TAG = CollectActivity.class.getName();

    CollectActivity mContext;
    int pageId = 0;
    String path;
    CollectAdapter mAdapter;
    ArrayList<CollectBean> mCollectList;
    private int action = I.ACTION_DOWNLOAD;

    /**下拉刷新控件
     * */

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    TextView mtvHint;
    GridLayoutManager mGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        mContext = this;
        initView();
        setListener();
        initData();
    }


    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swl_collect);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );

        mtvHint = (TextView) findViewById(R.id.tv_refresh_hint);
        mGridLayoutManager = new GridLayoutManager(mContext,I.COLUM_NUM);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_collect);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mCollectList = new ArrayList<CollectBean>();
        mAdapter = new CollectAdapter(mContext,mCollectList);
        mRecyclerView.setAdapter(mAdapter);
        DisplayUtils.initBackWithTitle(mContext,"宝贝收藏");

    }

    private Response.Listener<CollectBean[]> responseDownloadListener(){
        return new Response.Listener<CollectBean[]>() {
            @Override
            public void onResponse(CollectBean[] collect) {
                Log.e(TAG,collect.toString());
                if(collect!=null){
                    Log.e(TAG,collect.toString());
                    mAdapter.setMore(true);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mtvHint.setVisibility(View.GONE);
                    mAdapter.setFooterText(getResources().getString(R.string.load_more));
                    ArrayList<CollectBean> list = Utils.array2List(collect);
                    Log.e(TAG,list.toString());
                    if(action==I.ACTION_DOWNLOAD||action == I.ACTION_PULL_DOWN){
                        mAdapter.initList(list);
                    }else if(action == I.ACTION_PULL_UP) {
                        mAdapter.addList(list);
                    }
                    if (collect.length<I.PAGE_SIZE_DEFAULT){
                        mAdapter.setMore(false);
                        mAdapter.setFooterText(getResources().getString(R.string.no_more));
                    }
                }

            }
        };
    }

    private void setListener() {
        registerListReceiver();
        setPullDownRefreshListener();
        setPullUpRefreshListener();
    }
    /**下拉事件监听*/
    private void setPullUpRefreshListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mtvHint.setVisibility(View.VISIBLE);
                pageId=0;
                action = I.ACTION_PULL_DOWN;
                getpath(pageId);
                mContext.executeRequest(new GsonRequest<CollectBean[]>(path,
                        CollectBean[].class,responseDownloadListener(),mContext.errorListener()));
            }
        });
    }

    /**上拉事件监听*/
    private void setPullDownRefreshListener() {
        mRecyclerView.setOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    int lastItemPostion;
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState==RecyclerView.SCROLL_STATE_IDLE&&lastItemPostion==mAdapter.getItemCount()-1){
                            if(mAdapter.isMore()){
                                mSwipeRefreshLayout.setRefreshing(true);
                                action = I.ACTION_PULL_UP;
                                pageId += I.PAGE_SIZE_DEFAULT;
                                getpath(pageId);
                                mContext.executeRequest(new GsonRequest<CollectBean[]>(path,
                                        CollectBean[].class,responseDownloadListener(),mContext.errorListener()));
                            }
                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        //获取最后列表的下标
                        lastItemPostion = mGridLayoutManager.findLastCompletelyVisibleItemPosition();
                        mSwipeRefreshLayout.setEnabled(mGridLayoutManager.findFirstCompletelyVisibleItemPosition()==0);
                    }
                });
    }


    private void initData() {
        getpath(pageId);
        Log.e(TAG,path.toString());
        mContext.executeRequest(new GsonRequest<CollectBean[]>(path, CollectBean[].class,
                responseDownloadListener(), mContext.errorListener()));
    }

    private String getpath(int pageid){
        try {
            User user = FuLiCenterApplication.getInstance().getUser();
            path = new ApiParams()
                    .with(I.Collect.USER_NAME,user.getMUserName())
                    .with(I.PAGE_ID,pageId+"")
                    .with(I.PAGE_SIZE,I.PAGE_SIZE_DEFAULT+"")
                    .getRequestUrl(I.REQUEST_FIND_COLLECTS);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    class UpdateCollectListReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    }

    UpdateCollectListReceiver mReceiver;

    private void registerListReceiver(){
        mReceiver = new UpdateCollectListReceiver();
        IntentFilter filter = new IntentFilter("update_collect_count");
        registerReceiver(mReceiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mReceiver!=null){
            unregisterReceiver(mReceiver);
        }
    }
}
