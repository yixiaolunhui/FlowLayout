# For most of the current demand flowlayout

# 效果图
![image](https://github.com/dalong982242260/FlowLayout/blob/master/gif/flowlayout.gif?raw=true)

## 1.0.3版本新增功能：
解决设置宽度问题，去除flowwidth字段，宽度可设置layout_width参数具体dp或者权重都可以。


## 1.0.2版本新增功能：
增加设置宽度flowWidth，解决设置宽度，内容显示不全的问题，如果没有设置flowwidth默认手机宽度，layout_width参数其实无效，请知晓


## 1.0.1版本新增功能：
增加设置每个item的内边距



# 如何使用

              <com.dalong.flowlayout.FlowLayout
                                 android:id="@+id/mFlowLayout"
                                 app:horizontalSpacing="8dp" //水平间距
                                 app:verticalSpacing="8dp" //竖直间距
                                 app:textSize="16sp"  //文字大小
                                 app:equally="true"   //布局是否均匀分配 已最大的为基准 默认是true
                                 app:stype="select"   //是否可以选择  提供tag和select  tag只能显示不能点击选择  select可以点击选择
                                 app:isSingle="false" //是否是单选    true就是单选 false 就是多选    默认是单选
                                 app:itemPaddingLeft="5dp"  //每个item的内左边距
                                 app:itemPaddingTop="2dp"   //每个item的内上边距
                                 app:itemPaddingRight="5dp" //每个item的内右边距
                                 app:itemPaddingBottom="2dp" //每个item的内底边距
                                 app:textColor="@color/test_flowlayout_item_text_color" //设置文字颜色选择器
                                 app:flowBackground="@drawable/flow_item_bg_selector"  //设置流布局布局选择器  默认一套 
                                 android:paddingLeft="8dp"
                                 android:paddingRight="8dp"
                                 android:layout_width="200dp"
                                 android:layout_height="wrap_content"/>

# gradle使用

   compile 'com.dalong:flowlayout:1.0.3'





    


