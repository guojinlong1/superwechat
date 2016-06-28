package cn.ucai.fulicenter.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.CatChildFilterButton;
import cn.ucai.fulicenter.view.DisplayUtils;

public class CategoryChildActivity extends BaseActivity {

    CategoryChildActivity mContext;
    int pageId = 0;
    String path;
    //价格排序
    Button mbtnPriceSort;
    //按上架时间排序
    Button mbtnAddTimeSort;
    /**
     * 商品价格排序
     * true是升序
     * false是降序
     * */
    boolean mSortByPriceAsc;

    CatChildFilterButton mCatChildFilterButton;

    /**
     * 商品按上架时间排序
     * true升序
     * false降序
     * */
    boolean mSortByAddTimeAsc;
    private int sortBy;

    GoodAdapter mAdapter;
    ArrayList<NewGoodBean> mGoodlist;
    private int action = I.ACTION_DOWNLOAD;

    /**下拉刷新控件
     * */

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    TextView mtvHint;
    GridLayoutManager mGridLayoutManager;

    String groupName;
    ArrayList<CategoryChildBean> mChildList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_child);
        mContext = this;
        sortBy = I.SORT_BY_ADDTIME_DESC;

        initView();
        setListener();
        initData();
    }


    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swl_category_child);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );

        mtvHint = (TextView) findViewById(R.id.tv_refresh_hint);
        mGridLayoutManager = new GridLayoutManager(mContext,I.COLUM_NUM);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_category_child);

        mbtnPriceSort = (Button) findViewById(R.id.btn_price_sort);
        mbtnAddTimeSort = (Button) findViewById(R.id.btn_add_time_sort);

        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mGoodlist = new ArrayList<NewGoodBean>();
        mAdapter = new GoodAdapter(mContext,mGoodlist,sortBy);
        mRecyclerView.setAdapter(mAdapter);
        String categoryChildTitle = "";
        DisplayUtils.initBackWithTitle(mContext,categoryChildTitle);

        mCatChildFilterButton = (CatChildFilterButton) findViewById(R.id.btnCatChildFilter);
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
        SortStateChangedListener mSortStateChangedListener = new SortStateChangedListener();
        mbtnAddTimeSort.setOnClickListener(mSortStateChangedListener);
        mbtnPriceSort.setOnClickListener(mSortStateChangedListener);
       // mCatChildFilterButton.setOnCatFilterClickListener();
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
                executeRequest(new GsonRequest<NewGoodBean[]>(path,
                        NewGoodBean[].class,responseDownloadListener(),errorListener()));
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
                                executeRequest(new GsonRequest<NewGoodBean[]>(path,
                                        NewGoodBean[].class,responseDownloadListener(),errorListener()));
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
        executeRequest(new GsonRequest<NewGoodBean[]>(path, NewGoodBean[].class,
                responseDownloadListener(),errorListener()));
    }

    private String getpath(int pageid){
        try {
            int catId = getIntent().getIntExtra(I.CategoryChild.CAT_ID,0);
            path = new ApiParams()
                    .with(I.NewAndBoutiqueGood.CAT_ID,catId+"")
                    .with(I.PAGE_ID,pageid+"")
                    .with(I.PAGE_SIZE,I.PAGE_SIZE_DEFAULT+"")
                    .getRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS);
            Log.e("path",path.toString());
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    class SortStateChangedListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Drawable right = null;
            int resId ;
            switch (v.getId()){
                case R.id.btn_price_sort:
                    if(mSortByPriceAsc){
                        sortBy = I.SORT_BY_PRICE_ASC;
                        right = mContext.getResources().getDrawable(R.drawable.arrow_order_up);
                        resId = R.drawable.arrow_order_down;
                    }else{
                        sortBy = I.SORT_BY_PRICE_DESC;
                        right = mContext.getResources().getDrawable(R.drawable.arrow_order_down);
                        resId = R.drawable.arrow_order_up;
                    }

                    mSortByPriceAsc = !mSortByPriceAsc;
                    right.setBounds(0,0, ImageUtils.getDrawableWidth(mContext,resId),ImageUtils.getDrawableHeight(mContext,resId));
                    mbtnPriceSort.setCompoundDrawables(null,null,right,null);
                    break;
                case R.id.btn_add_time_sort:
                    if(mSortByAddTimeAsc){
                        sortBy = I.SORT_BY_ADDTIME_ASC;
                        right = mContext.getResources().getDrawable(R.drawable.arrow_order_up);
                        resId = R.drawable.arrow_order_down;
                    }else{
                        sortBy = I.SORT_BY_ADDTIME_DESC;
                        right = mContext.getResources().getDrawable(R.drawable.arrow_order_down);
                        resId = R.drawable.arrow_order_up;
                    }

                    mSortByAddTimeAsc = !mSortByAddTimeAsc;
                    right.setBounds(0,0, ImageUtils.getDrawableWidth(mContext,resId),ImageUtils.getDrawableHeight(mContext,resId));
                    mbtnAddTimeSort.setCompoundDrawables(null,null,right,null);
                    break;
            }
            mAdapter.setSortBy(sortBy);
        }
    }

}
