package com.dna.controller;


import com.dna.entity.ResultDto;
import com.dna.service.impl.SaasServiceImpl;
import com.dna.utils.ESClient;
import org.apache.commons.lang3.ObjectUtils;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

@RestController
@RequestMapping("/saas")
public class SaasController {

    RestHighLevelClient client = ESClient.getInstance().getHighLevelClient();

    @Autowired
    SaasServiceImpl saasService;

    /**
     * @param
     * @description: biz_alarm_msg 查询
     * @return: com.dna.entity.ResultDto
     * @author: SUJUN
     * @time: 2020/10/20 14:33
     */
    @RequestMapping("/alarmMsgSearch")
    public ResultDto alarmMsgSearch(HashMap<String, Object> reqMap) throws IOException {

        ResultDto resultDto = saasService.alarmMsgSearch(reqMap);
        return resultDto;
    }


    /**
     * @param
     * @description: push_msg 查询
     * @return: com.dna.entity.ResultDto
     * @author: SUJUN
     * @time: 2020/10/20 14:33
     */
    @RequestMapping("/pushMsgSearch")
    public ResultDto pushMsgSearch(HashMap<String, Object> reqMap) throws IOException {

        ResultDto resultDto = saasService.pushMsgSearch(reqMap);
        return resultDto;
    }


    /**
     * @param
     * @description: service_objs_join_vehicle 车牌号查询
     * @return: com.dna.entity.ResultDto
     * @author: SUJUN
     * @time: 2020/10/20 14:33
     */
    @RequestMapping("/carNoSearch")
    public ResultDto carNoSearch(HashMap<String, Object> reqMap) throws IOException {

//        ResultDto resultDto = saasService.carNoSearch(reqMap);
        return saasService.carNoSearch(reqMap);

    }

    /**
     * @param
     * @description: device_status_vehicle_summary 查询统计表的油耗
     * @return: com.dna.entity.ResultDto
     * @author: SUJUN
     * @time: 2020/10/20 14:33
     */
    @RequestMapping("/oilWearSearch")
    public ResultDto oilWearSearch(HashMap<String, Object> reqMap) throws IOException {

        ResultDto resultDto = saasService.oilWearSearch(reqMap);
        return resultDto;

    }

}