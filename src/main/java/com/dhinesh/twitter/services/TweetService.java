package com.dhinesh.twitter.services;

import java.io.IOException;
import java.util.List;

import com.dhinesh.twitter.App;
import com.dhinesh.twitter.db.Repository;
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

@Path("/tweets")
public class TweetService {

    Repository repo = App.repo;
    ObjectMapper mapper = App.mapper;

    @GET
    @Path("/")
    @Produces("application/json")
    public List<Tweet> getTweets() {
        List<Tweet> tweets = repo.getTweets();

        return tweets;
    }

    @POST
    @Path("/new")
    public void newTweet(String json) {
        Tweet tweet = null;
        try {
            tweet = mapper.readValue(json, Tweet.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }

        // System.out.println(tweet.toString());
        int result = repo.createTweet(tweet);

        if (result == 0) {
            throw new WebApplicationException(Response.Status.ACCEPTED);

        } else {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @PUT
    @Path("/update")
    @Produces("application/json")
    public Tweet updateTweet(String json) {

        Tweet tweet = null;
        try {
            tweet = mapper.readValue(json, Tweet.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }

        // System.out.println(tweet.toString());
        // Only content and visibility can be updated.
        int result = repo.updateTweet(tweet);

        if (result == 0) {
            return repo.getTweetsById(tweet.getTweetId());

        } else {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @DELETE
    @Path("/delete/{id}")
    @Produces("application/json")
    public String deleteTweet(@PathParam("id") int id) {

        Tweet tweet = repo.getTweetsById(id);

        if(tweet == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        int result = repo.deleteTweet(id);
        if (result == 0) {
            return "success";
        } else {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
