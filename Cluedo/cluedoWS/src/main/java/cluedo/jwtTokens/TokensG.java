package cluedo.jwtTokens;


import cluedo.modele.Utilisateur;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class TokensG {

    public static final String SECRET = "test";
    public static final long VALIDATION_TOKEN_HOURS = 600000;
    public static final String AUTH_TOKEN_NAME = "Authorization";

    public String createToken(Utilisateur user) {
        Claims claims = Jwts.claims().setSubject(user.getNom());
        claims.put("pseudo",user.getNom());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("http://devglan.com/")
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + VALIDATION_TOKEN_HOURS*1000))
                        .signWith(SignatureAlgorithm.HS256,SECRET)
                        .compact();
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromTokenDate(token);
    }

    public  Date getClaimFromTokenDate(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.getExpiration();
    }

    public Jws<Claims> decodeToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET.getBytes())
                .parseClaimsJws(token);
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
    public  String getClaimFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    public String refresh(String token){
        Claims claims = decodeToken(token).getBody();// on decode le token, si il est faux on a une exception levé, sinon on recup le body -> exception si date dépassé ou invalid signature
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + VALIDATION_TOKEN_HOURS))
                .signWith(SignatureAlgorithm.HS512, SECRET.getBytes())
                .compact();
    }
}