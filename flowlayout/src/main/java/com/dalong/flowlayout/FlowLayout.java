package com.dalong.flowlayout;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 流布局
 * Created by zhouweilong on 16/4/22.
 */
public class FlowLayout extends LinearLayout {

    private final static int  STYPE_SELECT=0;// 可选择的

    private final static int  STYPE_TAG=1;//便签 没有选中区分

    private  int mMaxNum=-1;//最大值

    private  int mGivity;//默认是0

    private  int mItemPaddingLeft;//左边距

    private  int mItemPaddingTop;//上边距

    private  int mItemPaddingRight;//右边距

    private  int mItemPaddingBottom;//底边距

    private  boolean mIsSingle=true;//是否单选

    private  boolean mEqually=true;//是否平均

    private int mSrcW;

    private int mStype=STYPE_SELECT;//默认可选择的

    private int mHorizontalSpacing;//水平间距

    private int mVerticalSpacing;//竖直间距

    private int mBackground;//背景

    private ColorStateList mTextColor=getResources().getColorStateList(R.color.flowlayout_item_text_bg);//文字选择器

    private int mTextSize;//文字的大小

    private Context mContext;// 上下文

    private WindowManager wm;

    private LayoutInflater mLayoutInflater;

    private List<Flow> mData;

    private ViewGroup mFlowLayout;

    // view列表
    private List<View> mItemViews = new ArrayList<View>();

    //最大的item
    private int maxItemW;

    private int maxOneRowItemCount = -1;

    //选择表示 默认是－1不选择
    private int mSelectPosition = -1;

    //选择回调
    public OnSelectListener mOnSelectListener;

    public FlowLayout(Context context) {
        this(context,null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.FlowLayout);
        mStype=typedArray.getInt(R.styleable.FlowLayout_stype,STYPE_SELECT);
        mTextColor=typedArray.getColorStateList(R.styleable.FlowLayout_textColor);
        mBackground=typedArray.getResourceId(R.styleable.FlowLayout_flowBackground,R.drawable.flowlayout_item_bg_selector);
        mTextSize = px2sp(context,(int) typedArray.getDimensionPixelSize(R.styleable.FlowLayout_textSize,14));
        mHorizontalSpacing=typedArray.getDimensionPixelSize(R.styleable.FlowLayout_horizontalSpacing,6);
        mVerticalSpacing=typedArray.getDimensionPixelSize(R.styleable.FlowLayout_verticalSpacing,6);
        mItemPaddingLeft=typedArray.getDimensionPixelSize(R.styleable.FlowLayout_itemPaddingLeft,15);
        mItemPaddingTop=typedArray.getDimensionPixelSize(R.styleable.FlowLayout_itemPaddingTop,10);
        mItemPaddingRight=typedArray.getDimensionPixelSize(R.styleable.FlowLayout_itemPaddingRight,15);
        mItemPaddingBottom=typedArray.getDimensionPixelSize(R.styleable.FlowLayout_itemPaddingBottom,10);
        mEqually=typedArray.getBoolean(R.styleable.FlowLayout_equally,true);
        mIsSingle=typedArray.getBoolean(R.styleable.FlowLayout_isSingle,true);
        mGivity=typedArray.getInt(R.styleable.FlowLayout_flowGravity,0);
        mMaxNum=typedArray.getInt(R.styleable.FlowLayout_maxNum,-1);
        typedArray.recycle();

        this.mContext = context;
        this.mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        this.mSrcW = context.getResources().getDisplayMetrics().widthPixels;

    }

    /**
     * 设置数据
     * @param data
     */
    public void  setFlowData(List<Flow> data){
        this.mData=data;
        View  view= mLayoutInflater.inflate(R.layout.view_flow_layout,this);
        mFlowLayout=(LinearLayout)view.findViewById(R.id.view_flow_layout);
        clearViews();
        initView();

    }



