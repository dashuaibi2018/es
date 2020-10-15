package com.dna;

import com.dna.entity.Product;
import com.dna.service.ProductService;
import lombok.SneakyThrows;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * @description: ES-Transport API
 * @author: SUJUN
 * @time: 2020/10/14 17:37
 */
@SpringBootTest
public class TransportTests {
    @Autowired
    private ProductService service;

    static TransportClient GetInstance() throws UnknownHostException {
        TransportClient ESClient = new PreBuiltTransportClient(Settings.builder().put("cluster.name", "docker-cluster").build())
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.2.202"), 9300));
        return ESClient;
    }


    @Test
    @SneakyThrows
    void esCRUD() {
        Settings settings = Settings.builder().put("cluster.name", "docker-cluster").build();
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.2.202"), 9300));

        //导入数据
//        create();
        //查询
//        get(client);
//        getAll(client);
//        update(client);
//        deleteById(client);
//        deleteIndex();

        client.close();


    }


    @Test
    void deleteIndex() throws UnknownHostException {
        AcknowledgedResponse response = TransportTests.GetInstance().admin().indices().prepareDelete("product2").execute().actionGet();
        if (response.isAcknowledged()) System.out.println("索引删除成功");

    }

    @Test
    @SneakyThrows
    void create() {
        List<Product> list = service.list();
        for (Product item : list) {
            System.out.println(item.getCreatetime().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            IndexResponse response = TransportTests.GetInstance().prepareIndex("product2", "_doc", UUID.randomUUID().toString())//item.getId().toString())
                    .setSource(XContentFactory.jsonBuilder()
                            .startObject()
                            .field("name", item.getName())
                            .field("desc", item.getDesc())
                            .field("price", item.getPrice())
                            .field("date", item.getCreatetime().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            .field("tags", item.getTags().split(","))
                            .endObject())
                    .get();

            System.out.println(response.getResult());
        }

    }

    void get(TransportClient client) {
        GetResponse response = client.prepareGet("product2", "_doc", "1").get();

        System.out.println(response.getIndex());
        System.out.println(response.getType());
        System.out.println(response.getId());

        System.out.println(response.getSourceAsString());
        System.out.println(response.getSourceAsMap());


    }

    void getAll(TransportClient client) {
        SearchResponse response = client.prepareSearch("product2").get();

        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            String res = hit.getSourceAsString();
            System.out.println("res " + res);
        }

    }

    @SneakyThrows
    void update(TransportClient client) {
        UpdateResponse response = client.prepareUpdate("product2", "_doc", "1")
                .setDoc(XContentFactory.jsonBuilder().startObject()
                        .field("name", "new name").endObject())
                .get();
        System.out.println(response.getResult());
    }

    void deleteById(TransportClient client) {
        DeleteResponse response = client.prepareDelete("prodect2", "_doc", "1").get();
        System.out.println(response.getResult());
    }


    /**
     * @param
     * @description: 多条件查询
     * @return: void
     * @author: SUJUN
     * @time: 2020/10/15 11:11
     */
    @Test
    void multiSearch() throws UnknownHostException {
        SearchResponse response = TransportTests.GetInstance().prepareSearch("product2").setTypes("_doc")
                .setQuery(QueryBuilders.termQuery("name", "xiaomi"))
                .setPostFilter(QueryBuilders.rangeQuery("price").from(0).to(4000))
                .setFrom(0).setSize(5).
                        get();
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(("res " + hit.getSourceAsMap()));
        }

    }

    /**
     * @param
     * @description: 聚合查询
     * @return: void
     * @author: SUJUN
     * @time: 2020/10/15 13:50
     */
    @Test
    void aggSearch() throws UnknownHostException {

        //1.计算并返回聚合分析对象
        SearchResponse response = TransportTests.GetInstance().prepareSearch("product2")
                .addAggregation(
                        AggregationBuilders.dateHistogram("group_by_month")
                                .field("date")
                                .calendarInterval(DateHistogramInterval.MONTH)
                                .minDocCount(1)
                                .subAggregation(
                                        AggregationBuilders
                                                .terms("by_tag")
                                                .field("tags.keyword")
                                                .subAggregation(
                                                        AggregationBuilders
                                                                .avg("avg_price")
                                                                .field("price")
                                                )
                                )
                )
                .execute().actionGet();

        //2. 输出结果
        Map<String, Aggregation> map = response.getAggregations().asMap();
        Aggregation group_by_month = map.get("group_by_month");
        Histogram dates = (Histogram) group_by_month;
        Iterator<Histogram.Bucket> buckets = (Iterator<Histogram.Bucket>) dates.getBuckets().iterator();

        while (buckets.hasNext()) {
            Histogram.Bucket dateBucket = buckets.next();
            System.out.println("\n\n月份 " + dateBucket.getKeyAsString() + "\n计数：" + dateBucket.getDocCount());
            Aggregation by_tag = dateBucket.getAggregations().asMap().get("by_tag");
            StringTerms terms = (StringTerms) by_tag;
            Iterator<StringTerms.Bucket> tagBuckets = terms.getBuckets().iterator();

            while (tagBuckets.hasNext()) {
                StringTerms.Bucket tagBucket = tagBuckets.next();
                System.out.println("\n\n标签名称 " + tagBucket.getKey() + "\n标签数：" + tagBucket.getDocCount());
                Aggregation avg_price = tagBucket.getAggregations().get("avg_price");
                Avg avgPrice = (Avg) avg_price;
                System.out.println("\n\n均价 " + avgPrice.getValue() + "\n");
            }

        }


    }


}
