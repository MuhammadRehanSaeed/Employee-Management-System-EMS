# 🏢 EMS – Employee Management System

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-green)
![Java](https://img.shields.io/badge/Java-17-orange)
![JWT](https://img.shields.io/badge/Auth-JWT-blue)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue)
![License](https://img.shields.io/badge/License-MIT-lightgrey)
![Status](https://img.shields.io/badge/Status-In%20Development-orange)

---

## 📌 Overview

EMS (Employee Management System) is a role-based REST API backend built with **Spring Boot, Spring Security, and JWT authentication**. It manages employees, tracks daily attendance (check-in / check-out), handles leave requests, and sends automated email notifications — all with structured **ELK/Kibana-compatible JSON logging** to both console and file.

---

## 🚀 Features

### 🔐 Authentication & Authorization
- JWT-based stateless login
- Role-based access control — **ADMIN** and **EMPLOYEE**
- Account lock support (inactive accounts blocked at JWT filter level)
- Spring Security with `DaoAuthenticationProvider`
- Logout Methodology
- Token Blacklisting after Logout

### 👤 User & Employee Management
- Admin creates user accounts with auto-generated temporary passwords
- Welcome email sent automatically on user creation
- Admin creates employee profiles linked to user accounts
- Full employee CRUD (create, read, update, delete)
- Paginated listing of users and employees

### ⏱️ Attendance System
- Employee check-in / check-out with timestamp recording
- Late arrival detection based on configurable office start time
- Half-day detection based on configurable minimum working minutes
- Daily attendance tracking per employee
- Admin attendance report by date (present / absent / half-day per employee)
- Admin and employee attendance history views

### 🏖️ Leave Management
- Employee can apply for leave with date range and leave type
- Admin can approve or reject pending leave requests
- Admin can view all leave records or filter by employee
- Employee can view their own leave history

### 📧 Email Notifications
- Async email dispatch via `JavaMailSender` (non-blocking thread pool)
- Welcome email with temporary password and reset link on user registration
- Configurable SMTP settings via environment variables

### 📋 Structured JSON Logging (ELK / Kibana)
- Every layer (controller → service → filter) is fully logged
- **Console** — single-line JSON per event, ready for log shippers
- **File** — rolling daily logs at `logs/ems-app.log`, gzip-compressed, 30-day retention
- Log fields: `@timestamp`, `level`, `message`, `logger_name`, `thread_name`, `app`, `env`
- Async appenders — logging never blocks request threads

### 🔧 Utilities
- `SecurityUtil.getAuthenticatedUserId()` — single shared method for extracting the authenticated user ID from the Spring Security context, used across all services

---

## 🛠️ Tech Stack

| Layer            | Technology                          |
|------------------|-------------------------------------|
| Backend          | Spring Boot 4.0.6                   |
| Language         | Java 17                             |
| Security         | Spring Security + JWT (jjwt 0.12.6) |
| Database         | MySQL                               |
| ORM              | Spring Data JPA (Hibernate)         |
| DTO Mapping      | MapStruct 1.6.2                     |
| Logging          | Logback + logstash-logback-encoder 7.4 |
| Email            | Spring Mail (JavaMailSender)        |
| API Docs         | SpringDoc OpenAPI (Swagger UI)      |
| Build Tool       | Maven                               |
| Boilerplate      | Lombok                              |

---

## 📁 Project Structure

```
com.rehancode.ems
 ├── Config
 │   ├── DetailsService        # CustomUserDetailsService, UserPrinicple
 │   ├── Jwt                   # JwtFilter, JwtService
 │   ├── AsyncConfig           # Thread pool for async email
 │   ├── OpenApiConfig         # Swagger / OpenAPI setup
 │   └── SecurityConfig        # Security filter chain, auth provider
 ├── Constants                 # OFFICE_START_TIME, HALF_DAY_MINUTES
 ├── Controller                # AdminController, AttendanceController,
 │                             # AuthController, EmployeeController, LeaveController
 ├── Dto                       # Request/Response DTOs
 │   └── MapStruct             # MapStruct mapper interfaces
 ├── Enum                      # Role, Status, AttendanceStatus, LeaveStatus, LeaveType
 ├── Exception                 # Custom exceptions + GlobalExceptionHandler
 ├── Model                     # UsersModel, EmployeeModel, AttendanceModel, LeaveModel
 ├── Repository                # Spring Data JPA repositories
 ├── Service
 │   └── impl                  # AdminServiceImpl, AttendanceServiceImpl,
 │                             # AuthServiceImpl, EmailServiceImpl,
 │                             # EmployeeServiceImpl, LeaveServiceImpl
 └── Util
     ├── EmailTemplates        # Email body templates
     ├── SecurityUtil          # getAuthenticatedUserId() shared helper
     └── Util                  # generateTempPassword()
```

---

## 🔑 Roles & Permissions

### 🟢 ADMIN
| Action | Endpoint |
|--------|----------|
| Create user account | `POST /api/admin/create` |
| Create employee profile | `POST /api/admin/createEmp` |
| Get user by ID | `GET /api/admin/getUser/{id}` |
| Delete user | `DELETE /api/admin/deleteUser/{id}` |
| List all users (paginated) | `GET /api/admin/users?page=0&size=10` |
| List all employees (paginated) | `GET /api/admin/employees?page=0&size=10` |
| Get employee by ID | `GET /api/admin/getEmp/{id}` |
| Update employee | `PUT /api/admin/update/{id}` |
| Delete employee | `DELETE /api/admin/deleteEmp/{id}` |
| Attendance history (paginated) | `GET /api/admin/getAttendanceHistory?page=0&size=10` |
| Attendance history by employee | `GET /api/admin/getAttendanceHistory/{id}` |
| Daily attendance report | `GET /api/admin/report?date=2025-01-15` |
| All leave requests | `GET /api/admin/getAdminLeaves` |
| Leave requests by employee | `GET /api/admin/getAdminLeaves/{id}` |
| Approve leave | `POST /api/admin/getAdminLeaves/{id}/approve` |
| Reject leave | `POST /api/admin/getAdminLeaves/{id}/reject` |

### 🔵 EMPLOYEE
| Action | Endpoint |
|--------|----------|
| Login | `POST /api/auth/login` |
| Check-in | `POST /api/employee/checkIn` |
| Check-out | `POST /api/employee/checkOut` |
| Today's attendance | `GET /api/employee/today-attendance` |
| Attendance history | `GET /api/employee/attendance-history` |
| My profile | `GET /api/employee/me` |
| Update profile | `PUT /api/employee/updateEmp` |
| Change password | `POST /api/employee/change-password` |
| Apply for leave | `POST /api/leave/apply-leave` |
| My leave history | `GET /api/leave/getLeaves` |

---

## ⚙️ Configuration

### Environment Variables

The following must be set before running the application:

| Variable | Description |
|----------|-------------|
| `JWT_SECRET` | Secret key for signing JWT tokens (min 32 chars) |
| `DB_PASSWORD` | MySQL root password |
| `SMTP_USER` | Gmail address used to send emails |
| `SMTP_PASSWORD` | Gmail app password (not account password) |

### `application.properties` (key settings)

```properties
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/EMS?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Karachi
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${SMTP_USER}
spring.mail.password=${SMTP_PASSWORD}

app.base-url=http://localhost:8080
password.change.path=/api/employee/change-password
```

---

## 📋 Logging

Logging is configured via `src/main/resources/logback-spring.xml`.

### Output format (ELK / Kibana compatible)
```json
{"@timestamp":"2025-01-15T10:30:00.000+05:00","level":"INFO","message":"Login successful for username='john' role='EMPLOYEE'","logger_name":"com.rehancode.ems.Service.impl.AuthServiceImpl","thread_name":"http-nio-8080-exec-1","app":"EMS","env":"default"}
```

### Appenders
| Appender | Destination | Format | Rotation |
|----------|-------------|--------|----------|
| CONSOLE  | stdout | Single-line JSON | — |
| FILE     | `logs/ems-app.log` | Single-line JSON | Daily + 50 MB, 30 days, 500 MB cap |

### Log levels
| Package | Level |
|---------|-------|
| `com.rehancode.ems` | DEBUG |
| `org.hibernate.SQL` | DEBUG |
| `org.springframework.security` | INFO |
| `org.springframework.web` | INFO |
| Root | INFO |

---

## 🚀 Running the Application

### Prerequisites
- Java 17+
- MySQL 8+
- Maven 3.9+

### Steps

1. **Create the database**
   ```sql
   CREATE DATABASE EMS;
   ```

2. **Set environment variables**
   ```bash
   set JWT_SECRET=your_secret_key_minimum_32_characters
   set DB_PASSWORD=your_mysql_password
   set SMTP_USER=your@gmail.com
   set SMTP_PASSWORD=your_app_password
   ```

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access Swagger UI**
   ```
   http://localhost:8080/swagger-ui.html
   ```

---

## 🧩 Key Design Decisions

- **`SecurityUtil.getAuthenticatedUserId()`** — centralises the Spring Security context extraction that was previously duplicated in every service method. All services (`AttendanceServiceImpl`, `EmployeeServiceImpl`, `LeaveServiceImpl`) call this single utility instead of repeating the 10-line auth check.

- **Async email** — email dispatch runs on a dedicated `MailAsync-` thread pool (core: 5, max: 10, queue: 50) so user registration never blocks on SMTP.

- **Temporary passwords** — on user creation, a random 8-character password is generated and emailed to the user. The user must change it via `/api/employee/change-password`.

- **Stateless sessions** — `SessionCreationPolicy.STATELESS` enforced; all state is carried in the JWT.

---

## 📌 Future Improvements
- Attendance analytics dashboard
- Export reports (PDF / Excel)
- Refresh token support
- Password reset via email link

---

## ⚡ Author

**Muhammad Rehan Saeed** — Spring Boot EMS Project
