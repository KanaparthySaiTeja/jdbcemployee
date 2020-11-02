package com.cg.jdbc;
import java.sql.*;
import java.util.Date;
import java.util.Enumeration;
import java.lang.*;
public class Dbdemo {
    public static void main(String[] args) {
        String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?verifyServerCertificate=false&useSSL=true";
        String userName = "root";
        String password = "1919";
        Connection con=null;
        Statement stmt=null;
        ResultSet rs=null;
        try {
            //1st step to load driver
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded!");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find the driver in the classpath!", e);
        }

        listDrivers();

        try {
            System.out.println("Connecting to database:" + jdbcURL);
            // 2nd is to get the connection object
            con = DriverManager.getConnection(jdbcURL, userName, password);
            System.out.println("Connection is successful!!!!" + con);
            String sql = "SELECT * from employee_payroll;";
            //3rd to create the statement object
            stmt = con.createStatement();
            //4th is to execute SQL query
            rs = stmt.executeQuery(sql);
            while(rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("Name");
                String phone = rs.getString("phone_no");
                String address = rs.getString("Address");
                String department = rs.getString("Department");
                double basic_pay = rs.getDouble("basic_pay");
                double deductions = rs.getDouble("deductions");
                double tax_pay = rs.getDouble("taxable_pay");
                double income_tax = rs.getDouble("income_tax");
                double net_pay = rs.getDouble("net_pay");
                Date start = rs.getDate("start");
                String gender;
                try {
                    gender = String.valueOf(rs.getString("Gender").charAt(0));
                }

                catch (Exception e)
                {
                    gender = "N";
                    e.printStackTrace();
                }


                System.out.println(id + "," + name + "," + phone + "," + address +  "," +
                        department + "," + basic_pay + "," + deductions + "," + net_pay + "," + start+" ,"+gender);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                con.close();
                rs.close();
                stmt.close();
            }
            catch(SQLException e)
            {
                e.printStackTrace();
            }

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