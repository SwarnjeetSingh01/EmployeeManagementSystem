package EMC.EmployeeManagementSystem.controller;

import EMC.EmployeeManagementSystem.model.Company;
import EMC.EmployeeManagementSystem.model.Department;
import EMC.EmployeeManagementSystem.model.Employee;
import EMC.EmployeeManagementSystem.service.DepartmentService;
import EMC.EmployeeManagementSystem.service.EmployeeService;
import EMC.EmployeeManagementSystem.util.AuthUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeWebController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private AuthUtils authUtils;

    // ── List / Search ─────────────────────────────────────────────────────────

    @GetMapping
    public String listEmployees(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Company company = authUtils.getCurrentCompany();
        Page<Employee> employeePage = employeeService.searchEmployees(company, search, deptId, status, page, size);
        List<Department> departments = departmentService.getAllDepartments(company);

        model.addAttribute("employees", employeePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeePage.getTotalPages());
        model.addAttribute("totalElements", employeePage.getTotalElements());
        model.addAttribute("departments", departments);
        model.addAttribute("search", search);
        model.addAttribute("deptId", deptId);
        model.addAttribute("status", status);
        model.addAttribute("activePage", "employees");
        model.addAttribute("pageTitle", "Employees");
        return "employee_list";
    }

    // ── Add Form ──────────────────────────────────────────────────────────────

    @GetMapping("/new")
    public String newEmployeeForm(Model model) {
        Company company = authUtils.getCurrentCompany();
        model.addAttribute("employee", new Employee());
        model.addAttribute("departments", departmentService.getAllDepartments(company));
        model.addAttribute("managers", employeeService.getAllEmployees(company));
        model.addAttribute("statuses", Employee.Status.values());
        model.addAttribute("formTitle", "Add New Employee");
        model.addAttribute("isEdit", false);
        model.addAttribute("activePage", "employees");
        model.addAttribute("pageTitle", "Add Employee");
        return "employee_form";
    }

    // ── Edit Form ─────────────────────────────────────────────────────────────

    @GetMapping("/edit/{id}")
    public String editEmployeeForm(@PathVariable Long id, Model model) {
        Company company = authUtils.getCurrentCompany();
        Employee employee = employeeService.getEmployeeById(company, id);
        model.addAttribute("employee", employee);
        model.addAttribute("departments", departmentService.getAllDepartments(company));
        
        List<Employee> allEmployees = employeeService.getAllEmployees(company);
        allEmployees.removeIf(e -> e.getId().equals(id)); // Cannot be manager of self
        model.addAttribute("managers", allEmployees);
        
        model.addAttribute("statuses", Employee.Status.values());
        model.addAttribute("formTitle", "Edit Employee — " + employee.getName());
        model.addAttribute("isEdit", true);
        model.addAttribute("activePage", "employees");
        model.addAttribute("pageTitle", "Edit Employee");
        return "employee_form";
    }

    // ── Save (Add or Update) ──────────────────────────────────────────────────

    @PostMapping("/save")
    public String saveEmployee(
            @Valid @ModelAttribute("employee") Employee employee,
            BindingResult result,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long managerId,
            Model model,
            RedirectAttributes ra) {

        Company company = authUtils.getCurrentCompany();

        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments(company));
            model.addAttribute("managers", employeeService.getAllEmployees(company));
            model.addAttribute("statuses", Employee.Status.values());
            boolean isEdit = employee.getId() != null;
            model.addAttribute("isEdit", isEdit);
            model.addAttribute("formTitle", isEdit
                    ? "Edit Employee — " + employee.getName()
                    : "Add New Employee");
            model.addAttribute("activePage", "employees");
            model.addAttribute("pageTitle", isEdit ? "Edit Employee" : "Add Employee");
            return "employee_form";
        }

        // Resolve department
        if (departmentId != null && departmentId > 0) {
            Department dept = departmentService.getDepartmentById(company, departmentId);
            employee.setDepartment(dept);
        } else {
            employee.setDepartment(null);
        }

        // Resolve manager
        if (managerId != null && managerId > 0) {
            Employee mgr = new Employee();
            mgr.setId(managerId);
            employee.setManager(mgr);
        } else {
            employee.setManager(null);
        }

        boolean isNew = employee.getId() == null;
        if (isNew) {
            employeeService.saveEmployee(company, employee);
            ra.addFlashAttribute("successMessage", "Employee added successfully.");
        } else {
            employeeService.updateEmployee(company, employee.getId(), employee);
            ra.addFlashAttribute("successMessage", "Employee updated successfully.");
        }

        return "redirect:/employees";
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @PostMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes ra) {
        Company company = authUtils.getCurrentCompany();
        employeeService.deleteEmployee(company, id);
        ra.addFlashAttribute("successMessage", "Employee deleted successfully.");
        return "redirect:/employees";
    }

    // ── CSV Export ────────────────────────────────────────────────────────────

    @GetMapping("/export")
    public void exportCsv(HttpServletResponse response) throws IOException {
        Company company = authUtils.getCurrentCompany();
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"employees.csv\"");

        PrintWriter writer = response.getWriter();
        writer.println("ID,Name,Email,Phone,Job Title,Department,Salary,Status,Hire Date");

        for (Employee emp : employeeService.getAllEmployees(company)) {
            writer.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%.2f,%s,%s%n",
                    emp.getId(),
                    emp.getName().replace("\"", "\"\""),
                    emp.getEmail().replace("\"", "\"\""),
                    emp.getPhone() != null ? emp.getPhone().replace("\"", "\"\"") : "",
                    emp.getJobTitle() != null ? emp.getJobTitle().replace("\"", "\"\"") : "",
                    emp.getDepartmentName().replace("\"", "\"\""),
                    emp.getSalary() != null ? emp.getSalary() : 0.0,
                    emp.getStatus(),
                    emp.getHireDate() != null ? emp.getHireDate().toString() : ""
            );
        }
    }
}
