package EMC.EmployeeManagementSystem.repository;

import EMC.EmployeeManagementSystem.model.Company;
import EMC.EmployeeManagementSystem.model.Employee;
import EMC.EmployeeManagementSystem.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findAllByCompanyOrderByStartDateDesc(Company company);
    List<LeaveRequest> findAllByCompanyAndEmployeeOrderByStartDateDesc(Company company, Employee employee);
    long countByCompanyAndStatus(Company company, LeaveRequest.LeaveStatus status);
}
