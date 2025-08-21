package service
import dao.Employee
import dao.EmployeeDao
import dao.Role
import dao.Department
import jakarta.ws.rs.core.Response
import org.slf4j.LoggerFactory
import java.util.UUID

class EmployeeService(
    private val employeeDao: EmployeeDao
) {
    private val log = LoggerFactory.getLogger(EmployeeService::class.java)

    fun addEmployee(
        firstName: String,
        lastName: String,
        role: Role,
        department: Department,
        reportingTo: UUID?
    ): Employee {
        val emp = Employee(
            firstName = firstName,
            lastName = lastName,
            roleId = role.id,
            departmentId = department.id,
            reportingTo = reportingTo
        )
        employeeDao.insert(emp)
        log.info("Employee added successfully id=${emp.employeeId}")
        return emp
    }

    fun getAllEmployees(): List<Employee> = employeeDao.getAll()

    fun deleteEmployee(id: UUID): Boolean {
        val deleted = employeeDao.delete(id)
        return deleted > 0
    }
}