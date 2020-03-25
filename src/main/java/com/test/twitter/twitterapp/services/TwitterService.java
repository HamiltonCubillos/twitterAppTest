package com.test.twitter.twitterapp.services;

import com.test.twitter.twitterapp.entities.TweetEntity;
import com.test.twitter.twitterapp.respositories.TwitterRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.test.twitter.twitterapp.constants.Constants.*;


@Service
public class TwitterService {

    @Value("${oauth.consumerKey}")
    private String consumerKey;

    @Value("${oauth.consumerSecret}")
    private String consumerSecret;

    @Value("${oauth.acessToken}")
    private String acessToken;

    @Value("${oauth.accessTokenSecret}")
    private String accessTokenSecret;

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TwitterService.class);

    @Autowired
    private TwitterRepository twitterRepository;

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

    public boolean hasUserMoreFifteenhundredFollowers(final User user) {
        return ((null != user) && (user.getFollowersCount() > FIFTEENHUNDRED));
    }

    public List<TweetEntity> getTweetsBySubject(final String subjectOfTweets) {
        final Twitter twitter = getTwitterObject();
        final Query query = new Query(subjectOfTweets);
        long lastID = Long.MAX_VALUE;
        final int numberOfTweetsMaximum = 512;
        final List<Status> recoveredtweets = new ArrayList<>(ZERO_INT);
        int tweetSubstraction;
        int currentTweetsSize;
        QueryResult result;
        while (numberOfTweetsMaximum > (currentTweetsSize = (recoveredtweets.size()))) {
            tweetSubstraction = numberOfTweetsMaximum - currentTweetsSize;
            query.setCount(ONE_HUNDRED < tweetSubstraction ? ONE_HUNDRED : tweetSubstraction);
            try {
                result = twitter.search(query);
                recoveredtweets.addAll(result.getTweets());
                LOGGER.debug(new StringBuilder("Se han recopilado ").append(currentTweetsSize).append(" tweets").toString());
                for (Status t : recoveredtweets)
                    if (t.getId() < lastID) lastID = t.getId();
            } catch (TwitterException te) {
                LOGGER.error(new StringBuilder("TwitterException, no se puede conectar: ").append(te).toString());
            }
            query.setMaxId(lastID - 1);
        }
        return getTweetsValid(recoveredtweets);
    }

    private List<TweetEntity> getTweetsValid(final List<Status> tweets) {
        final int tweetsSize = tweets.size();
        Status status;
        String username, msg;
        String country;
        Place place;
        User user;
        final List<TweetEntity> validTweets = new ArrayList<>(ZERO_INT);
        for (int i = ZERO_INT; i < tweetsSize; i++) {
            status = (Status) tweets.get(i);
            user = status.getUser();
            username = (null == user) ? EMPTY_STRING : user.getScreenName();
            msg = status.getText();
            place = status.getPlace();
            country = (null == place) ? EMPTY_STRING : place.getCountry();
            if ((isTweetLangEsFrOrIt(status)) && (hasUserMoreFifteenhundredFollowers(user))) {
                validTweets.add(getTweetInitialized(username, msg, country));
                LOGGER.debug(new StringBuilder(i).append(" El usuario: ").append(username).append(" escribió desde ")
                        .append(country).append(" el siguiente mensaje ").append(msg).toString());
            }
        }
        return validTweets;
    }

    public TweetEntity getTweetInitialized(final String userName, final String text, final String location) {
        final com.test.twitter.twitterapp.entities.TweetEntity tweetEntity =
                new com.test.twitter.twitterapp.entities.TweetEntity();
        tweetEntity.setUserName(userName);
        tweetEntity.setText(text);
        tweetEntity.setLocation(location);
        return twitterRepository.save(tweetEntity);
    }

    private Twitter getTwitterObject() {
        final ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(acessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);
        return new TwitterFactory(cb.build()).getInstance();
    }


    public void showTweetsInLogger(final ArrayList<Status> tweets) {
        for (int i = ZERO_INT; i < tweets.size(); i++) {
            final Status t = (Status) tweets.get(i);
            final String user = t.getUser().getScreenName();
            final String msg = t.getText();
            LOGGER.debug(new StringBuilder(i).append(" El usuario: ").append(user).append(" escribió ")
                    .append(msg).toString());
        }
    }
}
