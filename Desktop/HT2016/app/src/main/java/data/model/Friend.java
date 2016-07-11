package data.model;

public class Friend {

    private String id;
    private String status;
    private String name;

    //mutator
    public void setid(String id) {
        this.id = id;
    }

    public void setstatus(String s) {
        this.status = s;
    }

    public void setname(String s) {
        this.name = s;
    }

    //accessor
    public String getid() {
        return this.id;
    }

    public String getstatus() {
        return this.status;
    }

    public String getname() {
        return this.name;
    }
}
