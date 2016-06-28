package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;


import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.fragment.BoutiqueFragment;
import cn.ucai.fulicenter.fragment.CategoryFragment;
import cn.ucai.fulicenter.fragment.NewGoodFragment;
import cn.ucai.fulicenter.fragment.PersonalCenterFragment;
import cn.ucai.fulicenter.utils.Utils;

public class FuLiCenterMainActivity extends BaseActivity {

    RadioButton mRbNewGood;
    RadioButton mRbBoutique;
    RadioButton mRbCategory;
    RadioButton mRbCart;
    RadioButton mRbPersonCenter;
    TextView mtvCartHint;
    RadioButton[] mRadios = new RadioButton[5];
    NewGoodFragment mNewGoodFragment;
    BoutiqueFragment mBoutiqueFragment;
    CategoryFragment mCategoryFragment;
    PersonalCenterFragment mPersonalCenterFragment;
    private Fragment[] mFragments = new Fragment[5];
    private int index;
    private int currentTabIndex;


    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fu_li_center_main);
        initView();
        registerReceiver();
        initFragment();
        // 添加显示第一个fragment
        getSupportFragmentManager()
                .beginTransaction()
                .add(cn.ucai.fulicenter.R.id.fragment_container, mNewGoodFragment)
                .add(cn.ucai.fulicenter.R.id.fragment_container, mBoutiqueFragment)
                .hide(mBoutiqueFragment)
                .add(cn.ucai.fulicenter.R.id.fragment_container, mCategoryFragment)
                .hide(mCategoryFragment)
//                .add(cn.ucai.fulicenter.R.id.fragment_container, mPersonalCenterFragment)
//                .hide(mPersonalCenterFragment)
                  .show(mNewGoodFragment)
                .commit();
        mtvCartHint.setVisibility(View.GONE);
    }

    private void initFragment() {
        mNewGoodFragment = new NewGoodFragment();
        mBoutiqueFragment = new BoutiqueFragment();
        mCategoryFragment = new CategoryFragment();
        mPersonalCenterFragment = new PersonalCenterFragment();

        mFragments[0] = mNewGoodFragment;
        mFragments[1] = mBoutiqueFragment;
        mFragments[2] = mCategoryFragment;
        mFragments[4] = mPersonalCenterFragment;
    }

    private void initView() {
        mtvCartHint = (TextView) findViewById(R.id.tvCartHint);
        mRbNewGood = (RadioButton) findViewById(R.id.layout_new_good);
        mRbBoutique = (RadioButton) findViewById(R.id.layout_boutique);
        mRbCategory = (RadioButton) findViewById(R.id.layout_category);
        mRbPersonCenter = (RadioButton) findViewById(R.id.personcenter);
        mRbCart = (RadioButton) findViewById(R.id.cart);

        mRadios[0] = mRbNewGood;
        mRadios[1] = mRbBoutique;
        mRadios[2] = mRbCategory;
        mRadios[3] = mRbCart;
        mRadios[4] = mRbPersonCenter;

    }

    public void onCheckedChange(View view){
        switch (view.getId()) {
            case cn.ucai.fulicenter.R.id.layout_new_good:
                index = 0;
                break;
            case cn.ucai.fulicenter.R.id.layout_boutique:
                index = 1;
                break;
            case cn.ucai.fulicenter.R.id.layout_category:
                index = 2;
                break;
            case cn.ucai.fulicenter.R.id.cart:
                index = 3;
                break;
            case cn.ucai.fulicenter.R.id.personcenter:
                if(FuLiCenterApplication.getInstance().getUser()!=null){
                    index = 4;
                }else {
                    gotoLogin();
                }

                break;
        }
        if(currentTabIndex!=index){
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(mFragments[currentTabIndex]);
            if (!mFragments[index].isAdded()) {
                trx.add(cn.ucai.fulicenter.R.id.fragment_container, mFragments[index]);
            }
            trx.show(mFragments[index]).commit();
            setRadioChecked(index);
            currentTabIndex = index;
        }
    }

    private void gotoLogin() {
        startActivity(new Intent(this,LoginActivity.class).putExtra("action","personal"));
    }

    private void setRadioChecked(int index){
        for(int i=0;i<mRadios.length;i++){
            if(i==index){
                mRadios[i].setChecked(true);

            }else {
                Log.e("main",i+"");
                mRadios[i].setSelected(false);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        final String action = getIntent().getStringExtra("action");
        if(action!=null&&FuLiCenterApplication.getInstance().getUser()!=null){
            if(action.equals("personal")){
                index = 4;
            }
        }else {
            setRadioChecked(index);
        }
        if(currentTabIndex==4 && FuLiCenterApplication.getInstance().getUser()==null){
            index = 0;
        }
        if(currentTabIndex!=index){
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(mFragments[currentTabIndex]);
            if (!mFragments[index].isAdded()) {
                trx.add(cn.ucai.fulicenter.R.id.fragment_container, mFragments[index]);
            }
            trx.show(mFragments[index]).commit();
            setRadioChecked(index);
            currentTabIndex = index;
        }
    }

    class UpdateCartReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int count = Utils.sumCartCount();
                if(count>0){
                    mtvCartHint.setVisibility(View.VISIBLE);
                    mtvCartHint.setText(""+count);
                }else {
                    mtvCartHint.setVisibility(View.GONE);
                }

            if(FuLiCenterApplication.getInstance().getUser()==null){
                mtvCartHint.setText("0");
                mtvCartHint.setVisibility(View.GONE);
            }
        }
    }

    UpdateCartReceiver mReceiver;

    private void registerReceiver(){
        mReceiver = new UpdateCartReceiver();
        IntentFilter filter = new IntentFilter("update_car_list");
        filter.addAction("update_user");
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
