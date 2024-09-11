package com.example.userservice.services;

import com.example.userservice.models.User;
import com.example.userservice.repositories.IUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    //we directly use the attribute of bCryptPasswordEncoder because of spring security
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private IUserRepository userRepository;

    //Add the dependency of bCryptPasswordEncoder, so create the method of BCryptPasswordEncoder in the BCryptConfiguration class.
    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder, IUserRepository userRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }
    public User signUp(String fullName, String email, String password){
        User user = new User();
        user.setName(fullName);
        user.setEmail(email);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
        return user;

    }
}
