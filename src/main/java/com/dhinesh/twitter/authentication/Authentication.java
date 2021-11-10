package com.dhinesh.twitter.authentication;


import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.dhinesh.twitter.db.Repository;
import com.dhinesh.twitter.models.Credentials;
import com.dhinesh.twitter.models.User;
import com.dhinesh.twitter.services.KeyService;


import java.security.Key;
import io.jsonwebtoken.*;
import java.util.Date; 


// https://stackoverflow.com/questions/26777083/how-to-implement-rest-token-based-authentication-with-jax-rs-and-jersey/26778123#26778123

@Path("/login")
public class Authentication {
    
    final Repository repo;
    User user;

    public Authentication() {
        repo = new Repository();

    }
    
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public Response authenticateUser(Credentials credentials) {
        
        try {

            String username = credentials.getUsername();
            String password = credentials.getPassword();

            // Authenticate the user using the credentials provided
            authenticate(username, password);

            // Issue a token for the user
            String token = issueToken(username);
 
            // Return the token on the response
            return Response.ok(token).build();

        } catch (Exception e) {
            //e.printStackTrace();
            return Response.status(Response.Status.FORBIDDEN).build();
        }

    }


    private void authenticate(String username, String password) throws Exception {
        // Authenticate against a database
        // Throw an Exception if the credentials are invalid
        user = repo.getUser(username);
        String bcryptHashString = user.getPassword();

        if(user == null || !repo.verifyPassword(password, bcryptHashString)) {
            throw new Exception("Wrong credentials");
        }
    }

    private String issueToken(String username) {

        Repository repo = new Repository();

        // Issue a token (can be a random String persisted to a database or a JWT token)
        

        // The issued token must be associated to a user
        user = repo.getUser(username);
        
        String userId = user.getUserId();
        String token = createJWT(userId, "admin", username);

        // System.out.println(token);

        repo.storeToken(userId, token);

        // Return the issued token
        return token;
    }

    private String createJWT(String id, String issuer, String subject) {
 
        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        String key = KeyService.ACCESS_TOKEN_SECRET;
     
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
     
        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(key);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
     
        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                                    .setIssuedAt(now)
                                    .setSubject(subject)
                                    .setIssuer(issuer)
                                    .signWith(signatureAlgorithm, signingKey);
     
        //if it has been specified, let's add the expiration
        // if (ttlMillis >= 0) {
        // long expMillis = nowMillis + ttlMillis;
        //     Date exp = new Date(expMillis);
        //     builder.setExpiration(exp);
        // }
     
        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

   

}
