package com.cg.jdbc;


import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class EmployeePayrollDBService {

    private PreparedStatement employeePayrollDataStatement;
    private static EmployeePayrollDBService employeePayrollDBService;

    private EmployeePayrollDBService(){
    }

    public static EmployeePayrollDBService getInstance(){
        if(employeePayrollDBService == null)
            employeePayrollDBService = new EmployeePayrollDBService();
        return employeePayrollDBService;
    }

    private Connection getConnection(){
        String jdbcURL ="jdbc:mysql://localhost:3306/payroll_service?allowPublicKeyRetrieval=true&useSSL=false";
        String userName="root";
        String password="1919";
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver Loaded!!");
        }catch (ClassNotFoundException e) {
            throw new IllegalStateException("driver not found in the classpath", e);
        }

        listDrivers();
        try {
            System.out.println("Connecting to the Database " + jdbcURL);
            connection = DriverManager.getConnection(jdbcURL, userName, password);
            System.out.println("Connection was successful");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void listDrivers() {
        Enumeration<Driver> driverList = DriverManager.getDrivers();
        while (driverList.hasMoreElements()) {
            Driver driverClass = (Driver) driverList.nextElement();
            System.out.println( "  " + driverClass.getClass().getName() );
        }
    }

    public List<EmployeePayrollData> readData(){
        String sql = "Select * from employee_payroll";
        return this.getEmployeeDataUsingDB(sql);
    }

    public int updateEmployeeData(String name, double salary) {
        return this.updateEmployeeDataUsingPreparedStatement( name,salary );
    }

    private int updateEmployeeDataUsingStatement(String name,double salary){
        String sql = String.format( "update employee_payroll set salary = %.2f where name = '%s';",salary,name );
        try(Connection connection =this.getConnection()) {
            Statement statement = connection.createStatement();
            return statement.executeUpdate( sql );
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    private int updateEmployeeDataUsingPreparedStatement(String name,double salary){
        String sql = "update employee_payroll set salary = ? where name = ?";
        try(Connection connection =this.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble( 1,salary );
            preparedStatement.setString( 2,name );
            int status =preparedStatement.executeUpdate();
            return status;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public List<EmployeePayrollData> getEmployeePayrollData(String name) {
        List<EmployeePayrollData> employeePayrollList = null;
        if(this.employeePayrollDataStatement == null)
            this.prepareStatementForemployeeData();
        try{
            employeePayrollDataStatement.setString( 1,name );
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            employeePayrollList = this.getEmployeePayrollData( resultSet );
        }catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try{
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString( "name" );
                double salary = resultSet.getDouble("salary");
                LocalDate startDate = resultSet.getDate("start").toLocalDate();
                employeePayrollList.add(new EmployeePayrollData( id,name,salary,startDate ));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private void prepareStatementForemployeeData() {
        try{
            Connection connection = this.getConnection();
            String sql = "Select * from employee_payroll where name = ?";
            employeePayrollDataStatement = connection.prepareStatement( sql );
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public List<EmployeePayrollData> getEmployeePayrollForDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = String.format( "select * from employee_payroll where start between '%s' and '%s';",
                Date.valueOf(startDate),Date.valueOf(endDate));
        return this.getEmployeeDataUsingDB(sql);
    }

    private List<EmployeePayrollData> getEmployeeDataUsingDB(String sql) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try(Connection connection =this.getConnection()){
            Statement statement =connection.createStatement();
            ResultSet resultSet = statement.executeQuery( sql );
            employeePayrollList = this.getEmployeePayrollData( resultSet );
        }catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    public Map<String, Double> getAverageSalaryByGender() {
        String query = "select gender,avg(salary) as avg_salary from employee_payroll group by gender";
        Map<String,Double> genderToAverageSalaryMap = new HashMap<>();
        try(Connection connection = this.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String gender = rs.getString( 1 );
                Double avg = rs.getDouble( 2 );
                genderToAverageSalaryMap.put( gender,avg );
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return genderToAverageSalaryMap;
    }

    public EmployeePayrollData addEmployeeToPayrollUC7(String name,String gender, double salary, LocalDate startDate) {
        int employee_id = -1;
        EmployeePayrollData employeePayrollData = null;
        String sql = String.format( "Insert into employee_payroll(name,gender,salary,start)" +
                "values ('%s','%s','%s','%s')",name,gender,salary,Date.valueOf(startDate));
        try(Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            int rowaffected = statement.executeUpdate( sql,statement.RETURN_GENERATED_KEYS );
            if(rowaffected == 1){
                ResultSet resultSet = statement.getGeneratedKeys();
                if(resultSet.next())
                    employee_id = resultSet.getInt( 1 );
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollData;
    }

    public EmployeePayrollData addEmployeeToPayroll(String name,String gender, double salary, LocalDate startDate,String comp_name,String dept_name,String comp_id) {
        int employee_id = -1;
        Connection connection = null;
        EmployeePayrollData employeePayrollData = null;
        try {
            connection = this.getConnection();
            connection.setAutoCommit( false );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try(Statement statement = connection.createStatement()){
            String sql = String.format( "Insert into employee_payroll(name,gender,salary,start,comp_name,dept_name,comp_id)" +
                    "values ('%s','%s','%s','%s','%s','%s','%s')",name,gender,salary,Date.valueOf(startDate),comp_name,dept_name,comp_id);
            int rowaffected = statement.executeUpdate( sql,statement.RETURN_GENERATED_KEYS );
            if(rowaffected == 1){
                ResultSet resultSet = statement.getGeneratedKeys();
                if(resultSet.next())
                    employee_id = resultSet.getInt( 1 );
            }
        } catch (SQLException e){
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        try(Statement statement = connection.createStatement()){
            double deductions = salary*0.2;
            double taxablepay = salary-deductions;
            double tax = taxablepay*0.1;
            double netPay = salary-tax;
            String sql = String.format( "Insert into payroll_details(employee_id,basic_pay,deductions,taxable_pay,tax,net_pay) values"+
                    "(%s, %s, %s, %s, %s, %s)",employee_id,salary,deductions,taxablepay,tax,netPay);

            int rowAffected = statement.executeUpdate(sql);
            if (rowAffected == 1)
                employeePayrollData = new EmployeePayrollData( employee_id,name,gender,salary,startDate,comp_name,dept_name,comp_id);
        }catch (Exception e){
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        try(Statement statement = connection.createStatement()){
            String sql = String.format( "Insert into company(employee_id,comp_name,comp_id) values"+
                    "(%s, '%s', '%s')",employee_id,comp_name,comp_id);

            int rowAffected = statement.executeUpdate(sql);
            if (rowAffected == 1)
                employeePayrollData = new EmployeePayrollData( employee_id,name,gender,salary,startDate,comp_name,dept_name,comp_id);
        }catch (Exception e){
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        try(Statement statement = connection.createStatement()){
            String sql = String.format( "Insert into department(dept_name,emp_id) values"+
                    "( '%s', %s)",dept_name,employee_id);

            int rowAffected = statement.executeUpdate(sql);
            if (rowAffected == 1)
                employeePayrollData = new EmployeePayrollData( employee_id,name,gender,salary,startDate,comp_name,dept_name,comp_id);
        }catch (Exception e){
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        try(Statement statement = connection.createStatement()){
            String sql = String.format( "Insert into employee_department(emp_id,dept_name) values"+
                    "( %s, '%s')",employee_id,dept_name);

            int rowAffected = statement.executeUpdate(sql);
            if (rowAffected == 1)
                employeePayrollData = new EmployeePayrollData( employee_id,name,gender,salary,startDate,comp_name,dept_name,comp_id);
        }catch (Exception e){
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        try(Statement statement = connection.createStatement()){
            String sql = String.format( "Insert into employee_department(emp_id,dept_name) values"+
                    "( %s, '%s')",employee_id,dept_name);

            int rowAffected = statement.executeUpdate(sql);
            if (rowAffected == 1)
                employeePayrollData = new EmployeePayrollData( employee_id,name,gender,salary,startDate,comp_name,dept_name,comp_id);
        }catch (Exception e){
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        try {
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
        return employeePayrollData;
    }
}