package com.dna.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ResultDto implements Serializable {
    private String total;
    private String costTime;
    private Object data;
    //    private String resultCode;
//    private String resultMessage;
    private String tag;
    List<Map<String, Object>> recordList = new ArrayList<>();
    Map<String, Object> categoryMap = new HashMap<>();

}