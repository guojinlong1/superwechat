package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.BoutiqueChildActivity;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.task.UpdateCartTask;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.FootViewHolder;

/**
 * Created by Administrator on 2016/6/15 0015.
 */
public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final static String TAG = CartAdapter.class.getName();

    Context mContext;
    ArrayList<CartBean> mCartList;
    ViewGroup parent;
    static final int TYPE_ITEM = 0;
    static final int TYPE_FOOTER=1;
    boolean isMore;

    public CartAdapter(Context mContext, ArrayList<CartBean> list, int sortBy) {
        this.mContext = mContext;
        this.mCartList = list;
    }




    public CartAdapter(Context context, ArrayList<CartBean> mNewGoodList) {
        this.mContext = context;
        this.mCartList = mNewGoodList;
    }

    public boolean isMore(){
        return isMore;
    }

    public void setMore(boolean more){
        isMore = more;

    }
    public void initList(ArrayList<CartBean> list) {
        this.mCartList.clear();
        this.mCartList.addAll(list);
        notifyDataSetChanged();
    }

    public void addList(ArrayList<CartBean> contactList) {
        this.mCartList.addAll(contactList);
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View layout = null;
        final LayoutInflater filter = LayoutInflater.from(mContext);
        layout = filter.inflate(R.layout.item_cart, parent, false);
        RecyclerView.ViewHolder holder = new CartItemViewHolder(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        CartItemViewHolder holder1 = (CartItemViewHolder)holder;
        final CartBean cartBean = mCartList.get(position);
        GoodDetailsBean good = cartBean.getGoods();
        if(good==null){
            return;
        }
        holder1.mtvGoodName.setText(good.getGoodsName());
        holder1.mtvPrice.setText(good.getCurrencyPrice());
        holder1.mtvCount.setText(""+cartBean.getCount());
        holder1.mChecked.setChecked(cartBean.isChecked());
        ImageUtils.setNewGoodThumb(cartBean.getGoods().getGoodsThumb(),holder1.mAvatar);

        AddDelCartClickListener listener = new AddDelCartClickListener(good);
        holder1.mivAdd.setOnClickListener(listener);
        holder1.mivDel.setOnClickListener(listener);

        holder1.mChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e(TAG,"checked:::::::::::::::"+isChecked);
                cartBean.setChecked(isChecked);
                Log.e(TAG,"cart::::::::::::"+cartBean.isChecked());
                new UpdateCartTask(mContext,cartBean).exectue();
                Log.e(TAG,"cart1::::::::::::"+cartBean.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCartList ==null?0: mCartList.size();
    }



    class CartItemViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView mAvatar;
        TextView mtvGoodName, mtvPrice,mtvCount;
        ImageView mivAdd,mivDel;
        CheckBox mChecked;

        public CartItemViewHolder(View itemView) {
            super(itemView);
            mAvatar = (NetworkImageView) itemView.findViewById(R.id.GoodThumb);
            mtvGoodName = (TextView) itemView.findViewById(R.id.tvGoodName1);
            mtvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            mtvCount = (TextView) itemView.findViewById(R.id.tvCartCount);
            mivAdd = (ImageView) itemView.findViewById(R.id.ivAddCart);
            mivDel = (ImageView) itemView.findViewById(R.id.ivDelCart);
            mChecked = (CheckBox) itemView.findViewById(R.id.checkSelect);

        }
    }

    class AddDelCartClickListener implements View.OnClickListener{

        GoodDetailsBean goods;

        public AddDelCartClickListener(GoodDetailsBean goods) {
            this.goods = goods;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ivAddCart:
                    Log.e(TAG,"good:"+goods.toString());
                    Utils.addCart(goods,mContext);
                    break;
                case R.id.ivDelCart:
                    Utils.DelCart(goods,mContext);
                    break;
            }
        }
    }
}


