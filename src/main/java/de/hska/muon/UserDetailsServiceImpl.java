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
import java.util.List;

/**
 * Created by gerrit on 21.06.17.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final String USER_SERVICE_URL = "http://user:8082/users";

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

        User[] users = restTemplate.getForObject(USER_SERVICE_URL, User[].class);
        System.out.println("Users:\n" + users);
        return users;
    }

    class UserDetailsImpl implements UserDetails {

        private User _user;

        public UserDetailsImpl(User user) {
            _user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            List<GrantedAuthority> ret = new ArrayList<>();
            ret.add(TRUST);
            ret.add(READ);
            if(_user.getRole() == 2) {
                ret.add(WRITE);
            }
            return ret;
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

    public final GrantedAuthority READ = new GrantedAuthority() {
        @Override
        public String getAuthority() {
            return "read";
        }
    };

    public final GrantedAuthority WRITE = new GrantedAuthority() {
        @Override
        public String getAuthority() {
            return "write";
        }
    };

    public final GrantedAuthority TRUST = new GrantedAuthority() {
        @Override
        public String getAuthority() {
            return "trust";
        }
    };
}
