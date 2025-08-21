package dto
import java.time.LocalDateTime
import java.util.UUID

data class CheckInDto(
    val employeeId: UUID,
    val checkIn: LocalDateTime
)
