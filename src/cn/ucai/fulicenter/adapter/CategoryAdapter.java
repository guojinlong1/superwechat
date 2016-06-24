package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.zip.Inflater;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CategoryChildActivity;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.utils.ImageUtils;

/**
 * Created by Administrator on 2016/6/23 0023.
 */
public class CategoryAdapter extends BaseExpandableListAdapter {

    ArrayList<CategoryGroupBean> mGroupList;
    ArrayList<ArrayList<CategoryChildBean>> mChildList;
    Context mContext;

    public CategoryAdapter(ArrayList<CategoryGroupBean> mGroupList,
                           ArrayList<ArrayList<CategoryChildBean>> mChildList, Context mContext) {
        this.mGroupList = mGroupList;
        this.mChildList = mChildList;
        this.mContext = mContext;
    }

    @Override
    public int getGroupCount() {
        return mGroupList==null?0:mGroupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildList==null||mChildList.get(groupPosition)==null?0:mChildList.get(groupPosition).size();
    }

    @Override
    public CategoryGroupBean getGroup(int groupPosition) {
        return mGroupList.get(groupPosition);
    }

    @Override
    public CategoryChildBean getChild(int groupPosition, int childPosition) {
        return mChildList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View layout, ViewGroup parent) {
        ViewGroupHolder holder;
        if(layout==null){
//            layout = View.inflate(mContext, R.layout.item_category,null);
            layout = LayoutInflater.from(mContext).inflate(R.layout.item_category,parent,false);
            holder = new ViewGroupHolder();
            holder.nivCategory = (NetworkImageView) layout.findViewById(R.id.niv_category);
            holder.tvCategoryName = (TextView) layout.findViewById(R.id.tv_category_name);
            holder.ivExpand = (ImageView) layout.findViewById(R.id.iv_expand);
            layout.setTag(holder);
        }else {
            holder = (ViewGroupHolder) layout.getTag();
        }
        CategoryGroupBean group = getGroup(groupPosition);
        holder.tvCategoryName.setText(group.getName());
        String imgUrl = group.getImageUrl();
        String url = I.DOWNLOAD_DOWNLOAD_CATEGORY_GROUP_IMAGE_URL+imgUrl;
        Log.e("main2",url.toString());
        ImageUtils.setThumb(url,holder.nivCategory);
        if(isExpanded){
            holder.ivExpand.setImageResource(R.drawable.expand_off);
        }else {
            holder.ivExpand.setImageResource(R.drawable.expand_on);
        }
        return layout;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View layout, ViewGroup parent) {
        ChildViewHolder holder = null;
        if(holder==null){
            layout = View.inflate(mContext,R.layout.item_category_child,null);
            holder = new ChildViewHolder();
            holder.ivCategoryChildThumb = (NetworkImageView) layout.findViewById(R.id.niv_category_child);
            holder.layoutChild = (RelativeLayout) layout.findViewById(R.id.layout_category_child);
            holder.tvChildChildName = (TextView) layout.findViewById(R.id.tv_category_child);
            layout.setTag(holder);
        }else {
            holder = (ChildViewHolder) layout.getTag();
        }

        final CategoryChildBean child = getChild(groupPosition,childPosition);
        String name = child.getName();
        holder.tvChildChildName.setText(name);

        String ImgUrl = child.getImageUrl();
        String url = I.DOWNLOAD_DOWNLOAD_CATEGORY_CHILD_IMAGE_URL + ImgUrl;
        ImageUtils.setThumb(url,holder.ivCategoryChildThumb);

        holder.layoutChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, CategoryChildActivity.class)
                .putExtra(I.CategoryChild.CAT_ID,child.getId()));
            }
        });


        return layout;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class ViewGroupHolder{
        NetworkImageView nivCategory;
        TextView tvCategoryName;
        ImageView ivExpand;
    }

    class ChildViewHolder{
        RelativeLayout layoutChild;
        NetworkImageView ivCategoryChildThumb;
        TextView tvChildChildName;
    }

    public void addItem(ArrayList<CategoryGroupBean> groupList,
                       ArrayList<ArrayList<CategoryChildBean>> childList){
        this.mGroupList = groupList;
        this.mChildList = childList;
        notifyDataSetChanged();
    }
}
