package EMC.EmployeeManagementSystem.controller;

import EMC.EmployeeManagementSystem.model.Company;
import EMC.EmployeeManagementSystem.model.Employee;
import EMC.EmployeeManagementSystem.service.EmployeeService;
import EMC.EmployeeManagementSystem.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/org-chart")
public class OrgChartController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AuthUtils authUtils;

    @GetMapping
    public String orgChartPage(Model model) {
        model.addAttribute("activePage", "org-chart");
        model.addAttribute("pageTitle", "Organization Chart");
        return "org_chart";
    }

    @GetMapping("/api/data")
    @ResponseBody
    public List<Map<String, Object>> getOrgChartData() {
        Company company = authUtils.getCurrentCompany();
        List<Employee> employees = employeeService.getAllEmployees(company);
        
        List<Map<String, Object>> data = new ArrayList<>();
        for (Employee emp : employees) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", emp.getId().toString());
            node.put("name", emp.getName());
            node.put("title", emp.getJobTitle() != null ? emp.getJobTitle() : "Employee");
            node.put("department", emp.getDepartmentName());
            node.put("parentId", emp.getManager() != null ? emp.getManager().getId().toString() : null);
            data.add(node);
        }
        
        return data;
    }
}
