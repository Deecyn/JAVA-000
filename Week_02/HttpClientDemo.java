import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpClientDemo {
    public static void main(String[] args) {
        doGet("http://localhost:8801");
    }

    private static void doGet(String url){
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            System.out.println("响应状态：" + response.getStatusLine());
            if(httpEntity != null){
                System.out.println("响应内容长度：" + httpEntity.getContentLength());
                System.out.println("响应内容：" + EntityUtils.toString(httpEntity));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(httpClient != null){
                    httpClient.close();
                }
                if(response != null){
                    response.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
