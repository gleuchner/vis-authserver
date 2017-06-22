package de.hska.muon;

import de.hska.muon.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by gerrit on 21.06.17.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final String USER_SERVICE_URL = "http://user-service/users";

    private final RestTemplate restTemplate;

    @Autowired
    public UserDetailsServiceImpl(final RestTemplate oAuthRestTemplate) {
        this.restTemplate = oAuthRestTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User[] users = getUsers();
        for (User user :users) {
            if (user.getUsername().equals(username)) {
                return new UserDetailsImpl(user);
            }
        }

        throw new UsernameNotFoundException("User with name \"" + username + "\" not found");
    }

    private User[] getUsers() {
        return restTemplate.getForObject(USER_SERVICE_URL, User[].class);
    }

    class UserDetailsImpl implements UserDetails {

        private User _user;

        public UserDetailsImpl(User user) {
            _user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return new ArrayList<>();
        }

        @Override
        public String getPassword() {
            return _user.getPassword();
        }

        @Override
        public String getUsername() {
            return _user.getUsername();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
