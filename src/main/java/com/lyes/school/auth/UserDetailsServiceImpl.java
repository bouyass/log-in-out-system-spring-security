package com.lyes.school.auth;

import com.lyes.school.entities.Privilege;
import com.lyes.school.entities.Role;
import com.lyes.school.entities.User;
import com.lyes.school.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles){
        return getGrantedAuthorities(getPrivileges(roles));
    }

    private List<String> getPrivileges(Collection<Role> roles){
        List<String> privileges = new ArrayList<>();
        List<Privilege> collection = new ArrayList<>();
        for(Role role: roles){
            collection.addAll(role.getPrivileges());
        }
        for(Privilege privilege : collection){
            privileges.add(privilege.getName());
        }

        return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges){
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(String privilege : privileges){
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }
}
