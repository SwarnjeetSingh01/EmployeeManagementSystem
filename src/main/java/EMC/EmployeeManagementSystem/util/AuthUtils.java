package EMC.EmployeeManagementSystem.util;

import EMC.EmployeeManagementSystem.model.Company;
import EMC.EmployeeManagementSystem.model.User;
import EMC.EmployeeManagementSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        return userRepository.findByUsername(auth.getName()).orElse(null);
    }

    public Company getCurrentCompany() {
        User current = getCurrentUser();
        return current != null ? current.getCompany() : null;
    }
}
