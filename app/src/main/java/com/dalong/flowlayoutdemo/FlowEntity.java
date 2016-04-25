package com.dalong.flowlayoutdemo;

import com.dalong.flowlayout.Flow;

/**
 * Created by zhouweilong on 16/4/25.
 */
public class FlowEntity extends Flow {
    public String id;
    public String name;

    public FlowEntity(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getFlowId() {
        return id;
    }

    @Override
    public String getFlowName() {
        return name;
    }
}
