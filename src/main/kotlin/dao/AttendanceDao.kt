package dao
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import dto.WorkingHoursSummaryDto
class AttendanceDao(private val jdbi: Jdbi) {

    fun insertCheckIn(employeeId: UUID, checkInTime: LocalDateTime) {
        jdbi.useHandle<Exception> { handle ->
            handle.createUpdate("""
                INSERT INTO attendance (employee_id, check_in_time)
                VALUES (:employeeId, :checkInTime)
            """)
                .bind("employeeId", employeeId)
                .bind("checkInTime", checkInTime)
                .execute()
        }
    }

    fun updateCheckOut(attendanceId: UUID, checkOutTime: LocalDateTime): Int {
        return jdbi.withHandle<Int, Exception> { handle ->
            handle.createUpdate("UPDATE attendance SET check_out_time = :checkOutTime WHERE attendance_id = :attendanceId")
                .bind("checkOutTime", checkOutTime)
                .bind("attendanceId", attendanceId)
                .execute()
        }
    }

    fun findIncompleteAttendance(employeeId: UUID, date: LocalDate): Attendance? {
        return jdbi.withHandle<Attendance?, Exception> { handle ->
            handle.createQuery("""
                SELECT
                    attendance_id as "attendanceId",
                    employee_id as "employeeId",
                    check_in_time as "dateTimeOfCheckIn",
                    check_out_time as "dateTimeOfCheckOut"
                FROM attendance
                WHERE employee_id = :employeeId
                  AND check_in_time >= :startOfDay
                  AND check_in_time <= :endOfDay
                  AND check_out_time IS NULL
            """)
                .bind("employeeId", employeeId)
                .bind("startOfDay", date.atStartOfDay())
                .bind("endOfDay", date.atTime(LocalTime.MAX))
                .mapTo<Attendance>()
                .findOne()
                .orElse(null)
        }
    }

    fun getWorkingHoursSummary(fromDate: LocalDate, toDate: LocalDate): List<WorkingHoursSummaryDto> {
        return jdbi.withHandle<List<WorkingHoursSummaryDto>, Exception> { handle ->
            handle.createQuery("""
                SELECT
                    employee_id AS "employeeId",
                    -- Calculate the total seconds worked and convert to hours
                    SUM(EXTRACT(EPOCH FROM (check_out_time - check_in_time))) / 3600.0 AS "totalHours"
                FROM
                    attendance
                WHERE
                    -- Filter records within the date range (inclusive)
                    check_in_time >= :startOfFromDate
                    AND check_in_time < :startOfNextDay
                    AND check_out_time IS NOT NULL
                GROUP BY
                    employee_id
                ORDER BY
                    employee_id;
            """)
                .bind("startOfFromDate", fromDate.atStartOfDay())
                .bind("startOfNextDay", toDate.plusDays(1).atStartOfDay()) // Exclusive end for accurate range
                .mapTo<WorkingHoursSummaryDto>()
                .list()
        }
    }

    fun getAllAttendance(): List<Attendance> {
        return jdbi.withHandle<List<Attendance>, Exception> { handle ->
            handle.createQuery("""
            SELECT
                attendance_id as "attendanceId",
                employee_id as "employeeId",
                check_in_time as "dateTimeOfCheckIn",
                check_out_time as "dateTimeOfCheckOut",
                TO_CHAR(check_out_time - check_in_time, 'HH24:MI:SS') as "workedHours"
            FROM attendance
            ORDER BY check_in_time DESC
        """)
                .mapTo<Attendance>()
                .list()
        }
    }
}