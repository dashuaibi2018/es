package com.dna.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultDto implements Serializable {
    private String total;
    private String costTime;
    private Object data;
    //    private String resultCode;
//    private String resultMessage;
    private String tag;
    List<Map<String, Object>> recordList = new ArrayList<>();
    Map<String, Object> categoryMap = new HashMap<>();

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCostTime() {
        return costTime;
    }

    public void setCostTime(String costTime) {
        this.costTime = costTime;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<Map<String, Object>> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Map<String, Object>> recordList) {
        this.recordList = recordList;
    }

    public Map<String, Object> getCategoryMap() {
        return categoryMap;
    }

    public void setCategoryMap(Map<String, Object> categoryMap) {
        this.categoryMap = categoryMap;
    }

    @Override
    public String toString() {
        return "ResultDto{" +
                "total='" + total + '\'' +
                ", costTime='" + costTime + '\'' +
                ", data=" + data +
                ", tag='" + tag + '\'' +
                ", recordList=" + recordList +
                ", categoryMap=" + categoryMap +
                '}';
    }
}