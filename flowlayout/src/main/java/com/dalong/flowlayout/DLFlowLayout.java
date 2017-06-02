package com.dalong.flowlayout;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by dalong  on 2017/5/31.
 */

public class DLFlowLayout extends ViewGroup {
    // 可选择的
    private final static int STYPE_SELECT = 0;
    //便签 没有选中区分
    private final static int STYPE_TAG = 1;
    //每行的view列表
    private List<List<View>> mViewLinesList = new ArrayList<>();
    //所有子view集合
    private List<View> mChildList = new ArrayList<>();
    //每行的高
    private List<Integer> mLineHeights = new ArrayList<>();
    private int usefulWidth;
    //是否单选
    private boolean mIsSingle = true;
    //是否平均
    private boolean mEqually = true;
    private ColorStateList mTextColor = getResources().getColorStateList(R.color.flowlayout_item_text_bg);//文字选择器
    //背景
    private int mBackground;
    //默认可选择的
    private int mStype = STYPE_SELECT;
    //文字的大小
    private float mTextSize;
    private Context mContext;
    //选择表示 默认是－1不选择
    private int mSelectPosition = -1;
    //选择最大值
    private int maxSelectNum = -1;
    //水平间距
    private int mHorizontalSpacing;
    //竖直间距
    private int mVerticalSpacing;
    private List<Flow> mData = new ArrayList<>();
    //选择回调
    public OnSelectListener mOnSelectListener;
    //最大的item
    private int maxItemW;

    public DLFlowLayout(Context context) {
        this(context, null);
    }

