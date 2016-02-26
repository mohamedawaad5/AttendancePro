package com.codemorning.attendancepro;

/**
 * Created by alina on 2/23/2016.
 */
public class Student {

    private String studentId;
    private String studentTitle;
    private String classTitle;

    Student(String studentId, String studentTitle, String classTitle) {
        this.studentId = studentId;
        this.studentTitle = studentTitle;
        this.classTitle = classTitle;

    }

    public String getId() {
        return studentId;
    }
    public void setId(String id) {
        this.studentId = id;
    }
    public String getTitle() {
        return studentTitle;
    }
    public void setTitle(String title) {
        this.studentTitle = title;
    }



    @Override
    public String toString() {
        return this.getTitle();
    }

}
