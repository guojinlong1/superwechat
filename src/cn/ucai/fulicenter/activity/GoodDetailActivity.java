
package cn.ucai.fulicenter.activity;

        import android.os.Bundle;
        import android.os.PersistableBundle;
        import android.util.Log;
        import android.view.View;
        import android.webkit.WebSettings;
        import android.webkit.WebView;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.android.volley.Response;
        import com.android.volley.toolbox.NetworkImageView;

        import cn.ucai.fulicenter.D;
        import cn.ucai.fulicenter.I;
        import cn.ucai.fulicenter.R;
        import cn.ucai.fulicenter.bean.AlbumBean;
        import cn.ucai.fulicenter.bean.GoodDetailsBean;
        import cn.ucai.fulicenter.data.ApiParams;
        import cn.ucai.fulicenter.data.GsonRequest;
        import cn.ucai.fulicenter.utils.ImageUtils;
        import cn.ucai.fulicenter.utils.Utils;
        import cn.ucai.fulicenter.view.DisplayUtils;
        import cn.ucai.fulicenter.view.FlowIndicator;
        import cn.ucai.fulicenter.view.SlideAutoLoopView;

/**
 * Created by Administrator on 2016/6/22 0022.
 */
public class GoodDetailActivity extends BaseActivity {

    GoodDetailActivity mContext;
    GoodDetailsBean mGood;
    int mGoodsId;

    SlideAutoLoopView mSlideAutoLoopView;
    FlowIndicator mFlowIndicator;

    LinearLayout mLinearLayout;
    ImageView mivCollect;
    ImageView mivAddcart;
    ImageView mivShare;
    TextView mtvCartCOunt;

    TextView tvGoodName;
    TextView tvEnglishName;
    TextView tvShopPrice;
    TextView tvCurrencyPrice;
    WebView wvGoodBrief;

    int mCurrentColor;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContext=this;
        setContentView(R.layout.activity_good_detail2);
        initView();
        initData();
    }

    private void initData() {
        mGoodsId = getIntent().getIntExtra(D.NewGood.KEY_GOODS_ID,0);
        try {
            String path = new ApiParams()
                    .with(D.NewGood.KEY_GOODS_ID,mGoodsId+"")
                    .getRequestUrl(I.REQUEST_FIND_GOOD_DETAILS);
            executeRequest(new GsonRequest<GoodDetailsBean>(path, GoodDetailsBean.class,
                    responseDownloadGoodDetailsListener(),errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<GoodDetailsBean> responseDownloadGoodDetailsListener() {
        return new Response.Listener<GoodDetailsBean>() {
            @Override
            public void onResponse(GoodDetailsBean goodDetailsBean) {
                if(goodDetailsBean!=null){
                    mGood = goodDetailsBean;
                    Log.e("goodDetailsBean",goodDetailsBean.toString());

                    //设置商品名称，价格，webview的简介
                    DisplayUtils.initBackWithTitle(GoodDetailActivity.this,"商品详情");
                    tvCurrencyPrice.setText(mGood.getCurrencyPrice());
                    tvEnglishName.setText(mGood.getGoodsEnglishName());
                    Log.e("goodDetailsBean",mGood.getGoodsEnglishName());
                    tvGoodName.setText(mGood.getGoodsName());
                    wvGoodBrief.loadDataWithBaseURL(null,mGood.getGoodsBrief().trim(),D.TEXT_HTML,D.UTF_8,null);

                    initColorsBanner();
                }else {
                    Utils.showToast(mContext,"下载商品详情失败", Toast.LENGTH_SHORT);
                }
            }
        };
    }

    private void initColorsBanner() {
        updateColor(0);
        for(int i=0;i<mGood.getProperties().length;i++){
            mCurrentColor=i;
            View layout = View.inflate(mContext,R.layout.layout_property_color,null);
            final NetworkImageView ivColor = (NetworkImageView) layout.findViewById(R.id.ivColorItem);
            String colorImg = mGood.getProperties()[i].getColorImg();
            if(colorImg.isEmpty()){
                continue;
            }
            ImageUtils.setGoodDetail(colorImg,ivColor);
            mLinearLayout.addView(layout);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateColor(mCurrentColor);
                }
            });
        }
    }

    private void updateColor(int i) {
        AlbumBean[] albums = mGood.getProperties()[i].getAlbums();
        String[] albumsUrl = new String[albums.length];
        for(int j=0;j<albumsUrl.length;j++){
            albumsUrl[j]=albums[j].getImgUrl();
        }
        mSlideAutoLoopView.startPlayLoop(mFlowIndicator,albumsUrl,albumsUrl.length);
    }


    private void initView() {
        mivCollect = (ImageView) findViewById(R.id.iv_collect);
        mivAddcart = (ImageView) findViewById(R.id.iv_cart);
        mivShare = (ImageView) findViewById(R.id.iv_share);
        mtvCartCOunt = (TextView) findViewById(R.id.tv_cart_count);

        mSlideAutoLoopView = (SlideAutoLoopView) findViewById(R.id.salv);
        mFlowIndicator = (FlowIndicator) findViewById(R.id.indicator);
        mLinearLayout = (LinearLayout) findViewById(R.id.layoutColorSelector);
        tvCurrencyPrice = (TextView) findViewById(R.id.tv_now_price);
        tvGoodName = (TextView) findViewById(R.id.tv_chinese_name);
        tvEnglishName = (TextView) findViewById(R.id.tv_english_name);
        tvShopPrice = (TextView) findViewById(R.id.tv_price);
        wvGoodBrief = (WebView) findViewById(R.id.wv_good_brief);
        WebSettings setting = wvGoodBrief.getSettings();
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        setting.setBuiltInZoomControls(true);
    }
}
