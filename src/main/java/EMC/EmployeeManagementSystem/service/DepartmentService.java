package EMC.EmployeeManagementSystem.service;

import EMC.EmployeeManagementSystem.exception.ResourceNotFoundException;
import EMC.EmployeeManagementSystem.model.Company;
import EMC.EmployeeManagementSystem.model.Department;
import EMC.EmployeeManagementSystem.repository.DepartmentRepository;
import EMC.EmployeeManagementSystem.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Department> getAllDepartments(Company company) {
        return departmentRepository.findAllByCompanyOrderByNameAsc(company);
    }

    public Department getDepartmentById(Company company, Long id) {
        return departmentRepository.findByCompanyAndId(company, id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id));
    }

    @Transactional
    public Department saveDepartment(Company company, Department department) {
        department.setCompany(company);
        Long selfId = department.getId() != null ? department.getId() : -1L;
        if (departmentRepository.existsByCompanyAndNameIgnoreCaseAndIdNot(company, department.getName(), selfId)) {
            throw new IllegalArgumentException("A department named '" + department.getName() + "' already exists.");
        }
        return departmentRepository.save(department);
    }

    @Transactional
    public void deleteDepartment(Company company, Long id) {
        Department dept = getDepartmentById(company, id);
        long empCount = employeeRepository.countByCompanyAndDepartment(company, dept);
        if (empCount > 0) {
            throw new IllegalStateException(
                "Cannot delete '" + dept.getName() + "': " + empCount +
                " employee(s) are still assigned to this department."
            );
        }
        departmentRepository.delete(dept);
    }
}
