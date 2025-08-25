# Employee Attendance System

## Key Features
- Employee Management: Add, view, and delete employee profiles.
- Attendance Tracking: Check-in and Check-out functionality.
- Reporting: Generate summaries of working hours.

## Tech Stacks
- Kotlin: Contains entire application logic.
- Dropwizard: Framework used to build RestAPIs.
- PostgreSQL: Database used to store employee and attendance data.
- JDBI: Interacts with the database.
## Request and Response Flow
1. The config.yml file  defines the server port for the application to run and the database credentials needed to establish a connection.
2. The embedded Jetty server receives the client's initial HTTP request.
3. The Jersey framework routes the request to the appropriate Resource method based on the URL and HTTP method.
4. Jackson deserializes the request's JSON body into a validated DTO within the Resource layer.
5. The Resource delegates the request to the Service layer to execute core business logic.
6. The Service calls the DAO layer, which uses JDBI to interact with the database.
7. The DAO fetches data from the database and maps the results to model objects.
8. The resulting data flows back up the stack from the DAO through the Service to the Resource.
9. Jackson serializes the final response object to JSON, and Jetty sends the HTTP response back to the client.

## Folders
## config/
Holds the application's configuration.
- Configuration.kt: Maps settings from config.yml,like port numbers and database username,passwords,url.

## dao/
Contains objects that directly query the database.
- Attendance.kt: Defines the data structure for a single attendance record.
- Employee.kt: Defines the data structure for an employee and its associated roles and departments.
- AttendanceDao.kt: Executes all SQL queries for the attendance table.
- EmployeeDao.kt: Executes all SQL queries for the employee table.

## dto/
Defines the structure of data sent and received over the API.
- CheckInDto.kt: Defines the JSON request body for checking in.
- CheckOutDto.kt: Defines the JSON request body for checking out.
- WorkingHoursSummaryDto.kt: Defines the JSON response for the summary report.

## model/
Holds data models for specific API requests.
- EmployeeRequest.kt: Defines the JSON request body for creating a new employee.

## resource/
Defines the API endpoints that users can access.
- AttendanceResource.kt: Handles all web requests related to attendance.
- EmployeeResource.kt: Handles all web requests related to employee.

## service/
Contains the application's core business logic and rules.
- AttendanceService.kt: Validates and performs all attendance-related actions.
- EmployeeService.kt: Manages employee data.

## Root File
- Application.kt: Initializes and connects all the above components to start the application.

## API Endpoints:
### Employee
- POST- /employees: Adds a new employee.
- GET- /employees: Retrieves all employees.
- DELETE- /employees/{id}: Deletes a specific employee.

### Attendance
- POST- /attendance/checkin: Records an employee's check-in time.
- PUT- /attendance/checkout: Records an employee's check-out time.
- GET- /attendance/all: Retrieves all attendance records.
- GET- /attendance/summary: Reports total hours worked within a date range for all employees (?fromDate=...&toDate=...).