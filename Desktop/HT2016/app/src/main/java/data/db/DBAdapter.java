package data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import data.model.Author;
import data.model.Conference;
import data.model.Keynote;
import data.model.Paper;
import data.model.PaperContent;
import data.model.Poster;
import data.model.Session;
import data.model.Workshop;

public class DBAdapter {

    private final static String CreateAuthor = "create table author " +
            "(ID text primary key, " +
            "name text not null" +
            ")";
    private final static String CreateAuthorToPaper = "create table authorToPaper " +
            "(authorID text not null, " +
            "contentID text not null " +
            ")";
    private final static String CreateConference = "create table conference " +
            "(ID text primary key, "
            + "title text not null, " +
            "startDate text not null, " +
            "endDate text not null, " +
            "location text not null, " +
            "timestamp text not null, " +
            "description text not null" +
            ")";
    private final static String CreateSession = "create table session (" +
            "ID text primary key not null, "
            + "name text, " +
            "beginTime text, " +
            "endTime text, " +
            "date text," +
            "day_id text," +
            "room text)";
    private final static String CreatePoster = "create table poster (" +
            "ID text primary key not null, "
            + "name text, " +
            "beginTime text, " +
            "endTime text, " +
            "date text, " +
            "day_id text, " +
            "room text, " +
            "eventSessionID text)";
    private final static String CreatePaper = "create table paper (" +
            "ID text, " +
            "presentationID text primary key not null, " +
            "sessionID text, " +
            "date text," +
            "starred text, " +
            "scheduled text, " +
            "exactbeginTime text, " +
            "exactendTime text, " +
            "type text," +
            "dayid text," +
            "paperAbstract text," +
            "title text," +
            "authors text," +
            "recommended text)";
    private final static String CreatePaperContent = "create table papercontent (" +
            "ID text primary key not null, "
            + "title text, " +
            "type text, " +
            "paperAbstract text ," +
            "contentlink text ," +
            "authors text)";
    private final static String CreateMyScheduled = "create table myscheduledpaper " +
            "(paperID text)";
    private final static String CreateMyStarred = "create table mystarredpaper " +
            "(presentationID text)";
    private final static String CreateMyRecommended = "create table myrecommendedpaper " +
            "(paperID text)";
    private final static String CreateKeynote = "create table keynote " +
            "(ID text primary key, " +
            "title text not null, " +
            "beginTime text not null, " +
            "endTime text not null, " +
            "date text not null," +
            "speakerName text not null," +
            "speakerAffiliation text not null, " +
            "description text not null, " +
            "dayid text not null, " +
            "room text)";
    private final static String CreateUser = "create table user" +
            "(ID text primary key)";
    private final static String CreateWorkshopDes = "create table workshopDes (" +
            "ID text primary key, "
            + "name text, " +
            "beginTime text, " +
            "endTime text, " +
            "date text," +
            "dayid text," +
            "content text," +
            "eventSessionID text," +
            "room text)";
    private final Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    public DBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public DBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    /*Author*/
    public long insertAuthor(String id, String name) {
        ContentValues values = new ContentValues();
        values.put("ID", id);
        values.put("name", name);
        return mDb.insert("author", null, values);
    }

    public ArrayList<Author> getAllAuthor() {
        Cursor cursor = mDb.query("author", new String[]{"ID", "name"}, null, null, null, null, "name");

        ArrayList<Author> authorList = new ArrayList<Author>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Author author = new Author();
            author.id = cursor.getString(0);
            author.name = cursor.getString(1);
            authorList.add(author);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        System.out.println("???????????????" + authorList.size());
        return authorList;
    }

    public int deleteAllAuthor() {
        return mDb.delete("author", null, null);
    }


    /*Author to paper*/
    public long insertAuthorToPaper(String authorID, String contentID) {
        ContentValues values = new ContentValues();
        values.put("authorID", authorID);
        values.put("contentID", contentID);
        return mDb.insert("authorToPaper", null, values);
    }

//    public ArrayList<String> getAllContentIDByAuthorID(String authorID) {
//        Cursor cursor = mDb.rawQuery("select a.contentID" + " " +
//                "from authorToPaper a" + " " +
//                "where a.contentID = '" + authorID + "'", null);
//
//        ArrayList<String> contentIDList = new ArrayList<String>();
//
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            String contentID = cursor.getString(0);
//            contentIDList.add(contentID);
//            cursor.moveToNext();
//        }
//        if (cursor != null && !cursor.isClosed()) {
//            cursor.close();
//        }
//        return contentIDList;
//    }