    /**
     *  初始化view
     */
    private void initView() {
        if (null == mData || 0 == mData.size()) {
            return;
        }
        for(int i=0;i<mData.size();i++){
            View itemView=mLayoutInflater.inflate(R.layout.view_flow_item,null);
            CheckBox chk = (CheckBox) itemView.findViewById(R.id.single_select_chk);
            LinearLayout mFlowItem = (LinearLayout) itemView.findViewById(R.id.view_flow_item);
            chk.setOnClickListener(new OnChkClickEvent2());
            chk.setText(mData.get(i).getFlowName());
            chk.setTextSize(TypedValue.COMPLEX_UNIT_SP,mTextSize);
            chk.setPadding(mItemPaddingLeft,mItemPaddingTop,mItemPaddingRight,mItemPaddingBottom);
            if(mTextColor!=null) chk.setTextColor(mTextColor);
            chk.setBackgroundResource(mBackground);
            chk.setTag(i);
            setUnChecked(chk);
            mFlowItem.setPadding(mHorizontalSpacing/2,0,mHorizontalSpacing/2,0);
            mItemViews.add(itemView);
            // 测量最长的一个条目的位置
            measureView(itemView);
            int width = itemView.getMeasuredWidth();
            // 获取最长的条目长度  （比较后选择最大的一个单选框）
            if (width > maxItemW) {
                maxItemW = width;
            }
        }
        // 每行最大条目数目（屏幕宽度-左右两个页边距个8dip，再除以最长的条目的宽度）
        maxOneRowItemCount = (mSrcW - getPaddingLeft()-getPaddingRight()) / maxItemW;

        // 如果每行最大条目数为0，说明长度最大的条目太宽了，连一行一个都显示不完全，就让他一行显示一条。
        if (maxOneRowItemCount == 0) {
            maxOneRowItemCount = 1;
        }
        // 开始添加条目
        if(mEqually)
            addItem();
        else
            addItem2();
    }

    private void addItem2() {
        mSrcW= mSrcW - getPaddingLeft()-getPaddingRight();
        // new一个新的行的线性布局，用于添加每行的条目
        LinearLayout llytRow = getNewRow();
        for (int i = 0; i < mItemViews.size(); i++) {
            measureView(llytRow);//测量当前的布局
            View itemView=mItemViews.get(i);
            measureView(itemView);//测量i位置的的布局
            int llytRowWidth=llytRow.getMeasuredWidth();
            int itemViewWidth=itemView.getMeasuredWidth();
            //当mSrcw小于lytRowWidth+itemViewWidth的时候已经超出一行，所以需要重新新建一行，否则就直接add
            if(llytRowWidth+itemViewWidth>mSrcW){
                ViewGroup parent = (ViewGroup) llytRow.getParent();
                if (parent != null) {
                    parent.removeAllViewsInLayout();
                }
                mFlowLayout.addView(llytRow);
                llytRow = getNewRow();
                llytRow.addView(itemView);
            }else{
                llytRow.addView(itemView);
                if(i==mItemViews.size()-1){
                    ViewGroup parent = (ViewGroup) llytRow.getParent();
                    if (parent != null) {
                        parent.removeAllViewsInLayout();
                    }
                    mFlowLayout.addView(llytRow);
                }
            }

        }
    }

