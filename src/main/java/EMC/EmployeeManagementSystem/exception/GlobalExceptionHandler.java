package EMC.EmployeeManagementSystem.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorCode", "404");
        model.addAttribute("errorTitle", "Page Not Found");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResource(NoResourceFoundException ex, Model model) {
        model.addAttribute("errorCode", "404");
        model.addAttribute("errorTitle", "Page Not Found");
        model.addAttribute("errorMessage", "The page you're looking for doesn't exist.");
        return "error/404";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrity(DataIntegrityViolationException ex, Model model) {
        model.addAttribute("errorCode", "409");
        model.addAttribute("errorTitle", "Data Conflict");
        model.addAttribute("errorMessage",
                "A record with that information already exists (e.g., duplicate email address). " +
                "Please go back and use a unique value.");
        return "error/500";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex, Model model) {
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorTitle", "Something Went Wrong");
        model.addAttribute("errorMessage",
                "An unexpected error occurred. Please try again or contact your administrator.");
        return "error/500";
    }
}
