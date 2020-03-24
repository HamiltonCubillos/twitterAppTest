package com.test.twitter.twitterapp.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TweetEntity {
    @Id
    @GeneratedValue
    private long id;
    private String user;
    private String text;
    private String location;
    private Boolean hasBeenValidate;
}
