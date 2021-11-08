package com.dhinesh.twitter.services;

import java.io.IOException;

import com.dhinesh.twitter.App;
import com.dhinesh.twitter.db.Repository;
import com.dhinesh.twitter.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@Path("/")
public class AuthenticationService {

    Repository repo = App.repo;
    ObjectMapper mapper = App.mapper;

    @POST
    @Path("/signup")
    @Produces("application/json")
    public User addUser(String json) {

        User user = null;
        try {
            user = mapper.readValue(json, User.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }

        //System.out.println(user.toString());
        int result = repo.addUser(user);

        if (result == 0) {
            return repo.getUser(user.getUsername());
        } else {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

}