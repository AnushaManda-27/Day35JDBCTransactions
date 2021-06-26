import java.time.LocalDate;

public class EmployeePayrollData {
	public int id;
	public String name;
	public double salary;
	public LocalDate startDate;
	
	public EmployeePayrollData(int employee_id, String name, double salary) {
	this.id =  employee_id;
	this.name = name;
	this.salary = salary;
	}
	
	public EmployeePayrollData(int employee_id, String name, double salary, LocalDate startDate) {
	this(employee_id,name,salary);
	this.startDate = startDate;
	}
	
	@Override
	public String toString() {
		return "EmployeePayrollData [id=" + id + ", name=" + name + ", salary=" + salary + "]";
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)return true;
		if (o == null || getClass() != o.getClass()) return false;
	EmployeePayrollData that = (EmployeePayrollData) o;
	return id == that.id &&
			Double.compare(that.salary, salary) == 0 &&
			name.equals(that.name);	
	}
}
