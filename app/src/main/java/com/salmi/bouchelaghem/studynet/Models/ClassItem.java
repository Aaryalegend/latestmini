package com.salmi.bouchelaghem.studynet.Models;

public class ClassItem {
    private String group;
    private String startHour;
    private String endHour;
    private String subject;
    private String type;
    public ClassItem() {
        // Default constructor required for Firestore
    }
    public ClassItem(String group, String startHour, String endHour, String subject, String type) {
        this.group = group;
        this.startHour = startHour;
        this.endHour = endHour;
        this.subject = subject;
        this.type = type;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public String getEndHour() {
        return endHour;
    }

    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
