package com.viintro.Viintro.Model;

/**
 * Created by rkanawade on 13/04/17.
 */

public class InvitationsSent {


    private String name;
    private String designation_course;
    private String company_college;
    private String image;
    private int mutual_connections;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation_course() {
        return designation_course;
    }

    public void setDesignation_course(String designation_course) {
        this.designation_course = designation_course;
    }

    public String getCompany_college() {
        return company_college;
    }

    public void setCompany_college(String company_college) {
        this.company_college = company_college;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getMutual_connections() {
        return mutual_connections;
    }

    public void setMutual_connections(int mutual_connections) {
        this.mutual_connections = mutual_connections;
    }
}

