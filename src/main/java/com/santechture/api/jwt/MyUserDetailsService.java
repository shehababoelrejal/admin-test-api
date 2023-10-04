package com.santechture.api.jwt;

import com.santechture.api.entity.Admin;
import com.santechture.api.entity.User;
import com.santechture.api.repository.AdminRepository;
import com.santechture.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MyUserDetailsService implements UserDetailsService
{
    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        Admin admin = adminRepository.findByUsernameIgnoreCase(username);
        if (admin == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(
                admin.getUsername(), admin.getPassword(), new ArrayList<>());
    }
}
