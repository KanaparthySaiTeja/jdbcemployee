package com.cg.jdbc;
import java.sql.*;
import java.util.Date;
import java.util.Enumeration;
public class Dbdemo {
    public static void main(String[] args) {
        String jdbcURL = "jdbc:mysql://localhost/payroll_service? allowPublicKeyRetrieval=true & useSSL=false";
        String userName = "root";
        String password = "1919";
        Connection con;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find the driver in the classpath!", e);
        }

        listDrivers();

        try {
            System.out.println("Connecting to database:" + jdbcURL);
            con = DriverManager.getConnection(jdbcURL, userName, password);
            System.out.println("Connection is successful!!!!" + con);

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void listDrivers() {
        Enumeration<Driver> driverList = DriverManager.getDrivers();
        while (driverList.hasMoreElements()) {
            Driver driverClass = driverList.nextElement();
            System.out.println(driverClass.getClass().getName());
        }

    }
}
