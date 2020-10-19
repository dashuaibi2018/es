package com.dna.controller;


import com.dna.utils.ESClient;
import com.dna.utils.ResultDto;
import lombok.SneakyThrows;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/city")
public class ClientController {

    RestHighLevelClient client = ESClient.getInstance().getHighLevelClient();

    /**
     * @param keyword
     * @param from
     * @param size
     * @description: matchQuery（分词）   matchPhraseQuery()（不分词）
     * @return: com.dna.utils.ResultDto
     * @author: SUJUN
     * @time: 2020/10/19 16:34
     */
    @SneakyThrows
    @RequestMapping("/cityInfo")
    public ResultDto getCityInfo(@RequestParam(value = "keyword", required = true) String keyword,
                                 @RequestParam(value = "from", required = true) Integer from,
                                 @RequestParam(value = "size", required = true) Integer size) {

        ResultDto res = new ResultDto();

        SearchRequest searchRequest = new SearchRequest("push_msg");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("msg", keyword)).from(0).size(20);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        res.setData(searchResponse.getHits());
        return res;
    }


    /**
     * @param scrollId
     * @description: scroll游标滚动查询
     * @return: com.dna.utils.ResultDto
     * @author: SUJUN
     * @time: 2020/10/19 16:57
     */
    @SneakyThrows
    @RequestMapping("/scroll")
    public ResultDto scroll(String scrollId) {
        ResultDto res = new ResultDto();

        SearchRequest searchRequest = new SearchRequest("push_msg");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(2); //每次获取20条数据
        searchRequest.source(searchSourceBuilder);

        searchRequest.scroll(TimeValue.timeValueMillis(10L));

        SearchResponse searchResponse = scrollId == null
                ? client.search(searchRequest, RequestOptions.DEFAULT)
                : client.scroll(new SearchScrollRequest(scrollId), RequestOptions.DEFAULT);
        scrollId = searchResponse.getScrollId();

        res.setTag(scrollId);
        res.setData(searchResponse.getHits().getHits());
        return res;
    }


    /**
     * @param
     * @description: 批量增删改
     * @return: com.dna.utils.ResultDto
     * @author: SUJUN
     * @time: 2020/10/19 17:51
     */
    @SneakyThrows
    @RequestMapping("/bulkCRUD")
    public ResultDto bulkCRUD() {
        ResultDto res = new ResultDto();

        BulkRequest request = new BulkRequest("test_sj");
        request.add(new DeleteRequest("test_sj", "6"))
                .add(new UpdateRequest("test_sj", "_UqVK3UBvlFwiZ4OIdNJ").doc(XContentType.JSON, "desc", "dalaji"))
                .add(new IndexRequest("test_sj").id("88").source(XContentType.JSON, "name", "maozedong"));

        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        res.setData(response);
        return res;
    }


    /**
     * @param name
     * @description: 模糊查询  众泰汽车--> 众泰骑车
     * @return: com.dna.utils.ResultDto
     * @author: SUJUN
     * @time: 2020/10/19 18:24
     */
    @SneakyThrows
    @RequestMapping("/fuzzyQuery")
    public ResultDto fuzzyQuery(String name) {   //"传祺GS0"
        ResultDto res = new ResultDto();

        SearchRequest searchRequest = new SearchRequest("service_objs_join_vehicle");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.fuzzyQuery("product_name.keyword", name).fuzziness(Fuzziness.AUTO));
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        res.setData(response.getHits().getHits());
        return res;
    }


    /**
     * @param name
     * @description: termQuery 精确查询
     * @return: com.dna.utils.ResultDto
     * @author: SUJUN
     * @time: 2020/10/19 18:40
     */
    @SneakyThrows
    @RequestMapping("/termQuery")
    public ResultDto termQuery(String name) {
        ResultDto res = new ResultDto();

        SearchRequest searchRequest = new SearchRequest("service_objs_join_vehicle");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("product_name", name));
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        res.setData(response.getHits().getHits());
        return res;
    }

    /**
     * @param name
     * @description: prefixQuery 前缀查询   matchPhrasePrefixQuery 短语前缀查询        regexpQuery()正则查询
     * @return: com.dna.utils.ResultDto
     * @author: SUJUN
     * @time: 2020/10/19 18:41
     */
    @SneakyThrows
    @RequestMapping("/prefixQuery")
    public ResultDto prefixQuery(String name) {
        ResultDto res = new ResultDto();

        SearchRequest searchRequest = new SearchRequest("service_objs_join_vehicle");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.prefixQuery("license_plate_no.keyword", name));  //xx.keyword == matchPhrasePrefixQuery()
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        res.setData(response.getHits().getHits());
        return res;
    }


    /**
     * @param name
     * @description: 全文检索
     * @return: com.dna.utils.ResultDto
     * @author: SUJUN
     * @time: 2020/10/19 18:55
     */
    @SneakyThrows
    @RequestMapping("/matchQuery")
    public ResultDto matchQuery(String name) {
        ResultDto res = new ResultDto();

        SearchRequest searchRequest = new SearchRequest("service_objs_join_vehicle");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("product_name", name).analyzer("ik_max_word")); //指定分词器
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        res.setData(response.getHits().getHits());
        return res;
    }


}