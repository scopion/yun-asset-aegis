package com.aliyun.domain.sample;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
//import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;

import java.io.IOException;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    //初始化client
    private static IAcsClient client = null;

    static {
        String regionId = "cn-hangzhou"; //必填固定值，必须为“cn-hanghou”
        String accessKeyId = "sfdasdfaetwer"; // your accessKey
        String accessKeySecret = "asgsdartertwetyw43534qergfta";// your accessSecret
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        // 若报Can not find endpoint to access异常，请添加以下此行代码
        // DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Alidns", "alidns.aliyuncs.com");
        client = new DefaultAcsClient(profile);

    }

    public static void main(String[] args) throws IOException {
//
        System.out.println("Hello World!");
        //登陆资产系统
        API httpapi = new API();
        HttpClient httpClient = httpapi.login();
        //获取域名
        try {
            DescribeDomainsRequest describeDomainsRequest = new DescribeDomainsRequest();
//            int TotalCount = describeDomainsResponse.getTotalCount().intValue();
//
//            System.out.println(TotalCount);
            long PageNumber;
            int size = 20;
            int i = 0;
            //循环读取不同页
            for (PageNumber = 1L; size >= 20; PageNumber++) {
                System.out.println("1111111");
                describeDomainsRequest.setPageNumber(PageNumber);
                DescribeDomainsResponse describeDomainsResponse = client.getAcsResponse(describeDomainsRequest);
                List<DescribeDomainsResponse.Domain> list = describeDomainsResponse.getDomains();
                size = list.size();
                System.out.println(size);
                //获取一页中的域名
                for (DescribeDomainsResponse.Domain domain : list) {
//                    JSONObject json =  JSONObject.fromObject(domain);
//                    System.out.println(json);
                    String domainname = domain.getDomainName();
                    String instanceId = domain.getInstanceId();
                    if(instanceId == null){
                        instanceId = domainname;
                    }
                    String bname = "33";
                    String instancename = domainname;
                    String innerIp = "";
                    String outIp = "";
                    String osname = domainname;
                    String region = domainname;
                    String os_sec = "阿里云";
                    String status = "1";
                    String holder = domainname;
                    System.out.println(domainname);
                    //调用API，插入数据库
                    httpapi.insertinstance( httpClient, instanceId, bname, instancename,domainname,innerIp,
                            outIp, osname, region, os_sec, status,holder);
                    System.out.println("post insert  instances database ok");

                }
                System.out.println("page" + PageNumber + "  done ");


            }
            System.out.println("all done");


        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
