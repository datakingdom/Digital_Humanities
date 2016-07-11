package activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import data.UserScheduledToServer;
import data.db.DBAdapter;
import data.model.Conference;
import data.model.Paper;
import data.model.Session;
import edu.pitt.is.DH2016.R;

public class PosterDetail extends Activity implements Runnable {
    private final int MENU_HOME = Menu.FIRST;
    private final int MENU_TRACK = Menu.FIRST + 1;
    private final int MENU_SESSION = Menu.FIRST + 2;
    private final int MENU_STAR = Menu.FIRST + 3;
    private final int MENU_SCHEDULE = Menu.FIRST + 4;
    private String wtitle, wid, room, eventSessionIDList;
    private TextView t1, t4, tv;
    private ListView lv;
    private ListAdapter adapter;
    private ImageButton ib;
    private DBAdapter db = new DBAdapter(this);
    private UserScheduledToServer us2s;
    private String paperStatus;
    private ProgressDialog pd;
    private String paperID;
    private int pos;
    private String[] eventSessionIDs;
    private Session session;
    private ArrayList<Session> sList = new ArrayList<Session>();
    private ArrayList<Paper> pList = new ArrayList<Paper>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pd.dismiss();
            // update interface here

            if (paperStatus.compareTo("yes") == 0) {
                ib.setImageResource(R.drawable.yes_schedule);
                updateUserPaperStatus(paperID, "yes", "schedule");
                insertMyScheduledPaper(paperID);
//                pList.get(pos).scheduled = "yes";
//                adapter.notifyDataSetChanged();

            }
            if (paperStatus.compareTo("no") == 0) {
                ib.setImageResource(R.drawable.no_schedule);
                updateUserPaperStatus(paperID, "no", "schedule");
                deleteMyScheduledPaper(paperID);
//                pList.get(pos).scheduled = "no";
//                adapter.notifyDataSetChanged();

            }

        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.workshopdetail);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            eventSessionIDList = b.getString("eventSessionIDList");
            eventSessionIDs = eventSessionIDList.split(";");
            for (int i = 0; i < eventSessionIDs.length; i++) {
                session = db.open().getSessionByID(eventSessionIDs[i]);
                sList.add(session);
            }
            db.close();
            wtitle = session.name;
            room = session.room;

        }

        tv = (TextView) findViewById(R.id.TextView);
        tv.setText("Poster");


        us2s = new UserScheduledToServer();
        t1 = (TextView) findViewById(R.id.TextView01);
        t1.setText(wtitle);

        t4 = (TextView) findViewById(R.id.TextView04);
        if (room == null || "null".compareToIgnoreCase(room) == 0 || "".compareTo(room) == 0)
            t4.setVisibility(View.GONE);
        else {
            t4.setText(room);
        }

        t4.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                        .appendQueryParameter("q", room)
                        .build();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(geoLocation);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.d("Debug", "Couldn't call " + room + ", no receiving apps installed!");
                }
            }

        });

        // get paper by session id
        for (int i = 0; i < eventSessionIDs.length; i++) {
            pList.addAll(getPaperData(eventSessionIDs[i]));
        }

        lv = (ListView) findViewById(R.id.ListView01);
        adapter = new MyListViewAdapter(pList);
        lv.setAdapter(adapter);

    }

    private void CallSignin() {
        Intent in = new Intent(PosterDetail.this, Signin.class);
        in.putExtra("activity", "PaperInfo");
        in.putExtra("paperID", wid);
        in.putExtra("title", wtitle);
        in.putExtra("room", room);
        in.putExtra("eventSessionIDList", eventSessionIDList);
        startActivity(in);
    }

    public String getUserID() {
        String id = "";

        try {
            SharedPreferences getUserID = getSharedPreferences("userinfo", 0);
            id = getUserID.getString("userID", "");
        } catch (Exception e) {
        }

        if (id.compareTo("") != 0)
            Conference.userSignin = true;
        return id;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_HOME, 0, "Home").setIcon(R.drawable.home);
        menu.add(0, MENU_TRACK, 0, "Proceedings").setIcon(R.drawable.proceedings);
        menu.add(0, MENU_SESSION, 0, "Schedule").setIcon(R.drawable.session);
        menu.add(0, MENU_STAR, 0, "My favourite").setIcon(R.drawable.star);
        menu.add(0, MENU_SCHEDULE, 0, "My Schedule").setIcon(R.drawable.schedule);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent itemintent = new Intent();
        switch (item.getItemId()) {
            case MENU_HOME:
                this.finish();
                itemintent.setClass(PosterDetail.this, MainInterface.class);
                startActivity(itemintent);
                return true;
            case MENU_SESSION:
                this.finish();
                itemintent.setClass(PosterDetail.this, ProgramByDay.class);
                startActivity(itemintent);
                return true;
            case MENU_STAR:
                this.finish();
                itemintent.setClass(PosterDetail.this, MyStaredPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_TRACK:
                this.finish();
                itemintent.setClass(PosterDetail.this, Proceedings.class);
                startActivity(itemintent);
                return true;
            case MENU_SCHEDULE:
                this.finish();
                itemintent.setClass(PosterDetail.this, MyScheduledPapers.class);
                startActivity(itemintent);
                return true;
        }
        return false;
    }

    public void updateUserPaperStatus(String paperID, String status,
                                      String which) {
        db = new DBAdapter(this);
        db.open();
        if (which.compareTo("schedule") == 0)
            db.updatePaperBySchedule(paperID, status);
        else
            db.updatePaperByStar(paperID, status);
        db.close();
    }

    public String getPaperScheduled(String paperID) {
        String status;
        db = new DBAdapter(this);
        db.open();

        status = db.getPaperScheduledStatus(paperID);

        db.close();

        return status;
    }

    public String getPaperStarred(String paperID) {
        String status;
        db = new DBAdapter(this);
        db.open();

        status = db.getPaperStarredStatus(paperID);

        db.close();

        return status;
    }

    public void insertMyScheduledPaper(String paperID) {
        db = new DBAdapter(this);
        db.open();
        db.insertMyScheduledPaper(paperID);
        db.close();
    }

    public void deleteMyScheduledPaper(String paperID) {
        db = new DBAdapter(this);
        db.open();
        db.deleteMyScheduledPaper(paperID);
        db.close();
    }

    public void insertMyStarredPaper(String paperID) {
        db = new DBAdapter(this);
        db.open();
        db.insertMyStarredPaper(paperID);
        db.close();
    }

    public void deleteMyStarredPaper(String paperID) {
        db = new DBAdapter(this);
        db.open();
        db.deleteMyStarredPaper(paperID);
        db.close();
    }

    public void run() {
        // TODO Auto-generated method stub
        if (getPaperScheduled(paperID).compareTo("yes") == 0)
            paperStatus = us2s.DeleteScheduledPaper2Sever(paperID);
        else if (getPaperScheduled(paperID).compareTo("no") == 0)
            paperStatus = us2s.addScheduledPaper2Sever(paperID);
        handler.sendEmptyMessage(0);
    }

    public ArrayList<Paper> getPaperData(String sessionID) {
        ArrayList<Paper> papers = new ArrayList<Paper>();
        // get data at local
        db = new DBAdapter(this);
        db.open();
        papers = db.getPapersBysessionID(sessionID);
        db.close();

        return papers;
    }

    public void callThread() {

        pd = ProgressDialog.show(this, "Synchronization", "Please Wait...",
                true, false);
        Thread thread = new Thread(this);
        thread.start();

    }

    public final class ViewHolder {
        TextView firstCharHintTextView, title, location;
        TextView t1, t2, t3, type;
        ImageButton star, schedule;

    }

    private class MyListViewAdapter extends BaseAdapter implements
            OnClickListener {
        private ArrayList<Paper> childs;

        public MyListViewAdapter(ArrayList<Paper> child) {
            this.childs = child;
        }

        public int getCount() {
            return childs.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int childPos, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder vh = null;
            SimpleDateFormat sdfSource = new SimpleDateFormat("HH:mm");
            SimpleDateFormat sdfDestination = new SimpleDateFormat("h:mm a");
            Date beginDate, endDate;
            String begTime, endTime;
            if (convertView == null) {
                LayoutInflater li = getLayoutInflater();
                convertView = li.inflate(R.layout.paperitem, null);
                vh = new ViewHolder();
                vh.t1 = (TextView) convertView.findViewById(R.id.time);
                vh.t2 = (TextView) convertView.findViewById(R.id.title);
                vh.t2.setOnClickListener(this);
                vh.t3 = (TextView) convertView.findViewById(R.id.author);
                vh.type = (TextView) convertView.findViewById(R.id.type);
                vh.schedule = (ImageButton) convertView
                        .findViewById(R.id.ImageButton01);
                vh.star = (ImageButton) convertView
                        .findViewById(R.id.ImageButton02);

                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            try {
                beginDate = sdfSource.parse(childs.get(childPos).exactbeginTime);
                endDate = sdfSource.parse(childs.get(childPos).exactendTime);
                begTime = sdfDestination.format(beginDate);
                endTime = sdfDestination.format(endDate);
                vh.t1.setText(childs.get(childPos).date + "\t" + begTime + " - " + endTime);
            } catch (Exception e) {
                System.out.println("Date Exception");
            }
            if (childs.get(childPos).scheduled.compareTo("yes") == 0)
                vh.schedule.setImageResource(R.drawable.yes_schedule);
            else
                vh.schedule.setImageResource(R.drawable.no_schedule);
            vh.schedule.setFocusable(false);
            vh.schedule.setOnClickListener(this);
            vh.schedule.setTag(childs.get(childPos).id + ";" + childPos);

            if (childs.get(childPos).starred.compareTo("yes") == 0)
                vh.star.setImageResource(R.drawable.yes_star);
            else
                vh.star.setImageResource(R.drawable.no_star);
            vh.star.setFocusable(false);
            vh.star.setOnClickListener(this);
            vh.star.setTag(childs.get(childPos).presentationID + ";" + childPos);

            if (childs.get(childPos).recommended.compareTo("yes") == 0)
                vh.t2.setText(Html.fromHtml(childs.get(childPos).title + "<font color=\"#ff0000\"> &lt;Recommended&gt; </font>"));
            else
                vh.t2.setText(childs.get(childPos).title);
            vh.t2.setTag(childPos);
            vh.t3.setText(childs.get(childPos).authors);
            vh.type.setText(childs.get(childPos).type);
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            TextView tv;
            switch (v.getId()) {
                case R.id.ImageButton01:
                    ib = (ImageButton) v;
                    String s = ib.getTag().toString();
                    String st[] = s.split(";");
                    paperID = st[0];
                    pos = Integer.parseInt(st[1]);
                    Conference.userID = getUserID();
                    if (Conference.userSignin) {
                        paperStatus = "";
                        callThread();
                    } else {
                        CallSignin();
                    }
                    break;
                case R.id.ImageButton02:
                    ib = (ImageButton) v;
                    String s1 = ib.getTag().toString();
                    String st1[] = s1.split(";");
                    paperID = st1[0];
                    pos = Integer.parseInt(st1[1]);

                    if (getPaperStarred(paperID).compareTo("no") == 0) {
                        ib.setImageResource(R.drawable.yes_star);
                        updateUserPaperStatus(paperID, "yes", "star");
                        insertMyStarredPaper(paperID);
                        childs.get(pos).starred = "yes";
//                        this.notifyDataSetChanged();

                    } else {
                        ib.setImageResource(R.drawable.no_star);
                        updateUserPaperStatus(paperID, "no", "star");
                        deleteMyStarredPaper(paperID);
                        childs.get(pos).starred = "no";
//                        this.notifyDataSetChanged();

                    }

                    break;
                case R.id.title:
                    int idx;
                    tv = (TextView) v;
                    String s2 = tv.getTag().toString();
                    String st2[] = s2.split(";");
                    idx = Integer.parseInt(st2[0]);

                    finish();
                    Intent in = new Intent(PosterDetail.this, PaperDetail.class);
                    in.putExtra("id", childs.get(idx).id);
                    in.putExtra("title", childs.get(idx).title);
                    in.putExtra("authors", childs.get(idx).authors);
                    in.putExtra("date", childs.get(idx).date);
                    in.putExtra("abstract", childs.get(idx).paperAbstract);
                    in.putExtra("room", room);
                    in.putExtra("contentlink", childs.get(idx).contentlink);
                    in.putExtra("bTime", childs.get(idx).exactbeginTime);
                    in.putExtra("eTime", childs.get(idx).exactendTime);
                    in.putExtra("presentationID", childs.get(idx).presentationID);
                    in.putExtra("activity", "PosterDetail");
                    in.putExtra("key", wid + "%" + wtitle + "%" + room + "%" + sList.get(pos).ID + "%" + eventSessionIDList);
                    startActivity(in);
                    break;
                default:
                    break;
            }
        }
    }

}
