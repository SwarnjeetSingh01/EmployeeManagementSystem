package EMC.EmployeeManagementSystem.controller;

import EMC.EmployeeManagementSystem.model.Company;
import EMC.EmployeeManagementSystem.model.LeaveRequest;
import EMC.EmployeeManagementSystem.model.User;
import EMC.EmployeeManagementSystem.service.LeaveService;
import EMC.EmployeeManagementSystem.util.AuthUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/leave")
public class LeaveWebController {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private AuthUtils authUtils;

    @GetMapping
    public String listLeaveRequests(Model model) {
        Company company = authUtils.getCurrentCompany();
        User currentUser = authUtils.getCurrentUser();
        
        List<LeaveRequest> requests;
        if (currentUser.getRole() == User.Role.ROLE_EMPLOYEE) {
            requests = leaveService.getEmployeeLeaveRequests(company, currentUser.getEmployeeProfile());
        } else {
            requests = leaveService.getAllCompanyLeaveRequests(company);
        }

        model.addAttribute("requests", requests);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("activePage", "leave");
        model.addAttribute("pageTitle", "Time Off");
        return "leave_list";
    }

    @GetMapping("/new")
    public String newLeaveRequestForm(Model model) {
        model.addAttribute("leaveRequest", new LeaveRequest());
        model.addAttribute("leaveTypes", LeaveRequest.LeaveType.values());
        model.addAttribute("activePage", "leave");
        model.addAttribute("pageTitle", "Request Time Off");
        return "leave_form";
    }

    @PostMapping("/save")
    public String saveLeaveRequest(
            @Valid @ModelAttribute("leaveRequest") LeaveRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            model.addAttribute("leaveTypes", LeaveRequest.LeaveType.values());
            model.addAttribute("activePage", "leave");
            model.addAttribute("pageTitle", "Request Time Off");
            return "leave_form";
        }

        Company company = authUtils.getCurrentCompany();
        User currentUser = authUtils.getCurrentUser();

        try {
            leaveService.saveLeaveRequest(company, currentUser.getEmployeeProfile(), request);
            ra.addFlashAttribute("successMessage", "Time-off request submitted successfully.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            model.addAttribute("leaveTypes", LeaveRequest.LeaveType.values());
            return "leave_form";
        }

        return "redirect:/leave";
    }

    @PostMapping("/approve/{id}")
    public String approveRequest(@PathVariable Long id, RedirectAttributes ra) {
        Company company = authUtils.getCurrentCompany();
        leaveService.updateLeaveStatus(company, id, LeaveRequest.LeaveStatus.APPROVED);
        ra.addFlashAttribute("successMessage", "Request approved.");
        return "redirect:/leave";
    }

    @PostMapping("/reject/{id}")
    public String rejectRequest(@PathVariable Long id, RedirectAttributes ra) {
        Company company = authUtils.getCurrentCompany();
        leaveService.updateLeaveStatus(company, id, LeaveRequest.LeaveStatus.REJECTED);
        ra.addFlashAttribute("successMessage", "Request rejected.");
        return "redirect:/leave";
    }
}
