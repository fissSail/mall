package com.yff.mall.mallthirdparty;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.yff.mall.mallthirdparty.component.SendSmsComponent;
import com.yff.mall.mallthirdparty.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class MallThirdPartyApplicationTests {

    @Autowired
    private OSSClient ossClient;

    @Autowired
    private SendSmsComponent sendSmsComponent;

    @Test
    void contextLoads() {
        try {

            InputStream inputStream = new FileInputStream("C:\\Users\\yanff\\OneDrive\\图片\\本机照片\\微信图片_20210310154626.jpg");

            ossClient.putObject("mall-yff-2021", "22.jpg", inputStream);
        } catch (OSSException | FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }
    }

    @Test
    public void sendSms(){
        String host = "https://dfsns.market.alicloudapi.com";
        String path = "/data/send_sms";
        String method = "POST";
        String appcode = "ae1308bcf1fd40579a88b63257734cb3";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("content", "code:yff123");
        bodys.put("phone_number", "18170090855");
        bodys.put("template_id", "TPL_0000");


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendsms(){
        sendSmsComponent.sandSms("18170090855","yff");
    }

}
