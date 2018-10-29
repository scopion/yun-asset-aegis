package com.aliyun.sts.sample;


import java.sql.*;
import java.util.ArrayList;

public class mysql {
    public static final String url = "jdbc:mysql://127.0.0.1:3306/databasen?useUnicode=true&characterEncoding=utf-8"; //数据库连接
    public static final String name = "com.mysql.cj.jdbc.Driver";   //程序驱动
    public static final String user = "sfasdfwer";  //用户名
    public static final String password = "gsdaewrtan"; //密码

    public Connection connection = null;
    public PreparedStatement preparedStatement = null;


    public mysql()
    {
        try
        {
            Class.forName(name);// 指定连接类型
            connection = DriverManager.getConnection(url, user, password);// 获取连接
//            pst = conn.prepareStatement(sql);// 准备执行语句
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *
     * 方法名称: close ；
     * 方法描述:  关闭数据库连接 ；
     * 参数 ：
     * 返回类型: void ；
     */
    public void close()
    {
        try
        {
            this.connection.close();
            this.preparedStatement.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public void updatenum(int num,String arn){
        String sql = "UPDATE accountcontacters SET instancenum = ? WHERE arn = ? ";
        try {
            preparedStatement = connection.prepareStatement(sql); // 准备执行语句
            preparedStatement.setInt(1,num);
            preparedStatement.setString(2,arn);
            int result = preparedStatement.executeUpdate();
            System.out.println("出结果");
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void updateorinsert(String instanceId,String instanceName,String privateIpAddress,String publicIpAddress,String eipAddress,String OSNEnvironment,String OSName,String regionId,String serialNumber,String updateTime,String status,String arn){


        //要执行的SQL
        String sql = "insert into cloudhost(instanceId,instanceName,privateIpAddress,publicIpAddress,eipAddress,OSNEnvironment,OSName,regionId,serialNumber,updateTime,status,arn) values(?,?,?,?,?,?,?,?,?,?,?,?) on duplicate key update instanceName=VALUES(instanceName),privateIpAddress=VALUES(privateIpAddress),publicIpAddress=VALUES(publicIpAddress),eipAddress=VALUES(eipAddress),OSNEnvironment=VALUES(OSNEnvironment),OSName=VALUES(OSName),regionId=VALUES(regionId),serialNumber=VALUES(serialNumber),updateTime=VALUES(updateTime),status=VALUES(status),arn=VALUES(arn)";


        try {

            preparedStatement = connection.prepareStatement(sql); // 准备执行语句
            preparedStatement.setString(1,instanceId);
            preparedStatement.setString(2,instanceName);
            preparedStatement.setString(3,privateIpAddress);
            preparedStatement.setString(4,publicIpAddress);
            preparedStatement.setString(5,eipAddress);
            preparedStatement.setString(6,OSNEnvironment);
            preparedStatement.setString(7,OSName);
            preparedStatement.setString(8,regionId);
            preparedStatement.setString(9,serialNumber);
            preparedStatement.setString(10,updateTime);
            preparedStatement.setString(11,status);
            preparedStatement.setString(12,arn);
            int result = preparedStatement.executeUpdate();

//            ResultSet rs = preparedStatement.executeQuery(sql);

            if (result>0){System.out.println("updatesuccess");}
            else{System.out.println("update fail");}
            /**
            while (rs.next()) {

                String hostid = rs.getString(1);

                System.out.println(hostid);
                dbhostid.add(hostid);

            }
             rs.close();
             **/
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println(dbhostid);

//        return dbhostid;

    }
    /**
     *
     * @方法名称: executeNonquery ；
     * @方法描述: 插入、修改、删除等操作 ；
     * @参数 ：@param sql：插入语句
     * @返回类型: boolean ；
     */
    public void delinstance(){
        String sql = "delete from cloudhost where updateTime <(select date_sub(now(),interval 1 week));";

//        boolean flag = false;
        try
        {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();

        } catch (Exception e)
        {
            System.out.println("操作数据库时出现错误！！");
            e.printStackTrace();
        }
    }

    public ArrayList<String> getarn(){
        String sql = "SELECT arn FROM `accountcontacters` WHERE arn like 'acs:ram%' ";
        ArrayList<String> arnlist = new ArrayList<String>();
        try {
            preparedStatement = connection.prepareStatement(sql); // 准备执行语句
            ResultSet result = preparedStatement.executeQuery(sql);
            while(result.next()){
                arnlist.add(result.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  arnlist;
    }

    /**
    public boolean updatedb(String instanceid,String bname,String instancename,String domain,String innerIpAddress,
                            String publicIpAddress,String osname,String region,String os_sec,String status,String holder){
        String sql = "";

        boolean flag = false;

        try
        {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
            flag = true;

        } catch (Exception e)
        {
            System.out.println("操作数据库时出现错误！！");
            e.printStackTrace();
        }

        return flag;

    }

    public boolean insertdb(String instanceid,String bname,String instancename,String domain,String innerIpAddress,
                            String publicIpAddress,String osname,String region,String os_sec,String status,String holder){
        String sql = "";

        boolean flag = false;

        try
        {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
            flag = true;

        } catch (Exception e)
        {
            System.out.println("操作数据库时出现错误！！");
            e.printStackTrace();
        }

        return flag;

    }
     **/








}
