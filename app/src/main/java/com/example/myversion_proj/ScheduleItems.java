package com.example.myversion_proj;

public class ScheduleItems {

    private String startTime;
    private String endTime;
    private String subject;
    private String className;
    private String room;

    public ScheduleItems(String startTime, String endTime, String subject, String className, String room) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.subject = subject;
        this.className = className;
        this.room = room;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
