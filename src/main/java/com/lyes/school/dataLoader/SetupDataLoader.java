package com.lyes.school.dataLoader;

import com.lyes.school.entities.Privilege;
import com.lyes.school.entities.Role;
import com.lyes.school.entities.User;
import com.lyes.school.repositories.PrivilegeRepository;
import com.lyes.school.repositories.RoleRepository;
import com.lyes.school.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PrivilegeRepository privilegeRepository;
    @Autowired
    PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(alreadySetup)
            return;

        Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        List<Privilege> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_STUDENT", Arrays.asList(readPrivilege));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        Role studentRole = roleRepository.findByName("ROLE_STUDENT");

        User user = new User();
        user.setUsername("lyes");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setRoles(Arrays.asList(adminRole));
        user.setVocation("admin");
        userRepository.save(user);

        User user1 = new User();
        user1.setUsername("karim");
        user1.setPassword(passwordEncoder.encode("karim"));
        user1.setRoles(Arrays.asList(studentRole));
        user1.setVocation("student");
        userRepository.save(user1);

        alreadySetup = false;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name){
        Privilege privilege = privilegeRepository.findByName(name);
        if(privilege == null){
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(String name, Collection<Privilege> privileges){
        Role role = roleRepository.findByName(name);
        if(role == null){
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }
}
