package data.model;

public class UserBreif {

    private String id;
    private String name;
    private String username;

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
}
