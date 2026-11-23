package ch.unil.doplab.cinerate.ui;

import ch.unil.doplab.cinerate.CineRateService;
import ch.unil.doplab.cinerate.domain.User;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.logging.Logger;

// Mouhamed Sarira
@SessionScoped
@Named
public class RegisterBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(RegisterBean.class.getName());

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;

    @Inject
    CineRateService theService;

    public RegisterBean() {
        reset();
    }

    public void reset() {
        firstName = null;
        lastName = null;
        email = null;
        username = null;
        password = null;
    }

    public String register() {
        LoginBean.invalidateSession();
        String errorMessage;
        User user = new User(firstName, lastName, email, username, password);
        try {
            user = theService.addUser(user);
            log.info("User successfully registered: " + user.getUsername());
            reset();
            return "Login?faces-redirect=true";
        } catch (Exception e) {
            errorMessage = e.getMessage();
            log.warning("Failed to register new user " + username + ": " + errorMessage);
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Registration failed. " + errorMessage, null));
        return "Register";
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
