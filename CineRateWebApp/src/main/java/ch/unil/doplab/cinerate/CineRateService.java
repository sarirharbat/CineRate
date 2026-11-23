package ch.unil.doplab.cinerate;

import ch.unil.doplab.cinerate.domain.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Mouhamed Sarira
@ApplicationScoped
public class CineRateService {
    private static final String BASE_URL = "http://localhost:8080/CineRateService-1.0-SNAPSHOT/api";
    private WebTarget userTarget;
    private WebTarget serviceTarget;

    @PostConstruct
    public void init() {
        Client client = ClientBuilder.newClient();
        userTarget = client.target(BASE_URL).path("users");
        serviceTarget = client.target(BASE_URL).path("service");
    }

    public void resetService() {
        serviceTarget
                .path("reset")
                .request()
                .get(String.class);
    }

    public List<User> getAllUsers() {
        var users = userTarget
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<User>>() {
                });
        return users;
    }

    public User getUser(String id) {
        var user = userTarget
                .path(id)
                .request()
                .get(User.class);
        return user;
    }

    public User addUser(User user) throws Exception {
        user.setUUID(null);
        var response = userTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(toJson(user), MediaType.APPLICATION_JSON));
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            return response.readEntity(User.class);
        } else {
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
            throw Utils.buildException(description);
        }
    }

    public boolean setUser(User user) throws Exception {
        var response = userTarget
                .path(user.getUUID().toString())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(toJson(user), MediaType.APPLICATION_JSON));
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return true;
        } else {
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
            throw Utils.buildException(description);
        }
    }

    public boolean deleteUser(String id) {
        var response = userTarget
                .path(id)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

    public User login(String username, String password) {
        var credentials = new User();
        credentials.setUsername(username);
        credentials.setPassword(password);
        var response = userTarget
                .path("login")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(toJson(credentials), MediaType.APPLICATION_JSON));
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.readEntity(User.class);
        }
        return null;
    }

    // The password is never in the JSON of a User (@JsonbTransient in the domain),
    // so the fields sent to the service are written one by one.
    private Map<String, String> toJson(User user) {
        var json = new HashMap<String, String>();
        if (user.getFirstName() != null) {
            json.put("firstName", user.getFirstName());
        }
        if (user.getLastName() != null) {
            json.put("lastName", user.getLastName());
        }
        if (user.getEmail() != null) {
            json.put("email", user.getEmail());
        }
        if (user.getUsername() != null) {
            json.put("username", user.getUsername());
        }
        if (user.getPassword() != null) {
            json.put("password", user.getPassword());
        }
        return json;
    }
}
