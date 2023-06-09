package com.example.securityservice.web;

import com.example.securityservice.DTO.*;
import com.example.securityservice.Entities.Role;
import com.example.securityservice.Entities.User;
import com.example.securityservice.Srvice.UserService;
import com.example.securityservice.exceptions.EntityAlreadyExistException;
import com.example.securityservice.exceptions.EntityNotFoundException;
import com.example.securityservice.exceptions.InvalidEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public UserResponseDTO addUser(@RequestBody UserRequestDto userRequestDto){
        return userService.signIn(userRequestDto);
    }
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<UserResponseDTO> getAllUsers(){
        return userService.getAllUsers();
    }
    @GetMapping("/{email}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public UserResponseDTO getUserByEmail(@PathVariable(name = "email") String email){
        return userService.getUserByEmail(email);
    }
    @PutMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN','SCOPE_STUDENT')")
    public UserResponseDTO UpdatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO){
        return userService.updatePassword(updatePasswordDTO);
    }
    @DeleteMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public void deleteUser(@PathVariable Long id) {
       userService.deleteUser(id);
    }

    @PutMapping ("/roles/{userId}/{role}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public UserResponseDTO addRoleToUser(@PathVariable Long userId,@PathVariable String role)throws EntityNotFoundException {
        return userService.addRoleToUser(userId, role);
    }
}
