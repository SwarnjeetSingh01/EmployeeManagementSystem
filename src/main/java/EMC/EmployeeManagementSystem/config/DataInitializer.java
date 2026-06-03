package EMC.EmployeeManagementSystem.config;

import EMC.EmployeeManagementSystem.model.Company;
import EMC.EmployeeManagementSystem.model.Department;
import EMC.EmployeeManagementSystem.model.Employee;
import EMC.EmployeeManagementSystem.model.User;
import EMC.EmployeeManagementSystem.repository.CompanyRepository;
import EMC.EmployeeManagementSystem.repository.DepartmentRepository;
import EMC.EmployeeManagementSystem.repository.EmployeeRepository;
import EMC.EmployeeManagementSystem.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            CompanyRepository companyRepo,
            UserRepository userRepo,
            DepartmentRepository deptRepo,
            EmployeeRepository empRepo,
            PasswordEncoder passwordEncoder) {
        
        return args -> {
            // Only insert data if the database is empty
            if (companyRepo.count() == 0) {
                // 1. Create Companies
                Company acme = new Company();
                acme.setName("Acme Corp");
                acme.setDomain("acme.com");

                Company globex = new Company();
                globex.setName("Globex Inc");
                globex.setDomain("globex.com");

                companyRepo.saveAll(Arrays.asList(acme, globex));

                // 2. Create Departments for Acme
                Department engineering = new Department();
                engineering.setName("Engineering");
                engineering.setCompany(acme);

                Department sales = new Department();
                sales.setName("Sales");
                sales.setCompany(acme);

                Department hr = new Department();
                hr.setName("Human Resources");
                hr.setCompany(acme);

                deptRepo.saveAll(Arrays.asList(engineering, sales, hr));

                // 3. Create Employees with Hierarchy for Org Chart
                Employee ceo = new Employee();
                ceo.setName("Alice Johnson");
                ceo.setEmail("alice.ceo@acme.com");
                ceo.setJobTitle("Chief Executive Officer");
                ceo.setCompany(acme);
                ceo.setDepartment(engineering);
                ceo.setSalary(250000.0);
                ceo.setHireDate(LocalDate.now().minusYears(3));
                empRepo.save(ceo);

                Employee cto = new Employee();
                cto.setName("Bob Smith");
                cto.setEmail("bob.cto@acme.com");
                cto.setJobTitle("Chief Technology Officer");
                cto.setCompany(acme);
                cto.setDepartment(engineering);
                cto.setManager(ceo);
                cto.setSalary(180000.0);
                cto.setHireDate(LocalDate.now().minusYears(2));
                empRepo.save(cto);

                Employee vpSales = new Employee();
                vpSales.setName("Eve Davis");
                vpSales.setEmail("eve.vp@acme.com");
                vpSales.setJobTitle("VP of Sales");
                vpSales.setCompany(acme);
                vpSales.setDepartment(sales);
                vpSales.setManager(ceo);
                vpSales.setSalary(170000.0);
                vpSales.setHireDate(LocalDate.now().minusYears(1));
                empRepo.save(vpSales);

                Employee dev1 = new Employee();
                dev1.setName("Charlie Brown");
                dev1.setEmail("charlie@acme.com");
                dev1.setJobTitle("Senior Developer");
                dev1.setCompany(acme);
                dev1.setDepartment(engineering);
                dev1.setManager(cto);
                dev1.setSalary(120000.0);
                dev1.setHireDate(LocalDate.now().minusMonths(6));
                empRepo.save(dev1);

                Employee dev2 = new Employee();
                dev2.setName("David Lee");
                dev2.setEmail("david@acme.com");
                dev2.setJobTitle("Backend Developer");
                dev2.setCompany(acme);
                dev2.setDepartment(engineering);
                dev2.setManager(cto);
                dev2.setSalary(105000.0);
                dev2.setHireDate(LocalDate.now().minusMonths(2));
                empRepo.save(dev2);

                Employee ae1 = new Employee();
                ae1.setName("Frank Miller");
                ae1.setEmail("frank@acme.com");
                ae1.setJobTitle("Account Executive");
                ae1.setCompany(acme);
                ae1.setDepartment(sales);
                ae1.setManager(vpSales);
                ae1.setSalary(95000.0);
                ae1.setHireDate(LocalDate.now().minusMonths(8));
                empRepo.save(ae1);

                // 4. Create Users
                // Super Admin
                User superadmin = new User();
                superadmin.setUsername("admin");
                superadmin.setPassword(passwordEncoder.encode("admin@123"));
                superadmin.setRole(User.Role.ROLE_SUPERADMIN);
                superadmin.setCompany(acme);
                userRepo.save(superadmin);

                // Acme HR Admin
                User acmeHr = new User();
                acmeHr.setUsername("hr@acme.com");
                acmeHr.setPassword(passwordEncoder.encode("password"));
                acmeHr.setRole(User.Role.ROLE_HR);
                acmeHr.setCompany(acme);
                userRepo.save(acmeHr);

                // Acme Employee Login (Charlie)
                User charlieUser = new User();
                charlieUser.setUsername("charlie@acme.com");
                charlieUser.setPassword(passwordEncoder.encode("password"));
                charlieUser.setRole(User.Role.ROLE_EMPLOYEE);
                charlieUser.setCompany(acme);
                charlieUser.setEmployeeProfile(dev1);
                userRepo.save(charlieUser);

                // 5. Globex Setup (to prove multi-tenancy isolation)
                Department marketingGlobex = new Department();
                marketingGlobex.setName("Marketing");
                marketingGlobex.setCompany(globex);
                deptRepo.save(marketingGlobex);

                Employee globexEmp = new Employee();
                globexEmp.setName("Gina Ray");
                globexEmp.setEmail("gina@globex.com");
                globexEmp.setJobTitle("Marketing Director");
                globexEmp.setCompany(globex);
                globexEmp.setDepartment(marketingGlobex);
                globexEmp.setSalary(130000.0);
                empRepo.save(globexEmp);

                User globexHr = new User();
                globexHr.setUsername("hr@globex.com");
                globexHr.setPassword(passwordEncoder.encode("password"));
                globexHr.setRole(User.Role.ROLE_HR);
                globexHr.setCompany(globex);
                userRepo.save(globexHr);
            }
        };
    }
}
