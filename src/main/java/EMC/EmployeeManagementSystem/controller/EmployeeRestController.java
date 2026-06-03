package EMC.EmployeeManagementSystem.controller;

import EMC.EmployeeManagementSystem.model.Company;
import EMC.EmployeeManagementSystem.model.Employee;
import EMC.EmployeeManagementSystem.service.EmployeeService;
import EMC.EmployeeManagementSystem.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeRestController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AuthUtils authUtils;

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        Company company = authUtils.getCurrentCompany();
        return ResponseEntity.ok(employeeService.getAllEmployees(company));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployee(@PathVariable Long id) {
        Company company = authUtils.getCurrentCompany();
        return ResponseEntity.ok(employeeService.getEmployeeById(company, id));
    }

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        Company company = authUtils.getCurrentCompany();
        return ResponseEntity.ok(employeeService.saveEmployee(company, employee));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable Long id, @RequestBody Employee details) {
        Company company = authUtils.getCurrentCompany();
        return ResponseEntity.ok(employeeService.updateEmployee(company, id, details));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        Company company = authUtils.getCurrentCompany();
        employeeService.deleteEmployee(company, id);
        return ResponseEntity.noContent().build();
    }
}
