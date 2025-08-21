package dto
import java.time.LocalDateTime
import java.util.UUID

data class CheckOutDto(
    val employeeId: UUID,
    val checkOut: LocalDateTime
)
