package model
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class EmployeeRequest(
    @get:NotBlank(message = "First name is required")
    val firstname: String,

    @get:NotBlank(message = "Last name is required")
    val lastname: String,

    @get:NotBlank(message = "Role is required")
    val role: String,  // Will be mapped to Role enum

    @get:NotBlank(message = "Department is required")
    val department: String,  // Will be mapped to Department enum

    val reportingto: String? = null
)
