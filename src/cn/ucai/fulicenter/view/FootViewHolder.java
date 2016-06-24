package cn.ucai.fulicenter.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cn.ucai.fulicenter.R;

/**
 * Created by Administrator on 2016/6/21 0021.
 */
public class FootViewHolder extends RecyclerView.ViewHolder{
    public TextView tvFooter;
    public FootViewHolder(View itemView) {
        super(itemView);
        tvFooter = (TextView) itemView.findViewById(R.id.tv_footer);
    }
}
