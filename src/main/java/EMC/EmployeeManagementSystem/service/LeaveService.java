package EMC.EmployeeManagementSystem.service;

import EMC.EmployeeManagementSystem.exception.ResourceNotFoundException;
import EMC.EmployeeManagementSystem.model.Company;
import EMC.EmployeeManagementSystem.model.Employee;
import EMC.EmployeeManagementSystem.model.LeaveRequest;
import EMC.EmployeeManagementSystem.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LeaveService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    public List<LeaveRequest> getAllCompanyLeaveRequests(Company company) {
        return leaveRequestRepository.findAllByCompanyOrderByStartDateDesc(company);
    }

    public List<LeaveRequest> getEmployeeLeaveRequests(Company company, Employee employee) {
        return leaveRequestRepository.findAllByCompanyAndEmployeeOrderByStartDateDesc(company, employee);
    }

    public LeaveRequest getLeaveRequestById(Company company, Long id) {
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest", id));
        if (!request.getCompany().getId().equals(company.getId())) {
            throw new IllegalArgumentException("Unauthorized access to leave request.");
        }
        return request;
    }

    @Transactional
    public LeaveRequest saveLeaveRequest(Company company, Employee employee, LeaveRequest request) {
        request.setCompany(company);
        request.setEmployee(employee);
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date.");
        }
        return leaveRequestRepository.save(request);
    }

    @Transactional
    public LeaveRequest updateLeaveStatus(Company company, Long id, LeaveRequest.LeaveStatus newStatus) {
        LeaveRequest request = getLeaveRequestById(company, id);
        request.setStatus(newStatus);
        return leaveRequestRepository.save(request);
    }

    public long countPendingApprovals(Company company) {
        return leaveRequestRepository.countByCompanyAndStatus(company, LeaveRequest.LeaveStatus.PENDING);
    }
}
