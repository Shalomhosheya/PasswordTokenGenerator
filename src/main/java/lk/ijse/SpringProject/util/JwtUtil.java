package lk.ijse.SpringProject.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lk.ijse.SpringProject.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Component
@PropertySource(ignoreResourceNotFound = true,value = "class:otherprops.properties")
public class JwtUtil implements Serializable {
    private static final long serialVersionUID=234234523523L;
    public static final long JWT_TOKEN_VALIDITY=24*68*60*12;

    @Value("${jwt.secret}")
    private String secretKey;

    public String getUsernameFromToken(String token){
        return getClaimFromToken(token, Claims::getSubject);
    }
    public Date getExpirationDatefromToken(String token){
        return getClaimFromToken(token,Claims::getExpiration);
    }
    public Claims getUserRoleCodeFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }
    public <T>T getClaimFromToken(String token, Function<Claims,T>claimResolver){
        final Claims claims = getAllClaimsFromToken(token);
        return claimResolver.apply(claims);
    }
    public Claims getAllClaimsFromToken(String token){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }
    private Boolean isTokenExpired(String token){
        final Date expiration = getExpirationDatefromToken(token);
        return expiration.before(new Date());
    }
    public String generateToken(UserDto userDTO) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role",userDTO.getRole());
        return doGenerateToken(claims, userDTO.getEmail());
    }
    public  String doGenerateToken(Map<String,Object>claims,String subject){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+JWT_TOKEN_VALIDITY *1000))
                .signWith(SignatureAlgorithm.ES256,secretKey).compact();
    }
    public Boolean validationToken(String token, UserDetails userDetails){
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername())&&!isTokenExpired(token));
    }
}
