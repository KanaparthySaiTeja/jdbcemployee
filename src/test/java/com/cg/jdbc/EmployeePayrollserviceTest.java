package com.cg.jdbc;

import org.junit.Assert;
        import org.junit.Test;
        import java.util.List;
        import static com.cg.jdbc.EmployeePayrollService.IOService.DB_IO;

public class EmployeePayrollserviceTest {

    @Test
    public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData( DB_IO);
        Assert.assertEquals(4,employeePayrollData.size());
    }

    @Test
    public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDataBase() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData( DB_IO);
        employeePayrollService.updateEmployeeSalary("Kiran",36000);
        boolean result = employeePayrollService.checkEmployeePayrollInsyncWithDB("Kiran");
        Assert.assertTrue( result );
    }

}