    private void addItem() {
        // 计算出一共有多少行，
        int rowCount = mItemViews.size() / maxOneRowItemCount;

        // 总条目个数除以每行的条目数，判断是否有余数。
        int lastRowItemCount = mItemViews.size() % maxOneRowItemCount;

        // 重新绘制每个条目的宽度
        int itemW = (mSrcW - getPaddingLeft()-getPaddingRight()) / maxOneRowItemCount;
        for (int i = 0; i < rowCount; i++) {
            // new一个新的行的线性布局，用于添加每行的条目
            LinearLayout llytRow = getNewRow();
            for (int j = 0; j < maxOneRowItemCount; j++) {
                View itemView = mItemViews.get(i * maxOneRowItemCount + j);
                // 重新绘制条目的宽度
                resetItemWidth(itemView, itemW);
                ViewGroup parent = (ViewGroup) itemView.getParent();
                if (parent != null) {
                    parent.removeAllViewsInLayout();
                }
                llytRow.addView(itemView);
            }
            mFlowLayout.addView(llytRow);
        }

        // 如果有还剩下不够一行的条目，重新开一行，添加余下的条目view
        if (lastRowItemCount != 0 && rowCount > 0) {
            LinearLayout llytRow = getNewRow();
            for (int k = 0; k < lastRowItemCount; k++) {
                View itemView = mItemViews.get(maxOneRowItemCount * rowCount + k);
                resetItemWidth(itemView, itemW);
                ViewGroup parent = (ViewGroup) itemView.getParent();
                if (parent != null) {
                    parent.removeAllViewsInLayout();
                }
                llytRow.addView(itemView);
            }
            mFlowLayout.addView(llytRow);
            // 如果总条目数连一行都填不满
        } else if (rowCount == 0) {
            LinearLayout llytRow = getNewRow();
            for (int j = 0; j < mData.size(); j++) {
                View itemView = mItemViews.get(j);
                //如果一行都不够就不按手机宽度平分，取原来的宽度
//				itemW = (mSrcW - dip2px(mContext, 16)) / mData.size();
//				resetItemWidth(itemView, itemW);
                ViewGroup parent = (ViewGroup) itemView.getParent();
                if (parent != null) {
                    parent.removeAllViewsInLayout();
                }
                llytRow.addView(itemView);
            }
            mFlowLayout.addView(llytRow);
        }

        if(mStype!=STYPE_SELECT){
            setAllUnEnable();
        }
    }
    /**
     * 重新设置条目宽度
     */
    private void resetItemWidth(View itemView, int itemW) {
        LinearLayout itemViewRoot = (LinearLayout) itemView
                .findViewById(R.id.view_flow_item);
        LayoutParams lp = new LayoutParams(itemW,
                LayoutParams.MATCH_PARENT);
        itemViewRoot.setLayoutParams(lp);
    }
    /**
     * 设置一个新行
     *
     * @return
     */
    private LinearLayout getNewRow() {
        LinearLayout llytRow = new LinearLayout(mContext);
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        llytRow.setOrientation(LinearLayout.HORIZONTAL);
        lp.setMargins(0, mVerticalSpacing/2, 0, mVerticalSpacing/2);
        switch (mGivity){
            case 0:
                llytRow.setGravity(Gravity.LEFT);
                break;
            case 1:
                llytRow.setGravity(Gravity.CENTER);
                break;
            case 2:
                llytRow.setGravity(Gravity.RIGHT);
                break;

        }
        llytRow.setLayoutParams(lp);
        return llytRow;
    }
    /**
     * 测量一个单选按钮的长度
     */
    public void measureView(View v) {
        if (v == null) {
            return;
        }
        int w = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);

