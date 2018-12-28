package com.aliyun.aegis.sample;
import com.aliyuncs.AcsResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.BasicCredentials;
import com.aliyuncs.aegis.model.v20161111.*;
import com.aliyuncs.auth.STSAssumeRoleSessionCredentialsProvider;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class Aegis {
    public static void main(String[] args) throws ClientException, IOException {
        int sum = 0;
        BasicCredentials basicCredentials = new BasicCredentials("asfdasdfateawfa", "sdgfasdfger435w2ertrg56464w6534");
        mysql db =new mysql();
		//获取arn列表
        ArrayList<String> arnlist = db.getarn();
		//清空数据库，重新添加
        db.delvul();
        db.delwarning();
        db.delloginlogs();
        db.delwebshell();
        db.deleventlist();
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou");
        SimpleDateFormat dsm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		//循环读取arn，获取数据
        Iterator it = arnlist.iterator();
        while (it.hasNext()) {
            int errorcount = 0;
            String arn = it.next().toString();
            System.out.println("start arn is" + arn);
            STSAssumeRoleSessionCredentialsProvider provider = new STSAssumeRoleSessionCredentialsProvider(basicCredentials, arn, profile);
            DefaultAcsClient client = new DefaultAcsClient(profile, provider);
			//获取vullist
            try {
                System.out.println("|-->Get vul list start...");
                DescribeVulListRequest describeVulListRequest = new DescribeVulListRequest();
                describeVulListRequest.setType("cve");
                describeVulListRequest.setDealed("n");
                DescribeVulListResponse describeVulListResponse = new DescribeVulListResponse();
                int PageNumber;
                int size = 20;
                //循环读取不同页
                for (PageNumber = 1; size >= 20; PageNumber++) {
                    //获取当前页机器列表
                    describeVulListRequest.setCurrentPage(PageNumber);
                    describeVulListResponse = client.getAcsResponse(describeVulListRequest);
                    List<DescribeVulListResponse.VulRecord> vulRecordList = describeVulListResponse.getVulRecords();
                    size = vulRecordList.size();
                    //循环插入机器
                    for (int i = 0; i < size; i++) {
                        String uuid = vulRecordList.get(i).getUuid();
                        String name = vulRecordList.get(i).getName();
                        String aliasName = vulRecordList.get(i).getAliasName();
                        String level = vulRecordList.get(i).getLevel();
                        String cvelist = vulRecordList.get(i).getRelated();
                        Long lasttime = vulRecordList.get(i).getLastTs();
                        String vultime = dsm.format(lasttime);
                        String updateCmd = "" ;
                        List<DescribeVulListResponse.VulRecord.ExtendContentJson.RpmEntityListItem> cmdlist = vulRecordList.get(i).getExtendContentJson().getRpmEntityList();
                        int listsize = cmdlist.size();
                        for (int j = 0; j < listsize; j++) {
                            updateCmd = updateCmd + "," +cmdlist.get(j).getUpdateCmd();
                        }
                        System.out.println(updateCmd);
                        String  necessity=vulRecordList.get(i).getNecessity();
                        db.insertvul(uuid,arn,name,aliasName,necessity,level,cvelist,updateCmd,vultime);
                    }
                }
                System.out.println(describeVulListResponse.getTotalCount());
                System.out.println("|-->Get vul list end...");
            } catch (ClientException e) {
//                e.printStackTrace();
                errorcount++;
                System.out.println("|-->!!!Get vul list error: arn is " + arn);
            }


//            try {
//                System.out.println("|-->Get vul details start...");
//                DescribeVulDetailsRequest dvdReq = new DescribeVulDetailsRequest();
//                dvdReq.setType("cve");
//                dvdReq.setName("oval:com.redhat.rhsa:def:20180169");
//                DescribeVulDetailsResponse dvdResp = client.getAcsResponse(dvdReq);
//                System.out.println(JSONObject.fromObject(dvdResp));
//                System.out.println("|-->Get vul details end...");
//            } catch (ClientException e) {
//                e.printStackTrace();
//                System.out.println("|-->!!!Get vul details error: arn is " + arn);
//            }

            try {
                System.out.println("|-->Get warning start...");
                DescribeWarningRequest describeWarningRequest = new DescribeWarningRequest();
                describeWarningRequest.setPageSize(20);
                describeWarningRequest.setDealed("n");
                describeWarningRequest.setStatusList("1");
                DescribeWarningResponse describeWarningResponse = new DescribeWarningResponse();
                int PageNumber;
                int size = 20;
                //循环读取不同页
                for (PageNumber = 1; size >= 20; PageNumber++) {
                    describeWarningRequest.setCurrentPage(PageNumber);
                    describeWarningResponse = client.getAcsResponse(describeWarningRequest);
                    //获取当前页机器列表
                    List<DescribeWarningResponse.Warning> warningList = describeWarningResponse.getWarnings();
                    size = warningList.size();
                    //循环插入机器
                    for (int i = 0; i < size; i++) {
                        String uuid = warningList.get(i).getUuid();
                        String level = warningList.get(i).getLevel();
                        String riskName = warningList.get(i).getRiskName();
                        List<DescribeWarningResponse.Warning.Detail> detailList =  warningList.get(i).getDetails();
                        int detailsize = detailList.size();
                        for (int j = 0;j < detailsize;j++ ){
                            String checkItem = detailList.get(j).getDetailItems().get(0).getValue();
                            String value = detailList.get(j).getDetailItems().get(1).getValue();
                            String solution1;
                            String solution2;
                            if(detailList.get(j).getDetailItems().size()==3){
                                solution1 = detailList.get(j).getDetailItems().get(2).getValue();
                            }else{
                                solution1 = "";
                            }
                            if(detailList.get(j).getDetailItems().size()==4){
                                 solution2 = detailList.get(j).getDetailItems().get(3).getValue();
                            }else{
                                 solution2 = "";
                            }
	                        String riskfoundtime = warningList.get(i).getlastTime();
                            db.insertwarning(uuid,arn,riskName,level,checkItem,value,solution1,solution2,riskfoundtime);
                        }
                    }
                }
                System.out.println(describeWarningResponse.getTotalCount());
                System.out.println("|-->Get warning end...");
            } catch (ClientException e) {
                //e.printStackTrace();
                errorcount++;
                System.out.println("|-->!!!Get warning error: arn is " + arn);
            }

            try {
                System.out.println("|-->Get webshell start...");
                DescribeWebshellRequest describeWebshellRequest = new DescribeWebshellRequest();
                describeWebshellRequest.setDealed("n");
                DescribeWebshellResponse describeWebshellResponse = new DescribeWebshellResponse();
                int PageNumber;
                int size = 20;
                //循环读取不同页
                describeWebshellResponse = client.getAcsResponse(describeWebshellRequest);
                //获取当前页机器列表
                List<DescribeWebshellResponse.WebshellListItem> webshellListItemList = describeWebshellResponse.getWebshellList();
                size = webshellListItemList.size();
                //循环插入机器
                for (int i = 0; i < size; i++) {
                    String instanceId = webshellListItemList.get(i).getInstanceId();
					System.out.println(instanceId);
                    String ip = webshellListItemList.get(i).getIp();
                    String webshell = webshellListItemList.get(i).getTrojanPath();
                    String foundtime = webshellListItemList.get(i).getFoundTime();
                    db.insertwebshell(arn,instanceId,ip,webshell,foundtime);
                }
                System.out.println("|-->Get webshell end...");
            } catch (ClientException e) {
                //e.printStackTrace();
                errorcount++;
                System.out.println("|-->!!!Get webshell error: arn is " + arn);
            }

            try {
                System.out.println("|-->Get suspicious event start...");
                DescribeSuspiciousEventsRequest describeSuspiciousEventsRequest = new DescribeSuspiciousEventsRequest();
                describeSuspiciousEventsRequest.setDealed("n");
                DescribeSuspiciousEventsResponse describeSuspiciousEventsResponse = new DescribeSuspiciousEventsResponse();
                int PageNumber;
                int size = 20;
                //循环读取不同页
                for (PageNumber = 1; size >= 20; PageNumber++) {
                    //获取当前页机器列表
                    describeSuspiciousEventsRequest.setCurrentPage(PageNumber);
                    describeSuspiciousEventsResponse = client.getAcsResponse(describeSuspiciousEventsRequest);
                    List<DescribeSuspiciousEventsResponse.LogListItem> describeSuspiciousEventsResponseLogList = describeSuspiciousEventsResponse.getLogList();
                    size = describeSuspiciousEventsResponseLogList.size();
                    //循环插入机器
                    for (int i = 0; i < size; i++) {
                        String instanceId = describeSuspiciousEventsResponseLogList.get(i).getInstanceId();
                        String ip = describeSuspiciousEventsResponseLogList.get(i).getIp();
                        String level = describeSuspiciousEventsResponseLogList.get(i).getLevel();
                        List<DescribeSuspiciousEventsResponse.LogListItem.DetailListItem> detailList = describeSuspiciousEventsResponseLogList.get(i).getDetailList();
                        String description1 = detailList.get(0).getValue();
                        String description2 = detailList.get(1).getValue();
                        String description3 = detailList.get(2).getValue();
                        String description4;
                        String description5 ;
                        if(detailList.size()>=4){
                            description4 = detailList.get(3).getValue();
                        }else{
                            description4 = "";
                        }
                        if(detailList.size()>=5){
                            description5 = detailList.get(4).getValue();
                        }else{
                            description5 = "";
                        }
                        Long lasttime = describeSuspiciousEventsResponseLogList.get(i).getLastTime();
                        String eventime = dsm.format(lasttime);		
                        db.inserteventlist(arn,instanceId,ip,level,description1,description2,description3,description4,description5,eventime);
                    }
                }
                System.out.println("|-->Get suspicious event end...");
            } catch (ClientException e) {
                //e.printStackTrace();
                errorcount++;
                System.out.println("|-->!!!Get supicious error: arn is " + arn);
            }

            try {
                System.out.println("|-->Get login log start...");
                DescribeLoginLogsRequest describeLoginLogsRequest = new DescribeLoginLogsRequest();
                describeLoginLogsRequest.setStatuses("0");
                DescribeLoginLogsResponse describeLoginLogsResponse = new DescribeLoginLogsResponse();
                int PageNumber;
                int size = 20;
                //循环读取不同页
                for (PageNumber = 1; size >= 20; PageNumber++) {
                    //获取当前页机器列表
                    describeLoginLogsRequest.setCurrentPage(PageNumber);
                    describeLoginLogsResponse = client.getAcsResponse(describeLoginLogsRequest);
                    List<DescribeLoginLogsResponse.LogListItem> describeLoginLogsResponseLogList = describeLoginLogsResponse.getLogList();
                    size = describeLoginLogsResponseLogList.size();
                    //循环插入机器
                    for (int i = 0; i < size; i++) {
                        String instanceId = describeLoginLogsResponseLogList.get(i).getInstanceId();
                        String ip = describeLoginLogsResponseLogList.get(i).getIp();
                        String username = describeLoginLogsResponseLogList.get(i).getUserName();
                        String loginSourceIp = describeLoginLogsResponseLogList.get(i).getLoginSourceIp();
                        String type = describeLoginLogsResponseLogList.get(i).getType();
                        Long logintime = describeLoginLogsResponseLogList.get(i).getLoginTime();
                        String time = dsm.format(logintime);
                        db.insertloglist(arn,instanceId,ip,username,loginSourceIp,type,time);
                    }
                }
                System.out.println("|-->Get login log end...");
            } catch (ClientException e) {
                //e.printStackTrace();
                errorcount++;
                System.out.println("|-->!!!Get login error: arn is " + arn);
            }

            try {
                DescribeBuySummaryRequest describeBuySummaryRequest = new DescribeBuySummaryRequest();
                DescribeBuySummaryResponse describeBuySummaryResponse = client.getAcsResponse(describeBuySummaryRequest);
                if (describeBuySummaryResponse.getBuySummary().getBuyStatus()){
                    //System.out.println("asdfaerwerqwetqwet");
                    db.updateaegistatus(arn,2);
                }
                else if (errorcount == 5) {
                    db.updateaegistatus(arn,0);
                }
                else {
                    //System.out.println("345234523462634");
                    db.updateaegistatus(arn,1);
                }
            } catch(Exception e) {
                //e.printStackTrace();
                System.out.println("|-->!!!Get buystatus error: arn is " + arn);
            }

        }
        System.out.println("all arn is end");
        System.out.println(sum);
        db.close();
        System.out.println("job is done");

    }
}
