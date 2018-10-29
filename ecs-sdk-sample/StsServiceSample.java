package com.aliyun.sts.sample;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.BasicCredentials;
import com.aliyuncs.auth.STSAssumeRoleSessionCredentialsProvider;
import com.aliyuncs.ecs.model.v20140526.DescribeInstancesRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeInstancesResponse;
import com.aliyuncs.ecs.model.v20140526.DescribeRegionsRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeRegionsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import net.sf.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.SimpleHttpConnectionManager;
//import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class StsServiceSample {
    public static void main(String[] args) throws ClientException, IOException {

        /**
        登陆资产系统
        API httpapi = new API();
        HttpClient httpClient = httpapi.gethttpclinet();

        新建instanceid yun 为空
        ArrayList<String> aliyuninstances = new ArrayList<String>();
        添加arn
        HashMap hashmapname     =     new HashMap();
        hashmapname.put("acs:ram::sdfasdfwer:role/3qwaer4",              "	asdfasfase");

        HashMap hashmap     =     new HashMap();
        hashmap.put("acs:ram::sdfas51234521345:role/345werqa",              "15");

         **/

        //统计各账号机器
        mysql db = new mysql();
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou");
        BasicCredentials basicCredentials = new BasicCredentials(
                "afsdasrtwert24r",
                "asdf345q134rqwsetfga"
        );
        //声明arnlist
        ArrayList<String> arnlist = db.getarn();

        //身份循环开始
        /**
        Iterator<Map.Entry<String, String>> iterator = hashmap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            System.out.println(entry.getKey() + "　：" + entry.getValue());
            System.out.println(entry.getKey().getClass() );
            System.out.println(entry.getValue().getClass() );
         //身份循环开始
         String arn = entry.getKey();
         **/


        //身份循环开始 获取，赋值
        Iterator it = arnlist.iterator();
        while (it.hasNext()) {
            String arn = it.next().toString();
            int sum = 0;
            System.out.println("start arn is" + arn);
            //切换身份
            STSAssumeRoleSessionCredentialsProvider provider = new STSAssumeRoleSessionCredentialsProvider(
                    basicCredentials,
                    //引用变量
                    arn,
                    profile
            );
            //实例化阿里云client
            DefaultAcsClient client = new DefaultAcsClient(profile, provider);
            //获取所有地区
            DescribeRegionsRequest regionsRequest = new DescribeRegionsRequest();
            try {
                DescribeRegionsResponse regionsResponse = client.getAcsResponse(regionsRequest);
                System.out.println("get regions success   ");
                //地区循环
                Iterator it1 = regionsResponse.getRegions().iterator();
                while (it1.hasNext()) {
                    //转换json对象
                    JSONObject jsonStu1 = JSONObject.fromObject(it1.next());
                    String region = jsonStu1.getString("regionId");
//                System.out.println("list  regions  ");
//                System.out.println(region);
                    //获取地区里的ECS
                    DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();
                    describeInstancesRequest.setRegionId(region);
                    try {
                        //获取地区机器总数,并判断是否插入数据库
                        int count = client.getAcsResponse(describeInstancesRequest).getTotalCount();
                        sum = sum + count;
                        if (count == 0) {
                            //当前地区没有机器，下个地区
                            System.out.println("there is no Instances in " + region + " region   ");
                        } else {
                            int PageNumber;
                            int size = 10;
                            //循环读取不同页
                            for (PageNumber = 1; size >= 10; PageNumber++) {
                                //获取当前页机器列表
                                describeInstancesRequest.setPageNumber(PageNumber);
                                DescribeInstancesResponse describeInstancesResponse = client.getAcsResponse(describeInstancesRequest);
                                List<DescribeInstancesResponse.Instance> instanceList = describeInstancesResponse.getInstances();
                                size = instanceList.size();
                                //循环插入机器
                                for (int i = 0; i < size; i++) {
                                    DescribeInstancesResponse.Instance instance = instanceList.get(i);
                                    //事业部
                                    String instanceId = instance.getInstanceId();
//                                    String bname = entry.getValue();
                                    String instanceName = instance.getInstanceName();
                                    String privateIpAddress = "";
                                    String publicIpAddress = "";
                                    List privateIpAddresslist = instance.getVpcAttributes().getPrivateIpAddress();
                                    String eipAddress = instance.getEipAddress().getIpAddress();
                                    List innerIpAddresslist = instance.getInnerIpAddress();
                                    List publicIpAddresslist = instance.getPublicIpAddress();
                                    if(innerIpAddresslist.isEmpty()){
                                        privateIpAddress = privateIpAddresslist.get(0).toString();
                                    }else {
                                        privateIpAddress = innerIpAddresslist.get(0).toString();
                                    }
                                    if(publicIpAddresslist.size()!=0){
                                        publicIpAddress = publicIpAddresslist.get(0).toString();
                                    }
                                    String OSNEnvironment = "阿里云";
                                    String OSName = instance.getOSName();
                                    String regionId = region;
                                    String serialNumber = instance.getSerialNumber();
                                    Date date = new Date();
                                    String strDateFormat = "yyyy-MM-dd HH:mm:ss";
                                    SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
                                    String updateTime = sdf.format(date);
                                    String status = instance.getStatus();
//                                    String arn = arn;

                                    /**
                                    //调用API，插入数据库
                                    httpapi.insertinstance( httpClient, instanceId, bname, instancename,domain,innerIp,
                                             outIp, osname, region, os_sec, status,holder);
                                    System.out.println("post insert  instances database ok");
                                    System.out.println(publicIpAddress);
                                    //添加到列表
                                    aliyuninstances.add(instanceId);
                                     **/

                                    try {
                                        db.updateorinsert(instanceId,instanceName,privateIpAddress,publicIpAddress,eipAddress,OSNEnvironment,OSName,regionId,serialNumber,updateTime,status,arn);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }
                                System.out.println("pages " + PageNumber + " done");
                            }
                            System.out.println("instance in " + region + " is done");
                        }
                    } catch (ClientException e) {
                        System.err.println(e.toString());
                    }
                }
            } catch (ClientException e) {
                e.printStackTrace();
            }

            try {
                db.updatenum(sum,arn);
            } catch (Exception e) {
              e.printStackTrace();
            }

            //当前身份结束
            System.out.println("this arn is end");
        }



        //身份循环结束
        System.out.println("all arn is end");
        db.delinstance();
        db.close();
        System.out.println("job is done");



        /**
        //退出接口
//        ((SimpleHttpConnectionManager)httpClient.getHttpConnectionManager()).closeIdleConnections(0);
//        System.out.println(aliyuninstances);
//        ArrayList<String> dbhostid = null;
//        mysql db = new mysql();
//        try {
//            dbhostid = db.getdbhostid();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Collection dbexit= new ArrayList<String>(dbhostid);
//        dbexit.removeAll(aliyuninstances);
//        System.out.println("old host "+dbexit);
//        Iterator it2 = dbexit.iterator();
//        while (it2.hasNext()) {
//
//            System.out.println("hostid need del");
//            String hostid = it2.next().toString();
//
//            try {
//                System.out.println(hostid);
////                db.delhostid(hostid);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        db.close();
         **/

    }
}
