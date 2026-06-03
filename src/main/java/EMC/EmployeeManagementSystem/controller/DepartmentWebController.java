package EMC.EmployeeManagementSystem.controller;

import EMC.EmployeeManagementSystem.model.Company;
import EMC.EmployeeManagementSystem.model.Department;
import EMC.EmployeeManagementSystem.service.DepartmentService;
import EMC.EmployeeManagementSystem.service.EmployeeService;
import EMC.EmployeeManagementSystem.util.AuthUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/departments")
public class DepartmentWebController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AuthUtils authUtils;

    // ── List ──────────────────────────────────────────────────────────────────

    @GetMapping
    public String listDepartments(Model model) {
        Company company = authUtils.getCurrentCompany();
        model.addAttribute("departments", departmentService.getAllDepartments(company));
        model.addAttribute("totalEmployees", employeeService.getAllEmployees(company).size());
        model.addAttribute("activePage", "departments");
        model.addAttribute("pageTitle", "Departments");
        return "department_list";
    }

    // ── Add Form ──────────────────────────────────────────────────────────────

    @GetMapping("/new")
    public String newDepartmentForm(Model model) {
        model.addAttribute("department", new Department());
        model.addAttribute("formTitle", "Add Department");
        model.addAttribute("isEdit", false);
        model.addAttribute("activePage", "departments");
        model.addAttribute("pageTitle", "Add Department");
        return "department_form";
    }

    // ── Edit Form ─────────────────────────────────────────────────────────────

    @GetMapping("/edit/{id}")
    public String editDepartmentForm(@PathVariable Long id, Model model) {
        Company company = authUtils.getCurrentCompany();
        Department dept = departmentService.getDepartmentById(company, id);
        model.addAttribute("department", dept);
        model.addAttribute("formTitle", "Edit Department — " + dept.getName());
        model.addAttribute("isEdit", true);
        model.addAttribute("activePage", "departments");
        model.addAttribute("pageTitle", "Edit Department");
        return "department_form";
    }

    // ── Save ──────────────────────────────────────────────────────────────────

    @PostMapping("/save")
    public String saveDepartment(
            @Valid @ModelAttribute("department") Department department,
            BindingResult result,
            Model model,
            RedirectAttributes ra) {

        Company company = authUtils.getCurrentCompany();

        if (result.hasErrors()) {
            boolean isEdit = department.getId() != null;
            model.addAttribute("isEdit", isEdit);
            model.addAttribute("formTitle", isEdit
                    ? "Edit Department — " + department.getName()
                    : "Add Department");
            model.addAttribute("activePage", "departments");
            model.addAttribute("pageTitle", isEdit ? "Edit Department" : "Add Department");
            return "department_form";
        }

        try {
            departmentService.saveDepartment(company, department);
            ra.addFlashAttribute("successMessage",
                    department.getId() == null
                            ? "Department created successfully."
                            : "Department updated successfully.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/departments";
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @PostMapping("/delete/{id}")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes ra) {
        Company company = authUtils.getCurrentCompany();
        try {
            departmentService.deleteDepartment(company, id);
            ra.addFlashAttribute("successMessage", "Department deleted successfully.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/departments";
    }
}
