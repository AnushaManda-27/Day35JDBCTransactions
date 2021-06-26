import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import com.jdbcTransactions.EmployeePayrollService.IOService;

class EmployeePayrollServiceTest {

	private IOService fileIOService = IOService.FILE_IO;

	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmps = {
				new EmployeePayrollData(1, "Bill", 10000.0),
				new EmployeePayrollData(2, "Teria", 20000.0),
				new EmployeePayrollData(3, "Charlie", 30000.0),
		};

		EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		employeePayrollService.writeEmployeePayrollData(fileIOService);
		employeePayrollService.printData(fileIOService);
		long entries = employeePayrollService.countEntries(fileIOService);
		//	Assert.assertEquals(3, entries);
	}

	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Assert.assertEquals(3, employeePayrollData.size());

	}

	@Test
	public void givenNewSalaryForEmployee_WhenUpdatedShouldSyncWithDB() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService ();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(fileIOService);
		employeePayrollService.updateEmployeeSalary("Teria", 30000.0);
		boolean result = employeePayrollService.checkEmployeePayrollInSynWithDB("Teria");
		Assert.assertTrue(result);
	}

	@Test
	public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService ();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		LocalDate startDate = LocalDate.of(2018, 01, 01);
		LocalDate endDate = LocalDate.now();
		List<EmployeePayrollData> employeePayrollData = 
				employeePayrollService.readEmployeePayrollForDateRange(IOService.DB_IO, startDate, endDate);
		Assert.assertEquals(3, employeePayrollData.size());
	}

	@Test
	public void givenDateRange_WhenAverageSalaryRetrieveByGender_ShouldReturnProperValue() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService ();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String,Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(averageSalaryByGender.get("M").equals(20000.0) &&
				averageSalaryByGender.get("F").equals(30000.0));
	}

	@Test
	public void givenNewEmployee_whenAdded_ShouldSyncWithDB() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService ();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.addEmployeeToPayroll("Mark",50000.00, LocalDate.now(),"M");
		boolean result = employeePayrollService.checkEmployeePayrollInSynWithDB("Mark");
		Assert.assertTrue(result);
	}

}
