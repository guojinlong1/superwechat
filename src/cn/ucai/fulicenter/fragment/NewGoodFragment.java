package cn.ucai.fulicenter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FuLiCenterMainActivity;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;


/**
 * Created by Administrator on 2016/6/20 0020.
 */
public class NewGoodFragment extends Fragment {
    FuLiCenterMainActivity mContext;
    int pageId = 0;
    String path;
    GoodAdapter mAdapter;
    ArrayList<NewGoodBean> mGoodlist;
    private int action = I.ACTION_DOWNLOAD;

    /**下拉刷新控件
     * */

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    TextView mtvHint;
    GridLayoutManager mGridLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_new_good, container, false);
        mContext = (FuLiCenterMainActivity)getActivity();
        initView(layout);
        setListener();
        initData();

        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initView(View layout) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swl_newgood);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );

        mtvHint = (TextView) layout.findViewById(R.id.tv_refresh_hint);
        mGridLayoutManager = new GridLayoutManager(mContext,I.COLUM_NUM);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.rv_newgood);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mGoodlist = new ArrayList<NewGoodBean>();
        mAdapter = new GoodAdapter(mContext,mGoodlist,I.SORT_BY_ADDTIME_DESC);
        mRecyclerView.setAdapter(mAdapter);

    }

    private Response.Listener<NewGoodBean[]> responseDownloadListener(){
        return new Response.Listener<NewGoodBean[]>() {
            @Override
            public void onResponse(NewGoodBean[] newGoodBeen) {
                if(newGoodBeen!=null){
                    mAdapter.setMore(true);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mtvHint.setVisibility(View.GONE);
                    mAdapter.setFooterText(getResources().getString(R.string.load_more));
                    ArrayList<NewGoodBean> list = Utils.array2List(newGoodBeen);
                    if(action==I.ACTION_DOWNLOAD||action == I.ACTION_PULL_DOWN){
                        mAdapter.initList(list);
                    }else if(action == I.ACTION_PULL_UP) {
                        mAdapter.addList(list);
                    }
                    if (newGoodBeen.length<I.PAGE_SIZE_DEFAULT){
                        mAdapter.setMore(false);
                        mAdapter.setFooterText(getResources().getString(R.string.no_more));
                    }



                }

            }
        };
    }

    private void setListener() {
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
                mContext.executeRequest(new GsonRequest<NewGoodBean[]>(path,
                        NewGoodBean[].class,responseDownloadListener(),mContext.errorListener()));
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
                        mContext.executeRequest(new GsonRequest<NewGoodBean[]>(path,
                                NewGoodBean[].class,responseDownloadListener(),mContext.errorListener()));
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
            mContext.executeRequest(new GsonRequest<NewGoodBean[]>(path, NewGoodBean[].class,
                    responseDownloadListener(), mContext.errorListener()));
    }

    private String getpath(int pageid){
        try {
             path = new ApiParams()
                    .with(I.NewAndBoutiqueGood.CAT_ID,I.CAT_ID+"")
                    .with(I.PAGE_ID,pageId+"")
                    .with(I.PAGE_SIZE,I.PAGE_SIZE_DEFAULT+"")
                    .getRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
