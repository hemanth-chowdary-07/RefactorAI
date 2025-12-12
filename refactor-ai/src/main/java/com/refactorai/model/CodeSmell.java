package com.refactorai.model;

public class CodeSmell {

    private String type;
    private String location;
    private String severity;
    private String description;

    public CodeSmell() {
    }

    public CodeSmell(String type, String location, String severity, String description) {
        this.type = type;
        this.location = location;
        this.severity = severity;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}