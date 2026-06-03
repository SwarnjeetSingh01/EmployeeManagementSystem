package EMC.EmployeeManagementSystem.dto;

import java.util.List;
import java.util.Map;

public class DashboardStatsDto {

    private long totalEmployees;
    private long activeEmployees;
    private long inactiveEmployees;
    private long totalDepartments;
    private List<Map<String, Object>> departmentBreakdown;
    private Map<String, Long> salaryDistribution;
    private List<EmployeeSummaryDto> recentHires;
    private long pendingLeaveRequests;

    // ── Nested DTO ────────────────────────────────────────────────────────────

    public static class EmployeeSummaryDto {
        private Long id;
        private String name;
        private String email;
        private String jobTitle;
        private String departmentName;
        private String status;
        private String initials;
        private int avatarColorIndex;
        private String hireDate;

        public EmployeeSummaryDto(Long id, String name, String email, String jobTitle,
                                  String departmentName, String status, String initials,
                                  int avatarColorIndex, String hireDate) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.jobTitle = jobTitle;
            this.departmentName = departmentName;
            this.status = status;
            this.initials = initials;
            this.avatarColorIndex = avatarColorIndex;
            this.hireDate = hireDate;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getJobTitle() { return jobTitle; }
        public String getDepartmentName() { return departmentName; }
        public String getStatus() { return status; }
        public String getInitials() { return initials; }
        public int getAvatarColorIndex() { return avatarColorIndex; }
        public String getHireDate() { return hireDate; }
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public long getTotalEmployees() { return totalEmployees; }
    public void setTotalEmployees(long totalEmployees) { this.totalEmployees = totalEmployees; }

    public long getActiveEmployees() { return activeEmployees; }
    public void setActiveEmployees(long activeEmployees) { this.activeEmployees = activeEmployees; }

    public long getInactiveEmployees() { return inactiveEmployees; }
    public void setInactiveEmployees(long inactiveEmployees) { this.inactiveEmployees = inactiveEmployees; }

    public long getTotalDepartments() { return totalDepartments; }
    public void setTotalDepartments(long totalDepartments) { this.totalDepartments = totalDepartments; }

    public List<Map<String, Object>> getDepartmentBreakdown() { return departmentBreakdown; }
    public void setDepartmentBreakdown(List<Map<String, Object>> departmentBreakdown) {
        this.departmentBreakdown = departmentBreakdown;
    }

    public Map<String, Long> getSalaryDistribution() { return salaryDistribution; }
    public void setSalaryDistribution(Map<String, Long> salaryDistribution) {
        this.salaryDistribution = salaryDistribution;
    }

    public List<EmployeeSummaryDto> getRecentHires() { return recentHires; }
    public void setRecentHires(List<EmployeeSummaryDto> recentHires) { this.recentHires = recentHires; }

    public long getPendingLeaveRequests() { return pendingLeaveRequests; }
    public void setPendingLeaveRequests(long pendingLeaveRequests) { this.pendingLeaveRequests = pendingLeaveRequests; }
}
