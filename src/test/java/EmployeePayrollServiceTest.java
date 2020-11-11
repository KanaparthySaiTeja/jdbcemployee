
import com.cg.jdbc.EmployeePayrollData;
import com.cg.jdbc.EmployeePayrollService;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class EmployeePayrollServiceTest {

    @Test
    public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData( EmployeePayrollService.IOService.DB_IO);
        Assert.assertEquals(4,employeePayrollData.size());
    }
}
