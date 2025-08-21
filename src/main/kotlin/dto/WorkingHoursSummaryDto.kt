package dto

import java.util.UUID
data class WorkingHoursSummaryDto(
    val employeeId: UUID,
    val totalHours: Double
)