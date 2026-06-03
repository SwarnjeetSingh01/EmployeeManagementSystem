package EMC.EmployeeManagementSystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email address is required")
    @Email(message = "Please enter a valid email address")
    @Column(nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "^[+]?[0-9\\s\\-().]{7,20}$|^$", message = "Please enter a valid phone number")
    private String phone;

    @Size(max = 100, message = "Job title cannot exceed 100 characters")
    private String jobTitle;

    @PastOrPresent(message = "Hire date cannot be in the future")
    private LocalDate hireDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Salary must be a positive number")
    private Double salary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    @JsonIgnoreProperties("employees")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "manager", "department", "company"})
    private Employee manager;

    public enum Status {
        ACTIVE, INACTIVE
    }

    // ── UI Helper Methods ─────────────────────────────────────────────────────

    public String getInitials() {
        if (name == null || name.isBlank()) return "?";
        String trimmed = name.trim();
        String[] parts = trimmed.split("\\s+");
        if (parts.length >= 2) {
            return (String.valueOf(parts[0].charAt(0)) +
                    String.valueOf(parts[1].charAt(0))).toUpperCase();
        }
        return String.valueOf(trimmed.charAt(0)).toUpperCase();
    }

    public int getAvatarColorIndex() {
        if (name == null) return 0;
        return Math.abs(name.hashCode()) % 6;
    }

    public String getFormattedSalary() {
        if (salary == null) return "—";
        return String.format("$%,.0f", salary);
    }

    public String getDepartmentName() {
        return department != null ? department.getName() : "Unassigned";
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public Employee getManager() { return manager; }
    public void setManager(Employee manager) { this.manager = manager; }
}
