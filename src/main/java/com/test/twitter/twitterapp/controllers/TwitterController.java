package com.test.twitter.twitterapp.controllers;

import com.test.twitter.twitterapp.services.TwitterService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;

import static com.test.twitter.twitterapp.constants.Constants.ONE_HUNDRED;
import static com.test.twitter.twitterapp.constants.Constants.ZERO_INT;

@RestController
public class TwitterController {
    @Value("${oauth.consumerKey}")
    private String consumerKey;

    @Value("${oauth.consumerSecret}")
    private String consumerSecret;

    @Value("${oauth.acessToken}")
    private String acessToken;

    @Value("${oauth.accessTokenSecret}")
    private String accessTokenSecret;

    @Autowired
    private TwitterService twitterService;

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TwitterController.class);


    @GetMapping("/getTweets")
    public void retriveTweets() {
        final Twitter twitter = getTwitterObject();
        final Query query = new Query("#covid19");
        long lastID = Long.MAX_VALUE;
        final int numberOfTweetsMaximum = 512;
        final ArrayList<Status> tweets = new ArrayList<>(ZERO_INT);
        int tweetSubstraction;
        int currentTweetsSize;
        QueryResult result;
        while (numberOfTweetsMaximum > (currentTweetsSize = (tweets.size()))) {
            tweetSubstraction = numberOfTweetsMaximum - currentTweetsSize;
            query.setCount(ONE_HUNDRED < tweetSubstraction ? ONE_HUNDRED : tweetSubstraction);
            try {
                result = twitter.search(query);
                tweets.addAll(result.getTweets());
                LOGGER.debug(new StringBuilder("Se han recopilado ").append(currentTweetsSize).append(" tweets").toString());
                for (Status t : tweets)
                    if (t.getId() < lastID) lastID = t.getId();

            } catch (TwitterException te) {
                LOGGER.error(new StringBuilder("TwitterException, no se puede conectar: ").append(te).toString());
            }
            query.setMaxId(lastID - 1);
        }
        for (int i = ZERO_INT; i < tweets.size(); i++) {
            final Status t = (Status) tweets.get(i);
            final String user = t.getUser().getScreenName();
            final String msg = t.getText();
            LOGGER.debug(new StringBuilder(i).append(" El usuario: ").append(user).append(" escribió ")
                    .append(msg).toString());
        }
    }
        /*
        final QueryResult result = twitter.search(query);
        final List<Status> tweets = result.getTweets();
        User currentUser;
        List<TweetEntity> tweetsResponse = new ArrayList<>(ZERO_INT);
        for (Status currentTweet : tweets) {
            currentUser = currentTweet.getUser();
            if (twitterService.isTweetLangEsFrOrIt(currentTweet) &&
                    (twitterService.hasUserMoreFifteenhundredFollowers(currentUser))) {
                tweetsResponse.add(twitterService.getTweetInitialized(currentUser.getScreenName(), currentTweet.getText(),
                        currentTweet.getPlace().getCountry(), false));
                System.out.println("@" + currentUser.getScreenName() + " - " + currentTweet.getText());
            }
        }
        System.exit(0);


        //System.out.println("Failed to search tweets: " + te.getMessage());
        System.exit(-1);*/


    private Twitter getTwitterObject() {
        final ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(acessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);
        return new TwitterFactory(cb.build()).getInstance();
    }
}
