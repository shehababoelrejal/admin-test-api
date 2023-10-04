package com.santechture.api.service;

import com.santechture.api.controller.UserController;
import com.santechture.api.dto.GeneralResponse;
import com.santechture.api.dto.admin.AdminDto;
import com.santechture.api.entity.Admin;
import com.santechture.api.exception.BusinessExceptions;
import com.santechture.api.jwt.AdminToken;
import com.santechture.api.jwt.AuthenticationResponse;
import com.santechture.api.jwt.JwtUtil;
import com.santechture.api.jwt.MyUserDetailsService;
import com.santechture.api.repository.AdminRepository;
import com.santechture.api.validation.LoginRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final Logger logger = LoggerFactory.getLogger(AdminService.class);

    private final AdminRepository adminRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailsService userDetailsService;
    @Autowired
    private JwtUtil jwtTokenUtil;


    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public ResponseEntity<?> login(LoginRequest request) throws BusinessExceptions {

        Admin admin = adminRepository.findByUsernameIgnoreCase(request.getUsername());

        if(Objects.isNull(admin) || !admin.getPassword().equals(request.getPassword())){
            throw new BusinessExceptions("login.credentials.not.match");
        }

        try
        {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(admin.getUsername(), admin.getPassword()));
        }
        catch (BadCredentialsException e)
        {
            logger.error("Bad Credentials Exception" + e);
            logger.error("User logged in failed.");
            return ResponseEntity.notFound().build();
        }
        final String jwt = jwtTokenUtil.generateToken(admin, getClaims(admin));
        logger.info("User logged in successfully.");
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
    private Map<String, Object> getClaims(Admin admin) {
        AdminToken adminToken = AdminToken.builder().id(admin.getAdminId()).username(admin.getUsername()).build();
        Map<String, Object> claims = new HashMap<>();
        claims.put( "admin" , adminToken );
        return claims;
    }
}
