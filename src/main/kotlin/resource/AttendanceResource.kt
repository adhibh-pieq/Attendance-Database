package resource
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import dto.CheckInDto
import dto.CheckOutDto
import service.AttendanceService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Path("/attendance")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class AttendanceResource(private val attendanceService: AttendanceService) {

    @POST
    @Path("/checkin")
    fun checkIn(request: CheckInDto): Response {
        return try {
            attendanceService.validateAndCheckIn(request.employeeId, request.checkIn)
            Response.status(Response.Status.CREATED).entity(mapOf("message" to "Check-in successful.")).build()
        } catch (e: jakarta.ws.rs.WebApplicationException) {
            Response.status(e.response.status).entity(mapOf("error" to e.message)).build()
        } catch (e: Exception) {
            Response.serverError().entity(mapOf("error" to "An unexpected error occurred.")).build()
        }
    }
    @PUT
    @Path("/checkout")
    fun checkOut(request: CheckOutDto): Response {
        return try {
            attendanceService.validateAndCheckOut(request.employeeId, request.checkOut)
            Response.ok(mapOf("message" to "Check-out successful.")).build()
        } catch (e: jakarta.ws.rs.WebApplicationException) {
            Response.status(e.response.status).entity(mapOf("error" to e.message)).build()
        } catch (e: Exception) {
            Response.serverError().entity(mapOf("error" to "An unexpected error occurred.")).build()
        }
    }

    @GET
    @Path("/all")
    fun getAllAttendanceRecords(): Response {
        val records = attendanceService.getAllAttendance()
        return Response.ok(records).build()
    }

    @GET
    @Path("/summary")
    fun getWorkingHoursSummary(
        @QueryParam("fromDate") fromDateStr: String?,
        @QueryParam("toDate") toDateStr: String?
    ): Response {
        if (fromDateStr.isNullOrBlank() || toDateStr.isNullOrBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to "Both 'fromDate' and 'toDate' query parameters are required."))
                .build()
        }

        return try {
            val fromDate = LocalDate.parse(fromDateStr, DateTimeFormatter.ISO_LOCAL_DATE)
            val toDate = LocalDate.parse(toDateStr, DateTimeFormatter.ISO_LOCAL_DATE)
            val summary = attendanceService.getWorkingHoursSummary(fromDate, toDate)
            Response.ok(summary).build()
        } catch (e: DateTimeParseException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to "Invalid date format. Use 'yyyy-MM-dd'."))
                .build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to e.message))
                .build()
        }
    }
}