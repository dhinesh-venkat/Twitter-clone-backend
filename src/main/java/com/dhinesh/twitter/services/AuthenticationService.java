package com.dhinesh.twitter.services;

import com.dhinesh.twitter.App;
import com.dhinesh.twitter.db.Repository;
import com.dhinesh.twitter.models.User;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@Path("/")
public class AuthenticationService {

    Repository repo = App.repo;

    @POST
    @Path("/signup")
    @Produces("application/json")
    @Consumes("application/json")
    public User addUser(User user) {
        System.out.println(user.toString());

        int result = repo.addUser(user);

        if(result == 0) {
            return repo.getUser(user.getUsername());
        } else {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }


}