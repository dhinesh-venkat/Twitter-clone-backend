package com.dhinesh.twitter.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.dhinesh.twitter.models.Follower;
import com.dhinesh.twitter.models.Like;
import com.dhinesh.twitter.models.Reply;
import com.dhinesh.twitter.models.Tweet;
import com.dhinesh.twitter.models.User;
import com.dhinesh.twitter.services.KeyService;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import javax.xml.bind.DatatypeConverter;

public class Repository {

    Connection conn = null;

    public Repository() {
        String url = "jdbc:mysql://localhost:3306/twitter";
        String username = "root";
        String password = "root";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String isValidToken(String token) {
        PreparedStatement st = null;
        String sql = "select user_id from tokens where token=?";
        String user_id = "";

        try {
            st = conn.prepareStatement(sql);
            st.setString(1, token);

            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                user_id = rs.getString("user_id");
            }

            return user_id;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return user_id;
    }

    private boolean existsId(String user_id) {
        boolean exists = false;
        PreparedStatement st = null;
        String sql = "select * from tokens where user_id=?";

        try {
            st = conn.prepareStatement(sql);
            st.setString(1, user_id);

            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                exists = true;
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return exists;
    }

    public void storeToken(String id, String token) {
        PreparedStatement st = null;

        if (existsId(id)) {

            String sql = "update Tokens set token=? where user_id=?";

            try {
                st = conn.prepareStatement(sql);

                st.setString(1, token);
                st.setString(2, id);

                st.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            // System.out.println("Token updated!");

        } else {
            String sql = "insert into Tokens (user_id,token) values (?,?)";

            try {
                st = conn.prepareStatement(sql);

                st.setString(1, id);
                st.setString(2, token);

                st.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            // System.out.println("Token added!");

        }
    }

    public String getUserFromToken(String token) {

        String key = KeyService.ACCESS_TOKEN_SECRET;

        // This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(key)).parseClaimsJws(token)
                .getBody();

        // System.out.println("ID: " + claims.getId());
        // System.out.println("Subject: " + claims.getSubject());
        // System.out.println("Issuer: " + claims.getIssuer());
        // System.out.println("Expiration: " + claims.getExpiration());

        // uncomment to return username
        // return claims.getSubject();

        // will return userId
        return claims.getId();
    }

    // generate user ID
    public String generateId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    // creates new user
    public int addUser(User user) {
        PreparedStatement st = null;
        String sql = "insert into Users values (?,?,?,CURRENT_TIMESTAMP(),?,?)";

        String id = generateId();

        try {
            st = conn.prepareStatement(sql);

            st.setString(1, id);
            st.setString(2, user.getUsername().toLowerCase());
            st.setString(3, user.getPassword());
            st.setString(4, user.getDisplayName());
            st.setString(5, user.getAvatar());

            st.executeUpdate();

        } catch (SQLException e) {
            // possible that the username already exists
            e.printStackTrace();
            return 1;

        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    // get user by username
    public User getUser(String username) {
        PreparedStatement st = null;
        User user = null;
        String sql = "select * from users where username=?";

        try {
            st = conn.prepareStatement(sql);
            st.setString(1, username);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                user = new User();

                user.setAvatar(rs.getString("avatar"));
                user.setCreatedAt(rs.getDate("created_at"));
                user.setDisplayName(rs.getString("display_name"));
                user.setPassword(rs.getString("password"));
                user.setUserId(rs.getString("user_id"));
                user.setUsername(rs.getString("username"));
            }

            return user;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return user;
    }

    // check username availability
    public String searchUsernameExact(String username) {
        PreparedStatement st = null;

        String id = "";

        username = username.toLowerCase().trim();

        String sql = "select user_id from users where username = ?";

        try {
            st = conn.prepareStatement(sql);
            st.setString(1, username);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                id = rs.getString("user_id");
            }

            return id;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return id;
    }

    // search users
    public List<User> searchUsername(String username) {
        List<User> users = new ArrayList<User>();

        PreparedStatement st = null;
        String sql = "select * from users where username like CONCAT('%',?,'%')";

        try {
            st = conn.prepareStatement(sql);
            st.setString(1, username);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                User user = new User();

                user.setAvatar(rs.getString("avatar"));
                user.setCreatedAt(rs.getDate("created_at"));
                user.setDisplayName(rs.getString("display_name"));
                user.setUserId(rs.getString("user_id"));
                user.setUsername(rs.getString("username"));

                users.add(user);
            }

            return users;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return users;
    }

    // get all tweets
    public List<Tweet> getTweets() {
        List<Tweet> tweets = new ArrayList<Tweet>();

        Statement st = null;
        String sql = "select * from tweets inner join users on tweets.owner_id=users.user_id order by tweets.created_at desc";

        try {
            st = conn.createStatement();

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Tweet tweet = new Tweet();

                tweet.setContent(rs.getString("content"));
                tweet.setCreatedAt(rs.getDate("created_at"));
                tweet.setLikes(rs.getInt("likes"));
                

                User owner = new User();
                owner.setUserId(rs.getString("owner_id"));
                owner.setUsername(rs.getString("username"));
                owner.setDisplayName(rs.getString("display_name"));
                owner.setAvatar(rs.getString("avatar"));
                tweet.setOwner(owner);

                tweet.setIsPublic(rs.getBoolean("public"));
                tweet.setTweetId(rs.getInt("tweet_id"));

                tweets.add(tweet);
            }

            return tweets;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tweets;

    }

    // create new tweet
    public int createTweet(Tweet tweet) {
        PreparedStatement st = null;
        String sql = "insert into tweets values (?,?,?,CURRENT_TIMESTAMP(),?,?)";

        try {
            st = conn.prepareStatement(sql);
            st.setInt(1, tweet.getTweetId());
            st.setString(2, tweet.getContent());
            st.setString(3, tweet.getOwner().getUserId());
            st.setBoolean(4, tweet.getIsPublic());
            st.setInt(5, tweet.getLikes());

            st.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    // update a tweet
    public int updateTweet(Tweet tweet) {
        PreparedStatement st = null;
        String sql = "update tweets set content=?,public=? where tweet_id=?";

        try {
            st = conn.prepareStatement(sql);
            st.setString(1, tweet.getContent());
            st.setBoolean(2, tweet.getIsPublic());
            st.setInt(3, tweet.getTweetId());

            st.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return 1;

        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    // delete a tweet
    public int deleteTweet(int id) {
        PreparedStatement st = null;
        String sql = "delete from tweets where tweet_id = ?";

        try {
            st = conn.prepareStatement(sql);
            st.setInt(1, id);

            st.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return 1;

        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    // update like count
    public int updateLike(int tweet_id) {
        PreparedStatement st = null;
        String sql = "update tweets set likes=(select count(liked_by) from likes where tweet_id=?) where tweet_id=?;";

        try {
            st = conn.prepareStatement(sql);
            st.setInt(1, tweet_id);
            st.setInt(2, tweet_id);

            st.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return 1;

        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    // like a tweet
    public int likeTweet(Like like) {
        PreparedStatement st = null;
        String sql = "insert into likes (tweet_id, liked_by) values (?, ?)";
        int result = 1;

        try {
            st = conn.prepareStatement(sql);
            st.setInt(1, like.getTweetId());
            st.setString(2, like.getLikedBy());

            st.executeUpdate();
            result = 0;
        } catch (Exception e) {
            e.printStackTrace();
            return result;

        } finally {
            try {
                st.close();
                result = updateLike(like.getTweetId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    // dislike a tweet
    public int dislikeTweet(Like like) {
        PreparedStatement st = null;
        String sql = "delete from likes where tweet_id=? and liked_by=?";
        int result = 1;

        try {
            st = conn.prepareStatement(sql);
            st.setInt(1, like.getTweetId());
            st.setString(2, like.getLikedBy());

            st.executeUpdate();
            result = 0;
        } catch (Exception e) {
            e.printStackTrace();
            return result;

        } finally {
            try {
                st.close();
                updateLike(like.getTweetId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    // save a tweet
    public int saveTweet(int tweet_id, String user_id) {
        PreparedStatement st = null;
        String sql = "insert into saved_posts (tweet_id, user_id) values (?, ?)";

        try {
            st = conn.prepareStatement(sql);
            st.setInt(1, tweet_id);
            st.setString(2, user_id);

            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 1;

        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    // unsave a tweet
    public int unsaveTweet(int id) {
        PreparedStatement st = null;
        String sql = "delete from saved_posts where id=?";

        try {
            st = conn.prepareStatement(sql);
            st.setInt(1, id);

            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 1;

        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    // get saved tweets
    public List<Tweet> getSavedTweets(String user_id) {
        List<Tweet> tweets = new ArrayList<Tweet>();

        PreparedStatement st = null;
        String sql = "select * from tweets inner join users on tweets.owner_id=users.user_id where tweet_id in (select tweet_id from saved_posts where user_id=?)";

        try {
            st = conn.prepareStatement(sql);
            st.setString(1, user_id);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Tweet tweet = new Tweet();

                tweet.setContent(rs.getString("content"));
                tweet.setCreatedAt(rs.getDate("created_at"));
                tweet.setLikes(rs.getInt("likes"));
                User owner = new User();
                owner.setUserId(rs.getString("owner_id"));
                owner.setUsername(rs.getString("username"));
                owner.setDisplayName(rs.getString("display_name"));
                owner.setAvatar(rs.getString("avatar"));
                tweet.setOwner(owner);
                tweet.setIsPublic(rs.getBoolean("public"));
                tweet.setTweetId(rs.getInt("tweet_id"));

                tweets.add(tweet);
            }

            return tweets;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tweets;

    }

    // create reply
    public int createReply(Reply reply) {
        PreparedStatement st = null;
        String sql = "insert into replies(content,reply_by,tweet_id,created_at) values (?, ?, ?, current_timestamp())";

        try {
            st = conn.prepareStatement(sql);
            st.setString(1, reply.getContent());
            st.setString(2, reply.getReplyBy());
            st.setInt(3, reply.getTweetId());

            st.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    // get all replies
    public List<Reply> getReplies(int tweet_id) {
        List<Reply> replies = new ArrayList<Reply>();

        PreparedStatement st = null;
        String sql = "select * from replies where tweet_id=? order by created_at desc";

        try {
            st = conn.prepareStatement(sql);
            st.setInt(1, tweet_id);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Reply reply = new Reply();

                reply.setContent(rs.getString("content"));
                reply.setCreatedAt(rs.getDate("created_at"));
                reply.setId(rs.getInt("id"));
                reply.setReplyBy(rs.getString("reply_by"));
                reply.setTweetId(rs.getInt("tweet_id"));

                replies.add(reply);
            }

            return replies;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return replies;

    }

    // get a reply by id
    public Reply getReplyById(int id) {
        Reply reply = null;

        PreparedStatement st = null;
        String sql = "select * from replies where id=?";

        try {
            st = conn.prepareStatement(sql);
            st.setInt(1, id);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                reply = new Reply();

                reply.setContent(rs.getString("content"));
                reply.setCreatedAt(rs.getDate("created_at"));
                reply.setId(rs.getInt("id"));
                reply.setReplyBy(rs.getString("reply_by"));
                reply.setTweetId(rs.getInt("tweet_id"));

                return reply;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return reply;
    }

    // update a tweet
    public int updateReply(Reply reply) {
        PreparedStatement st = null;
        String sql = "update replies set content=? where id=?";

        try {
            st = conn.prepareStatement(sql);
            st.setString(1, reply.getContent());
            st.setInt(2, reply.getId());

            st.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    // delete a reply
    public int deleteReply(int id) {
        PreparedStatement st = null;
        String sql = "delete from replies where id=?";

        try {
            st = conn.prepareStatement(sql);
            st.setInt(1, id);

            st.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return 1;

        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    // get followers (all who follow user_id)
    public List<Follower> getFollowers(String user_id) {
        List<Follower> followers = new ArrayList<Follower>();

        PreparedStatement st = null;
        // id, follower_id, username, display_name,avatar
        String sql = "SELECT followers.id, followers.followed_by, users.username, users.display_name, users.avatar FROM followers INNER JOIN users ON followers.followed_by=users.user_id where followers.user_id=? ORDER BY users.display_name ASC";

        try {
            st = conn.prepareStatement(sql);
            st.setString(1, user_id);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Follower follower = new Follower();

                User user = new User();
                user.setUserId(rs.getString("followed_by"));
                user.setUsername(rs.getString("username"));
                user.setDisplayName(rs.getString("display_name"));
                user.setAvatar(rs.getString("avatar"));

                follower.setId(rs.getInt("id"));
                follower.setUserId(user_id);
                follower.setFollowedBy(user);

                followers.add(follower);
            }

            return followers;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return followers;

    }

    // remove a follower
    public int removeFollower(int id) {
        PreparedStatement st = null;
        String sql = "delete from followers where id=?";

        try {
            st = conn.prepareStatement(sql);
            st.setInt(1, id);

            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    // get tweets by user_id
    public List<Tweet> getTweetsByUser(String user_id) {
        List<Tweet> tweets = new ArrayList<Tweet>();

        PreparedStatement st = null;
        String sql = "select * from tweets inner join users on tweets.owner_id=users.user_id where owner_id=? order by tweets.created_at desc";

        try {
            st = conn.prepareStatement(sql);
            st.setString(1, user_id);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Tweet tweet = new Tweet();

                tweet.setContent(rs.getString("content"));
                tweet.setCreatedAt(rs.getDate("created_at"));
                tweet.setLikes(rs.getInt("likes"));

                User owner = new User();
                owner.setUserId(rs.getString("owner_id"));
                owner.setUsername(rs.getString("username"));
                owner.setDisplayName(rs.getString("display_name"));
                owner.setAvatar(rs.getString("avatar"));
                tweet.setOwner(owner);

                tweet.setIsPublic(rs.getBoolean("public"));
                tweet.setTweetId(rs.getInt("tweet_id"));

                tweets.add(tweet);
            }

            return tweets;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tweets;

    }

    // get a tweet by id
    public Tweet getTweetsById(int tweet_id) {
        Tweet tweet = null;

        PreparedStatement st = null;
        String sql = "select * from tweets inner join users on tweets.owner_id=users.user_id where tweet_id=?";

        try {
            st = conn.prepareStatement(sql);
            st.setInt(1, tweet_id);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                tweet = new Tweet();

                tweet.setContent(rs.getString("content"));
                tweet.setCreatedAt(rs.getDate("created_at"));
                tweet.setLikes(rs.getInt("likes"));

                User owner = new User();
                owner.setUserId(rs.getString("owner_id"));
                owner.setUsername(rs.getString("username"));
                owner.setDisplayName(rs.getString("display_name"));
                owner.setAvatar(rs.getString("avatar"));
                tweet.setOwner(owner);

                tweet.setIsPublic(rs.getBoolean("public"));
                tweet.setTweetId(rs.getInt("tweet_id"));

                return tweet;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tweet;

    }

    // get all users user_id is following
    public List<Follower> getFollowing(String user_id) {
        List<Follower> followers = new ArrayList<Follower>();

        PreparedStatement st = null;
        // id, follower_id, username, display_name,avatar
        String sql = "SELECT followers.id, followers.user_id as 'following', users.username, users.display_name, users.avatar FROM followers INNER JOIN users ON followers.user_id=users.user_id where followers.followed_by=? ORDER BY users.display_name ASC;";

        try {
            st = conn.prepareStatement(sql);
            st.setString(1, user_id);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Follower follower = new Follower();

                User user = new User();
                user.setUserId(rs.getString("following"));
                user.setUsername(rs.getString("username"));
                user.setDisplayName(rs.getString("display_name"));
                user.setAvatar(rs.getString("avatar"));

                follower.setId(rs.getInt("id"));
                follower.setUserId(user_id);
                follower.setFollowing(user);

                followers.add(follower);
            }

            return followers;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return followers;

    }

    // follow him
    public int follow(String me, String him) {
        PreparedStatement st = null;
        String sql = "insert into followers (user_id, followed_by) values(?,?)";

        try {
            st = conn.prepareStatement(sql);
            st.setString(1, him);
            st.setString(2, me); // followed by me (current user)

            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    // unfollow him
    public int unfollow(int id) {
        PreparedStatement st = null;
        String sql = "delete from followers where id=?";

        try {
            st = conn.prepareStatement(sql);
            st.setInt(1, id);

            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    // Delete an user account
    public int removeUser(String user_id) {
        PreparedStatement st = null;
        String sql = "delete from users where user_id=?";

        try {
            st = conn.prepareStatement(sql);
            st.setString(1, user_id);

            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    // logout
    public int logout(String user_id) {
        PreparedStatement st = null;
        String sql = "delete from tokens where user_id=?";

        try {
            st = conn.prepareStatement(sql);
            st.setString(1, user_id);

            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public String hashPassword(String password) {
        // System.out.println("Before hashing : " + password);

        String bcryptHashString = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        // System.out.println("After hashing : " + bcryptHashString);

        return bcryptHashString;
    }


    public boolean verifyPassword(String password, String bcryptHashString) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), bcryptHashString);
        System.out.println(result.details);

        return result.verified;
    }
}