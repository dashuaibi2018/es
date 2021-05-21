package com.dna.controller;


import com.dna.entity.ResultDto;
import com.dna.utils.ESClient;
import lombok.SneakyThrows;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    @RequestMapping("/cityInfo")
    public ResultDto getCityInfo(@RequestParam(value = "keyword", required = true) String keyword,
                                 @RequestParam(value = "from", required = true) Integer from,
                                 @RequestParam(value = "size", required = true) Integer size) throws IOException {

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
    @RequestMapping("/scrollQuery")
    public ResultDto scrollQuery(String scrollId) throws IOException {
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
    @RequestMapping("/bulkCRUD")
    public ResultDto bulkCRUD() throws IOException {
        ResultDto res = new ResultDto();

        BulkRequest request = new BulkRequest("test_sj");
        request.add(new DeleteRequest("test_sj", "6"))
                .add(new UpdateRequest("test_sj", "_UqVK3UBvlFwiZ4OIdNJ").doc(XContentType.JSON, "desc", "dalaji"))
                .add(new IndexRequest("test_sj").id("88").source(XContentType.JSON, "name", "maozedong"));

        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        res.setData(response);
        return res;
    }



    //region Search template
    @RequestMapping("/templateSearch")
    @SneakyThrows
    public ResultDto templateSearch() {

        //region 创建模板并缓存 作用域为整个集群
        Request scriptRequest = new Request("POST", "_scripts/test_template_search");
        scriptRequest.setJsonEntity(
                "{" +
                        "  \"script\": {" +
                        "    \"lang\": \"mustache\"," +
                        "    \"source\": {" +
                        "      \"query\": { \"match\" : { \"{{field}}\" : \"{{value}}\" } }," +
                        "      \"size\" : \"{{size}}\"" +
                        "    }" +
                        "  }" +
                        "}");
        Response scriptResponse = client.getLowLevelClient().performRequest(scriptRequest);
        //endregion
        //***********************************     华丽的分割线     *******************************************************
        SearchTemplateRequest request = new SearchTemplateRequest();
        request.setRequest(new SearchRequest("msb_auto"));
        request.setScriptType(ScriptType.STORED);
        request.setScript("test_template_search");
        //region 本地模板
        //        request.setScriptType(ScriptType.INLINE);
//        request.setScript(
//                        "{\n" +
//                        "  \"from\": {{from}},\n" +
//                        "  \"size\": {{size}},\n" +
//                        "  \"query\": {\n" +
//                        "    \"match\": {\n" +
//                        "      \"master_brand_name\": \"{{master_brand_name}}\"\n" +
//                        "    }\n" +
//                        "  }\n" +
//                        "}");
        //endregion
        Map<String, Object> scriptParams = new HashMap<>();
        scriptParams.put("field", "master_brand_name");
        scriptParams.put("value", "一汽");
        scriptParams.put("size", 5);
        request.setScriptParams(scriptParams);
        SearchTemplateResponse response = client.searchTemplate(request, RequestOptions.DEFAULT);
        return null;
    }
    //endregion




    /**
     * @param name
     * @description: 纠错模糊查询  众泰汽车--> 众泰骑车
     * @return: com.dna.utils.ResultDto
     * @author: SUJUN
     * @time: 2020/10/19 18:24
     */
    @RequestMapping("/fuzzyQuery")
    public ResultDto fuzzyESQuery(String name) throws IOException {   //"传祺GS0"
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
    @RequestMapping("/termQuery")
    public ResultDto termESQuery(String name) throws IOException {
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
    @RequestMapping("/prefixQuery")
    public ResultDto prefixESQuery(String name) throws IOException {
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
    @RequestMapping("/matchQuery")
    public ResultDto matchESQuery(String name) throws IOException {
        ResultDto res = new ResultDto();

        SearchRequest searchRequest = new SearchRequest("service_objs_join_vehicle");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("product_name", name).analyzer("ik_max_word")); //指定分词器
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        res.setData(response.getHits().getHits());
        return res;
    }


    /**
     * @param
     * @description: multiSearch  一个请求同时多个查询  鸡肋  类似bool查询
     * @return: com.dna.utils.ResultDto
     * @author: SUJUN
     * @time: 2020/10/19 19:07
     */
    @RequestMapping("/multiSearch")
    public ResultDto multiSearch() throws IOException {
        ResultDto res = new ResultDto();
        MultiSearchRequest request = new MultiSearchRequest();

        SearchRequest firstSearchRequest = new SearchRequest("service_objs_join_vehicle");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("product_name", "昂科威"));
        firstSearchRequest.source(searchSourceBuilder);
        request.add(firstSearchRequest);

        SearchRequest secondSearchRequest = new SearchRequest("service_objs_join_vehicle");
        searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("product_name", "宝马"));  //matchPhraseQuery
        secondSearchRequest.source(searchSourceBuilder);
        request.add(secondSearchRequest);

        MultiSearchResponse response = client.msearch(request, RequestOptions.DEFAULT);
        res.setData(response);

        return res;
    }


    /**
     * @param
     * @description: boolSearch 多条件查询
     * @return: com.dna.entity.ResultDto
     * @author: SUJUN
     * @time: 2020/10/20 14:33
     */
    @RequestMapping("/boolSearch")
    public ResultDto boolSearch() throws IOException {
        ResultDto res = new ResultDto();
        MultiSearchRequest request = new MultiSearchRequest();

        SearchRequest searchRequest = new SearchRequest("service_objs_join_vehicle");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
//                        .must(QueryBuilders.matchQuery("product_name", "2019款").analyzer("ik_smart"))  //注意分词影响
                        .must(QueryBuilders.matchPhraseQuery("product_name", "宝马"))
                        .filter(QueryBuilders.termQuery("shake_threshold", "3000"))
                        .mustNot(QueryBuilders.termQuery("brand_name.keyword", "宝马"))
//                .should(QueryBuilders.termQuery("",""))
        );

        searchRequest.source(searchSourceBuilder);
        request.add(searchRequest);
        MultiSearchResponse response = client.msearch(request, RequestOptions.DEFAULT);
        res.setData(response.getResponses());

        return res;


    }


}