    public DLFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DLFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DLFlowLayout);
        mStype = typedArray.getInt(R.styleable.DLFlowLayout_DL_stype, STYPE_SELECT);
        mTextColor = typedArray.getColorStateList(R.styleable.DLFlowLayout_DL_textColor);
        mBackground = typedArray.getResourceId(R.styleable.DLFlowLayout_DL_flowBg, R.drawable.flowlayout_item_bg_selector);
        mTextSize = typedArray.getFloat(R.styleable.DLFlowLayout_DL_textSize, 14);
        mEqually = typedArray.getBoolean(R.styleable.DLFlowLayout_DL_equally, true);
        mIsSingle = typedArray.getBoolean(R.styleable.DLFlowLayout_DL_isSingle, true);
        maxSelectNum = typedArray.getInt(R.styleable.DLFlowLayout_DL_maxSelectNum, -1);
        mHorizontalSpacing = typedArray.getDimensionPixelSize(R.styleable.DLFlowLayout_DL_horizontalSpacing, 6);
        mVerticalSpacing = typedArray.getDimensionPixelSize(R.styleable.DLFlowLayout_DL_verticalSpacing, 6);
        typedArray.recycle();
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(super.generateDefaultLayoutParams());
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取父容器DLFlowlayout设置测量模式和大小
        int iWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int iHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int iWidthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int iHeightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        //获取控件的padding
        int mPaddingLeft = getPaddingLeft();
        int mPaddingRight = getPaddingRight();
        int mPaddingTop = getPaddingTop();
        int mPaddingBottom = getPaddingBottom();

        int iLineLeftRrightPadding = mPaddingLeft + mPaddingRight;

        //控件的宽高
        int measureWidth = iWidthSpecSize;
        int measureHeight = 0;
        //一行的宽高
        int iCurLineW = 0;
        int iCurLineH = 0;

        if (iWidthMode == MeasureSpec.EXACTLY && iHeightMode == MeasureSpec.EXACTLY) {
            measureWidth = iWidthSpecSize;
            measureHeight = iHeightSpecSize;
        } else {
            int iChildWidth;
            int iChildHeight;
            int childCount = getChildCount();
            List<View> viewList = new ArrayList<>();
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                if (mEqually) {
                    resetItemWidth(childView, maxItemW);
                }
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
                iChildWidth = childView.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
                iChildHeight = childView.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
                if (iCurLineW + iChildWidth + iLineLeftRrightPadding > iWidthSpecSize) {
                    /**1、记录当前行的信息***/
                    //记录当前行的最大宽度，高度叠加
                    measureWidth = Math.max(measureWidth, iCurLineW);
                    measureHeight += iCurLineH;
                    //将当前行的viewList添加至总的行view集合中
                    mViewLinesList.add(viewList);
                    //将行高添加至总的行高list中
                    mLineHeights.add(iCurLineH);

                    /**2、记录新一行的信息***/
                    //1、重新赋值新一行的宽、高
                    iCurLineW = iChildWidth;
                    iCurLineH = iChildHeight;
                    //2、新建一行的viewlist，添加新一行的view
                    viewList = new ArrayList<View>();
                    viewList.add(childView);

                } else {

                    //记录每行的信息
                    //1、行内宽度的叠加、高度比较
                    iCurLineW += iChildWidth;
                    iCurLineH = Math.max(iCurLineH, iChildHeight);
                    // 2、添加至当前行的viewList中
                    viewList.add(childView);
                }
                /*****3、如果正好是最后一行需要换行**********/
                if (i == childCount - 1) {
                    measureWidth = Math.max(measureWidth, iCurLineW);
                    measureHeight += iCurLineH;
                    //将当前行的viewList添加至总的行view集合中
                    mViewLinesList.add(viewList);
                    //将行高添加至总的行高list中
                    mLineHeights.add(iCurLineH);
                }
            }
            measureHeight = measureHeight + mPaddingTop + mPaddingBottom;
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        usefulWidth = r - l - getPaddingLeft() - getPaddingRight();
        int mPaddingLeft = getPaddingLeft();
        int mPaddingRight = getPaddingRight();
        int mPaddingTop = getPaddingTop();

        int left, top, right, bottom;
        int curTop = mPaddingTop;
        int curLeft = mEqually ? 0 : mPaddingLeft;
        int lineCount = mViewLinesList.size();
        int space = 0;
        if (mEqually) {
            if (mViewLinesList.size() > 0) {
                List<View> viewList = mViewLinesList.get(0);
                int lineViewSize = viewList.size();
                space = (getWidth() - viewList.get(0).getMeasuredWidth() * lineViewSize) / (lineViewSize + 1);
            }
        }
        for (int i = 0; i < lineCount; i++) {
            if (mEqually) {
                List<View> viewList = mViewLinesList.get(i);
                int lineViewSize = viewList.size();
                for (int j = 0; j < lineViewSize; j++) {
                    View childView = viewList.get(j);
                    MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
                    left = curLeft + space;
                    top = curTop + layoutParams.topMargin;
                    right = left + childView.getMeasuredWidth();
                    bottom = top + childView.getMeasuredHeight();
                    childView.layout(left, top, right, bottom);
                    curLeft += childView.getMeasuredWidth() + space;
                }
                //重置 左面为mPaddingLeft  top下一行叠加
                curLeft = mEqually ? 0 : mPaddingLeft;
                curTop += mLineHeights.get(i);
            } else {
                List<View> viewList = mViewLinesList.get(i);
                int lineViewSize = viewList.size();
                for (int j = 0; j < lineViewSize; j++) {
                    View childView = viewList.get(j);
                    MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
                    left = curLeft + layoutParams.leftMargin;
                    top = curTop + layoutParams.topMargin;
                    right = left + childView.getMeasuredWidth();
                    bottom = top + childView.getMeasuredHeight();
                    childView.layout(left, top, right, bottom);
                    curLeft += childView.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
                }
                //重置 左面为mPaddingLeft  top下一行叠加
                curLeft = mPaddingLeft;
                curTop += mLineHeights.get(i);
            }

        }
        mViewLinesList.clear();
        mLineHeights.clear();
    }


    /**
     * 重新压缩布局 让空白控件尽量少
     */
    public void relayoutToCompress() {
        post(new Runnable() {
            @Override
            public void run() {
                compress();
            }
        });
    }

    private void compress() {
        int childCount = this.getChildCount();
        if (0 == childCount) {
            return;
        }
        int count = childCount;
        View[] childs = new View[count];
        int[] spaces = new int[count];
        int n = 0;
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            childs[n] = v;
            LayoutParams childLp = v.getLayoutParams();
            int childWidth = v.getMeasuredWidth();
            if (childLp instanceof MarginLayoutParams) {
                MarginLayoutParams mlp = (MarginLayoutParams) childLp;
                spaces[n] = mlp.leftMargin + childWidth + mlp.rightMargin;
            } else {
                spaces[n] = childWidth;
            }
            n++;
        }
        int[] compressSpaces = new int[count];
        for (int i = 0; i < count; i++) {
            compressSpaces[i] = spaces[i] > usefulWidth ? usefulWidth : spaces[i];
        }
        sortToCompress(childs, compressSpaces);
        this.removeAllViews();

        for (View v : mChildList) {
            ViewGroup parent = (ViewGroup) v.getParent();
            if (parent != null) {
                parent.removeAllViewsInLayout();
            }
            this.addView(v);
        }
        mChildList.clear();
    }

    /**
     * 排序
     *
     * @param childs
     * @param spaces
     */
    private void sortToCompress(View[] childs, int[] spaces) {
        int childCount = childs.length;
        int[][] table = new int[childCount + 1][usefulWidth + 1];
        for (int i = 0; i < childCount + 1; i++) {
            for (int j = 0; j < usefulWidth; j++) {
                table[i][j] = 0;
            }
        }
        boolean[] flag = new boolean[childCount];
        for (int i = 0; i < childCount; i++) {
            flag[i] = false;
        }
        for (int i = 1; i <= childCount; i++) {
            for (int j = spaces[i - 1]; j <= usefulWidth; j++) {
                table[i][j] = (table[i - 1][j] > table[i - 1][j - spaces[i - 1]] + spaces[i - 1]) ? table[i - 1][j] : table[i - 1][j - spaces[i - 1]] + spaces[i - 1];
            }
        }
        int v = usefulWidth;
        for (int i = childCount; i > 0 && v >= spaces[i - 1]; i--) {
            if (table[i][v] == table[i - 1][v - spaces[i - 1]] + spaces[i - 1]) {
                flag[i - 1] = true;
                v = v - spaces[i - 1];
            }
        }
        int rest = childCount;
        View[] restArray;
        int[] restSpaces;
        for (int i = 0; i < flag.length; i++) {
            if (flag[i] == true) {
                mChildList.add(childs[i]);
                rest--;
            }
        }

        if (0 == rest) {
            return;
        }
        restArray = new View[rest];
        restSpaces = new int[rest];
        int index = 0;
        for (int i = 0; i < flag.length; i++) {
            if (flag[i] == false) {
                restArray[index] = childs[i];
                restSpaces[index] = spaces[i];
                index++;
            }
        }
        table = null;
        childs = null;
        flag = null;
        sortToCompress(restArray, restSpaces);
    }

    /**
     * 设置数据
     *
     * @param data 数据
     */
    public void setFlowData(List<Flow> data) {
        this.mData = data;
        post(new Runnable() {
            @Override
            public void run() {
                removeAllViews();
                addViews(mData);
                postInvalidate();
            }
        });

    }


    /**
     * add View
     *
     * @param mData
     */
    private void addViews(List<Flow> mData) {
        for (int i = 0; i < mData.size(); i++) {
            addChildView(mData.get(i));
        }
        if (mStype != STYPE_SELECT) {
            setAllUnEnable();
        }
    }

    /**
     * 添加view
     *
     * @param flow  数据
     */
    public void addChildView(Flow flow) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.view_flow_item, null);
        LinearLayout itemViewRoot = (LinearLayout) itemView.findViewById(R.id.view_flow_item);
        CheckBox chk = (CheckBox) itemView.findViewById(R.id.single_select_chk);
        chk.setOnClickListener(new OnItemClickEvent());
        chk.setText(flow.getFlowName());
        chk.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
        if (mTextColor != null) chk.setTextColor(mTextColor);
        chk.setBackgroundResource(mBackground);
        chk.setTag(mChildList == null ? 0 : mChildList.size());
        setUnChecked(chk);
        // 测量最长的一个条目的位置
        measureView(itemView);
        int width = itemView.getMeasuredWidth();
        MarginLayoutParams lp = new MarginLayoutParams(width, LayoutParams.MATCH_PARENT);
        lp.setMargins(dip2px(getContext(), mHorizontalSpacing / 2), dip2px(getContext(), mVerticalSpacing / 2),
                dip2px(getContext(), mHorizontalSpacing / 2), dip2px(getContext(), mVerticalSpacing / 2));
        itemViewRoot.setLayoutParams(lp);
        // 获取最长的条目长度  （比较后选择最大的一个单选框）
        if (width > maxItemW) {
            maxItemW = width;
        }
        mChildList.add(itemView);
        addView(itemView);

        if (mStype != STYPE_SELECT) {
            setUnRnable(itemView);
        }

    }


    /**
     * 重新设置条目宽度
     */
    private void resetItemWidth(View itemView, int itemW) {
        LinearLayout itemViewRoot = (LinearLayout) itemView
                .findViewById(R.id.view_flow_item);
        MarginLayoutParams lp = new MarginLayoutParams(itemW, LayoutParams.MATCH_PARENT);
        lp.setMargins(dip2px(getContext(), mHorizontalSpacing / 2), dip2px(getContext(), mVerticalSpacing / 2),
                dip2px(getContext(), mHorizontalSpacing / 2), dip2px(getContext(), mVerticalSpacing / 2));
        itemViewRoot.setLayoutParams(lp);
    }

    /**
     * 测量一个单选按钮的长度
     * @param v view
     *
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
     * (点击事件)
     * 条目CheckBox的点击事件
     */
    private class OnItemClickEvent implements OnClickListener {
        @Override
        public void onClick(View v) {
            CheckBox chk = (CheckBox) v;
            if (chk.isChecked()) {
                mSelectPosition = (Integer) chk.getTag();
                if (maxSelectNum != -1 && !mIsSingle && isSelectedIndexs().size() > maxSelectNum) {
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
     * @param selectName 选中的name
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
     * @param selectId  要设置选中的id
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
     * @param select  设置默认选中的index
     */
    public void setDefaultSelect(int select) {
        notifyAllItemView(select);
    }

    public void setDefaultSelects(int[] select) {
        for (int i = 0; i < select.length; i++) {
            notifyAllItemView(select[i]);
        }

    }

    /**
     * 刷新数据
     *
     * @param mSelectPosition 选中
     */
    private void notifyAllItemView(int mSelectPosition) {

        for (int i = 0; i < getChildCount(); i++) {
            CheckBox chk = (CheckBox) getChildAt(i).findViewById(R.id.single_select_chk);
            int tag = (Integer) chk.getTag();
            //选中状态  单选
            if (mSelectPosition >= 0) {
                if (chk.isEnabled()) {
                    if (tag == mSelectPosition) {
                        this.mSelectPosition = mSelectPosition;
                        setChecked(chk);
                    } else {
                        if (mIsSingle) {
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
        for (int i = 0; i < getChildCount(); i++) {
            CheckBox chk = (CheckBox) getChildAt(i).findViewById(R.id.single_select_chk);
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
     * @param position  位置
     */
    public void setUnEnable(int position) {
        for (int i = 0; i < getChildCount(); i++) {
            CheckBox chk = (CheckBox) getChildAt(i).findViewById(R.id.single_select_chk);
            int tag = (Integer) chk.getTag();
            if (tag == position)
                setUnEnabled(chk);
        }
    }

    /**
     * 设置所有不可点击
     */
    public void setAllUnEnable() {
        for (View itemView : mChildList) {
            setUnRnable(itemView);
        }
    }

    /**
     * 设置不可点击
     *
     * @param view 子view
     */
    public void setUnRnable(View view) {
        CheckBox chk = (CheckBox) view.findViewById(R.id.single_select_chk);
        setUnEnabled(chk);
    }

    /**
     * 获取是否被选择
     *
     * @return 是否被选择了
     */
    public boolean isSelectPosition() {
        for (int i = 0; i < getChildCount(); i++) {
            CheckBox chk = (CheckBox) getChildAt(i).findViewById(R.id.single_select_chk);
            if (chk.isChecked())
                return true;
        }
        return false;
    }

    /**
     * 获取选择的那个index(单选的时候可以直接用这个方法，如果的多选你用这个方法就会默认拿到第一个)
     *
     * @return 返回选中的集合
     */
    public int isSelectedIndex() {
        for (int i = 0; i < getChildCount(); i++) {
            CheckBox chk = (CheckBox) getChildAt(i).findViewById(R.id.single_select_chk);
            if (chk.isChecked())
                return i;
        }
        return -1;
    }

    /**
     * 获取选择的集合(多选)
     * @return 返回选中的index集合
     */
    public List<Integer> isSelectedIndexs() {
        List<Integer> mList = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            CheckBox chk = (CheckBox) getChildAt(i).findViewById(R.id.single_select_chk);
            if (chk.isChecked()) {
                mList.add(i);
            }
        }
        return mList;
    }

    /**
     * 设置还没有选择的样式
     *
     * @param view 子view
     */
    private void setUnChecked(CheckBox view) {
        view.setChecked(false);
    }

    /**
     * 设置被选择的样式
     *
     * @param view checkbox
     */
    private void setChecked(CheckBox view) {
        view.setChecked(true);
    }

    /**
     * 设置被设置能点击的样式
     *
     * @param view checkbox
     */
    private void setEnabled(CheckBox view) {
        view.setEnabled(true);
    }

    /**
     * 设置不能被点击的样式
     *
     * @param view checkbox
     */
    private void setUnEnabled(CheckBox view) {
        view.setEnabled(false);
    }


    /**
     * 描述：dip转换为px
     *
     * @param context  上下文
     * @param dipValue dip值
     * @return   px值
     */
    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
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
     * @param l 接口
     */
    public void setOnSelectListener(OnSelectListener l) {
        this.mOnSelectListener = l;
    }
}
