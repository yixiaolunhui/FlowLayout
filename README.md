# For most of the current demand flowlayout

# 效果图
![image](https://github.com/dalong982242260/FlowLayout/blob/master/gif/select.gif?raw=true)

![image](https://github.com/dalong982242260/FlowLayout/blob/master/gif/tag.gif?raw=true)

## 1.0.4版本新增功能：
解决流布局显示问其题，如设置padding或者在其他布局中显示问题。


# 如何使用

                                  <com.dalong.flowlayout.DLFlowLayout
                                             android:id="@+id/flowlayout"
                                             android:layout_width="match_parent"
                                             android:layout_height="wrap_content"
                                             android:background="@color/colorAccent"
                                             app:DL_equally="true"
                                             app:DL_flowBg="@drawable/bg_tag"
                                             app:DL_horizontalSpacing="5dp"
                                             app:DL_isSingle="false"
                                             app:DL_maxSelectNum="3"
                                             app:DL_stype="select"
                                             app:DL_textSize="15"
                                             app:DL_verticalSpacing="5dp"/>

# gradle使用

   compile 'com.dalong:flowlayout:1.0.4'





    


