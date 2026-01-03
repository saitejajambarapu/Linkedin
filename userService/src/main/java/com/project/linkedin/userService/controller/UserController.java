package com.project.linkedin.userService.controller;

import com.project.linkedin.userService.dto.LoginRequestDto;
import com.project.linkedin.userService.dto.SignupRequestDto;
import com.project.linkedin.userService.dto.UserDto;
import com.project.linkedin.userService.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(
            @RequestBody SignupRequestDto signupRequestDto) {

        UserDto userDto = authService.signup(signupRequestDto);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequestDto loginRequestDto) {

        String token = authService.login(loginRequestDto);
        return ResponseEntity.ok(token);
    }
}
