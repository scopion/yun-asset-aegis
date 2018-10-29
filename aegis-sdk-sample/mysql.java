package com.aliyun.aegis.sample;


import java.sql.*;
import java.util.ArrayList;

public class mysql {
    public static final String url = "jdbc:mysql://127.0.0.1:3306/databasen?useUnicode=true&characterEncoding=utf-8"; //数据库连接
    public static final String name = "com.mysql.cj.jdbc.Driver";   //程序驱动
    public static final String user = "user";  //用户名
    public static final String password = "passn"; //密码

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

    public void insertvul(String uuid,String arn,String name,String aliasName,String necessity,String level,String cvelist,String updatecmd,String time){
        String sql = "insert into aegisvullist(uuid,arn,name,aliasName,necessity,level,cvelist,updatecmd,time) values(?,?,?,?,?,?,?,?,?); ";
        try {
            preparedStatement = connection.prepareStatement(sql); 
            preparedStatement.setString(1,uuid);
            preparedStatement.setString(2,arn);
            preparedStatement.setString(3,name);
            preparedStatement.setString(4,aliasName);
            preparedStatement.setString(5,necessity);
            preparedStatement.setString(6,level);
            preparedStatement.setString(7,cvelist);
            preparedStatement.setString(8,updatecmd);
            preparedStatement.setString(9,time);
            int result = preparedStatement.executeUpdate();
            if (result>0){System.out.println("updatesuccess");}
            else{System.out.println("update fail");}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertwarning(String uuid,String arn,String riskName,String level,String checkItem,String value,String solution1,String solution2,String time){
        String sql = "insert into warninglist(uuid,arn,riskName,level,checkItem,value,solution1,solution2,time) values(?,?,?,?,?,?,?,?,?); ";
        try {
            preparedStatement = connection.prepareStatement(sql); 
            preparedStatement.setString(1,uuid);
            preparedStatement.setString(2,arn);
            preparedStatement.setString(3,riskName);
            preparedStatement.setString(4,level);
            preparedStatement.setString(5,checkItem);
            preparedStatement.setString(6,value);
            preparedStatement.setString(7,solution1);
            preparedStatement.setString(8,solution2);
            preparedStatement.setString(9,time);
            int result = preparedStatement.executeUpdate();
            if (result>0){System.out.println("updatesuccess");}
            else{System.out.println("update fail");}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertwebshell(String arn,String instanceId,String ip,String webshell,String time){
        String sql = "insert into webshelllist(arn,instanceId,ip,webshell,time) values(?,?,?,?,?); ";
        try {
            preparedStatement = connection.prepareStatement(sql); 
            preparedStatement.setString(1,arn);
            preparedStatement.setString(2,instanceId);
            preparedStatement.setString(3,ip);
            preparedStatement.setString(4,webshell);
            preparedStatement.setString(5,time);
            int result = preparedStatement.executeUpdate();
            if (result>0){System.out.println("updatesuccess");}
            else{System.out.println("update fail");}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void inserteventlist(String arn,String instanceId,String ip,String level,String description1,String description2,String description3,String description4,String description5,String time){
        String sql = "insert into eventlist(arn,instanceId,ip,level,description1,description2,description3,description4,description5,time) values(?,?,?,?,?,?,?,?,?,?); ";
        try {
            preparedStatement = connection.prepareStatement(sql); 
            preparedStatement.setString(1,arn);
            preparedStatement.setString(2,instanceId);
            preparedStatement.setString(3,ip);
            preparedStatement.setString(4,level);
            preparedStatement.setString(5,description1);
            preparedStatement.setString(6,description2);
            preparedStatement.setString(7,description3);
            preparedStatement.setString(8,description4);
            preparedStatement.setString(9,description5);
            preparedStatement.setString(10,time);
            int result = preparedStatement.executeUpdate();
            if (result>0){System.out.println("updatesuccess");}
            else{System.out.println("update fail");}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertloglist(String arn,String instanceId,String ip,String username,String loginSourceIp,String type,String time){
        String sql = "insert into loginlogs(arn,instanceId,ip,username,loginSourceIp,type,time) values(?,?,?,?,?,?,?); ";
        try {
            preparedStatement = connection.prepareStatement(sql); 
            preparedStatement.setString(1,arn);
            preparedStatement.setString(2,instanceId);
            preparedStatement.setString(3,ip);
            preparedStatement.setString(4,username);
            preparedStatement.setString(5,loginSourceIp);
            preparedStatement.setString(6,type);
            preparedStatement.setString(7,time);
            int result = preparedStatement.executeUpdate();
            if (result>0){System.out.println("updatesuccess");}
            else{System.out.println("update fail");}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @方法名称: executeNonquery ；
     * @方法描述: 插入、修改、删除等操作 ；
     * @参数 ：@param sql：插入语句
     * @返回类型: boolean ；
     */

    public void delvul(){
        String sql = "TRUNCATE TABLE aegisvullist;";
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
    public void delwarning(){
        String sql = "TRUNCATE TABLE warninglist;";
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
    public void delwebshell(){
        String sql = "TRUNCATE TABLE webshelllist;";
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
    public void deleventlist(){
        String sql = "TRUNCATE TABLE eventlist;";
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
    public void delloginlogs(){
        String sql = "TRUNCATE TABLE loginlogs;";
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
    public void updateaegistatus(String arn,int i){
        String sql = "UPDATE accountcontacters SET aegistatus = ? WHERE arn = ? ";
        try {
            preparedStatement = connection.prepareStatement(sql); // 准备执行语句
            preparedStatement.setInt(1,i);
            preparedStatement.setString(2,arn);
            int result = preparedStatement.executeUpdate();
            System.out.println("出结果");
            System.out.println(result);
        } catch (Exception e) {
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

}


