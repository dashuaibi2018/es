package com.dna.utils;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.sniff.Sniffer;

import java.io.IOException;

public class ESClient {

    private static ESClient ESClient;
    private static final String HOSTS = "127.0.0.1:9200";
    private RestClientBuilder builer;
    static Sniffer sniffer;
    private static RestHighLevelClient highClient;

    public ESClient() {
    }

    public static ESClient getInstance() {
        if (ESClient == null) {
            synchronized (ESClient.class) {
                if (ESClient == null) {
                    ESClient = new ESClient();
                    ESClient.initBuilder();
                }
            }
        }
        return ESClient;
    }


    public RestClientBuilder initBuilder() {
        String[] hosts = HOSTS.split(",");
        HttpHost[] httpHosts = new HttpHost[hosts.length];
        for (int i = 0; i < hosts.length; i++) {
            String[] hostSplit = hosts[i].split(":");
            httpHosts[i] = new HttpHost(hostSplit[0], Integer.parseInt(hostSplit[1]), "http");
        }
        builer = RestClient.builder(httpHosts);

        /*RestClientBuilder在构建RestClient实例时可以设置以下可选配置参数*/

        /*1.设置请求头，避免每个请求都必须指定*/
        Header[] defaultHeaders = new Header[]{
                new BasicHeader("Content-Type", "application/json")
        };
        builer.setDefaultHeaders(defaultHeaders);

        //启动嗅探器
//        SniffOnFailureListener sniffOnFailureListener = new SniffOnFailureListener();
//        /*3.设置每次节点发生故障时收到通知的侦听器，内部嗅探到故障时启用*/
//        builer.setFailureListener(sniffOnFailureListener);
//        Sniffer.builder(restClient)
//                .setSniffIntervalMillis(5000)
//                .setSniffAfterFailureDelayMillis(10000)
//                .build();
//        sniffOnFailureListener.setSniffer(sniffer);

        /*4.修改默认请求配置的回调（例如：请求超时，认证等）*/
        builer.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setSocketTimeout(10000));

        return builer;
    }


    public RestHighLevelClient getHighLevelClient() {
        if (highClient == null) {
            synchronized (RestHighLevelClient.class) {
                if (highClient == null) {
                    highClient = new RestHighLevelClient(builer);
                }
            }
        }

        //10s刷新并更新一次节点
//        sniffer = Sniffer.builder(highClient.getLowLevelClient())
////                .setSniffIntervalMillis(5000)
////                .setSniffAfterFailureDelayMillis(15000)
////                .build();

        return highClient;
    }


    public void closeClient() {
        if (null != highClient) {
            try {
                sniffer.close();   //在highClient关闭之前操作
                highClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}