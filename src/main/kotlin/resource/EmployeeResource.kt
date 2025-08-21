package resource

import dao.Department
import dao.Role
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import model.EmployeeRequest
import org.slf4j.LoggerFactory
import service.EmployeeService
import java.util.UUID

@Path("/employees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class EmployeeResource(private val employeeService: EmployeeService) {

    private val log = LoggerFactory.getLogger(EmployeeResource::class.java)

    @POST
    fun addEmployee(@Valid request: EmployeeRequest): Response {
        // Validate Role and Department strings
        val role = Role.fromName(request.role)
            ?: return Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to "Invalid role specified. Valid roles are: ${Role.entries.map { it.name }}"))
                .build()

        val department = Department.fromName(request.department)
            ?: return Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to "Invalid department specified. Valid departments are: ${Department.entries.map { it.name }}"))
                .build()

        // Validate reportingTo UUID format if it exists
        val reportingToUUID = try {
            request.reportingto?.let { UUID.fromString(it) }
        } catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to "Invalid UUID format for 'reportingto' field."))
                .build()
        }

        return try {
            val newEmployee = employeeService.addEmployee(
                firstName = request.firstname,
                lastName = request.lastname,
                role = role,
                department = department,
                reportingTo = reportingToUUID
            )
            // On success, return 201 Created with the new employee object
            Response.status(Response.Status.CREATED).entity(newEmployee).build()
        } catch (e: Exception) {
            log.error("Error adding employee", e)
            // Handle potential database constraint violations or other errors
            Response.status(Response.Status.CONFLICT)
                .entity(mapOf("error" to (e.message ?: "Failed to add employee due to a data conflict.")))
                .build()
        }
    }

    @GET
    fun getAllEmployees(): Response {
        val employeeList = employeeService.getAllEmployees()
        return Response.ok(employeeList).build()
    }

    @DELETE
    @Path("/{id}")
    fun deleteEmployee(@PathParam("id") idStr: String): Response {
        val employeeId = try {
            UUID.fromString(idStr)
        } catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(mapOf("error" to "Invalid employee UUID format.")).build()
        }

        // The service returns a Boolean
        val wasDeleted = employeeService.deleteEmployee(employeeId)

        return if (wasDeleted) {
            // If true, return 200 OK
            Response.ok(mapOf("message" to "Employee deleted successfully.")).build()
        } else {
            // If false, it means no employee was found with that ID
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("error" to "Employee not found."))
                .build()
        }
    }
}