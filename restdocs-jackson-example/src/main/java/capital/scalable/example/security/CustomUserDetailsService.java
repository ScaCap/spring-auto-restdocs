package capital.scalable.example.security;

import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Usually, you would consult a database, but for simplicity we hardcode a user
        // with username "test" and password "test".
        if ("test".equals(username)) {
            return new User(username, "test", Collections.<GrantedAuthority>emptyList());
        } else {
            throw new UsernameNotFoundException(String.format("User %s does not exist!", username));
        }
    }
}
