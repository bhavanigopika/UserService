package com.example.userservice.dtos;

import com.example.userservice.models.Role;
import com.example.userservice.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDTO {
    private String name;
    private String email;
    private List<Role> rolesList;
    private boolean isEmailVerified;

    //accepts the user object and convert it into userDTO - we apply abstraction here
    public static UserDTO from(User user) {
        if(user == null){
            return null;
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRolesList(user.getRolesList());
        userDTO.setEmailVerified(user.isEmailVerified());

        return userDTO;

    }

}
