package au.com.roaringventures.util.security;

import au.com.roaringventures.util.security.request.CreateUserRequest;
import au.com.roaringventures.util.security.request.SearchUsersRequest;
import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.json.mgmt.users.User;
import com.auth0.json.mgmt.users.UsersPage;
import com.auth0.net.AuthRequest;
import com.auth0.net.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class Auth0Helper {

    @Value("${auth0.domain}")
    private String domain;

    @Value("${auth0.manage.clientId}")
    private String managementClientId;

    @Value("${auth0.manage.secret}")
    private String managementSecret;

    private AuthAPI authAPI;

    @PostConstruct
    private void init() {
        authAPI = new AuthAPI(domain, managementClientId, managementSecret);
    }

    public ManagementAPI getManagementApi() {
        try {
            AuthRequest authRequest = authAPI.requestToken("https://" + domain + "/api/v2/");
            TokenHolder holder = authRequest.execute();
            return new ManagementAPI(domain, holder.getAccessToken());
        } catch (Auth0Exception e) {
            throw new AuthException("Error accessing Auth0 management API", e);
        }
    }

    public User getUser(String id) {
        try {
            ManagementAPI mgmt = getManagementApi();
            UserFilter filter = new UserFilter();
            Request<User> request = mgmt.users().get(id, filter);
            return request.execute();
        } catch (Exception e) {
            throw new AuthException("Error getting user from Auth0 API", e);
        }
    }

    public UsersPage searchUsers(SearchUsersRequest request) {
        try {
            ManagementAPI mgmt = getManagementApi();
            UserFilter filter = new UserFilter()
                    .withPage(request.getPage(), request.getSize())
                    .withTotals(true);
            Request<UsersPage> auth0Req = mgmt.users().list(filter);
            return auth0Req.execute();
        } catch (Exception e) {
            throw new AuthException("Error searching users with the Auth0 management API", e);
        }
    }

    public User createUser(CreateUserRequest request) {
        try {
            ManagementAPI mgmt = getManagementApi();
            User user = new User("Username-Password-Authentication");
            user.setGivenName(request.getFirstName());
            user.setFamilyName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword().toCharArray());
            user.setEmailVerified(true);
            Request<User> auth0Req = mgmt.users().create(user);
            return auth0Req.execute();
        } catch (Exception e) {
            throw new AuthException("Error creating new user with the Auth0 management API", e);
        }
    }
}
