package com.cg.jdbc;


import java.time.LocalDate;
import java.util.Objects;

public class EmployeePayrollData {

    int id;
    String name;
    double salary;
    LocalDate startDate;
    String gender;
    String comp_name;
    String dept_name;
    String comp_id;


    public EmployeePayrollData(int id, String name, double salary, LocalDate startDate) {
        this.id=id;
        this.name=name;
        this.salary=salary;
        this.startDate=startDate;
    }

    public EmployeePayrollData(int id, String name,String gender, double salary, LocalDate startDate) {
        this(id,name,salary,startDate);
        this.gender = gender;
    }

    public EmployeePayrollData(int id, String name,String gender, double salary, LocalDate startDate, String comp_name,String dept_name,String comp_id) {
        this(id,name,gender,salary,startDate);
        this.comp_name = comp_name;
        this.dept_name = dept_name;
        this.comp_id=comp_id;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getComp_name() {
        return comp_name;
    }

    public void setComp_name(String comp_name) {
        this.comp_name = comp_name;
    }

    public String getDept_name() {
        return dept_name;
    }

    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }

    @Override
    public String toString() {
        return "EmployeePayrollData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", salary=" + salary +
                ", startDate=" + startDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeePayrollData that = (EmployeePayrollData) o;
        return id == that.id &&
                Double.compare( that.salary, salary ) == 0 &&
                Objects.equals( name, that.name ) &&
                Objects.equals( startDate, that.startDate );
    }

    @Override
    public int hashCode(){
        return Objects.hash( name,gender,salary,startDate );
    }
}