# Enterprise Employee Management System (B2B SaaS)

A robust, multi-tenant Employee Management System built with **Spring Boot** and **Thymeleaf**. This application is designed as a B2B Software-as-a-Service (SaaS), allowing multiple companies to manage their workforce with strict data isolation, role-based access control, and advanced HR workflows.

## 🚀 Features

### 🏢 Multi-Tenancy Architecture
- **Tenant Isolation**: Data is strictly scoped by `Company`. Users from one company cannot access or modify data belonging to another.
- **Unified Codebase**: A single instance of the application serves multiple companies seamlessly using dynamic contextual filtering.

### 🔐 Role-Based Access Control (RBAC)
- **Spring Security Integration**: Fully secured routes with database-backed authentication and BCrypt password encoding.
- **Roles**:
  - `SUPERADMIN`: System-wide access across all tenants.
  - `HR`: Company-wide administrative access to manage departments, employees, and time-off requests.
  - `EMPLOYEE`: Restricted access to view the directory, submit leave requests, and view the organizational chart.

### 👥 Human Resources Workflows
- **Employee Directory**: Centralized management of employee profiles, contact information, job titles, and salaries.
- **Department Management**: Group employees logically into departments.
- **Time-Off (Leave) Management**: Employees can submit time-off requests. HR administrators can review, approve, or reject these requests with a dedicated dashboard view.

### 📊 Organizational Visualization
- **Dynamic Org Chart**: Interactive, hierarchical organization chart built with Google Charts API to visualize reporting structures.

### 🎨 Modern UI/UX
- **Responsive Design**: Built with Bootstrap 5 and custom CSS for a clean, professional interface.
- **Interactive Dashboards**: Metrics and pending task alerts (e.g., pending time-off requests) surfaced immediately upon login.

---

## 🛠️ Tech Stack

- **Backend**: Java 21, Spring Boot 3.4
- **Database**: PostgreSQL
- **ORM / Data Access**: Spring Data JPA, Hibernate
- **Security**: Spring Security 6
- **Frontend**: Thymeleaf, HTML5, Vanilla CSS, Bootstrap 5
- **Data Visualization**: Google Charts API
- **Build Tool**: Maven

---

## 🚦 Getting Started

### Prerequisites
- **Java 21** or higher
- **PostgreSQL** installed and running
- **Maven** (or use the included wrapper)

### Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/EmployeeManagementSystem.git
   cd EmployeeManagementSystem
   ```

2. **Configure Database:**
   Create a PostgreSQL database named `employeedb`.
   Update `src/main/resources/application.properties` with your PostgreSQL credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/employeedb
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   ```

3. **Initialize Data:**
   By default, `spring.jpa.hibernate.ddl-auto=update` is used. On the first run, the `DataInitializer` bean will automatically populate the database with demo companies, departments, users, and a hierarchy of employees if the database is empty.

4. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Access the application:**
   Navigate to `http://localhost:8080` in your web browser.

---

## 🔑 Demo Accounts

The `DataInitializer` sets up the following accounts out-of-the-box for testing:

| Role | Username | Password | Notes |
| :--- | :--- | :--- | :--- |
| **System Admin** | `admin` | `admin@123` | Full system access. |
| **HR Manager** | `hr@acme.com` | `password` | Acme Corp HR admin. |
| **Standard Employee** | `charlie@acme.com` | `password` | Acme Corp developer. |

---

## 📝 License

This project is open-source and available under the [MIT License](LICENSE).