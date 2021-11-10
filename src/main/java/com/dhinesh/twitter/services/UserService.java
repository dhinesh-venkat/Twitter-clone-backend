package com.dhinesh.twitter.services;

import java.util.List;

import com.dhinesh.twitter.App;
import com.dhinesh.twitter.authentication.Secured;
import com.dhinesh.twitter.db.Repository;
import com.dhinesh.twitter.models.Follower;
import com.dhinesh.twitter.models.Tweet;
import com.dhinesh.twitter.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/users")
public class UserService {
    
    Repository repo = App.repo;
    ObjectMapper mapper = App.mapper;

    @GET
    @Secured
    @Path("/following")
    @Produces("application/json")
    public List<Follower> getFollowing(@Context SecurityContext securityContext) {
        String user_id = securityContext.getUserPrincipal().getName();

        List<Follower> followingList = repo.getFollowing(user_id);

        return followingList;
    }

    @GET
    @Secured
    @Path("/search/{username}")
    @Produces("application/json")
    public List<User> search(@PathParam("username") String username) {
        List<User> users = repo.searchUsername(username);

        return users;
    }

    @GET
    @Secured
    @Path("/profile/{id}")
    @Produces("application/json")
    public List<Tweet> profile(@PathParam("id") String user_id) {
        List<Tweet> tweets = repo.getTweetsByUser(user_id);

        return tweets;
    }

    @POST
    @Secured
    @Path("/follow/{id}")
    public void follow(@Context SecurityContext securityContext, @PathParam("id") String his_user_id) {
        // other person's id must be on url
        String my_user_id = securityContext.getUserPrincipal().getName();

        int result = repo.follow(my_user_id, his_user_id);

        if (result == 0) {
            throw new WebApplicationException(Response.Status.OK);
        } else {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @DELETE
    @Secured
    @Path("/unfollow/{id}")
    @Produces("application/json")
    public String deleteTweet(@PathParam("id") int id) {
        int result = repo.unfollow(id);
        if (result == 0) {
            throw new WebApplicationException(Response.Status.OK);
        } else {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @DELETE
    @Secured
    @Path("/profile/delete")
    @Produces("application/json")
    public String deleteTweet(@Context SecurityContext securityContext) {
        int result = repo.removeUser(securityContext.getUserPrincipal().getName());
        
        if (result == 0) {
            throw new WebApplicationException(Response.Status.OK);
        } else {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
