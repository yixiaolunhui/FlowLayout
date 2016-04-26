package com.dalong.flowlayoutdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dalong.flowlayout.Flow;
import com.dalong.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FlowLayout mFlowLayout,mFlowLayout2,mFlowLayout3,mFlowLayout4,mFlowLayout5,mFlowLayout6;
    private List<Flow> mList;
    private Button mBtn1,mBtn2,mBtn3,mBtn4,mBtn5,mBtn6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intView();
        initData();

    }


    private void initData() {
        mList=getPhoneList();
        mFlowLayout.setFlowData(mList);
        mFlowLayout.setDefaultSelect(1);

        mFlowLayout2.setFlowData(mList);
        mFlowLayout2.setDefaultSelect(2);

        mFlowLayout3.setFlowData(mList);
        mFlowLayout3.setDefaultSelect(3);

        mFlowLayout4.setFlowData(mList);
        mFlowLayout4.setDefaultSelect(4);

        mFlowLayout5.setFlowData(mList);
        mFlowLayout5.setDefaultSelect(3);

        mFlowLayout6.setFlowData(mList);
        mFlowLayout6.setDefaultSelects(new int[]{1,3,5});

    }

    private void intView() {
        mFlowLayout=(FlowLayout)findViewById(R.id.mFlowLayout);
        mFlowLayout2=(FlowLayout)findViewById(R.id.mFlowLayout2);
        mFlowLayout3=(FlowLayout)findViewById(R.id.mFlowLayout3);
        mFlowLayout4=(FlowLayout)findViewById(R.id.mFlowLayout4);
        mFlowLayout5=(FlowLayout)findViewById(R.id.mFlowLayout5);
        mFlowLayout6=(FlowLayout)findViewById(R.id.mFlowLayout6);
        mBtn1=(Button)findViewById(R.id.btn);
        mBtn2=(Button)findViewById(R.id.btn2);
        mBtn3=(Button)findViewById(R.id.btn3);
        mBtn4=(Button)findViewById(R.id.btn4);
        mBtn5=(Button)findViewById(R.id.btn5);
        mBtn6=(Button)findViewById(R.id.btn6);
        mBtn1.setOnClickListener(this);
        mBtn2.setOnClickListener(this);
        mBtn3.setOnClickListener(this);
        mBtn4.setOnClickListener(this);
        mBtn5.setOnClickListener(this);
        mBtn6.setOnClickListener(this);
    }


    /**
     * 电话费
     * @return
     */
    public List<Flow>  getPhoneList(){
        List<Flow> list=new ArrayList<>();
        Flow mFlow=new FlowEntity("1","5元");
        Flow mFlow2=new FlowEntity("2","10元");
        Flow mFlow3=new FlowEntity("3","20元");
        Flow mFlow4=new FlowEntity("4","30元");
        Flow mFlow5=new FlowEntity("5","50元");
        Flow mFlow6=new FlowEntity("6","100元");
        Flow mFlow7=new FlowEntity("7","200元");
        Flow mFlow8=new FlowEntity("8","500元");
        Flow mFlow9=new FlowEntity("9","1000元");
        Flow mFlow10=new FlowEntity("10","2000元");
        Flow mFlow11=new FlowEntity("11","3000元");
        Flow mFlow12=new FlowEntity("12","5000元");
        list.add(mFlow);
        list.add(mFlow2);
        list.add(mFlow3);
        list.add(mFlow4);
        list.add(mFlow5);
        list.add(mFlow6);
        list.add(mFlow7);
        list.add(mFlow8);
        list.add(mFlow9);
        list.add(mFlow10);
        list.add(mFlow11);
        list.add(mFlow12);

        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn:
                if(mFlowLayout.isSelectPosition()){
                    Toast.makeText(MainActivity.this,"你选择："+mList.get(mFlowLayout.isSelectedIndex()).getFlowName(),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"你没有选择",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn2:
                if(mFlowLayout2.isSelectPosition()){
                    Toast.makeText(MainActivity.this,"你选择："+mList.get(mFlowLayout2.isSelectedIndex()).getFlowName(),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"你没有选择",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn3:
                if(mFlowLayout3.isSelectPosition()){
                    Toast.makeText(MainActivity.this,"你选择："+mList.get(mFlowLayout3.isSelectedIndex()).getFlowName(),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"你没有选择",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn4:
                if(mFlowLayout4.isSelectPosition()){
                    Toast.makeText(MainActivity.this,"你选择："+mList.get(mFlowLayout4.isSelectedIndex()).getFlowName(),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"你没有选择",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn5:
                if(mFlowLayout5.isSelectPosition()){
                    Toast.makeText(MainActivity.this,"你选择："+mList.get(mFlowLayout5.isSelectedIndex()).getFlowName(),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"你没有选择",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn6:
                if(mFlowLayout6.isSelectPosition()){
                    StringBuffer buffer=new StringBuffer();
                    for (int i=0;i<mFlowLayout6.isSelectedIndexs().size();i++){
                        buffer.append(mList.get(mFlowLayout6.isSelectedIndexs().get(i)).getFlowName());
                    }
                    Toast.makeText(MainActivity.this,"你选择："+buffer.toString(),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"你没有选择",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
