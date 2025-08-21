# Folder

## config/
This folder holds the application's startup configuration.

Configuration.kt: Maps settings from config.yml, especially for the database connection.

## dao/
This folder contains objects that directly query the database.

Attendance.kt: Defines the data structure for a single attendance record.

Employee.kt: Defines the data structure for an employee and its associated roles and departments.

AttendanceDao.kt: Executes all SQL queries for the attendance table.

EmployeeDao.kt: Executes all SQL queries for the employee table.

## dto/
This folder defines the structure of data sent and received over the API.

CheckInDto.kt: Defines the JSON request body for clocking in.

CheckOutDto.kt: Defines the JSON request body for clocking out.

WorkingHoursSummaryDto.kt: Defines the JSON response for the work hours report.

## model/
This folder holds data models for specific API requests.

EmployeeRequest.kt: Defines the JSON request body for creating a new employee.

## resource/
This folder defines the API endpoints that users can access.

AttendanceResource.kt: Handles all web requests related to attendance, like /checkin and /checkout.

EmployeeResource.kt: Handles all web requests for creating, viewing, and deleting employees at the /employees path.

## service/
This folder contains the application's core business logic and rules.

AttendanceService.kt: Validates and orchestrates all attendance-related actions.

EmployeeService.kt: Implements the logic for managing employee data.

## Root File
Application.kt: Initializes and connects all the above components to start the application.

## API Endpoints:

### Employee

POST- /employees: Adds a new employee.

GET- /employees: Retrieves all employees.

DELETE- /employees/{id}: Deletes a specific employee.

### Attendance

POST- /attendance/checkin: Records an employee's check-in time.

PUT- /attendance/checkout: Records an employee's check-out time.

GET- /attendance/all: Retrieves all attendance records.

GET- /attendance/summary: Reports total hours worked within a date range (?fromDate=...&toDate=...).