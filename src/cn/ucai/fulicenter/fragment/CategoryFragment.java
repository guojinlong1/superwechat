package cn.ucai.fulicenter.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FuLiCenterMainActivity;
import cn.ucai.fulicenter.adapter.CategoryAdapter;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {
    public static final String TAG = CategoryFragment.class.getName();
    FuLiCenterMainActivity mContext;
    ArrayList<CategoryGroupBean> mGroupList;
    ArrayList<ArrayList<CategoryChildBean>> mChildList;
    ExpandableListView melvCategory;

    CategoryAdapter mAdapter;
    int groupCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_category, container, false);
        mContext = (FuLiCenterMainActivity) getActivity();
        initView(layout);
        initData();
        return layout;
    }

    private void initData() {
        mGroupList = new ArrayList<CategoryGroupBean>();
        mChildList = new ArrayList<ArrayList<CategoryChildBean>>();

        try {
            String path = new ApiParams()
                    .getRequestUrl(I.REQUEST_FIND_CATEGORY_GROUP);
            Log.e("main1",path.toString());
            mContext.executeRequest(new GsonRequest<CategoryGroupBean[]>(path,CategoryGroupBean[].class,
                    responseDownCategoryListener(),mContext.errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<CategoryGroupBean[]> responseDownCategoryListener() {
       return new Response.Listener<CategoryGroupBean[]>() {
           @Override
           public void onResponse(CategoryGroupBean[] categoryGroupBeen) {
               Log.e("main1",categoryGroupBeen.toString());
               if(categoryGroupBeen!=null){

                       try {
                           mGroupList = Utils.array2List(categoryGroupBeen);
                           Log.e("main1",categoryGroupBeen.toString());
                           int i =0;
                           for(CategoryGroupBean group : mGroupList) {
                               mChildList.add(i, new ArrayList<CategoryChildBean>());
                               String path = new ApiParams()
                                       .with(I.CategoryChild.PARENT_ID, group.getId() + "")
                                       .with(I.PAGE_ID, "0")
                                       .with(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT + "")
                                       .getRequestUrl(I.REQUEST_FIND_CATEGORY_CHILDREN);
                                mContext.executeRequest(new GsonRequest<CategoryChildBean[]>(path,CategoryChildBean[].class,
                                        responseDownCategoryChildListener(i),mContext.errorListener()));
                           i++;
                           }
                       } catch (Exception e) {
                           e.printStackTrace();
                       }


               }
           }
       };
    }

    private Response.Listener<CategoryChildBean[]> responseDownCategoryChildListener(final int i) {
        return new Response.Listener<CategoryChildBean[]>() {
            @Override
            public void onResponse(CategoryChildBean[] categoryChildBeen) {
                groupCount++;
                if(categoryChildBeen!=null){
                    ArrayList<CategoryChildBean> childList = Utils.array2List(categoryChildBeen);
                    if(childList!=null){
                        mChildList.set(i,childList);

                    }

                }
                if(mGroupList.size()==groupCount){
                    mAdapter.addItem(mGroupList,mChildList);
                }
            }
        };
    }

    private void initView(View layout) {
        mGroupList = new ArrayList<CategoryGroupBean>();
        mChildList = new ArrayList<ArrayList<CategoryChildBean>>();
        melvCategory = (ExpandableListView) layout.findViewById(R.id.exCategroy);
        mAdapter = new CategoryAdapter(mGroupList,mChildList,mContext);
        melvCategory.setAdapter(mAdapter);

    }

}
