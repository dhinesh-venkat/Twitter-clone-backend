package com.dhinesh.twitter.services;

import java.io.IOException;
import java.util.List;

import com.dhinesh.twitter.App;
import com.dhinesh.twitter.authentication.Secured;
import com.dhinesh.twitter.db.Repository;
import com.dhinesh.twitter.models.Like;
import com.dhinesh.twitter.models.Reply;
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

@Path("/tweets")
public class TweetService {

    Repository repo = App.repo;
    ObjectMapper mapper = App.mapper;

    @GET
    @Secured
    @Path("/")
    @Produces("application/json")
    public List<Tweet> getTweets() {
        List<Tweet> tweets = repo.getTweets();

        return tweets;
    }

    @POST
    @Secured
    @Path("/new")
    public void newTweet(String json, @Context SecurityContext securityContext) {
        Tweet tweet = null;
        try {
            tweet = mapper.readValue(json, Tweet.class);

            String user_id = securityContext.getUserPrincipal().getName();
            User owner = new User();
            owner.setUserId(user_id);

            tweet.setOwner(owner);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }


        // System.out.println(tweet.toString());
        int result = repo.createTweet(tweet);

        if (result == 0) {
            throw new WebApplicationException(Response.Status.OK);

        } else {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @PUT
    @Secured
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
    @Secured
    @Path("/delete/{id}")
    @Produces("application/json")
    public String deleteTweet(@PathParam("id") int id) {

        Tweet tweet = repo.getTweetsById(id);

        if(tweet == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        int result = repo.deleteTweet(id);
        if (result == 0) {
            throw new WebApplicationException(Response.Status.OK);
        } else {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Secured
    @Path("/like/{id}")
    public void likeTweet(@PathParam("id") int tweet_id, @Context SecurityContext securityContext) {
        Like like = new Like();
        like.setTweetId(tweet_id);
        like.setLikedBy(securityContext.getUserPrincipal().getName());

        int result = repo.likeTweet(like);

        if (result == 0) {
            throw new WebApplicationException(Response.Status.OK);
        } else {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @DELETE
    @Secured
    @Path("/dislike/{id}")
    @Produces("application/json")
    public void dislikeTweet(@PathParam("id") int tweet_id, @Context SecurityContext securityContext) {
        Like like = new Like();
        like.setTweetId(tweet_id);
        like.setLikedBy(securityContext.getUserPrincipal().getName());

        int result = repo.dislikeTweet(like);

        if (result == 0) {
            throw new WebApplicationException(Response.Status.OK);
        } else {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Secured
    @Path("/{id}/replies")
    @Produces("application/json")
    public List<Reply> getReplies(@PathParam("id") int tweet_id) {
        List<Reply> replies = repo.getReplies(tweet_id);

        return replies;
    }

    @POST
    @Secured
    @Path("/replies/new")
    public void createReply(String json, @Context SecurityContext securityContext) {
        Reply reply = null;

        try {
            reply = mapper.readValue(json, Reply.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }

        String user_id = securityContext.getUserPrincipal().getName();
        reply.setReplyBy(user_id);

        int result = repo.createReply(reply);

        if (result == 0) {
            throw new WebApplicationException(Response.Status.OK);
        } else {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @PUT
    @Secured
    @Path("/replies/update")
    @Produces("application/json")
    public Reply updateReply(String json) {

        Reply reply = null;
        try {
            reply = mapper.readValue(json, Reply.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }

        // System.out.println(reply.toString());
        // Only content can be updated.
        int result = repo.updateReply(reply);

        if (result == 0) {
            return repo.getReplyById(reply.getId());
        } else {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @DELETE
    @Secured
    @Path("/replies/delete/{id}")
    @Produces("application/json")
    public void deleteReply(@PathParam("id") int id) {
        int result = repo.deleteReply(id);

        if (result == 0) {
            throw new WebApplicationException(Response.Status.OK);
        } else {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Secured
    @Path("/saved")
    @Produces("application/json")
    public List<Tweet> getSavedTweets(@Context SecurityContext securityContext) {
        String user_id = securityContext.getUserPrincipal().getName();

        List<Tweet> tweets = repo.getSavedTweets(user_id);

        return tweets;
    }

    @POST
    @Secured
    @Path("/save/{id}")
    public void save(@PathParam("id") int tweet_id, @Context SecurityContext securityContext) {
        String user_id = securityContext.getUserPrincipal().getName();

        int result = repo.saveTweet(tweet_id, user_id);

        if (result == 0) {
            throw new WebApplicationException(Response.Status.OK);
        } else {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @DELETE
    @Secured
    @Path("/unsave/{id}")
    @Produces("application/json")
    public void unsave(@PathParam("id") int id) {
        int result = repo.unsaveTweet(id);

        if (result == 0) {
            throw new WebApplicationException(Response.Status.OK);
        } else {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
