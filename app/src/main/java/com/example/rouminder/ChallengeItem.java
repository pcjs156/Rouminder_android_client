package com.example.rouminder;

public class ChallengeItem {
    private String challengeName;

    public ChallengeItem(String challengeName) {
        this.challengeName = challengeName;
    }

    public String getChallengeName() { return challengeName; }
    public void setChallengeName(String challengeName) {this.challengeName = challengeName;}
}