package com.project.linkedin.userService.service;

import com.project.linkedin.userService.dto.LoginRequestDto;
import com.project.linkedin.userService.dto.SignupRequestDto;
import com.project.linkedin.userService.dto.UserDto;
import com.project.linkedin.userService.entity.User;
import com.project.linkedin.userService.exception.BadRequestException;
import com.project.linkedin.userService.exception.ResourceNotFoundException;
import com.project.linkedin.userService.repository.UserRepository;
import com.project.linkedin.userService.utility.BCrypt;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtService jwtService;

    public UserDto signup(SignupRequestDto signupRequestDto) {
        log.info("signup a user with email: {}",signupRequestDto.getEmail());
        boolean userExists = userRepository.existsByEmail(signupRequestDto.getEmail());
        if(userExists) throw  new BadRequestException("user with this email already exists: "+signupRequestDto.getEmail());
        User user = modelMapper.map(signupRequestDto,User.class);
        user.setPassword(BCrypt.hash(signupRequestDto.getPassword()));
        userRepository.save(user);
        UserDto userDto = modelMapper.map(user,UserDto.class);
        return userDto;
    }

    public String login(LoginRequestDto loginRequestDto) {
        log.info("login request for user with email: {}", loginRequestDto.getEmail());
        User user = userRepository
                .findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new BadRequestException("incorrect email or password"
                ));
        boolean isPasswordmatch = BCrypt.match(loginRequestDto.getPassword(),user.getPassword());
        if(!isPasswordmatch){
            throw new BadRequestException("incorrect email or password");
        }

        return jwtService.generateAccessToken(user);


    }
}
