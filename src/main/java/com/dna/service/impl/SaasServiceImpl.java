package com.dna.service.impl;

import com.dna.entity.ResultDto;
import com.dna.service.ISaasService;
import com.dna.utils.ESClient;
import com.google.gson.Gson;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneId;
import java.util.*;

@DubboService(version = "1.0.0", timeout = 10000, interfaceClass = ISaasService.class)
@Service
public class SaasServiceImpl implements ISaasService {

    RestHighLevelClient client = ESClient.getInstance().getHighLevelClient();

    @Override
    public ResultDto alarmMsgSearch(HashMap<String, Object> reqMap) throws IOException {

        MultiSearchRequest request = new MultiSearchRequest();

        ArrayList<String> objIdList = (ArrayList<String>) reqMap.get("obj_id");
        ArrayList<String> alarmTypeList = (ArrayList<String>) reqMap.get("alarm_type");
        String handle_status = ObjectUtils.toString(reqMap.get("handle_status"));

//        System.out.println(reqMap);
//        System.out.println("--------------------------------------------------------------------------------------------------");
//        System.out.println(new Gson().toJson(reqMap));
//        System.out.println("--------------------------------------------------------------------------------------------------");
//        System.out.println(new Gson().fromJson(new Gson().toJson(reqMap), HashMap.class));

        SearchRequest searchRequest = new SearchRequest("biz_alarm_msg");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termsQuery("obj_id", objIdList))
//                        .must(QueryBuilders.termsQuery("alarm_type", alarmTypeList))
//                        .must(QueryBuilders.termQuery("handle_status", 0))
                        .filter(QueryBuilders.termsQuery("alarm_type", alarmTypeList))
                        .filter(QueryBuilders.termQuery("handle_status", handle_status))
        );
        searchSourceBuilder.sort("alarm_time", SortOrder.DESC).from(0).size(20);
        searchRequest.source(searchSourceBuilder);

        request.add(searchRequest);
        MultiSearchResponse response = client.msearch(request, RequestOptions.DEFAULT);

        return getMultiRes(response);

    }

    @Override
    public ResultDto pushMsgSearch(HashMap<String, Object> reqMap) throws IOException {

        MultiSearchRequest request = new MultiSearchRequest();

        ArrayList<String> receivedList = new ArrayList<>();
        Collections.addAll(receivedList, "0", "1", "2");
        ArrayList<String> msgTypeList = new ArrayList<>();
        Collections.addAll(msgTypeList, "17");

        SearchRequest searchRequest = new SearchRequest("push_msg");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("user_id", "16072415330002514"))
//                        .must(QueryBuilders.termsQuery("alarm_type", alarmTypeList))
//                        .must(QueryBuilders.termQuery("handle_status", 0))
                        .must(QueryBuilders.termQuery("app_name.keyword", "HeXieQiChe"))
                        .must(QueryBuilders.termsQuery("received", receivedList))
                        .must(QueryBuilders.termsQuery("msg_type", msgTypeList))
                        .must(QueryBuilders.termQuery("push_mode", "0"))
                        .mustNot(QueryBuilders.termQuery("clean_flag", 1))
        ).from(0).size(20)
                .fetchSource("app_name,received,msg_type,clean_flag".split(","), null);
