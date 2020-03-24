package com.test.twitter.twitterapp.services;

import com.test.twitter.twitterapp.entities.TweetEntity;
import org.springframework.stereotype.Service;
import twitter4j.*;

import static com.test.twitter.twitterapp.constants.Constants.*;


@Service
public class TwitterService {
    public String getNameOfFirstTrendInTheWorld(final Twitter twitter) {
        String nameFirstTrendInTheWorld = null;
        Trends trends = null;
        try {
            trends = twitter.getPlaceTrends(ONE_INT);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        if (null != trends) {
            nameFirstTrendInTheWorld = trends.getTrends()[0].getName();
        }
        return nameFirstTrendInTheWorld;
    }

    public boolean isTweetLangEsFrOrIt(final Status tweet) {
        final String langOfTweet = tweet.getLang();
        return ((ISO_LANGUAGE_SPANISH.equalsIgnoreCase(langOfTweet)) || (IS_LANGUAGE_FRENCH.equalsIgnoreCase(langOfTweet))
                || (IS_LANGUAGE_ITALIAN.equalsIgnoreCase(langOfTweet)));
    }

    public boolean hasUserMoreFifteenhundredFollowers(final User user){
        return user.getFollowersCount() > FIFTEENHUNDRED;
    }

    public TweetEntity getTweetInitialized(final String userName, final String text, final String location,
                                           final boolean hasBeenValidate){
        final com.test.twitter.twitterapp.entities.TweetEntity tweetEntity =
            new com.test.twitter.twitterapp.entities.TweetEntity();
        tweetEntity.setUser(userName);
        tweetEntity.setText(text);
        tweetEntity.setLocation(location);
        tweetEntity.setHasBeenValidate(hasBeenValidate);
        return tweetEntity;
    }
}
