package ch.unil.doplab.cinerate.ui;

import ch.unil.doplab.cinerate.CineRateService;
import ch.unil.doplab.cinerate.domain.User;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.UUID;

// Mouhamed Sarira
@SessionScoped
@Named
public class UserBean extends User implements Serializable {
    private static final long serialVersionUID = 1L;

    private User theUser;
    private String currentPassword;
    private String newPassword;
    private boolean changed;
    private String dialogMessage;

    @Inject
    CineRateService theService;

    public UserBean() {
        this(null, null, null, null, null, null);
    }

    public UserBean(UUID id, String firstName, String lastName, String email, String username, String password) {
        super(id, firstName, lastName, email, username, password);
        init();
        theUser = new User(id, firstName, lastName, email, username, password);
    }

    public void init() {
        theUser = null;
        currentPassword = null;
        newPassword = null;
        changed = false;
        dialogMessage = null;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getDialogMessage() {
        return dialogMessage;
    }

    public boolean isButtonDisabled() {
        return !changed;
    }

    public void checkIfChanged() {
        boolean firstNameChanged = !theUser.getFirstName().equals(this.getFirstName());
        boolean lastNameChanged = !theUser.getLastName().equals(this.getLastName());
        boolean emailChanged = !theUser.getEmail().equals(this.getEmail());
        boolean usernameChanged = !theUser.getUsername().equals(this.getUsername());
        changed = firstNameChanged || lastNameChanged || emailChanged || usernameChanged;
    }

    public void undoChanges() {
        this.replaceWith(theUser);
        changed = false;
    }

    public void loadUser() {
        var id = this.getUUID();
        if (id != null) {
            theUser = theService.getUser(id.toString());
            replaceWith(theUser);
        }
    }

    public void updateUser() {
        try {
            if (this.getUUID() != null) {
                this.setPassword(null);
                theService.setUser(this);
                theUser = theService.getUser(this.getUUID().toString());
                changed = false;
            }
        } catch (Exception e) {
            dialogMessage = e.getMessage();
            PrimeFaces.current().executeScript("PF('updateErrorDialog').show();");
        }
    }

    public void savePasswordChange() {
        if (theService.login(this.getUsername(), currentPassword) != null) {
            this.setPassword(newPassword);
            try {
                theService.setUser(this);
                dialogMessage = "Your password was successfully changed.";
            } catch (Exception e) {
                dialogMessage = e.getMessage();
            }
            this.setPassword(null);
        } else {
            dialogMessage = "Your password could not be changed because the current password you entered is incorrect.";
        }
        PrimeFaces.current().executeScript("PF('passwordChangeDialog').show();");
        resetPasswordChange();
    }

    public void resetPasswordChange() {
        currentPassword = null;
        newPassword = null;
    }

    public String deleteUser() {
        theService.deleteUser(this.getUUID().toString());
        LoginBean.invalidateSession();
        return "Login?faces-redirect=true";
    }
}
