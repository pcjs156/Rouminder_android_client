package com.example.rouminder;

public class GoalItem {
    private String goalContent;
    private String goalRestTime;
    private String goalCount;
    private String goalTime;
    private boolean goalImgCheckBoxChecked;

    public GoalItem(String goalContent, String goalRestTime, String goalCount, String goalTime, boolean goalImgCheckBoxChecked) {
        this.goalContent = goalContent;
        this.goalRestTime = goalRestTime;
        this.goalCount = goalCount;
        this.goalTime = goalTime;
        this.goalImgCheckBoxChecked = goalImgCheckBoxChecked;
    }

    public String getGoalContent() {
        return goalContent;
    }

    public void setGoalContent(String goalContent) {
        this.goalContent = goalContent;
    }

    public String getGoalRestTime() {
        return goalRestTime;
    }

    public void setGoalRestTime(String goalRestTime) {
        this.goalRestTime = goalRestTime;
    }

    public String getGoalCount() {
        return goalCount;
    }

    public void setGoalCount(String goalCount) {
        this.goalCount = goalCount;
    }

    public String getGoalTime() {
        return goalTime;
    }

    public void setGoalTime(String goalTime) {
        this.goalTime = goalTime;
    }

    public boolean isGoalImgCheckBoxChecked() {
        return goalImgCheckBoxChecked;
    }

    public void setGoalImgCheckBoxChecked(boolean goalImgCheckBoxChecked) {
        this.goalImgCheckBoxChecked = goalImgCheckBoxChecked;
    }
}
