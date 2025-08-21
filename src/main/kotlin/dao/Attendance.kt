package dao
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime
import java.util.UUID

data class Attendance(
    val attendanceId: UUID,
    val employeeId: UUID,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    var dateTimeOfCheckIn: LocalDateTime,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    var dateTimeOfCheckOut: LocalDateTime? = null,
    var workedHours: String = ""
)