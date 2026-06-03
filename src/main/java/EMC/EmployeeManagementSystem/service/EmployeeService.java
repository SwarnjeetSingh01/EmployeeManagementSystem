package EMC.EmployeeManagementSystem.service;

import EMC.EmployeeManagementSystem.dto.DashboardStatsDto;
import EMC.EmployeeManagementSystem.exception.ResourceNotFoundException;
import EMC.EmployeeManagementSystem.model.Company;
import EMC.EmployeeManagementSystem.model.Department;
import EMC.EmployeeManagementSystem.model.Employee;
import EMC.EmployeeManagementSystem.repository.DepartmentRepository;
import EMC.EmployeeManagementSystem.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    // ── Read ──────────────────────────────────────────────────────────────────

    public List<Employee> getAllEmployees(Company company) {
        return employeeRepository.findAllByCompanyOrderByNameAsc(company);
    }

    public Employee getEmployeeById(Company company, Long id) {
        return employeeRepository.findByCompanyAndId(company, id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
    }

    public Page<Employee> searchEmployees(Company company, String search, Long deptId, String statusStr, int page, int size) {
        Employee.Status status = null;
        if (statusStr != null && !statusStr.isBlank()) {
            try { status = Employee.Status.valueOf(statusStr.toUpperCase()); } catch (IllegalArgumentException ignored) {}
        }

        String name = (search != null && !search.isBlank()) ? search.trim() : null;
        Long deptIdParam = (deptId != null && deptId > 0) ? deptId : null;

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return employeeRepository.searchEmployees(company, name, deptIdParam, status, pageable);
    }

    // ── Write ─────────────────────────────────────────────────────────────────

    @Transactional
    public Employee saveEmployee(Company company, Employee employee) {
        employee.setCompany(company);
        
        // Ensure assigned manager also belongs to this company
        if (employee.getManager() != null && employee.getManager().getId() != null) {
             Employee mgr = getEmployeeById(company, employee.getManager().getId());
             employee.setManager(mgr);
        } else {
             employee.setManager(null);
        }
        
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(Company company, Long id, Employee details) {
        Employee employee = getEmployeeById(company, id);
        employee.setName(details.getName());
        employee.setEmail(details.getEmail());
        employee.setPhone(details.getPhone());
        employee.setJobTitle(details.getJobTitle());
        employee.setHireDate(details.getHireDate());
        employee.setSalary(details.getSalary());
        employee.setStatus(details.getStatus());
        employee.setDepartment(details.getDepartment());
        
        if (details.getManager() != null && details.getManager().getId() != null) {
             Employee mgr = getEmployeeById(company, details.getManager().getId());
             employee.setManager(mgr);
        } else {
             employee.setManager(null);
        }

        return employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(Company company, Long id) {
        Employee emp = getEmployeeById(company, id);
        employeeRepository.delete(emp);
    }

    // ── Dashboard Stats ───────────────────────────────────────────────────────

    public DashboardStatsDto getDashboardStats(Company company) {
        DashboardStatsDto stats = new DashboardStatsDto();

        long total = employeeRepository.countByCompany(company);
        long active = employeeRepository.countByCompanyAndStatus(company, Employee.Status.ACTIVE);
        long inactive = employeeRepository.countByCompanyAndStatus(company, Employee.Status.INACTIVE);
        
        List<Department> departments = departmentRepository.findAllByCompanyOrderByNameAsc(company);
        long deptCount = departments.size();

        stats.setTotalEmployees(total);
        stats.setActiveEmployees(active);
        stats.setInactiveEmployees(inactive);
        stats.setTotalDepartments(deptCount);

        // Department breakdown for doughnut chart
        List<Map<String, Object>> breakdown = new ArrayList<>();
        for (Department dept : departments) {
            long count = employeeRepository.countByCompanyAndDepartment(company, dept);
            if (count > 0) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("name", dept.getName());
                entry.put("count", count);
                breakdown.add(entry);
            }
        }
        stats.setDepartmentBreakdown(breakdown);

        // Salary distribution for bar chart
        Map<String, Long> salaryDist = new LinkedHashMap<>();
        salaryDist.put("Under $50k", employeeRepository.countSalaryUnder50k(company));
        salaryDist.put("$50k - $80k", employeeRepository.countSalary50kTo80k(company));
        salaryDist.put("Above $80k", employeeRepository.countSalaryAbove80k(company));
        stats.setSalaryDistribution(salaryDist);

        // Recent hires
        List<Employee> recent = employeeRepository.findTop5ByCompanyOrderByHireDateDesc(company);
        List<DashboardStatsDto.EmployeeSummaryDto> recentDtos = new ArrayList<>();
        for (Employee emp : recent) {
            recentDtos.add(new DashboardStatsDto.EmployeeSummaryDto(
                    emp.getId(),
                    emp.getName(),
                    emp.getEmail(),
                    emp.getJobTitle() != null ? emp.getJobTitle() : "—",
                    emp.getDepartmentName(),
                    emp.getStatus() != null ? emp.getStatus().name() : "ACTIVE",
                    emp.getInitials(),
                    emp.getAvatarColorIndex(),
                    emp.getHireDate() != null ? emp.getHireDate().toString() : "—"
            ));
        }
        stats.setRecentHires(recentDtos);

        return stats;
    }
}
