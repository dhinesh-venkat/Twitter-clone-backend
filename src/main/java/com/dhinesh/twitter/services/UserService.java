package com.dhinesh.twitter.services;

import java.io.IOException;
import java.util.List;

import com.dhinesh.twitter.App;
import com.dhinesh.twitter.authentication.Secured;
import com.dhinesh.twitter.db.Repository;
import com.dhinesh.twitter.models.Follower;
import com.dhinesh.twitter.models.Like;
import com.dhinesh.twitter.models.Reply;
import com.dhinesh.twitter.models.SaveTweet;
import com.dhinesh.twitter.models.Tweet;
import com.dhinesh.twitter.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
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
    @Path("/following/{id}")
    @Produces("application/json")
    public List<Follower> getFollowing(@PathParam("id") String user_id) {
        List<Follower> followingList = repo.getFollowing(user_id);

        return followingList;
    }

    @GET
    @Path("/search/{username}")
    @Produces("application/json")
    public List<User> search(@PathParam("username") String username) {
        List<User> users = repo.searchUsername(username);

        return users;
    }

    @GET
    @Path("/{id}/profile")
    @Produces("application/json")
    public List<Tweet> profile(@PathParam("id") String user_id) {
        List<Tweet> tweets = repo.getTweetsByUser(user_id);

        return tweets;
    }

    @POST
    @Path("/follow")
    public void newTweet(String json) {
        Follower follower = null;
        try {
            follower = mapper.readValue(json, Follower.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }

        // System.out.println(follower.toString());
        int result = repo.follow(follower.getFollowedBy().getUserId(), follower.getUserId());

        if (result == 0) {
            throw new WebApplicationException(Response.Status.OK);

        } else {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @DELETE
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
        int result = repo.deleteAccount(securityContext.getUserPrincipal().getName());
        
        if (result == 0) {
            throw new WebApplicationException(Response.Status.OK);
        } else {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