//        searchSourceBuilder.sort("alarm_time", SortOrder.DESC).from(0).size(20);
        searchRequest.source(searchSourceBuilder);

        request.add(searchRequest);
        MultiSearchResponse response = client.msearch(request, RequestOptions.DEFAULT);

        return getMultiRes(response);
    }

    @Override
    public ResultDto carNoSearch(HashMap<String, Object> reqMap) throws IOException {

        MultiSearchRequest request = new MultiSearchRequest();

        SearchRequest searchRequest = new SearchRequest("service_objs_join_vehicle");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                        .filter(QueryBuilders.termQuery("status", "1"))
                        .filter(QueryBuilders.termQuery("rec_status", "1"))
//                .filter(QueryBuilders.prefixQuery("license_plate_no.keyword", "苏A6A5M0"))
                        .filter(QueryBuilders.wildcardQuery("license_plate_no.keyword", "*6A5*"))
        ).from(0).size(20)
                .fetchSource("license_plate_no".split(","), null);

        searchSourceBuilder.sort("creat_time", SortOrder.DESC).from(0).size(10);
        searchRequest.source(searchSourceBuilder);

        request.add(searchRequest);
        MultiSearchResponse response = client.msearch(request, RequestOptions.DEFAULT);

        return getMultiRes(response);
    }


    @Override
    public ResultDto oilWearSearch(HashMap<String, Object> reqMap) throws IOException {

        MultiSearchRequest request = new MultiSearchRequest();

        SearchRequest searchRequest = new SearchRequest("device_status_vehicle_summary");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .filter(QueryBuilders.rangeQuery("summary_date").timeZone("Asia/Shanghai").format("yyyy-MM-dd HH:mm:ss").gte("2019-10-01 00:00:00").lte("2020-09-25 23:59:59"))
                .filter(QueryBuilders.termQuery("is_contract", "1"))
                .filter(QueryBuilders.termQuery("operator_corp_id", "dinacarrier"))
                .filter(QueryBuilders.termQuery("corp_id", "12120710341890007"))
        ).aggregation(
                AggregationBuilders.dateHistogram("by_summary_date")
                        .order(BucketOrder.aggregation("_key", true))  //时间区间聚合按_key升序
//                        .order(BucketOrder.key(true))   //按桶的英文字母升序
//                        .order(BucketOrder.count(false))  //按桶的doc_count升序
//                        .order(BucketOrder.aggregation("distanceTotal",true)) //按单值 度量子聚合 (由聚合名称标识)对桶进行排序
                        .timeZone(ZoneId.of("Asia/Shanghai"))
                        .field("summary_date").calendarInterval(DateHistogramInterval.MONTH)
                        .subAggregation(AggregationBuilders.sum("distanceTotal").field("distance_total"))
                        .subAggregation(AggregationBuilders.sum("fuel").field("fuel_consumption"))
                        .subAggregation(AggregationBuilders.sum("time").field("drive_duration"))
        );

//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        TermsAggregationBuilder aggregation = AggregationBuilders.terms("by_company")
//                .field("company.keyword");
//        aggregation.subAggregation(AggregationBuilders.avg("average_age")
//                .field("age"));
//        searchSourceBuilder.aggregation(aggregation);

        searchSourceBuilder.size(0).trackTotalHits(true);
        searchRequest.source(searchSourceBuilder);

        request.add(searchRequest);
        MultiSearchResponse response = client.msearch(request, RequestOptions.DEFAULT);

        //处理返回结果集
        Aggregation summaryDate = response.getResponses()[0].getResponse().getAggregations().get("by_summary_date");
        Histogram bySummaryDate = (Histogram) summaryDate;
        Iterator<? extends Histogram.Bucket> buckets = bySummaryDate.getBuckets().iterator();

        while (buckets.hasNext()) {
            Histogram.Bucket dateBucket = buckets.next();
            System.out.println("\n\n月份 " + dateBucket.getKeyAsString() + "\n计数：" + dateBucket.getDocCount());

//            Aggregation fuelAgg = dateBucket.getAggregations().get("fuel");
//            Sum fuelSum = (Sum) fuelAgg;
//            System.out.println("名称 "+ fuelAgg.getName() + "count为 " + fuelSum.getValue());
            Iterator<Aggregation> sumIter = dateBucket.getAggregations().iterator();
            while (sumIter.hasNext()) {
                Sum next = (Sum) sumIter.next();
                System.out.println("name: " + next.getName() + "  count: " + (int) next.value());//new Double(next.value()).intValue()
            }
        }

        return getMultiRes(response);
    }


    /**
     * @param response
     * @description: 封装返回对象公共方法
     * @return: com.dna.entity.ResultDto
     * @author: SUJUN
     * @time: 2020/10/21 14:09
     */
    public ResultDto getMultiRes(MultiSearchResponse response) {
        ResultDto res = new ResultDto();
        List<Map<String, Object>> recordList = new ArrayList<>();

        SearchHits hits = response.getResponses()[0].getResponse().getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            recordList.add(sourceAsMap);
        }

        System.out.println(new Gson().toJson(recordList));

//        res.setData(response.getResponses()[0].getResponse().getHits().getHits());
        res.setRecordList(recordList);
        res.setTotal(ObjectUtils.toString(hits.getTotalHits().value));
        res.setCostTime(response.getResponses()[0].getResponse().getTook().toString());
        return res;
    }


}