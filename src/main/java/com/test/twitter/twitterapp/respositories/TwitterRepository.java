package com.test.twitter.twitterapp.respositories;

import com.test.twitter.twitterapp.entities.TweetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TwitterRepository extends JpaRepository<TweetEntity, Long> {
}
