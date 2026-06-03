package EMC.EmployeeManagementSystem.repository;

import EMC.EmployeeManagementSystem.model.Company;
import EMC.EmployeeManagementSystem.model.Department;
import EMC.EmployeeManagementSystem.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByCompanyAndId(Company company, Long id);

    @Query("SELECT e FROM Employee e WHERE e.company = :company AND " +
           "(:name IS NULL OR :name = '' OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(e.email) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:deptId IS NULL OR e.department.id = :deptId) AND " +
           "(:status IS NULL OR e.status = :status)")
    Page<Employee> searchEmployees(
            @Param("company") Company company,
            @Param("name") String name,
            @Param("deptId") Long deptId,
            @Param("status") Employee.Status status,
            Pageable pageable);

    long countByCompany(Company company);
    long countByCompanyAndStatus(Company company, Employee.Status status);
    long countByCompanyAndDepartment(Company company, Department department);

    List<Employee> findTop5ByCompanyOrderByHireDateDesc(Company company);
    List<Employee> findAllByCompanyOrderByNameAsc(Company company);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.company = :company AND e.salary < 50000")
    long countSalaryUnder50k(@Param("company") Company company);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.company = :company AND e.salary >= 50000 AND e.salary < 80000")
    long countSalary50kTo80k(@Param("company") Company company);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.company = :company AND e.salary >= 80000")
    long countSalaryAbove80k(@Param("company") Company company);

    List<Employee> findAllByCompany(Company company);
}
