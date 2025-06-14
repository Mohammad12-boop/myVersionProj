package com.example.myversion_proj;

public class ReplyCard {
    int studentId;
String studentName;
String submittedTime;
String filePath;

    public ReplyCard(int studentId, String studentName, String submittedTime, String filePath) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.submittedTime = submittedTime;
        this.filePath = filePath;
    }

    public ReplyCard(String studentName, String submittedTime, String filePath) {
        this.studentName = studentName;
        this.submittedTime = submittedTime;
        this.filePath = filePath;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSubmittedTime() {
        return submittedTime;
    }

    public void setSubmittedTime(String submittedTime) {
        this.submittedTime = submittedTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
