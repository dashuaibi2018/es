package com.dna;

import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.sniff.Sniffer;

public class HighLevelClient {

    private static RestClientBuilder restClientBuilder = ClientBuilders.getClientBuilder();

    //客户端实例化
    private static RestHighLevelClient restHighLevelClient;
    //嗅探器实例化
    private static Sniffer sniffer;
    //

    public static RestHighLevelClient getClient(){
        restHighLevelClient = new RestHighLevelClient(restClientBuilder);

        //10s刷新并更新一次节点
//        sniffer = Sniffer.builder(restHighLevelClient.getLowLevelClient())
//                .setSniffAfterFailureDelayMillis(5000)
//                .build();


        return restHighLevelClient;
    }


}