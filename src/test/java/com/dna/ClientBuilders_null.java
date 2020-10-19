package com.dna;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

public class ClientBuilders_null {
    private static final String HOSTS = "192.168.2.202:9200";


    public static RestClientBuilder getClientBuilder() {
        String[] hostNamePort = HOSTS.split(",");

        String host;
        int port;
        String[] temp;

        RestClientBuilder restClientBuilder = null;

        if (0 != hostNamePort.length) {
            for (String hostPort : hostNamePort) {
                temp = hostPort.split(":");
                host = temp[0].trim();
                port = Integer.parseInt(temp[1].trim());
                RestClient.builder(new HttpHost(host, port, "http"));
            }
        }

        /*RestClientBuilder在构建RestClient实例时可以设置以下可选配置参数*/

        /*1.设置请求头，避免每个请求都必须指定*/
        Header[] defaultHeaders = new Header[]{
                new BasicHeader("header", "application/json")
        };
        restClientBuilder.setDefaultHeaders(defaultHeaders);


        /*3.设置每次节点发生故障时收到通知的侦听器，内部嗅探到故障时启用*/
//        restClientBuilder.setFailureListener(onFailure(node) -> { super.onFailure(node);});

        /*4.修改默认请求配置的回调（例如：请求超时，认证等）*/
        restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setSocketTimeout(10000));


        return restClientBuilder;
    }


    public static void main(String[] args) {


    }
}