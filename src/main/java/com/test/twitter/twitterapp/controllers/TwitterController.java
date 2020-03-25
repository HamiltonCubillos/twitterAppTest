package com.test.twitter.twitterapp.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.test.twitter.twitterapp.entities.TweetEntity;
import com.test.twitter.twitterapp.response.StandardResponse;
import com.test.twitter.twitterapp.response.StatusResponse;
import com.test.twitter.twitterapp.services.TwitterService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.List;

@Controller
public class TwitterController {

    @Value("${subjectoftweets}")
    private String subjectOfTweets;

    @Autowired
    private TwitterService twitterService;

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TwitterController.class);

    @RequestMapping(value = "/getTweets", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> retriveTweets() {
        LOGGER.debug("Se ha detectado una petición para obtener tweets");
        final List<TweetEntity> recoveredTweets = twitterService.getTweetsBySubject(subjectOfTweets);
        final Type listOfTestObject = new TypeToken<List<TweetEntity>>() {}.getType();
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return ResponseEntity.status(200).body(
                gson.toJson(new StandardResponse(StatusResponse.SUCCESS, gson.toJsonTree(recoveredTweets, listOfTestObject))));
    }
}
