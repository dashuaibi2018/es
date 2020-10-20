package com.dna;

import com.dna.entity.City;
import com.dna.entity.Product;
import com.dna.service.CityService;
import com.dna.service.ProductService;
import com.dna.utils.ESClient;
import com.google.gson.Gson;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.client.sniff.ElasticsearchNodesSniffer;
import org.elasticsearch.client.sniff.SniffOnFailureListener;
import org.elasticsearch.client.sniff.Sniffer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @description:ES RestHighLevel API
 * @author: SUJUN
 * @time: 2020/10/14 17:33
 */

@SpringBootTest
class RestHighLevelTests {

//    public static final RestHighLevelClient ESClient = new RestHighLevelClient(
//            RestClient.builder(
//                    new HttpHost("192.168.2.202", 9200, "http")));

    static final RestHighLevelClient highLevelClient = ESClient.getInstance().getHighLevelClient();


    @Resource
    private ProductService productService;

    @Resource
    private CityService cityService;


    /**
     * @param
     * @description: 创建索引
     * @return: void
     * @author: SUJUN
     * @time: 2020/10/15 17:49
     */
    @Test
    public void createIndex() throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.2.202", 9200, "http")));

        CreateIndexRequest request = new CreateIndexRequest("test_sj");
        request.settings(Settings.builder().put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2));

        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        if (createIndexResponse.isAcknowledged()) {
            System.out.println("创建索引成功");
        } else {
            System.out.println("创建索引失败");
        }
    }

    @Test
    public void getIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("*");
        GetIndexResponse response = highLevelClient.indices().get(request, RequestOptions.DEFAULT);

        String[] indices = response.getIndices();
        for (String index : indices) {
            System.out.println("indexName: " + index);
        }
    }


    @Test
    public void deleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("test_sj");
        //AcknowledgedResponse
        AcknowledgedResponse response = highLevelClient.indices().delete(request, RequestOptions.DEFAULT);

        if (response.isAcknowledged()) {
            System.out.println("删除索引成功");
        } else {
            System.out.println("删除索引失败");
        }
    }

    @Test
    public void insertData() throws IOException {

        List<Product> list = productService.list();

        IndexRequest request = new IndexRequest("test_sj");
        Product product = list.get(0);
        Gson gson = new Gson();
        request.id(product.getId().toString());
        request.source(gson.toJson(product), XContentType.JSON);
        IndexResponse response = highLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(response);

    }


    @Test
    public void batchInsertData() throws IOException {
        BulkRequest request = new BulkRequest("test_sj");
        Gson gson = new Gson();
//        Product product = new Product();
//        product.setPrice(3999.00);
//        product.setDesc("xiaomi");
//        for (int i = 0; i < 10; i++) {
//            product.setName("ddname" + i);
//            request.add(new IndexRequest().source(gson.toJson(product), XContentType.JSON));
//        }

        List<Product> list = productService.list();
        for (Product product : list) {
            request.add(new IndexRequest().source(gson.toJson(product), XContentType.JSON));
        }

        BulkResponse response = highLevelClient.bulk(request, RequestOptions.DEFAULT);
        System.out.println("数量为：" + response.getItems().length);
    }


    @Test
    public void getById() throws IOException {
        GetRequest request = new GetRequest("test_sj", "40oCJXUBvlFwiZ4Og9N8");

        String[] includes = {"name", "price"};
        String[] excludes = {"desc"};
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
        request.fetchSourceContext(fetchSourceContext);
        GetResponse response = highLevelClient.get(request, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsMap());

    }


    @Test
    public void delById() throws IOException {
        DeleteRequest request = new DeleteRequest("test_sj", "40oCJXUBvlFwiZ4Og9N8");
        DeleteResponse response = highLevelClient.delete(request, RequestOptions.DEFAULT);
        System.out.println(response.getResult());

    }

    @Test
    public void multiGetById() throws IOException {

        MultiGetRequest request = new MultiGetRequest();
        request.add("test_sj", "7EoCJXUBvlFwiZ4Og9N8");
        request.add(new MultiGetRequest.Item("test_sj", "50oCJXUBvlFwiZ4Og9N8"));

        MultiGetResponse response = highLevelClient.mget(request, RequestOptions.DEFAULT);
        for (MultiGetItemResponse itemResponse : response) {
            System.out.println(itemResponse.getResponse().getSourceAsString());
        }
    }

    @Test
    public void updateByQuery() throws IOException {
        UpdateByQueryRequest request = new UpdateByQueryRequest("test_sj");

        //默认情况下版本冲突会 终止UpdateByQueryRequest进程
        //可设置版本冲突继续
        request.setConflicts("proceed");
        /*限制更新条数*/
        request.setBatchSize(10);
        request.setQuery(QueryBuilders.matchQuery("name", "name2 name5"));

        request.setScript(
                new Script(ScriptType.INLINE, "painless", "ctx._source.desc+='#';", Collections.emptyMap()));
        BulkByScrollResponse response = highLevelClient.updateByQuery(request, RequestOptions.DEFAULT);

        System.out.println(response.getSearchFailures());

    }


    //------------------------------------------------------------------------------------------------------------------------------
    @Test
    public void getHighLevelClient() {
        RestHighLevelClient client = ESClient.getInstance().getHighLevelClient();
        ESClient.getInstance().closeClient();
    }

    //嗅探器
    @Test
    public void sniffer() throws IOException {

        // 监听器
        SniffOnFailureListener sniffOnFailureListener = new SniffOnFailureListener();

        //1.获取Clients
        RestClient restClient = RestClient.builder(
                new HttpHost("192.168.2.202", 9200, "http")
        ).setFailureListener(sniffOnFailureListener).build();//设置用于监听嗅探失败的监听器 绑定监听器

        //2.使用HTTPS
        ElasticsearchNodesSniffer nodesSniffer = new ElasticsearchNodesSniffer(
                restClient, ElasticsearchNodesSniffer.DEFAULT_SNIFF_REQUEST_TIMEOUT, ElasticsearchNodesSniffer.Scheme.HTTPS
        );

        //3.为RestClient绑定嗅探器
        Sniffer sniffer = Sniffer.builder(restClient)
                .setSniffIntervalMillis(5000)  //每隔多久嗅探一次 默5分钟
                .setSniffAfterFailureDelayMillis(30000) //若未绑定监听器则无效，嗅探失败时触发一次嗅探，经过设置的时间之后再次嗅探，直至正常
                .setNodesSniffer(nodesSniffer) //使用HTTPS必须要设置的对象
                .build();

        //启动监听
        sniffOnFailureListener.setSniffer(sniffer);

        //注意释放嗅探器资源 关闭client之前先关闭嗅探器
        sniffer.close();
        restClient.close();
    }


    //测试自动探查节点
    @Test
    public void snifferTest() throws InterruptedException {
//        while(true){
//            Thread.sleep(5000);
//            System.out.println(highLevelClient);
//        }

        RestHighLevelClient client = ESClient.getInstance().getHighLevelClient();
        Iterator<Node> nodes = client.getLowLevelClient().getNodes().iterator();
        while (nodes.hasNext()) {
            Node node = nodes.next();
            System.out.println(node);
        }
        Thread.sleep(5000);
        System.out.println("1000年后：");
        nodes = client.getLowLevelClient().getNodes().iterator();
        while (nodes.hasNext()) {
            Node node = nodes.next();
            System.out.println(node);
        }
        ESClient.getInstance().closeClient();
    }

    /**
     * @param
     * @description: 批量插入
     * @return: void
     * @author: SUJUN
     * @time: 2020/10/19 10:01
     */
    @Test
    public void bulkInit() throws IOException {

        RestHighLevelClient client = ESClient.getInstance().getHighLevelClient();
        GetIndexRequest request = new GetIndexRequest("test_city");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        if (!exists) {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest("test_city");
            createIndexRequest.settings(Settings.builder()
                    .put("index.number_of_shards", 3)
                    .put("index.number_of_replicas", 2));
            CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        }

        List<City> cityList = cityService.list();
        BulkRequest bulkRequest = new BulkRequest("test_city");
        for (int i = 0; i < cityList.size(); i++) {
            bulkRequest.add(new IndexRequest().id(String.valueOf(i)).source(new Gson().toJson(cityList.get(i)), XContentType.JSON));
        }

        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(response.getItems().length);

        ESClient.getInstance().closeClient();
    }


}
