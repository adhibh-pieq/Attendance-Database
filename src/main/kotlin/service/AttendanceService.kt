package service // Or your package name
import dao.AttendanceDao
import dao.EmployeeDao
import dao.Attendance
import jakarta.ws.rs.core.Response
import java.time.LocalDateTime
import java.time.LocalDate
import java.util.UUID
import dto.WorkingHoursSummaryDto

class AttendanceService(
    private val attendanceDao: AttendanceDao,
    private val employeeDao: EmployeeDao // Inject EmployeeDao to check if employee exists
) {

    fun validateAndCheckIn(employeeId: UUID, checkIn: LocalDateTime) {
        if (employeeDao.getById(employeeId) == null) {
            // jakarta.ws.rs.NotFoundException is a good choice here
            throw jakarta.ws.rs.NotFoundException("Employee with ID $employeeId not found.")
        }
        if (checkIn.isAfter(LocalDateTime.now())) {
            throw jakarta.ws.rs.BadRequestException("Check-in time cannot be in the future.")
        }
        val date = checkIn.toLocalDate()
        if (attendanceDao.findIncompleteAttendance(employeeId, date) != null) {
            // jakarta.ws.rs.ClientErrorException is suitable for a 409 Conflict
            throw jakarta.ws.rs.ClientErrorException("Already checked in today for employee $employeeId", Response.Status.CONFLICT)
        }
        attendanceDao.insertCheckIn(employeeId, checkIn)
    }

    fun validateAndCheckOut(employeeId: UUID, checkOut: LocalDateTime) {
        if (employeeDao.getById(employeeId) == null) {
            throw jakarta.ws.rs.NotFoundException("Employee with ID $employeeId not found.")
        }
        if (checkOut.isAfter(LocalDateTime.now())) {
            throw jakarta.ws.rs.BadRequestException("Check-out time cannot be in the future.")
        }

        val attendance = attendanceDao.findIncompleteAttendance(employeeId, checkOut.toLocalDate())
            ?: throw jakarta.ws.rs.BadRequestException("No valid check-in record found for employee $employeeId on this day.")

        if (checkOut.isBefore(attendance.dateTimeOfCheckIn)) {
            throw jakarta.ws.rs.BadRequestException("Check-out time cannot be before check-in time.")
        }

        attendanceDao.updateCheckOut(attendance.attendanceId, checkOut)
    }

    fun getAllAttendance(): List<Attendance> = attendanceDao.getAllAttendance()

    fun getWorkingHoursSummary(fromDate: LocalDate, toDate: LocalDate): List<WorkingHoursSummaryDto> {
        if (fromDate.isAfter(toDate)) {
            // Optional: Add business logic validation
            throw IllegalArgumentException("fromDate cannot be after toDate.")
        }
        return attendanceDao.getWorkingHoursSummary(fromDate, toDate)
    }
}