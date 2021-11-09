package com.dhinesh.twitter.services;

import java.io.IOException;
import java.util.List;

import com.dhinesh.twitter.App;
import com.dhinesh.twitter.db.Repository;
import com.dhinesh.twitter.models.Follower;
import com.dhinesh.twitter.models.Like;
import com.dhinesh.twitter.models.Reply;
import com.dhinesh.twitter.models.SaveTweet;
import com.dhinesh.twitter.models.Tweet;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@Path("/followers")
public class FollowerService {
    
    Repository repo = App.repo;
    ObjectMapper mapper = App.mapper;

    @GET
    @Path("/{id}")
    @Produces("application/json")
    public List<Follower> getFollowers(@PathParam("id") String user_id) {
        List<Follower> followers = repo.getFollowers(user_id);

        return followers;
    }

    @DELETE
    @Path("/remove/{id}")
    @Produces("application/json")
    public void dislikeTweet(@PathParam("id") int id) {
        int result = repo.removeFollower(id);

        if (result == 0) {
            throw new WebApplicationException(Response.Status.OK);
        } else {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

}
