package EMC.EmployeeManagementSystem.repository;

import EMC.EmployeeManagementSystem.model.Company;
import EMC.EmployeeManagementSystem.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findAllByCompanyOrderByNameAsc(Company company);

    Optional<Department> findByCompanyAndId(Company company, Long id);

    Optional<Department> findByCompanyAndNameIgnoreCase(Company company, String name);

    boolean existsByCompanyAndNameIgnoreCaseAndIdNot(Company company, String name, Long id);
}
