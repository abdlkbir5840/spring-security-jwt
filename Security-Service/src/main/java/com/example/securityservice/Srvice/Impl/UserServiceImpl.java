package com.example.securityservice.Srvice.Impl;

import com.example.securityservice.DAO.RoleDAO;
import com.example.securityservice.DAO.UserDAO;
import com.example.securityservice.DTO.*;
import com.example.securityservice.Entities.Role;
import com.example.securityservice.Entities.User;
import com.example.securityservice.Mappers.RoleMapper;
import com.example.securityservice.Mappers.UserMapper;
import com.example.securityservice.Srvice.UserService;
import com.example.securityservice.exceptions.EntityAlreadyExistException;
import com.example.securityservice.exceptions.EntityNotFoundException;
import com.example.securityservice.exceptions.InvalidEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserDAO userDAO;
    private RoleDAO roleDAO;
    private UserMapper userMapper;
    private RoleMapper roleMapper;
    private PasswordEncoder passwordEncoder;
    @Autowired
    public UserServiceImpl(UserDAO userDAO, RoleDAO roleDAO, UserMapper userMapper, RoleMapper roleMapper, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDTO signIn(UserRequestDto userRequestDto) {
        User user = userMapper.dtoToModel(userRequestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        List<String> roles = userRequestDto.getRoleNames();
        List<Role>roleList = new ArrayList<>();
        for( String role : roles){
            roleList.add(roleDAO.findByRole(role).get());
        }
        user.setRoles(roleList);
        User userSaved = userDAO.save(user);
        return userMapper.modelToDto(userSaved);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
          List<User>users = userDAO.findAll();
          return userMapper.modelToDtos(users);
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        Optional<User> user = userDAO.findByEmail(email);
        return userMapper.modelToDto(user.get());
    }

    @Override
    public UserResponseDTO updatePassword(UpdatePasswordDTO updatePasswordDTO) {

           Optional<User> user = userDAO.findByEmail(updatePasswordDTO.getEmail());
           if (user.isEmpty()){
               throw new RuntimeException("user not found");
           }
           if(!updatePasswordDTO.getPassword().equals(user.get().getPassword())){
               throw new RuntimeException("password incorrect");
           }
           user.get().setPassword(passwordEncoder.encode(updatePasswordDTO.getNewPassword()));
           User userUpdated = userDAO.save(user.get());
           return userMapper.modelToDto(userUpdated);
    }

    @Override
    public void deleteUser(Long id) {
        if(!userDAO.findById(id).isPresent())
            throw new EntityNotFoundException("No User with id "+id+" were found");
        userDAO.deleteById(id);
    }


    @Override
    public UserResponseDTO addRoleToUser(Long userId, String role)throws EntityNotFoundException {
        if(!roleDAO.findByRole(role).isPresent())
            throw new EntityNotFoundException("No role with name "+role+" were found");
        User user = userDAO.findById(userId).get();
        user.getRoles().add(roleDAO.findByRole(role).get());
        return userMapper.modelToDto(
                userDAO.save(
                        user
                )
        );
    }
}
