package com.cg.jdbc;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollService {

    public enum IOService{DB_IO,REST_IO}

    private List<EmployeePayrollData> employeePayrollList;
    private EmployeePayrollDBService employeePayrollDBService;

    public EmployeePayrollService(){
        employeePayrollDBService = EmployeePayrollDBService.getInstance();

    }

    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList){
        this();
        this.employeePayrollList=employeePayrollList;
    }

    public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService){
        if(ioService.equals( IOService.DB_IO ))
            this.employeePayrollList =  employeePayrollDBService.readData();
        return this.employeePayrollList;
    }

    public void updateEmployeeSalary(String name, double salary) {
        //EmployeePayrollDBService employeePayrollDBService = EmployeePayrollDBService.getInstance();
        int result = employeePayrollDBService.updateEmployeeData(name,salary);
        if(result == 0) return;
        EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
        if(employeePayrollData!=null)
            employeePayrollData.salary = salary;
    }

    private EmployeePayrollData getEmployeePayrollData(String name) {
        EmployeePayrollData employeePayrollData;
        employeePayrollData = this.employeePayrollList.stream()
                .filter( employeepayrollDataItem -> employeepayrollDataItem.name.equals( name ))
                .findFirst().orElse( null );
        return employeePayrollData;
    }

    public boolean checkEmployeePayrollInsyncWithDB(String name) {
        //EmployeePayrollDBService employeePayrollDBService = EmployeePayrollDBService.getInstance();
        List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
        return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
    }

    public List<EmployeePayrollData> readEmployeePayrollForDateRange(IOService ioService, LocalDate startDate, LocalDate endDate) {
        if(ioService.equals( IOService.DB_IO ))
            return employeePayrollDBService.getEmployeePayrollForDateRange(startDate,endDate);
        return null;
    }

    public Map<String,Double> readAverageSalaryByGender(IOService ioService){
        if(ioService.equals( IOService.DB_IO ))
            return employeePayrollDBService.getAverageSalaryByGender();
        return null;
    }

    public void addEmployeeToPayroll(String name, String gender,double salary, LocalDate startDate) {
        employeePayrollList.add(employeePayrollDBService.addEmployeeToPayroll(name,gender,salary,startDate));
    }

    public void addEmployeesToPayroll(List<EmployeePayrollData> employeePayrollDataList){
        employeePayrollDataList.forEach( employeePayrollData -> {
            System.out.println("employees being added : "+employeePayrollData.name);
            this.addEmployeeToPayroll(employeePayrollData.name, employeePayrollData.gender, employeePayrollData.salary, employeePayrollData.startDate );
            System.out.println("Employees added: "+employeePayrollData.name);
        } );
        System.out.println(this.employeePayrollList);
    }

    public void addEmployeesToPayrollWithThreads(List<EmployeePayrollData> employeePayrollDataList) {
        Map<Integer, Boolean> employeeAdditionStatus = new HashMap<Integer, Boolean>();
        employeePayrollDataList.forEach( employeePayrollData -> {
            Runnable task = () -> {
                employeeAdditionStatus.put( employeePayrollData.hashCode(), false );
                System.out.println( "employees being added : " + Thread.currentThread().getName() );
                this.addEmployeeToPayroll( employeePayrollData.name,employeePayrollData.gender,employeePayrollData.salary,employeePayrollData.startDate );
                employeeAdditionStatus.put( employeePayrollData.hashCode(), true );
                System.out.println( "Employees added: " + Thread.currentThread().getName() );
            };
            Thread thread = new Thread( task, employeePayrollData.name );
            thread.start();
        });
        while (employeeAdditionStatus.containsValue( false )){
            try {
                Thread.sleep( 10 );
            }catch (InterruptedException e){
            }
        }
        System.out.println(this.employeePayrollList);
    }

    public long countEntries(IOService ioService){
        if(ioService.equals( IOService.DB_IO ))
            return employeePayrollList.size();
        return 0;
    }
}