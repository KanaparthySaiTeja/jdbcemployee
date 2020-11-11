package com.cg.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

public class DBDemo

{
    public static void main(String[] args) {
        String jdbcURL ="jdbc:mysql://localhost:3306/payroll_service?allowPublicKeyRetrieval=true&useSSL=false";
        String userName="root";
        String password="1919";
        Connection con;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded");
        }
        catch(ClassNotFoundException e)
        {
            throw new IllegalStateException("Cannot find the driver in the classpath",e);
        }

        try{
            System.out.println("Connecting to database:"+jdbcURL);
            con = DriverManager.getConnection(jdbcURL,userName,password);
            System.out.println("Connection is successful!!"+con);
        } catch (Exception e) {
            e.printStackTrace();
        }
        listDrivers();
    }
    private static void listDrivers(){
        Enumeration<Driver> driverList= DriverManager.getDrivers();
        while(driverList.hasMoreElements()){
            Driver driverClass =(Driver) driverList.nextElement();
            System.out.println("  "+driverClass.getClass().getName());
        }
    }
}