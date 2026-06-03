package EMC.EmployeeManagementSystem.controller;

import EMC.EmployeeManagementSystem.dto.DashboardStatsDto;
import EMC.EmployeeManagementSystem.model.Company;
import EMC.EmployeeManagementSystem.model.User;
import EMC.EmployeeManagementSystem.service.EmployeeService;
import EMC.EmployeeManagementSystem.service.LeaveService;
import EMC.EmployeeManagementSystem.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DashboardController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private AuthUtils authUtils;

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Company company = authUtils.getCurrentCompany();
        User currentUser = authUtils.getCurrentUser();
        DashboardStatsDto stats = employeeService.getDashboardStats(company);
        
        if (currentUser.getRole() != User.Role.ROLE_EMPLOYEE) {
            stats.setPendingLeaveRequests(leaveService.countPendingApprovals(company));
        }

        model.addAttribute("stats", stats);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("pageTitle", "Dashboard");
        return "dashboard";
    }

    @GetMapping("/api/dashboard/stats")
    @ResponseBody
    public DashboardStatsDto dashboardStats() {
        Company company = authUtils.getCurrentCompany();
        return employeeService.getDashboardStats(company);
    }
}