        v.measure(w, h);
    }

    /**
     * 接口
     *
     * @author zhouweilong
     */
    public interface OnSelectListener {
        void onSelect(int position);
        void onOutLimit();//超出限制数量
    }

    /**
     * 实现接口回调
     *
     * @param l
     */
    public void setOnSelectListener(OnSelectListener l) {
        this.mOnSelectListener = l;
    }
    /**
     * (点击事件)
     * 条目CheckBox的点击事件
     */
    private class OnChkClickEvent2 implements OnClickListener {
        @Override
        public void onClick(View v) {
            CheckBox chk = (CheckBox) v;
            if (chk.isChecked()) {
                mSelectPosition = (Integer) chk.getTag();
                if(mMaxNum!=-1&&!mIsSingle&&isSelectedIndexs().size()>mMaxNum){
                    setUnChecked(chk);
                    if (mOnSelectListener != null) {
                        mOnSelectListener.onOutLimit();// 超出限制
                    }
                    return;
                }
                notifyAllItemView(mSelectPosition);
            } else {
                setUnChecked(chk);
                mSelectPosition = -1;
            }
            ///只要使用这个回调就知道选中的是哪个了   我靠
            if (mOnSelectListener != null) {
                mOnSelectListener.onSelect(mSelectPosition);
            }


        }
    }


    /**
     * 设置默认选中项 name
     *
     * @param selectName
     */
    public void setDefaultSelectName(String selectName) {
        for (int i = 0; i < mData.size(); i++) {
            Flow propertyValuesJson = mData.get(i);
            if (propertyValuesJson.getFlowName().equals(selectName)) {
                notifyAllItemView(i);
                return;
            }

        }

    }
    /**
     * 设置默认选中项 id
     *
     * @param selectId
     */
    public void setDefaultSelectId(String selectId) {
        for (int i = 0; i < mData.size(); i++) {
            Flow propertyValuesJson = mData.get(i);
            if (propertyValuesJson.getFlowId().equals(selectId)) {
                notifyAllItemView(i);
                return;
            }
        }

    }

    /**
     * 设置默认选中项
     *
     * @param select
     */
    public void setDefaultSelect(int select) {
        notifyAllItemView(select);
    }

    public void setDefaultSelects(int[] select) {
        for (int i=0;i<select.length;i++){
            notifyAllItemView(select[i]);
        }

    }
    /**
     * 刷新数据
     *
     * @param mSelectPosition 选中
     */
    private void notifyAllItemView(int mSelectPosition) {

        for (View itemView : mItemViews) {
            CheckBox chk = (CheckBox) itemView.findViewById(R.id.single_select_chk);
            int tag = (Integer) chk.getTag();
            //选中状态  单选
            if (mSelectPosition >= 0) {
                if (chk.isEnabled()) {
                    if (tag == mSelectPosition) {
                        this.mSelectPosition = mSelectPosition;
                        setChecked(chk);
                    } else {
                        if(mIsSingle){
                            setUnChecked(chk);
                        }
                    }
                }

            }

        }
    }

    /**
     * 设置所有可点击
     */
    public void setAllEnable() {
        for (View itemView : mItemViews) {
            CheckBox chk = (CheckBox) itemView.findViewById(R.id.single_select_chk);
            int tag = (Integer) chk.getTag();
            setEnabled(chk);
            if (tag == mSelectPosition) {
                setChecked(chk);
            } else {
                setUnChecked(chk);
            }
        }
    }
    /**
     * 设置不可点击项
     */
    public void setUnEnable(int position) {
        for (View itemView : mItemViews) {
            CheckBox chk = (CheckBox) itemView.findViewById(R.id.single_select_chk);
            int tag = (Integer) chk.getTag();
            if (tag == position)
                setUnEnabled(chk);
        }
    }

    /**
     * 设置所有不可点击
     */
    public void setAllUnEnable() {
        for (View itemView : mItemViews) {
            CheckBox chk = (CheckBox) itemView.findViewById(R.id.single_select_chk);
            setUnEnabled(chk);
        }
    }

    /**
     * 获取是否被选择
     *
     * @return
     */
    public boolean isSelectPosition() {
        for (View itemView : mItemViews) {
            CheckBox chk = (CheckBox) itemView.findViewById(R.id.single_select_chk);
            if (chk.isChecked())
                return true;
        }
        return false;
    }

    /**
     * 获取选择的那个index(单选的时候可以直接用这个方法，如果的多选你用这个方法就会默认拿到第一个)
     *
     * @return
     */
    public int isSelectedIndex() {
        for (int i = 0; i < mItemViews.size(); i++) {
            CheckBox chk = (CheckBox) mItemViews.get(i).findViewById(R.id.single_select_chk);
            if (chk.isChecked())
                return i;
        }
        return -1;
    }
    /**
     * 获取选择的集合(多选)
     *
     * @return
     */
    public List<Integer> isSelectedIndexs() {
        List<Integer> mList=new ArrayList<>();
        for (int i = 0; i < mItemViews.size(); i++) {
            CheckBox chk = (CheckBox) mItemViews.get(i).findViewById(R.id.single_select_chk);
            if (chk.isChecked()){
                mList.add(i);
            }

        }
        return mList;
    }

    /**
     * 清理view
     */
    public void clearViews() {
        mItemViews.clear();
        mFlowLayout.removeAllViews();
    }
    /**
     * 设置还没有选择的样式
     *
     * @param view
     */
    private void setUnChecked(CheckBox view) {
        view.setChecked(false);
    }

    /**
     * 设置被选择的样式
     *
     * @param view
     */
    private void setChecked(CheckBox view) {
        view.setChecked(true);
    }

    /**
     * 设置被设置能点击的样式
     *
     * @param view
     */
    private void setEnabled(CheckBox view) {
        view.setEnabled(true);
    }
    /**
     * 设置不能被点击的样式
     *
     * @param view
     */
    private void setUnEnabled(CheckBox view) {
        view.setEnabled(false);
    }


    /**
     * 描述：dip转换为px
     *
     * @param context
     * @param dipValue
     * @return
     * @throws
     */
    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
}
