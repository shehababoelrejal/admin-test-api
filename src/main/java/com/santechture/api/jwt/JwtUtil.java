package com.santechture.api.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.santechture.api.entity.Admin;
import com.santechture.api.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil
{
    @Autowired
    private ObjectMapper objectMapper;

    private String SECRET_KEY = "Xv6PJ/6GT0rQ9DzZuTGV0N9x8wi9a3F0L/nuvDbCjZogzJzaF+duu0JjB8qPrZP2qR2UC5MN4WYYEYpfaWzqJQ==";
    public String extractUsername(String token)
    {
        return extractClaim(token, Claims::getSubject);
    }
    public Date extractExpiration(String token)
    {
        return extractClaim(token, Claims::getExpiration);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token)
    {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
    private Boolean isTokenExpired(String token)
    {
        return extractExpiration(token).before(new Date());
    }
    public String generateToken(Admin userDetails, Map<String,Object> claims)
    {
        return createToken(claims, userDetails);
    }
    public String createToken(Map<String, Object> claims, Admin subject)
    {
        return Jwts.builder().setClaims(claims).setSubject(subject.getUsername()).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }
    public Boolean validateToken(String token, UserDetails userDetails)
    {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    public AdminToken getUserFromToken(String token) {
        Map<String, Object> userMap = extractClaim( token ,
                claims -> ( Map<String, Object> ) claims.get( "admin" ) );

        return objectMapper.convertValue( userMap , AdminToken.class );
    }
}
