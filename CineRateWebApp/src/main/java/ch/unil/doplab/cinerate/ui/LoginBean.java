package ch.unil.doplab.cinerate.ui;

import ch.unil.doplab.cinerate.CineRateService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;

import java.io.Serializable;

// Mouhamed Sarira
@SessionScoped
@Named
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;

    @Inject
    CineRateService theService;

    @Inject
    UserBean userBean;

    public LoginBean() {
        reset();
    }

    public void reset() {
        username = null;
        password = null;
    }

    public String login() {
        var user = theService.login(username, password);
        var session = getSession(true);
        if (user != null) {
            session.setAttribute("uuid", user.getUUID());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());
            userBean.setUUID(user.getUUID());
            userBean.loadUser();
            return "UserProfile?faces-redirect=true";
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Invalid login", null));
        reset();
        return "Login";
    }

    public String logout() {
        invalidateSession();
        reset();
        return "Login?faces-redirect=true";
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

    public static HttpSession getSession(boolean create) {
        var facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null) {
            return null;
        }
        var externalContext = facesContext.getExternalContext();
        if (externalContext == null) {
            return null;
        }
        return (HttpSession) externalContext.getSession(create);
    }

    public static void invalidateSession() {
        var session = getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
