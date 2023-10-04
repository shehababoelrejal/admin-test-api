package com.santechture.api.controller;


import com.santechture.api.dto.GeneralResponse;
import com.santechture.api.exception.BusinessExceptions;
import com.santechture.api.service.UserService;
import com.santechture.api.validation.AddUserRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> list(Pageable pageable, @RequestHeader("Authorization") String token){
        return userService.list(pageable, token);
    }
    @PostMapping
    public ResponseEntity<GeneralResponse> addNewUser(@RequestBody AddUserRequest request, @RequestHeader("Authorization") String token) throws BusinessExceptions {
        return userService.addNewUser(request, token);
    }
}
