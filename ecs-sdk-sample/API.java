package com.aliyun.sts.sample;

import net.sf.json.JSONArray;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import net.sf.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class API {
    public HttpClient login() throws IOException {
        String url = "http://wool.com/login/";
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setContentCharset("UTF-8");
        PostMethod postMethod = new PostMethod(url);
        String loginname = "";
        String loginpass = "111111";

        NameValuePair[] data = {new NameValuePair("username",loginname),new NameValuePair("password",loginpass)};
        postMethod.setRequestBody(data);

        try {
        httpClient.executeMethod(postMethod);
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            postMethod.releaseConnection();
        }

        return httpClient;

    }

    public ArrayList<String> getdbinstances(HttpClient httpClient) throws IOException {
        String url2 = "http://wol.com/asset/api/";
        httpClient.getParams().setContentCharset("UTF-8");
        PostMethod postMethod2 = new PostMethod(url2);
        ArrayList<String> dbinstance = new ArrayList<String>();

        String responseMsg2 = null;
        try {
            httpClient.executeMethod(postMethod2);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.out.println(out);
            InputStream in = postMethod2.getResponseBodyAsStream();
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            responseMsg2 = out.toString("UTF-8");

        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            postMethod2.releaseConnection();
        }

        //转换json对象
        JSONArray jsonresponse = JSONObject.fromObject(responseMsg2).getJSONArray("data");
        Iterator itresponse = jsonresponse.iterator();
        while (itresponse.hasNext()){
            //获取数据库中hostid
            String hostid = JSONObject.fromObject(itresponse.next()).getString("hostid");
            dbinstance.add(hostid);
        }

        return dbinstance;
    }

    public void insertinstance(HttpClient httpClient,String instanceid,String bname,String instancename,String domain,String innerIpAddress,
                               String publicIpAddress,String osname,String region,String os_sec,String status,String holder){
        String url3 = "http://woal.com/asset/api/";
        httpClient.getParams().setContentCharset("UTF-8");
        PostMethod postMethod3 = new PostMethod(url3);

        NameValuePair[] postdata = {
                new NameValuePair("hostid",instanceid),
                new NameValuePair("bname",bname),
                new NameValuePair("domain_title",instancename),
                new NameValuePair("os_env","线上环境"),
                new NameValuePair("domain",domain),
                new NameValuePair("innerIp",innerIpAddress),
                new NameValuePair("outIp",publicIpAddress),
                new NameValuePair("os_name",osname),
                new NameValuePair("os_locate",region),
                new NameValuePair("status",status),
                //new NameValuePair("csrftoken",""),
                new NameValuePair("os_sec",os_sec),
                new NameValuePair("holder",holder)
        };
        postMethod3.setRequestBody(postdata);
        String responseMsg3 = null;

        try {
            System.out.println("插入数据");
            int code = httpClient.executeMethod(postMethod3);
            System.out.println("输出状态");
            System.out.println(code);//500
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.out.println("输出out对象");
            System.out.println(out);
            InputStream in = postMethod3.getResponseBodyAsStream();
            System.out.println("输出in对象");
            System.out.println(in);

            int len = 0;
            byte[] buf = new byte[1024];
            while((len=in.read(buf))!=-1){
                out.write(buf, 0, len);
            }
            responseMsg3 = out.toString("UTF-8");
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            postMethod3.releaseConnection();
        }
        System.out.println("输出返回");
        System.out.println(responseMsg3);


    }











}

