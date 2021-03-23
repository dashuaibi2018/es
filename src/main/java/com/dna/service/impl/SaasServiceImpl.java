package com.dna.service.impl;

import cn.hutool.core.convert.Convert;
import com.dna.entity.ResultDto;
import com.dna.service.ISaasService;
import com.dna.utils.ESClient;
import com.google.gson.Gson;
import lombok.SneakyThrows;
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
    private SortOrder order;

    @Override
    public ResultDto alarmMsgSearch(Map<String, Object> reqMap) throws IOException {

        MultiSearchRequest request = new MultiSearchRequest();

        int pageFrom = Integer.parseInt(reqMap.get("pageFrom").toString());
        int pageSize = Integer.parseInt(reqMap.get("pageSize").toString());

//        System.out.println(reqMap);
//        System.out.println("--------------------------------------------------------------------------------------------------");
//        System.out.println(new Gson().toJson(reqMap));
//        System.out.println("--------------------------------------------------------------------------------------------------");
//        System.out.println(new Gson().fromJson(new Gson().toJson(reqMap), HashMap.class));

        SearchRequest searchRequest = new SearchRequest("biz_alarm_msg");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termsQuery("obj_id", (ArrayList<String>) reqMap.get("objIdList")))
//                        .must(QueryBuilders.termsQuery("alarm_type", alarmTypeList))
                        .filter(QueryBuilders.termsQuery("alarm_type", (ArrayList<String>) reqMap.get("alarmTypeList")))
                        .filter(QueryBuilders.termQuery("handle_status", reqMap.get("handle_status")))
        );
        searchSourceBuilder.sort("alarm_time", SortOrder.DESC).from(pageFrom).size(pageSize);
        searchRequest.source(searchSourceBuilder);

        request.add(searchRequest);
        MultiSearchResponse response = client.msearch(request, RequestOptions.DEFAULT);

        return getMultiRes(response);

    }

    @Override
    public ResultDto pushMsgSearch(Map<String, Object> reqMap) throws IOException {

        MultiSearchRequest request = new MultiSearchRequest();

        int pageFrom = Integer.parseInt(reqMap.get("pageFrom").toString());
        int pageSize = Integer.parseInt(reqMap.get("pageSize").toString());

        SearchRequest searchRequest = new SearchRequest("push_msg");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("user_id", reqMap.get("user_id")))
                .must(QueryBuilders.termQuery("app_name.keyword", reqMap.get("app_name")))
                .must(QueryBuilders.termsQuery("received", (ArrayList<String>) reqMap.get("receivedList")))
                .must(QueryBuilders.termsQuery("msg_type", (ArrayList<String>) reqMap.get("msgTypeList")))
                .must(QueryBuilders.termQuery("push_mode", reqMap.get("push_mode")))
                .mustNot(QueryBuilders.termQuery("clean_flag", reqMap.get("clean_flag")))
        ).from(pageFrom).size(pageSize)
                .fetchSource("app_name,received,msg_type,clean_flag".split(","), null);
//        searchSourceBuilder.sort("alarm_time", SortOrder.DESC).from(pageFrom).size(pageSize);
        searchRequest.source(searchSourceBuilder);

        request.add(searchRequest);
        MultiSearchResponse response = client.msearch(request, RequestOptions.DEFAULT);

        return getMultiRes(response);
    }

    @Override
    public ResultDto carNoSearch(Map<String, Object> reqMap) throws IOException {

        MultiSearchRequest request = new MultiSearchRequest();

        int pageFrom = Integer.parseInt(reqMap.get("pageFrom").toString());
        int pageSize = Integer.parseInt(reqMap.get("pageSize").toString());

        SearchRequest searchRequest = new SearchRequest("service_objs_join_vehicle");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                        .filter(QueryBuilders.termQuery("status", reqMap.get("status")))
                        .filter(QueryBuilders.termQuery("rec_status", reqMap.get("rec_status")))
                        .filter(QueryBuilders.prefixQuery("license_plate_no.keyword", reqMap.get("license_plate_no").toString()))
//                        .filter(QueryBuilders.wildcardQuery("license_plate_no.keyword", reqMap.get("license_plate_no").toString()))
        ).from(pageFrom).size(pageSize)
                .fetchSource("license_plate_no,obj_id".split(","), null);

        SortOrder order = Convert.toStr(reqMap.get("sortOrder")) == "desc" ? SortOrder.DESC : SortOrder.ASC;
        searchSourceBuilder.sort(Convert.toStr(reqMap.get("sortField")), order).from(pageFrom).size(pageSize);
        searchRequest.source(searchSourceBuilder);

        request.add(searchRequest);
        MultiSearchResponse response = client.msearch(request, RequestOptions.DEFAULT);

        return getMultiRes(response);
    }


    @Override
    public ResultDto oilWearSearch(Map<String, Object> reqMap) throws IOException {

        MultiSearchRequest request = new MultiSearchRequest();

        SearchRequest searchRequest = new SearchRequest("device_status_vehicle_summary");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .filter(QueryBuilders.rangeQuery("summary_date").timeZone("Asia/Shanghai").format("yyyy-MM-dd HH:mm:ss").gte(reqMap.get("startTime")).lte(reqMap.get("endTime")))
                .filter(QueryBuilders.termQuery("is_contract", reqMap.get("is_contract")))
                .filter(QueryBuilders.termQuery("operator_corp_id", reqMap.get("operator_corp_id")))
                .filter(QueryBuilders.termQuery("corp_id", reqMap.get("corp_id")))
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

    @SneakyThrows
    public static void main(String[] args) {
        System.out.println(Convert.toStr(129));

        long[] b = {1, 2, 3, 4, 5};
        //bStr为："[1, 2, 3, 4, 5]"
        String bStr = Convert.toStr(b);
        System.out.println(bStr);

        String[] b1 = {"1", "2", "3", "4"};
        //结果为Integer数组
        Integer[] intArray = Convert.toIntArray(b1);

        long[] c = {1, 2, 3, 4, 5};
        //结果为Integer数组
        Integer[] intArray2 = Convert.toIntArray(c);

    }
}