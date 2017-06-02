package com.dalong.flowlayoutdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.dalong.flowlayout.DLFlowLayout;
import com.dalong.flowlayout.Flow;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DLMainActivity extends AppCompatActivity {

    private DLFlowLayout flowLayout;
    String[] texts = new String[]{
            "好的", "坏的", "不能理解的", "美好的一天",
            "你好", "是", "好", "名字", "姓名",
            "超级大坏蛋", "不", "就是你", "不是我"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlmain);
        initView();
        initListenr();

    }

    private void initView() {
        flowLayout = (DLFlowLayout) findViewById(R.id.flowlayout);
        flowLayout.setFlowData(getFlowData());
        flowLayout.setDefaultSelects(new int[]{});
    }

    private void initListenr() {
        flowLayout.setOnSelectListener(new DLFlowLayout.OnSelectListener() {
            @Override
            public void onSelect(int position) {
                Toast.makeText(DLMainActivity.this,"position："+position,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onOutLimit() {
                Toast.makeText(DLMainActivity.this,"超出限制",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void doClick(View view) {
        int a = new Random().nextInt(texts.length);
        Flow mFlow = new FlowEntity(String.valueOf(a), texts[a]);
        flowLayout.addChildView(mFlow);
    }

    public void relayoutToCompress(View view) {
        flowLayout.relayoutToCompress();
    }

    public void select(View view) {
        List<Integer> selectedIndexs = flowLayout.isSelectedIndexs();
        StringBuffer buffer=new StringBuffer();
        for (int i=0;i<selectedIndexs.size();i++){
            buffer.append(selectedIndexs.get(i)+",");
        }
        Toast.makeText(DLMainActivity.this,"你选择："+buffer.toString(),Toast.LENGTH_SHORT).show();
    }


    /**
     * 电话费
     *
     * @return
     */
    public List<Flow> getFlowData() {
        List<Flow> list = new ArrayList<>();
        for (int i = 0; i < texts.length; i++) {
            Flow mFlow = new FlowEntity((i + 1) + "", texts[i]);
            list.add(mFlow);
        }
        return list;
    }
}
