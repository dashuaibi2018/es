package com.dna.controller;


import com.dna.entity.ResultDto;
import com.dna.utils.ESClient;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@RestController
@RequestMapping("/saas")
public class SaasController {

    RestHighLevelClient client = ESClient.getInstance().getHighLevelClient();

    /**
     * @param
     * @description: biz_alarm_msg 查询
     * @return: com.dna.entity.ResultDto
     * @author: SUJUN
     * @time: 2020/10/20 14:33
     */
    @RequestMapping("/alarmMsgSearch")
    public ResultDto alarmMsgSearch(HashMap<String, Object> reqMap) throws IOException {
        ResultDto res = new ResultDto();
        MultiSearchRequest request = new MultiSearchRequest();


        ArrayList<String> objIdList = new ArrayList<>();
        Collections.addAll(objIdList, "18061110594379843", "18061209415874994", "18062912132930390", "18062914000298718", "18061110541928130", "66f55f543904437e98bce96a96903702", "677e464ec96d4cebb7903d902ae7186f", "edb2748cf51f42a5accf83bded7d3ec3", "1647b0c7b5ae43e781d2dc6234747c49", "19031415052563108", "22638356f9fb4468b5ed9c91bd442a9a", "26805c2ce6e848a4969feaafdd2b2f4b", "4e3b82c4db164eacb08322f249484704", "5b134d658f8e4863b5c6f03c3645ed55", "6ba3bc809a02499d9204ec334a87421d", "a1b87f86758c41e6b05ae176aa684de2", "18061110550768395", "18061209445476031", "e5d89ee8a0cd481ab57277ece65e1337", "18090518224140332", "aecdae7d7055476dac0a193729407923", "882f572381914d57b1a62657ca9ae764", "c6371ea71ba641aab74dcfed13c9499c", "ff6eafbd52194c539c9d0924496b4e71", "0385772f9123490eb624b9607357e3a3", "165f0c86981547629ceb5a7bfa112cdf", "18061209455676438", "19052318241678680", "19052318251978892", "1b5e29819502466ea3a53b42fd0c58e6", "1c985e7f56124d2bbf1f2baffa035ba4", "20011412550798793", "20011413100890083", "2d8a2922868244f094d27050431f6cbb", "2f2f407762324d66968e44fe5c8169bd", "3204d4163c39418c84568c8d21c7d7da", "4ae51c30712c4fe8ad7709bbe43a4d87", "4f73fc4d968f4e7ebc7bb3dd0bae3e5f", "57c978529848477ea782c9f4437bb4dc", "718a0e99ec95459ba93b385032721757", "766e53803697416bb3264c6f44917ee0", "7991114af1bc479c87cd40c27a1b23cf", "85a950ae03f8424aac237171e1721312", "92fd976f83a048a7a6b0a8d4941b44ff", "93b21abae55547169e46a23fb21b6174", "98cde790ca6f4442b0352a3c54e1862c", "a442cc46580f4d07ae5f35eb154a4b4b", "a6b06be796ab47e0b37fde04c008aba0", "ab2a4720cf924817be706e39fcb9325c", "bc6cefefdeca4d9ba656a407b3b2da10", "e596aa0aaa2f4bdfa6adf7078e3dbcfc", "fdda47eb8ce7473489fe165618a2c7a3");

        ArrayList<String> alarmTypeList = new ArrayList<>();
        Collections.addAll(alarmTypeList, "200", "201", "202", "203");

        reqMap.put("obj_id", objIdList);
        reqMap.put("alarm_type", alarmTypeList);
        reqMap.put("handle_status", "0");

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
                        .filter(QueryBuilders.termQuery("handle_status", 0))
        );
        searchSourceBuilder.sort("alarm_time", SortOrder.DESC).from(0).size(20);
        searchRequest.source(searchSourceBuilder);

        request.add(searchRequest);
        MultiSearchResponse response = client.msearch(request, RequestOptions.DEFAULT);

        return getMultiRes(response);
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
        ResultDto res = new ResultDto();
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


    /**
     * @param
     * @description: service_objs_join_vehicle 车牌号查询
     * @return: com.dna.entity.ResultDto
     * @author: SUJUN
     * @time: 2020/10/20 14:33
     */
    @RequestMapping("/carNoSearch")
    public ResultDto carNoSearch(HashMap<String, Object> reqMap) throws IOException {
        ResultDto res = new ResultDto();
        MultiSearchRequest request = new MultiSearchRequest();

        SearchRequest searchRequest = new SearchRequest("service_objs_join_vehicle");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("status", "1"))
                .filter(QueryBuilders.termQuery("rec_status", "1"))
                .filter(QueryBuilders.prefixQuery("license_plate_no.keyword", "苏A6A5M0"))
        ).from(0).size(20);

        searchSourceBuilder.sort("creat_time", SortOrder.DESC).from(0).size(10);
        searchRequest.source(searchSourceBuilder);

        request.add(searchRequest);
        MultiSearchResponse response = client.msearch(request, RequestOptions.DEFAULT);

        return getMultiRes(response);
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
        ResultDto res = new ResultDto();
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
                System.out.println("name: "+ next.getName() + "  count: " + (int)next.value());//new Double(next.value()).intValue()
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
        res.setData(response.getResponses());
        res.setTotal(ObjectUtils.toString(response.getResponses()[0].getResponse().getHits().getTotalHits().value));
        res.setCostTime(response.getResponses()[0].getResponse().getTook().toString());
        return res;
    }

}