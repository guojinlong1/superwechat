package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.BoutiqueChildActivity;
import cn.ucai.fulicenter.activity.GoodDetailActivity;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FootViewHolder;

/**
 * Created by Administrator on 2016/6/15 0015.
 */
public class BoutiqueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context mContext;
    ArrayList<BoutiqueBean> mBoutiqueList;
    ViewGroup parent;
    String footerText;
    static final int TYPE_ITEM = 0;
    static final int TYPE_FOOTER=1;
    boolean isMore;

    FootViewHolder mFooterViewHolder;
    public BoutiqueAdapter(Context mContext,ArrayList<BoutiqueBean> list,int sortBy) {
        this.mContext = mContext;
        this.mBoutiqueList = list;
    }




    public BoutiqueAdapter(Context context, ArrayList<BoutiqueBean> mNewGoodList) {
        this.mContext = context;
        this.mBoutiqueList = mNewGoodList;
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
    public void initList(ArrayList<BoutiqueBean> list) {
        this.mBoutiqueList.clear();
        this.mBoutiqueList.addAll(list);
        notifyDataSetChanged();
    }

    public void addList(ArrayList<BoutiqueBean> contactList) {
        this.mBoutiqueList.addAll(contactList);
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
                layout = filter.inflate(R.layout.item_boutique, parent, false);
                holder = new BoutiqueItemViewHolder(layout);
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
        BoutiqueItemViewHolder holder1 = (BoutiqueItemViewHolder)holder;
        final BoutiqueBean good = mBoutiqueList.get(position);
        holder1.mtvGoodName.setText(good.getName());
        holder1.mtvTitle.setText(good.getTitle());
        holder1.mtvDesciiption.setText(good.getDescription());
        ImageUtils.setNewGoodThumb(good.getImageurl(),holder1.mAvatar);
        holder1.ll_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, BoutiqueChildActivity.class)
                        .putExtra(I.Boutique.NAME,good.getName())
                .putExtra(I.Boutique.CAT_ID,good.getId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBoutiqueList ==null?1: mBoutiqueList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    class BoutiqueItemViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView mAvatar;
        TextView mtvGoodName, mtvTitle,mtvDesciiption;

        RelativeLayout ll_good;

        public BoutiqueItemViewHolder(View itemView) {
            super(itemView);
            mAvatar = (NetworkImageView) itemView.findViewById(R.id.layout_boutique);
            mtvGoodName = (TextView) itemView.findViewById(R.id.boutique_name);
            mtvTitle = (TextView) itemView.findViewById(R.id.boutique_title);
            mtvDesciiption = (TextView) itemView.findViewById(R.id.boutique_description);
            ll_good = (RelativeLayout) itemView.findViewById(R.id.boutique_item);

        }
    }
}


