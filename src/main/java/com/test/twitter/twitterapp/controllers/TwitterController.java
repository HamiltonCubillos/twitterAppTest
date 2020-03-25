package com.test.twitter.twitterapp.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.test.twitter.twitterapp.entities.TweetEntity;
import com.test.twitter.twitterapp.response.StandardResponse;
import com.test.twitter.twitterapp.response.StatusResponse;
import com.test.twitter.twitterapp.services.TwitterService;

import org.aspectj.lang.annotation.Before;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.List;

import static com.test.twitter.twitterapp.constants.Constants.*;

@Controller
public class TwitterController {

    @Value("${subjectoftweets}")
    private String subjectOfTweets;

    @Autowired
    private TwitterService twitterService;

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TwitterController.class);

    @RequestMapping(value = "/getTweets", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getTweets() {
        LOGGER.debug("Se ha detectado una petición para obtener tweets");
        twitterService.saveValidTweetsBySubject(subjectOfTweets);
        final List<TweetEntity> recoveredTweets = twitterService.getAllTweets();
        final Type listOfTestObject = new TypeToken<List<TweetEntity>>() {}.getType();
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return ResponseEntity.status(HttpStatus.OK).body(
                gson.toJson(new StandardResponse(StatusResponse.SUCCESS, gson.toJsonTree(recoveredTweets, listOfTestObject))));
    }

    @RequestMapping(value = "/validateTweet", params = "id", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<?> validateTweet(final @RequestParam("id") Long id) {
        LOGGER.debug("Se ha detectado una petición para validar el tweet");
        final TweetEntity tweetEntity = twitterService.tweetValidationById(id);
        int status;
        String message;
        if (null == tweetEntity){
            status = HTTP_VALUE_NOT_FOUND;
            message = new Gson().toJson(
                    new StandardResponse(StatusResponse.ERROR, new Gson().toJsonTree(new StringBuilder("Entidad con id ")
                            .append(id).append(" no encontrada!").toString())));
        }else{
            status = HTTP_VALUE_OK;
            message = new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS));
        }
        return ResponseEntity.status(status).body(message);
    }

    @RequestMapping(value = "/getMostPopularHastagsInTheWorld/{hastagsNumber}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getTenPopularHastagInTheWorld(final @PathVariable("hastagsNumber") Integer hastagsNumber) {
        LOGGER.debug(new StringBuilder("Se ha detectado una petición para obtener los ").append(hastagsNumber)
                .append(" hastag más usados").toString());
        final List<String> recoveredHastags = twitterService.getMostPopularHastagInTheWorld(hastagsNumber);
        return ResponseEntity.status(HttpStatus.OK).body(
                new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(recoveredHastags))));
    }

    @RequestMapping(value = "/getValidateTweetsByUser/{user}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getValidateTweetsByUser(final @PathVariable("user") String user) {
        LOGGER.debug(new StringBuilder("Se ha detectado una petición para obtener tweets validados por el usuario ")
                .append(user).toString());
        final List<TweetEntity> recoveredTweets = twitterService.getValidateTweetsByUser(user);
        final Type listOfTestObject = new TypeToken<List<TweetEntity>>() {}.getType();
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return ResponseEntity.status(HttpStatus.OK).body(
                gson.toJson(new StandardResponse(StatusResponse.SUCCESS, gson.toJsonTree(recoveredTweets, listOfTestObject))));
    }
}
