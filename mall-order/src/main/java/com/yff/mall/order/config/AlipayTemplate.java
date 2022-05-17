package com.yff.mall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.yff.mall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private String app_id = "2021000119609249";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCqEs8Sej3Aua9DrrQFO1Qquhq/j5Y5y2Ofo+uu9rxvSYXk1VfGFEkF0YN2cbGvp7kSkYoB2MEYldR1pMBpSQlF5BEn0IAuyqdbvFcEG6h9HGVeoSildDx4xUsaCekBjuGvp5yTrl8FvKz5gwpyrUAmVKon9iibcSQp0hayv0gkyJkzoFHzGfC6RgiC6snowiutL+36MeBICOuoyZ4Kt809a/dONbfz5AUbQph+szCfvaNhLmeJH1yW0QDfib66/iz9EZ7CGlNkyVqkts+YpUGoFQ3mz2KdrYeZ9pAC/cYzHifV7MAy6kEvpNJ8K+Xkbx3XCC5OqOX87m5//eqcLT4HAgMBAAECggEAAocRCMdET67yyeC40DMXhyMI7rvlkapSKrc283y7RDQlh0ccbNc0Vp/MgmAiMegeOgZrtKdSB1mCtNTj/yyUtLM1BhB1NzzPy+86o9b9ZA6d+xAOGJdnpqvX9+UZz2oeNIwbKZ63ztAFChhGeXnVsDwl4z4nUFgYH379015AZjuGwFacxhJO+KVM1ogdbrNE9abQGKe5ofJPPjrcZVP/yTiQOoi+TZEzmklODOvWEeFS1q/eVzsE19djcaqmxHfwcRSjl33zchEsTXc1lVMsrus7HT7xhph/NFqoyP+q21yPUBzTEQdshgdta8nPDOaleNKHPaZ3mqIow2ExmEAUEQKBgQDlk6Mjeuw1k+z7prDioskxhXJMpUSW6wChhDIcA73PVtOSw/Hc5Op2f9SIzhyLVwvLIH6A6gK2+Rf8ZFr1ueb3BkNP+9lxazhgoEQ/x1IngiFMbO6eLcIHKN+FtiPWoRRyXay5gLF8z0NgO59lR8RJ8JOsmLqpvgtVY5xP+WYm/QKBgQC9pfBF8dlZpooSr9HUqSOlR93DHUDKw41/F/LPQCujKlCFXmESspZsksfcpACR6KgxnQTJ8lXs4XkS7C0YFe5+w4g3nFH7ulLZyKZTwE3X472ac44I5SNGZQkSzP7FPyLDfGtYvSHBXcPkAJ6uKJMTHrOR9Zzz8J/4pAPHdrUiUwKBgHoDaE4AE7A6CVaJ9jLNj56JLQE9SOHozjIJf1RSTLv4ioPTLiVXaKFIOR3oZVVNExCHGZTMSlsSskPK1mkIv48EPhwxXxSfhpwcMYmwFqK2z5m9v2hEK34ApCN9dTip7aMG1DjXA/2wbj+kEIgUh2dAvEub9IEoZv+f7EC47SjFAoGANQ3c/9Q+t8PNUJj7HIEMxcorEdB3NcIrLWjntm7/RjAqQoU6pt5rEoilfOYseqUZmHeSxWQUAaa6Fj8YBmrsQ240B0ql7MQdNWvajAd6P3Eos7KsA3UGv0S4mxfaFhgpg4KlDTgr30p56Kj7u0EClBC8r5KEjAG+3vITVJOy7J0CgYEA2eYpU7F3EZOxeARgZlewx+NbexM0Ml+vcezQ4gswCchGEiZ87dH3D7mik3GpdFe6tilAwHUDFK9vk2A72GNbNitO4NWqQNGR+sSbphFJ64MJNHXUfIc6R5RWhuaAfai5tAnhZErP4RXKZwprPdbn8imR9FdOrLebxSnMUgZm7ns=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiO1X1nfDrfim8hw5sre3eHLMrc46/niMS9kaat2bwdsYDZFhhPvS/uYIFWUV5IM5R8sjzhSRxkjMvHol0sM7UbFyNg0G0K9updmiPoVbdyCV4au2R4fhvQ0dYfd5LI279e0bjal9gdR/o00aEju81BEfV8DH+DSwb0fne71PDIqX1+jK+KgMaLRuWwfQufgDBSwwiMDrHGi2ReTHXzLt+Sb02awkwIxpv58jsOm1JBIE5N5JaNYKL8lsgCawGUTMx87u/aTnjnsN8XvEbOFp48QI1VhbhgXJY6SwM4q5NRgRYDwxFBfE9821YjBdSpMbRtEr7FthBUx+KRueeNuRlQIDAQAB";


    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private String notify_url = "http://tawkl4n8w5.51xd.pub/pay/result";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private String return_url = "http://member.mall.com/memberOrderList.html";

    // 签名方式
    private String sign_type = "RSA2";

    // 字符编码格式
    private String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"timeout_express\":\"30m\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应：" + result);

        return result;

    }
}
