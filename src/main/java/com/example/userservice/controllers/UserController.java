package com.example.userservice.controllers;

import com.example.userservice.dtos.ResponseStatus;
import com.example.userservice.dtos.SignUpRequestDTO;
import com.example.userservice.dtos.SignUpResponseDTO;
import com.example.userservice.models.User;
import com.example.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users") //localhost:8080/users/
public class UserController {
    private UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    /*
    Controller take the request from User
    1st request get from User to controller is signUp
    2nd request get from User to controller is login
    3rd request get from User to controller is logout
    4th request get from User to controller is validate token
     */
    @PostMapping("/signUp")
    public SignUpResponseDTO signUp (@RequestBody SignUpRequestDTO signUpRequestDTO){//REST method
        /*User user = new User();
        return user;*/

        String name = signUpRequestDTO.getName();;
        String email = signUpRequestDTO.getEmail();
        String password = signUpRequestDTO.getPassword();

        /*
        signUp will call the userService to register the user and return the user back to the client. Instead of sending user back to the client
        Let's send singUpResponseDTO back to the client(because, I didn't use password to return back...refer singUpResponseDTO
        (i.e) user - what are all the things give and what are all the things return back
         */
        //return new ResponseEntity<>(HttpStatus.OK);

        SignUpResponseDTO signUpResponseDTO = new SignUpResponseDTO();

        try {
            //Register the user with name, email, password
            User user = userService.signUp(name, email, password);
            //Return or set only the name, email whatever I got and check the responseStatus so, we have used SignUpResponseDTO
            signUpResponseDTO.setName(user.getName());
            signUpResponseDTO.setEmail(user.getEmail());
            signUpResponseDTO.setResponseStatus(ResponseStatus.SUCCESS);
            return signUpResponseDTO;
        }catch (Exception ex){
            signUpResponseDTO.setResponseStatus(ResponseStatus.FAILURE);
            return signUpResponseDTO;
        }
    }
}
