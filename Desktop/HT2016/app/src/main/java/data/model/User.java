package data.model;

public class User {

    private String id;
    private String name;
    private String username;
    private String userroleID;
    private String email;
    private String bio;
    private String personalWebSite;
    private String position;
    private String departmentAffiliation;
    private String organizationAffiliation;
    private String city;
    private String country;
    private String linkedin;


    //mutator
    public void setid(String id) {
        this.id = id;
    }

    public void setname(String name) {
        this.name = name;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public void setemail(String email) {
        this.email = email;
    }

    public void setbio(String bio) {
        this.bio = bio;
    }

    public void setuserroleID(String userroleID) {
        this.userroleID = userroleID;
    }

    public void setpersonalwebsite(String s) {
        this.personalWebSite = s;
    }

    public void setposition(String s) {
        this.position = s;
    }

    public void setdepartment(String s) {
        this.departmentAffiliation = s;
    }

    public void setorganization(String s) {
        this.organizationAffiliation = s;
    }

    public void setcity(String s) {
        this.city = s;
    }

    public void setcountry(String s) {
        this.country = s;
    }

    public void setlinkedin(String s) {
        this.linkedin = s;
    }

    //accessor
    public String getid() {
        return this.id;
    }

    public String getname() {
        return this.name;
    }

    public String getusername() {
        return this.username;
    }

    public String getemail() {
        return this.email;
    }

    public String getbio() {
        return this.bio;
    }

    public String getuserroleID() {
        return this.userroleID;
    }

    public String getpersonalwebsite() {
        return this.personalWebSite;
    }

    public String getposition() {
        return this.position;
    }

    public String getdepartment() {
        return this.departmentAffiliation;
    }

    public String getorganization() {
        return this.organizationAffiliation;
    }

    public String getcity() {
        return this.city;
    }

    public String getcountry() {
        return this.country;
    }

    public String getlinkedin() {
        return this.linkedin;
    }
}
