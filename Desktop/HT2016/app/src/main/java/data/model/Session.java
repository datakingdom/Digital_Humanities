package data.model;

public class Session {

    public String ID;
    public String name;
    public String beginTime;
    public String endTime;
    public String room;
    public String date;
    public String day_id;

    public void setname(String s) {
        this.name = s;
    }

    public void setbeginTime(String s) {
        this.beginTime = s;
    }

    public void setendTime(String s) {
        this.endTime = s;
    }

    public void setroom(String s) {
        this.room = s;
    }

    public void setdate(String s) {
        this.date = s;
    }

    public void setday_id(String s) {
        this.day_id = s;
    }

    public String getID() {
        return this.ID;
    }

    public void setID(String s) {
        this.ID = s;
    }

    public String getname() {
        return this.name;
    }

    public String getbeginTime() {
        return this.beginTime;
    }

    public String getendTime() {
        return this.endTime;
    }

    public String getroom() {
        return this.room;
    }

    public String getdate() {
        return this.date;
    }

    public String getday_id() {
        return this.day_id;
    }
}
