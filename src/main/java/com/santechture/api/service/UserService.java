package com.santechture.api.service;

import com.santechture.api.dto.GeneralResponse;
import com.santechture.api.dto.user.UserDto;
import com.santechture.api.entity.Admin;
import com.santechture.api.entity.User;
import com.santechture.api.exception.BusinessExceptions;
import com.santechture.api.jwt.AdminToken;
import com.santechture.api.jwt.JwtUtil;
import com.santechture.api.repository.UserRepository;
import com.santechture.api.validation.AddUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private JwtUtil jwtUtil;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public ResponseEntity<GeneralResponse> list(Pageable pageable, String requestToken){
        final String token = requestToken.split( " " )[1].trim();
        AdminToken admin = jwtUtil.getUserFromToken(token);

        logger.info("id: " + admin.getId().toString());
        logger.info("username: " + admin.getUsername());
        return new GeneralResponse().response(userRepository.findAll(pageable));
    }

    public ResponseEntity<GeneralResponse> addNewUser(AddUserRequest request, String requestToken) throws BusinessExceptions {
        final String token = requestToken.split( " " )[1].trim();
        AdminToken admin = jwtUtil.getUserFromToken(token);

        logger.info(admin.getId().toString());
        logger.info(admin.getUsername());

        if(userRepository.existsByUsernameIgnoreCase(request.getUsername())){
            throw new BusinessExceptions("username.exist");
        } else if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new BusinessExceptions("email.exist");
        }

        User user = new User(request.getUsername(),request.getEmail());
        userRepository.save(user);

        return new GeneralResponse().response(new UserDto(user));
    }

}