    public int deleteAuthorToPaper() {
        return mDb.delete("authorToPaper", null, null);
    }


    /*Conference*/
    public long insertConference(String id, String title, String startDate,
                                 String endDate, String location, String description, String timstamp) {
        ContentValues values = new ContentValues();
        values.put("ID", id);
        values.put("title", title);
        values.put("startDate", startDate);
        values.put("endDate", endDate);
        values.put("location", location);
        values.put("description", description);
        values.put("timestamp", timstamp);
        return mDb.insert("conference", null, values);
    }

    public void getConferenceInfo() {
        Cursor cursor = mDb.query("conference", new String[]{"ID", "title",
                        "startDate", "endDate", "location", "description", "timestamp"}, null,
                null, null, null, null);

        if (cursor.moveToFirst()) {
            Conference.id = cursor.getString(0);
            Conference.title = cursor.getString(1);
            Conference.startDate = cursor.getString(2);
            Conference.endDate = cursor.getString(3);
            Conference.location = cursor.getString(4);
            Conference.description = cursor.getString(5);
            Conference.timstamp = cursor.getString(6);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    public int deleteConference() {
        return mDb.delete("conference", null, null);
    }

    /*Keynote*/
    public long insertKeynote(Keynote k) {
        ContentValues values = new ContentValues();
        values.put("ID", k.ID);
        values.put("title", k.title);
        values.put("beginTime", k.beginTime);
        values.put("endTime", k.endTime);
        values.put("date", k.date);
        values.put("speakerName", k.speakerName);
        values.put("speakerAffiliation", k.speakerAffiliation);
        values.put("description", k.description);
        values.put("room", k.room);
        values.put("dayid", k.dayid);
        return mDb.insert("keynote", null, values);
    }

    public ArrayList<Keynote> getKeynoteByDay() {
        ArrayList<Keynote> kList = new ArrayList<Keynote>();
        Cursor cursor = mDb.query("keynote", new String[]{"ID", "title",
                        "beginTime", "endTime", "date", "speakerName", "speakerAffiliation",
                        "description", "room"}, null,
                null, null, null, "dayid");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Keynote k = new Keynote();
            k.ID = cursor.getString(0);
            k.title = cursor.getString(1);
            k.beginTime = cursor.getString(2);
            k.endTime = cursor.getString(3);
            k.date = cursor.getString(4);
            k.speakerName = cursor.getString(5);
            k.speakerAffiliation = cursor.getString(6);
            k.description = cursor.getString(7);
            k.room = cursor.getString(8);
            kList.add(k);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return kList;
    }

    public int deleteKeynote() {
        return mDb.delete("keynote", null, null);
    }

    /*Workshops*/
    public long insertWorkshopDes(Workshop w) {
        ContentValues values = new ContentValues();
        values.put("ID", w.ID);
        values.put("name", w.name);
        values.put("beginTime", w.beginTime);
        values.put("endTime", w.endTime);
        values.put("date", w.date);
        values.put("room", w.room);
        values.put("dayid", w.day_id);
        values.put("content", w.content);
        values.put("eventSessionID", w.eventSessionID);
        return mDb.insert("workshopDes", null, values);
    }

    public ArrayList<Workshop> getWorkshopsDes() {
        ArrayList<Workshop> tList = new ArrayList<Workshop>();
        Cursor cursor = mDb.query("workshopDes", new String[]{"ID", "name",
                "beginTime", "endTime", "date", "room", "content", "eventSessionID"}, null, null, null, null, "dayid");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Workshop t = new Workshop();
            t.ID = cursor.getString(0);
            t.name = cursor.getString(1);
            t.beginTime = cursor.getString(2);
            t.endTime = cursor.getString(3);
            t.date = cursor.getString(4);
            t.room = cursor.getString(5);
            t.content = cursor.getString(6);
            t.eventSessionID = cursor.getString(7);
            tList.add(t);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return tList;
    }

    public int deleteWorkshopDes() {
        return mDb.delete("workshopDes", null, null);
    }

    public ArrayList<Workshop> getWorkshopBySessionID(String sessionID) {
        ArrayList<Workshop> wList = new ArrayList<Workshop>();
        Cursor cursor = mDb.query("workshopDes", new String[]{"ID", "name",
                        "beginTime", "endTime", "date", "room", "content", "eventSessionID"}, "eventSessionID='" + sessionID + "'",
                null, null, null, "beginTime, endTime");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Workshop w = new Workshop();
            w.ID = cursor.getString(0);
            w.name = cursor.getString(1);
            w.beginTime = cursor.getString(2);
            w.endTime = cursor.getString(3);
            w.date = cursor.getString(4);
            w.room = cursor.getString(5);
            w.content = cursor.getString(6);
            w.eventSessionID = cursor.getString(7);

            wList.add(w);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return wList;
    }

    //Session
    public long insertSession(Session se) {
        ContentValues values = new ContentValues();
        values.put("ID", se.ID);
        values.put("name", se.name);
        values.put("beginTime", se.beginTime);
        values.put("endTime", se.endTime);
        values.put("date", se.date);
        values.put("room", se.room);
        values.put("day_id", se.day_id);
        //values.put("room", se.room);
        return mDb.insert("session", null, values);
    }

    public ArrayList<Session> getSessionByidList(String[] s) {
        ArrayList<Session> sList = new ArrayList<Session>();

        for (int i = 0; i < s.length; i++) {
            sList.add(getSessionByID(s[i]));
        }

        return sList;
    }

    public Session getSessionByID(String ID) {
        Session se = new Session();
        Cursor cursor = mDb.query("session", new String[]{"ID", "name",
                        "beginTime", "endTime", "room", "date", "day_id"}, "ID='" + ID + "'",
                null, null, null, "beginTime, endTime");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            se.ID = cursor.getString(0);
            se.name = cursor.getString(1);
            se.beginTime = cursor.getString(2);
            se.endTime = cursor.getString(3);
            se.room = cursor.getString(4);
            se.date = cursor.getString(5);
            se.day_id = cursor.getString(6);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return se;
    }

    public ArrayList<Session> getSessionBydayID(String day_id) {
        ArrayList<Session> sessions = new ArrayList<Session>();
        Cursor cursor = mDb.query("session", new String[]{"ID", "name",
                        "beginTime", "endTime", "room", "date"}, "day_id='" + day_id + "'",
                null, null, null, "beginTime, endTime");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Session se = new Session();
            se.ID = cursor.getString(0);
            se.name = cursor.getString(1);
            se.beginTime = cursor.getString(2);
            se.endTime = cursor.getString(3);
            se.room = cursor.getString(4);
            se.date = cursor.getString(5);
            se.day_id = day_id;
            sessions.add(se);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return sessions;
    }

    public ArrayList<Session> getSessionByScheduledPapers(String date) {
        ArrayList<Session> sessions = new ArrayList<Session>();

        Cursor cursor = mDb.rawQuery("select s.ID, s.name,s.date,s.beginTime,s.endTime,s.room" + " " +
                "from paper p, myscheduledpaper mp, session s" + " " +
                "where p.ID=mp.paperID and p.sessionID = s.ID and s.day_id=" + date + " " +
                "group by s.ID " +
                "order by beginTime,endTime", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Session s = new Session();
            s.ID = cursor.getString(0);
            s.name = cursor.getString(1);
            s.date = cursor.getString(2);
            s.beginTime = cursor.getString(3);
            s.endTime = cursor.getString(4);
            s.room = cursor.getString(5);
            sessions.add(s);
            cursor.moveToNext();
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return sessions;
    }

    public ArrayList<Paper> getPapersScheduledBySessionID(String sID) {

        ArrayList<Paper> papers = new ArrayList<Paper>();

        Cursor cursor = mDb.rawQuery("select p.ID, pc.title,p.date,pc.paperAbstract,pc.contentlink," +
                "pc.authors,p.starred,p.scheduled, " +
                "p.exactbeginTime, p.exactendTime,pc.type,p.dayid,p.recommended,p.presentationID, s.room" + " " +
                "from paper p, papercontent pc, myscheduledpaper mp, session s" + " " +
                "where p.sessionID=" + sID + " and p.ID=mp.paperID and p.ID = pc.ID and p.sessionID = s.ID" + " " +
                "order by p.dayid, p.exactbeginTime", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Paper p = new Paper();
            p.id = cursor.getString(0);
            p.title = cursor.getString(1);
            p.date = cursor.getString(2);
            p.paperAbstract = cursor.getString(3);
            p.contentlink = cursor.getString(4);
            p.authors = cursor.getString(5);
            p.starred = cursor.getString(6);
            p.scheduled = cursor.getString(7);
            p.exactbeginTime = cursor.getString(8);
            p.exactendTime = cursor.getString(9);
            p.type = cursor.getString(10);
            p.day_id = cursor.getString(11);
            p.recommended = cursor.getString(12);
            p.presentationID = cursor.getString(13);
            p.room = cursor.getString(14);
            papers.add(p);
            cursor.moveToNext();
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return papers;
    }

    public int deleteSession() {
        return mDb.delete("session", null, null);
    }

    //Poster
    public long insertPoster(Poster p) {
        ContentValues values = new ContentValues();
        values.put("ID", p.ID);
        values.put("name", p.name);
        values.put("beginTime", p.beginTime);
        values.put("endTime", p.endTime);
        values.put("date", p.date);
        values.put("room", p.room);
        values.put("day_id", p.day_id);
        values.put("eventSessionID", p.eventSessionID);
        return mDb.insert("poster", null, values);
    }

    public ArrayList<Poster> getPoster() {
        ArrayList<Poster> poList = new ArrayList<Poster>();
        Cursor cursor = mDb.query("poster", new String[]{"ID", "name",
                "beginTime", "endTime", "date", "room", "day_id, eventSessionID"}, null, null, null, null, "beginTime");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Poster t = new Poster();
            t.ID = cursor.getString(0);
            t.name = cursor.getString(1);
            t.beginTime = cursor.getString(2);
            t.endTime = cursor.getString(3);
            t.date = cursor.getString(4);
            t.room = cursor.getString(5);
            t.day_id = cursor.getString(6);
            t.eventSessionID = cursor.getString(7);
            poList.add(t);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return poList;
    }

    public int deletePoster() {
        return mDb.delete("poster", null, null);
    }


    //Schedule Papers
    public long insertMyScheduledPaper(String ID) {
        ContentValues values = new ContentValues();
        values.put("paperID", ID);
        return mDb.insert("myscheduledpaper", null, values);
    }

    public void deleteMyScheduledPaper(String ID) {
        mDb.delete("myscheduledpaper", "paperID= '" + ID + "'", null);
    }

    public ArrayList<String> getMyStarredPaper() {
        ArrayList<String> tList = new ArrayList<String>();
        Cursor cursor = mDb.query("mystarredpaper", new String[]{"presentationID"}, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String t = new String();
            t = cursor.getString(0);
            tList.add(t);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return tList;
    }

    public long insertMyStarredPaper(String ID) {
        ContentValues values = new ContentValues();
        values.put("presentationID", ID);
        return mDb.insert("mystarredpaper", null, values);
    }

    public void deleteMyStarredPaper(String ID) {
        mDb.delete("mystarredpaper", "presentationID= '" + ID + "'", null);
    }

    //Recommended Papers
    public long insertMyRecommendedPaper(String ID) {
        ContentValues values = new ContentValues();
        values.put("paperID", ID);
        return mDb.insert("myrecommendedpaper", null, values);
    }

    public void deleteMyRecommendedPaper(String ID) {
        mDb.delete("myrecommendedpaper", "paperID= '" + ID + "'", null);
    }


    //Papers
    public long insertPaper(Paper p) {
        ContentValues values = new ContentValues();
        values.put("ID", p.id);
        values.put("presentationID", p.presentationID);
        values.put("sessionID", p.belongToSessionID);
        values.put("date", p.date);
        values.put("starred", p.starred);
        values.put("scheduled", p.scheduled);
        values.put("exactbeginTime", p.exactbeginTime);
        values.put("exactendTime", p.exactendTime);
        values.put("type", p.type);
        values.put("dayid", p.day_id);
        values.put("recommended", p.recommended);
        values.put("paperAbstract", p.paperAbstract);
        values.put("title", p.title);
        values.put("authors", p.authors);
        return mDb.insert("paper", null, values);
    }

    public long insertPaperContent(PaperContent pc) {
        ContentValues values = new ContentValues();
        values.put("ID", pc.id);
        values.put("title", pc.title);
        values.put("paperAbstract", pc.paperAbstract);
        values.put("contentlink", pc.contentlink);
        values.put("authors", pc.authors);
        values.put("type", pc.type);
        return mDb.insert("papercontent", null, values);

    }

    public int deletePaper() {
        return mDb.delete("paper", null, null);
    }

    public int deletePaperContent() {
        return mDb.delete("papercontent", null, null);
    }

    public ArrayList<Paper> getPapersByauthorName() {
        ArrayList<Paper> papers = new ArrayList<Paper>();
        Cursor cursor = mDb.rawQuery("select p.ID, c.title," +
                "p.date,c.paperAbstract,c.contentlink,c.authors,p.starred,p.scheduled, " +
                "p.exactbeginTime, p.exactendTime,c.type, p.dayid, p.recommended, s.room, p.presentationID" + " "
                + "from paper p, papercontent c, session s " + " " +
                "where p.sessionID = s.ID and p.ID = c.ID and c.authors<>'No author information available' and c.type<>'no-paper' order by c.authors", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Paper p = new Paper();
            p.id = cursor.getString(0);
            p.title = cursor.getString(1);
            p.date = cursor.getString(2);
            p.paperAbstract = cursor.getString(3);
            p.contentlink = cursor.getString(4);
            p.authors = cursor.getString(5);
            p.starred = cursor.getString(6);
            p.scheduled = cursor.getString(7);
            p.exactbeginTime = cursor.getString(8);
            p.exactendTime = cursor.getString(9);
            p.type = cursor.getString(10);
            p.day_id = cursor.getString(11);
            p.recommended = cursor.getString(12);
            p.room = cursor.getString(13);
            p.presentationID = cursor.getString(14);
            papers.add(p);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return papers;
    }

    public ArrayList<Paper> getPapersByAuthorID(String authorID) {
        ArrayList<Paper> papers = new ArrayList<Paper>();
        Cursor cursor = mDb.rawQuery("select p.ID, c.title," +
                "p.date,c.paperAbstract,c.contentlink,c.authors,p.starred,p.scheduled, " +
                "p.exactbeginTime, p.exactendTime,c.type, p.dayid, p.recommended, s.room, p.presentationID" + " "
                + "from paper p, papercontent c, session s, authorToPaper a " + " " +
                "where p.sessionID = s.ID and p.ID = c.ID and c.ID = a.contentID and a.authorID = '" + authorID + "' order by p.dayid", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Paper p = new Paper();
            p.id = cursor.getString(0);
            p.title = cursor.getString(1);
            p.date = cursor.getString(2);
            p.paperAbstract = cursor.getString(3);
            p.contentlink = cursor.getString(4);
            p.authors = cursor.getString(5);
            p.starred = cursor.getString(6);
            p.scheduled = cursor.getString(7);
            p.exactbeginTime = cursor.getString(8);
            p.exactendTime = cursor.getString(9);
            p.type = cursor.getString(10);
            p.day_id = cursor.getString(11);
            p.recommended = cursor.getString(12);
            p.room = cursor.getString(13);
            p.presentationID = cursor.getString(14);
            papers.add(p);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return papers;
    }

    public ArrayList<Paper> getPapersBypresentationName() {
        ArrayList<Paper> papers = new ArrayList<Paper>();
        Cursor cursor = mDb.rawQuery("select p.ID, c.title," +
                "p.date,c.paperAbstract,c.contentlink,c.authors,p.starred,p.scheduled, " +
                "p.exactbeginTime, p.exactendTime,c.type, p.dayid, p.recommended, s.room, p.presentationID " + " "
                + "from paper p, papercontent c, session s " +
                "where p.sessionID = s.ID and p.ID = c.ID and c.type<>'no-paper' order by c.title", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Paper p = new Paper();
            p.id = cursor.getString(0);
            p.title = cursor.getString(1);
            p.date = cursor.getString(2);
            p.paperAbstract = cursor.getString(3);
            p.contentlink = cursor.getString(4);
            p.authors = cursor.getString(5);
            p.starred = cursor.getString(6);
            p.scheduled = cursor.getString(7);
            p.exactbeginTime = cursor.getString(8);
            p.exactendTime = cursor.getString(9);
            p.type = cursor.getString(10);
            p.day_id = cursor.getString(11);
            p.recommended = cursor.getString(12);
            p.room = cursor.getString(13);
            p.presentationID = cursor.getString(14);
            papers.add(p);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return papers;
    }

    public ArrayList<Paper> getPapersBypresentationType() {
        ArrayList<Paper> papers = new ArrayList<Paper>();
        Cursor cursor = mDb.rawQuery("select p.ID, c.title," +
                "p.date,c.paperAbstract,c.contentlink,c.authors,p.starred,p.scheduled, " +
                "p.exactbeginTime, p.exactendTime, c.type, p.dayid, p.recommended, s.room, p.presentationID " + " "
                + "from paper p, papercontent c, session s where p.sessionID = s.ID and p.ID = c.ID and c.type<>'no-paper' order by c.type", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Paper p = new Paper();
            p.id = cursor.getString(0);
            p.title = cursor.getString(1);
            p.date = cursor.getString(2);
            p.paperAbstract = cursor.getString(3);
            p.contentlink = cursor.getString(4);
            p.authors = cursor.getString(5);
            p.starred = cursor.getString(6);
            p.scheduled = cursor.getString(7);
            p.exactbeginTime = cursor.getString(8);
            p.exactendTime = cursor.getString(9);
            p.type = cursor.getString(10);
            p.day_id = cursor.getString(11);
            p.recommended = cursor.getString(12);
            p.room = cursor.getString(13);
            p.presentationID = cursor.getString(14);
            papers.add(p);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return papers;
    }

    public ArrayList<Paper> getPapersBypresentationType(String type) {
        ArrayList<Paper> papers = new ArrayList<Paper>();
        Cursor cursor = mDb.rawQuery("select p.ID, c.title," +
                "p.date,c.paperAbstract,c.contentlink,c.authors,p.starred,p.scheduled, " +
                "p.exactbeginTime, p.exactendTime, c.type, p.dayid, p.recommended, s.room, p.presentationID " + " "
                + "from paper p, papercontent c, session s where p.sessionID = s.ID and p.ID = c.ID and c.type = " + "'" + type + "'" + " order by p.date", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Paper p = new Paper();
            p.id = cursor.getString(0);
            p.title = cursor.getString(1);
            p.date = cursor.getString(2);
            p.paperAbstract = cursor.getString(3);
            p.contentlink = cursor.getString(4);
            p.authors = cursor.getString(5);
            p.starred = cursor.getString(6);
            p.scheduled = cursor.getString(7);
            p.exactbeginTime = cursor.getString(8);
            p.exactendTime = cursor.getString(9);
            p.type = cursor.getString(10);
            p.day_id = cursor.getString(11);
            p.recommended = cursor.getString(12);
            p.room = cursor.getString(13);
            p.presentationID = cursor.getString(14);
            papers.add(p);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return papers;
    }

    public ArrayList<Paper> getPapersBysessionID(String sessionID) {
        ArrayList<Paper> papers = new ArrayList<Paper>();
        Cursor cursor = mDb.rawQuery("select p.ID, pc.title," +
                "p.date,pc.paperAbstract,pc.contentlink,pc.authors,p.starred,p.scheduled, " +
                "p.exactbeginTime, p.exactendTime, pc.type, p.dayid, p.recommended, s.room, p.presentationID " + " "
                + "from paper p, papercontent pc, session s where p.sessionID=" + sessionID + " and p.sessionID = s.ID and p.ID = pc.ID order by p.dayid, p.exactbeginTime", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Paper p = new Paper();
            p.id = cursor.getString(0);
            p.title = cursor.getString(1);
            p.date = cursor.getString(2);
            p.paperAbstract = cursor.getString(3);
            p.contentlink = cursor.getString(4);
            p.authors = cursor.getString(5);
            p.starred = cursor.getString(6);
            p.scheduled = cursor.getString(7);
            p.exactbeginTime = cursor.getString(8);
            p.exactendTime = cursor.getString(9);
            p.type = cursor.getString(10);
            System.out.println("+++++++++++++++++++++" + p.type);
            p.day_id = cursor.getString(11);
            p.recommended = cursor.getString(12);
            p.room = cursor.getString(13);
            p.presentationID = cursor.getString(14);
            papers.add(p);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return papers;
    }

    public ArrayList<Paper> getPapersByStared() {

        ArrayList<Paper> papers = new ArrayList<Paper>();

        Cursor cursor = mDb.rawQuery("select p.ID, pc.title," +
                "p.date,pc.paperAbstract,pc.contentlink,pc.authors,p.starred,p.scheduled, " +
                "p.exactbeginTime, p.exactendTime, pc.type, p.dayid, p.recommended, p.presentationID, s.room" + " "
                + "from session s, paper p, papercontent pc, mystarredpaper mp where s.ID=p.sessionID and p.presentationID=mp.presentationID and p.ID = pc.ID order by p.dayid, p.exactbeginTime", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Paper p = new Paper();
            p.id = cursor.getString(0);
            p.title = cursor.getString(1);
            p.date = cursor.getString(2);
            p.paperAbstract = cursor.getString(3);
            p.contentlink = cursor.getString(4);
            p.authors = cursor.getString(5);
            p.starred = cursor.getString(6);
            p.scheduled = cursor.getString(7);
            p.exactbeginTime = cursor.getString(8);
            p.exactendTime = cursor.getString(9);
            p.type = cursor.getString(10);
            p.day_id = cursor.getString(11);
            p.recommended = cursor.getString(12);
            p.presentationID = cursor.getString(13);
            p.room = cursor.getString(14);
            papers.add(p);
            cursor.moveToNext();
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return papers;
    }

    public ArrayList<Paper> getPapersByScheduled() {

        ArrayList<Paper> papers = new ArrayList<Paper>();

        Cursor cursor = mDb.rawQuery("select p.ID, pc.title,p.date,pc.paperAbstract,pc.contentlink," +
                "pc.authors,p.starred,p.scheduled, " +
                "p.exactbeginTime, p.exactendTime,pc.type,p.dayid,p.recommended, p.presentationID" + " " +
                "from paper p, papercontent pc, myscheduledpaper mp where p.ID=mp.paperID and p.ID=pc.ID order by p.dayid, p.exactbeginTime", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Paper p = new Paper();
            p.id = cursor.getString(0);
            p.title = cursor.getString(1);
            p.date = cursor.getString(2);
            p.paperAbstract = cursor.getString(3);
            p.contentlink = cursor.getString(4);
            p.authors = cursor.getString(5);
            p.starred = cursor.getString(6);
            p.scheduled = cursor.getString(7);
            p.exactbeginTime = cursor.getString(8);
            p.exactendTime = cursor.getString(9);
            p.type = cursor.getString(10);
            p.day_id = cursor.getString(11);
            p.recommended = cursor.getString(12);
            p.presentationID = cursor.getString(13);
            papers.add(p);
            cursor.moveToNext();
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return papers;
    }

    public ArrayList<Paper> getPapersByRecommended() {

        ArrayList<Paper> papers = new ArrayList<Paper>();

        Cursor cursor = mDb.rawQuery("select p.ID, pc.title,p.date,pc.paperAbstract,pc.contentlink," +
                "pc.authors,p.starred,p.scheduled, " +
                "p.exactbeginTime, p.exactendTime,pc.type,p.dayid,p.recommended,p.presentationID" + " " +
                "from paper p, papercontent pc, myrecommendedpaper mp where p.ID=mp.paperID and p.ID = pc.ID and pc.type <>'no-paper' order by p.dayid, p.exactbeginTime", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Paper p = new Paper();
            p.id = cursor.getString(0);
            p.title = cursor.getString(1);
            p.date = cursor.getString(2);
            p.paperAbstract = cursor.getString(3);
            p.contentlink = cursor.getString(4);
            p.authors = cursor.getString(5);
            p.starred = cursor.getString(6);
            p.scheduled = cursor.getString(7);
            p.exactbeginTime = cursor.getString(8);
            p.exactendTime = cursor.getString(9);
            p.type = cursor.getString(10);
            p.day_id = cursor.getString(11);
            p.recommended = cursor.getString(12);
            p.presentationID = cursor.getString(13);
            papers.add(p);
            cursor.moveToNext();
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return papers;
    }

    public Paper getPaperByID(String ID) {
        Paper p = new Paper();
        Cursor cursor = mDb.rawQuery("select p.ID, pc.title,p.date,pc.paperAbstract,pc.contentlink," +
                "pc.authors,p.starred,p.scheduled, " +
                "p.exactbeginTime, p.exactendTime,pc.type,p.dayid,p.recommended,p.presentationID" + " " +
                "from paper p, papercontent pc where p.ID=" + ID + " and p.ID = pc.ID order by p.dayid, p.exactbeginTime", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            p.id = cursor.getString(0);
            p.title = cursor.getString(1);
            p.date = cursor.getString(2);
            p.paperAbstract = cursor.getString(3);
            p.contentlink = cursor.getString(4);
            p.authors = cursor.getString(5);
            p.starred = cursor.getString(6);
            p.scheduled = cursor.getString(7);
            p.exactbeginTime = cursor.getString(8);
            p.exactendTime = cursor.getString(9);
            p.type = cursor.getString(10);
            p.day_id = cursor.getString(11);
            p.recommended = cursor.getString(12);
            p.presentationID = cursor.getString(13);
            cursor.moveToNext();
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return p;
    }

    public ArrayList<Paper> getPapersByPPlist(ArrayList<String> pList) {

        ArrayList<Paper> papers = new ArrayList<Paper>();

        for (int i = 0; i < pList.size(); i++) {
            papers.add(getPaperByID(pList.get(i).toString()));
        }
        return papers;
    }

    public int updatePaperByStar(String presentationID, String stared) {
        ContentValues vals = new ContentValues();
        vals.put("starred", stared);
        return mDb.update("paper", vals, "presentationID= '" + presentationID + "'", null);

    }

    public int updatePaperBySchedule(String paperID, String scheduled) {
        ContentValues vals = new ContentValues();
        vals.put("scheduled", scheduled);
        return mDb.update("paper", vals, "ID=" + paperID, null);
    }

    public int updatePaperByRecommend(String paperID, String recommend) {
        ContentValues vals = new ContentValues();
        vals.put("recommended", recommend);
        return mDb.update("paper", vals, "ID=" + paperID, null);
    }

    public int updatePaperScheduleToDefault() {
        ContentValues vals = new ContentValues();
        vals.put("scheduled", "no");
        return mDb.update("paper", vals, "scheduled='yes'", null);
    }

    public int deleteUserScheduled() {
        return mDb.delete("myscheduledpaper", null, null);
    }

    public int updatePaperRecommendToDefault() {
        ContentValues vals = new ContentValues();
        vals.put("recommended", "no");
        return mDb.update("paper", vals, "recommended='yes'", null);
    }

    public int deleteUserRecommended() {
        return mDb.delete("myrecommendedpaper", null, null);
    }

    public String getPaperScheduledStatus(String id) {
        String status = "";
        Cursor cursor = mDb.query("paper", new String[]{"scheduled"}, "ID='" + id + "'",
                null, null, null, null);

        if (cursor.moveToFirst()) {
            status = cursor.getString(0);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return status;
    }

    public String getPaperStarredStatus(String id) {
        String status = "";
        Cursor cursor = mDb.query("paper", new String[]{"starred"}, "presentationID='" + id + "'",
                null, null, null, null);

        if (cursor.moveToFirst()) {
            status = cursor.getString(0);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return status;
    }

    //User login
    public long insertUser(String ID) {
        ContentValues values = new ContentValues();
        values.put("ID", ID);
        return mDb.insert("user", null, values);
    }

    public String getUserID() {
        String id = "";
        try {
            Cursor cursor = mDb.query("user", new String[]{"ID"}, null,
                    null, null, null, null);

            if (cursor.moveToFirst()) {
                id = cursor.getString(0);
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
            System.out.print("bug!");
        }

        return id;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        // pleas increase this version num every time changing the structure of database
        private static final int DB_VERSION = 2;

        DatabaseHelper(Context context) {
            super(context, "iConference", null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CreateAuthor);
            db.execSQL(CreateAuthorToPaper);
            db.execSQL(CreateConference);
            db.execSQL(CreateSession);
            db.execSQL(CreatePaperContent);
            db.execSQL(CreatePaper);
            db.execSQL(CreateMyScheduled);
            db.execSQL(CreateMyStarred);
            db.execSQL(CreateKeynote);
            db.execSQL(CreateUser);
            db.execSQL(CreateWorkshopDes);
            db.execSQL(CreatePoster);
            db.execSQL(CreateMyRecommended);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS conference");
            db.execSQL("DROP TABLE IF EXISTS session");
            db.execSQL("DROP TABLE IF EXISTS papercontent");
            db.execSQL("DROP TABLE IF EXISTS paper");
            db.execSQL("DROP TABLE IF EXISTS myscheduledpaper");
            db.execSQL("DROP TABLE IF EXISTS mystarredpaper");
            db.execSQL("DROP TABLE IF EXISTS keynote");
            db.execSQL("DROP TABLE IF EXISTS user");
            db.execSQL("DROP TABLE IF EXISTS workshopDes");
            db.execSQL("DROP TABLE IF EXISTS poster");
            db.execSQL("DROP TABLE IF EXISTS myrecommendedpaper");
            db.execSQL("DROP TABLE IF EXISTS author");
            db.execSQL("DROP TABLE IF EXISTS authorToPaper");
            onCreate(db);
        }
    }
}
