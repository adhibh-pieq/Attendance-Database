import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.kotlinModule
import config.Configuration
import dao.AttendanceDao
import dao.EmployeeDao
import io.dropwizard.core.Application
import io.dropwizard.core.setup.Bootstrap
import io.dropwizard.core.setup.Environment
import io.dropwizard.jdbi3.JdbiFactory
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import resource.AttendanceResource
import resource.EmployeeResource
import service.AttendanceService
import service.EmployeeService

class AppMain : Application<Configuration>() {
    override fun initialize(bootstrap: Bootstrap<Configuration>) {
        bootstrap.objectMapper.registerModule(kotlinModule())//convert json data to kt data classes
        bootstrap.objectMapper.registerModule(JavaTimeModule())//handles date and time
        bootstrap.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    override fun run(configuration: Configuration, environment: Environment) {
        // Setup JDBI
        val factory = JdbiFactory() //creates JDBI instance
        val jdbi: Jdbi = factory.build(environment, configuration.database, "postgresql")
        jdbi.installPlugin(SqlObjectPlugin()) //allows writing sql in kotlin
        jdbi.installPlugin(KotlinPlugin()) //helps in mapping db rows to kotlin data classes

        // Employee components
        val employeeDao = EmployeeDao(jdbi)
        val employeeService = EmployeeService(employeeDao)

        // Attendance components
        val attendanceDao = AttendanceDao(jdbi)
        val attendanceService = AttendanceService(attendanceDao, employeeDao)

        // --- Register resources ---
        environment.jersey().register(EmployeeResource(employeeService))
        environment.jersey().register(AttendanceResource(attendanceService))
    }
}
fun main(args: Array<String>) {
    AppMain().run(*args)
}
