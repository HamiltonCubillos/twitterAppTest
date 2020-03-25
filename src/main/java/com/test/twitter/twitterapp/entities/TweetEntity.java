package com.test.twitter.twitterapp.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = "T_TWEETS")
@Entity
@Data
@NoArgsConstructor
public class TweetEntity {
    @Id
    @GeneratedValue
    private long id;
    @Column(name="user_name",nullable = false)
    private String userName;
    @Lob
    @Column(name="text",length = 100000)
    private String text;
    @Column(name="location")
    private String location;
    @Column(name="has_been_validate")
    private Boolean hasBeenValidate = false;
}
