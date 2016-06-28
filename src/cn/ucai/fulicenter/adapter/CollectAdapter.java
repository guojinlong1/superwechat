package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CollectActivity;
import cn.ucai.fulicenter.activity.GoodDetailActivity;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.task.DownLoadCollectionCountTask;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FootViewHolder;

/**
 * Created by Administrator on 2016/6/15 0015.
 */
public class CollectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    final static String TAG = CollectAdapter.class.getName();

    CollectActivity mContext;
    ArrayList<CollectBean> mCollectList;
    ViewGroup parent;
    String footerText;
    static final int TYPE_ITEM = 0;
    static final int TYPE_FOOTER=1;
    boolean isMore;
    FootViewHolder mFooterViewHolder;
    public CollectAdapter(Context context, ArrayList<CollectBean> list) {
        this.mContext = (CollectActivity) context;
        this.mCollectList = list;
    }

    public void setFooterText(String footerText){
        this.footerText = footerText;
        notifyDataSetChanged();
    }

    public boolean isMore(){
        return isMore;
    }

    public void setMore(boolean more){
        isMore = more;

    }
    public void initList(ArrayList<CollectBean> list) {
        this.mCollectList.clear();
        this.mCollectList.addAll(list);
        notifyDataSetChanged();
    }

    public void addList(ArrayList<CollectBean> contactList) {
        this.mCollectList.addAll(contactList);
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        RecyclerView.ViewHolder holder = null;
        View layout = null;
        final LayoutInflater filter = LayoutInflater.from(mContext);
        switch (viewType) {
            case TYPE_ITEM:
                layout = filter.inflate(R.layout.item_collect, parent, false);
                holder = new CollectViewHolder(layout);
                break;
            case TYPE_FOOTER :
                layout = filter.inflate(R.layout.item_footer, parent, false);
                holder = new FootViewHolder(layout);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            mFooterViewHolder = (FootViewHolder) holder;
            mFooterViewHolder.tvFooter.setText(footerText);
            return;
        }
        CollectViewHolder holder1 = (CollectViewHolder)holder;
        final CollectBean collect = mCollectList.get(position);
        holder1.mtvCollect.setText(collect.getGoodsName());
        ImageUtils.setNewGoodThumb(collect.getGoodsThumb(),holder1.mAvatar);
        holder1.ll_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, GoodDetailActivity.class).putExtra(D.NewGood.KEY_GOODS_ID,collect.getGoodsId()));
            }
        });

        holder1.mivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String path =new ApiParams()
                            .with(I.Collect.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                            .with(I.Collect.GOODS_ID,collect.getGoodsId()+"")
                            .getRequestUrl(I.REQUEST_DELETE_COLLECT);
                    Log.e(TAG,path.toString());
                    mContext.executeRequest(new GsonRequest<MessageBean>(path,MessageBean.class,
                            responseCollectListener(collect),mContext.errorListener()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Response.Listener<MessageBean> responseCollectListener(final CollectBean collect) {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                if(messageBean.isSuccess()){
                    Log.e(TAG,messageBean.toString());
                    mCollectList.remove(collect);
                    notifyDataSetChanged();
                    new DownLoadCollectionCountTask(mContext).execute();
                }
            }
        };
    }


    @Override
    public int getItemCount() {
        return mCollectList ==null?1: mCollectList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    class CollectViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView mAvatar;
        TextView mtvCollect;
        ImageView mivDel;
        LinearLayout ll_good;
        public CollectViewHolder(View itemView) {
            super(itemView);
            mAvatar = (NetworkImageView) itemView.findViewById(R.id.nivThumb);
            mtvCollect = (TextView) itemView.findViewById(R.id.tvGoodName);
            mivDel = (ImageView) itemView.findViewById(R.id.collect_delete);
            ll_good = (LinearLayout) itemView.findViewById(R.id.layout_collect);

        }
    }


}
