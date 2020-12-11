package com.lyes.school.controllers;

import com.lyes.school.entities.User;
import com.lyes.school.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/students")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    @GetMapping("/{userID}")
    public Optional<User> getUser(@PathVariable("userID") Long id){
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> user1 = userRepository.findById(id);
        if(!user.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("WRITE_PRIVILEGE"))) {
            if (user1.isPresent() && user1.orElse(null).getUsername().equals(user.getUsername())) {
                return user1;
            } else {
                return null;
            }
        }else{
            return user1;
        }
    }

    @PostMapping
    public User addUser(@RequestBody User user){
        System.out.println("lyes");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @PostMapping("/test")
    public String testPost(){
        System.out.println("Je suis la ");
        return "je suis la";
    }

}
