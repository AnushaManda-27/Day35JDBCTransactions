import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollDBService {

	private PreparedStatement employeePayrollDataStatement;
	private List<EmployeePayrollData> employeePayrollList;
	private int employeeId;
	private Object taxable_pay;
	private Object netpay;
	private static EmployeePayrollDBService employeePayrollDBService;
	EmployeePayrollDBService() {	
	}

	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null)
			employeePayrollDBService = new EmployeePayrollDBService();
		return employeePayrollDBService;
	}
	
	//public Object read;
	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName = "root";
		String password = "N@ge$h2019";
		Connection connection;
		System.out.println("Connecting to data base:" +jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, userName,password);
		System.out.println("Connection is sucessful " + connection);
		return connection;
	}
	public List<EmployeePayrollData> readData() {
		String sql = "SELECT * FROM employee_payroll;";
		return this.getEmployeePayrollDataUsingDB(sql);
	}

	public List<EmployeePayrollData> getEmployeePayrollForDateRange(LocalDate startDate, LocalDate endDate) {
		String sql = String.format("SELECT * FROM employee_payroll WHERE START BETWEEN '%s' AND '%s';", 
				Date.valueOf(startDate),Date.valueOf(endDate));
		return this.getEmployeePayrollDataUsingDB(sql);
	}

	public Map<String, Double> getAverageSalaryByGender() {
		String sql = "SELECT gender, Avg(salary) as avg_salary From employee_payroll GROUP BY gender;";
		Map<String, Double> genderToAverageSalaryMap = new HashMap<>();
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while(resultSet.next()) {
				String gender = resultSet.getString("gender");
				double salary = resultSet.getDouble("avg_salary");
				genderToAverageSalaryMap.put(gender, salary);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return genderToAverageSalaryMap;
	}

	private List<EmployeePayrollData> getEmployeePayrollDataUsingDB(String sql) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try {
			Connection connection = this.getConnection();
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			employeePayrollList = this.getEmployeePayrollData(result);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return employeePayrollList;
	}	

	public List<EmployeePayrollData> getEmployeePayrollData(String name) {
		List <EmployeePayrollData> employeePayrollData = null;
		if (this.employeePayrollDataStatement == null)
			this.prepareStatementForEmployeeData();
		try {
			employeePayrollDataStatement.setString(1, name);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			employeePayrollList = this.getEmployeePayrollData(resultSet);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try {
			while(resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				LocalDate startDate  = resultSet.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return employeePayrollList;
	}

	//prepareStatement 
	private void prepareStatementForEmployeeData() {
		try {
			Connection connection = this.getConnection();
			String sql = "Select * from employee_payroll where name = 'Teria' ";
			employeePayrollDataStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public int updateEmployeeData(String name, double salary) {
		
		return this.updateEmployeeDataUsingStatement(name, salary);
	}
	private int updateEmployeeDataUsingStatement(String name, double salary) {
		
		String sql = String.format("update employee_payroll set salary = %.2f where name = 'Teria';", salary);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);	
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		return 0;
	}

	public EmployeePayrollData addEmployeeToPayrollUC7 (String name, double salary, LocalDate startDate, String gender) {
		int Id= -1;
		EmployeePayrollData employeePayrollData = null;
		String sql = String.format("Insert INTO employee_payroll(name, gender, salary, start )" + " VALUES ('%s', '%s', '%s', '%s','%s',)", 
				name, gender, salary, Date.valueOf(startDate));
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if(resultSet.next()) Id = resultSet.getInt(1);
			}
			employeePayrollData = new EmployeePayrollData(Id, name,salary,startDate);
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		return employeePayrollData;
	}
	public EmployeePayrollData addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender) {
		int employeeID = -1;;
		Connection connection = null;
		EmployeePayrollData employeePayrollData = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e){
			e.printStackTrace(); 
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format("Insert INTO employee_payroll(name, gender, salary, start )" + " VALUES ('%s', '%s', '%s', '%s','%s',)", 
					name, gender, salary, Date.valueOf(startDate));
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if(resultSet.next())employeeId = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		try (Statement statement = connection.createStatement()){
			double deductions = salary * 0.2;
			double taxablePay = salary - deductions;
			double tax = taxablePay *0.1;
			double netPay = salary - tax;
			String sql = String.format("INSERT INTO payroll_details(employee_id, basic_pay, deductions, taxable_pay, tax, net_pay)" + 
					" VALUES('%s', '%s', '%s', '%s', '%s',)",
					employeeId, salary, deductions,taxable_pay, tax, netpay );
			int rowAffected = statement.executeUpdate(sql);
			if (rowAffected == 1) {
				employeePayrollData = new EmployeePayrollData(employeeId, name, salary,startDate);
			}
		} catch (SQLException e) {
			e.printStackTrace();

			try {
				connection.rollback();	
			}catch(SQLException ex) {
				ex.printStackTrace();
			}
		} try {
			connection.commit();
		} catch (SQLException e){
			e.printStackTrace();
		} finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return employeePayrollData;
	}
}
