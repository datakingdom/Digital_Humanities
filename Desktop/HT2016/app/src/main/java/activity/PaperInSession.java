package activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import data.UserScheduledToServer;
import data.db.DBAdapter;
import data.model.Conference;
import data.model.Paper;
import edu.pitt.is.DH2016.R;

public class PaperInSession extends Activity implements Runnable {
    private final int MENU_HOME = Menu.FIRST;
    private final int MENU_TRACK = Menu.FIRST + 1;
    private final int MENU_STAR = Menu.FIRST + 2;
    private final int MENU_SCHEDULE = Menu.FIRST + 3;
    private final int MENU_RECOMMEND = Menu.FIRST + 4;
    private String papersessionID, sessionName, sessionBTime, sessionETime,
            sessionDate, room;
    private TextView t1, t2, t4;
    private ListView lv;
    private DBAdapter db;
    private ArrayList<Paper> pList;
    private UserScheduledToServer us2s;
    private String paperStatus;
    private ProgressDialog pd;
    private ImageButton ib;
    private String paperID;
    private int pos;
    private ListViewAdapter adapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pd.dismiss();
            // update interface here

            if (paperStatus.compareTo("yes") == 0) {
                ib.setImageResource(R.drawable.yes_schedule);
                updateUserPaperStatus(paperID, "yes", "schedule");
                insertMyScheduledPaper(paperID);
                pList.get(pos).scheduled = "yes";
                adapter.notifyDataSetChanged();

            }
            if (paperStatus.compareTo("no") == 0) {
                ib.setImageResource(R.drawable.no_schedule);
                updateUserPaperStatus(paperID, "no", "schedule");
                deleteMyScheduledPaper(paperID);
                pList.get(pos).scheduled = "no";
                adapter.notifyDataSetChanged();

            }

        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.paperinsession);

        us2s = new UserScheduledToServer();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            papersessionID = b.getString("papersessionID");
            sessionName = b.getString("sessionName");
            sessionBTime = b.getString("bTime");
            sessionETime = b.getString("eTime");
            sessionDate = b.getString("date");
            room = b.getString("room");
        }


        t1 = (TextView) findViewById(R.id.TextView01);
        t1.setText(sessionName);

        SimpleDateFormat sdfSource = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdfDestination = new SimpleDateFormat("h:mm a");
        Date beginDate, endDate;
        String begTime, endTime;
        try {
            beginDate = sdfSource.parse(sessionBTime);
            endDate = sdfSource.parse(sessionETime);
            begTime = sdfDestination.format(beginDate);
            endTime = sdfDestination.format(endDate);
            t2 = (TextView) this.findViewById(R.id.TextView02);
            t2.setText(sessionDate + "\t" + begTime + "-" + endTime);
        } catch (Exception e) {
            System.out.println("Date Exception");
        }

        t4 = (TextView) findViewById(R.id.TextView04);
        if (room == null || "null".compareToIgnoreCase(room) == 0 || "".compareTo(room) == 0 || "N/A".compareToIgnoreCase(room) == 0)
            t4.setVisibility(View.GONE);
            //t4.setText("N/A");
        else {
            t4.setText("At " + room);
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

        lv = (ListView) findViewById(R.id.ListView01);
        pList = new ArrayList<Paper>();
        pList = getdata(papersessionID);
        adapter = new ListViewAdapter(this);
        lv.setAdapter(adapter);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_HOME, 0, "Home").setIcon(R.drawable.home);
        menu.add(0, MENU_TRACK, 0, "Proceedings").setIcon(R.drawable.proceedings);
        menu.add(0, MENU_STAR, 0, "My Favorite").setIcon(R.drawable.star);
        menu.add(0, MENU_SCHEDULE, 0, "My Schedule").setIcon(R.drawable.schedule);
        menu.add(0, MENU_RECOMMEND, 0, "Recommendation").setIcon(R.drawable.recommends);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent itemintent = new Intent();
        switch (item.getItemId()) {
            case MENU_HOME:
                this.finish();
                itemintent.setClass(PaperInSession.this, MainInterface.class);
                startActivity(itemintent);
                return true;
            case MENU_STAR:
                this.finish();
                itemintent.setClass(PaperInSession.this, MyStaredPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_TRACK:
                this.finish();
                itemintent.setClass(PaperInSession.this, Proceedings.class);
                startActivity(itemintent);
                return true;
            case MENU_SCHEDULE:
                this.finish();
                itemintent.setClass(PaperInSession.this, MyScheduledPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_RECOMMEND:
                this.finish();
                itemintent.setClass(PaperInSession.this, MyRecommendedPapers.class);
                startActivity(itemintent);
                return true;
        }
        return false;
    }

    private void CallSignin() {
        Intent in = new Intent(PaperInSession.this, Signin.class);
        in.putExtra("activity", "PaperInSession");
        in.putExtra("papersessionID", papersessionID);
        in.putExtra("sessionName", sessionName);
        in.putExtra("bTime", sessionBTime);
        in.putExtra("eTime", sessionETime);
        in.putExtra("date", sessionDate);
        in.putExtra("room", room);
        in.putExtra("paperID", paperID);

        startActivity(in);
    }

    private ArrayList<Paper> getdata(String sessionID) {
        ArrayList<Paper> pList = new ArrayList<Paper>();
        // get data at local
        db = new DBAdapter(this);
        db.open();
        pList = db.getPapersBysessionID(sessionID);
        db.close();

        return pList;
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

    public String getUserID() {
        String id = "";
        try {
            SharedPreferences getUserID = getSharedPreferences("userinfo", 0);
            id = getUserID.getString("userID", "");
            System.out.println("_+_+_+++++++++________" + id);
        } catch (Exception e) {
        }

        if (id.compareTo("") != 0)
            Conference.userSignin = true;
        return id;
    }

    public void run() {
        // TODO Auto-generated method stub
        if (getPaperScheduled(paperID).compareTo("yes") == 0)
            paperStatus = us2s.DeleteScheduledPaper2Sever(paperID);
        else if (getPaperScheduled(paperID).compareTo("no") == 0)
            paperStatus = us2s.addScheduledPaper2Sever(paperID);
        handler.sendEmptyMessage(0);
    }

    public void callThread() {

        pd = ProgressDialog.show(this, "Synchronization", "Please Wait...",
                true, false);
        Thread thread = new Thread(this);
        thread.start();

    }

    static class ViewHolder {
        TextView t1, t2, t3, type;
        ImageButton star, schedule;
    }

    private class ListViewAdapter extends BaseAdapter implements
            OnClickListener {
        public ListViewAdapter(Context c) {
        }

        public int getCount() {
            return pList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
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
                beginDate = sdfSource.parse(pList.get(position).exactbeginTime);
                endDate = sdfSource.parse(pList.get(position).exactendTime);
                begTime = sdfDestination.format(beginDate);
                endTime = sdfDestination.format(endDate);
                vh.t1.setText(sessionDate + "\t" + begTime + " - " + endTime);
            } catch (Exception e) {
                System.out.println("Date Exception");
            }
            if (pList.get(position).scheduled.compareTo("yes") == 0)
                vh.schedule.setImageResource(R.drawable.yes_schedule);
            else
                vh.schedule.setImageResource(R.drawable.no_schedule);

            vh.schedule.setOnClickListener(this);
            vh.schedule.setTag(pList.get(position).id + ";" + position);

            if (pList.get(position).starred.compareTo("yes") == 0)
                vh.star.setImageResource(R.drawable.yes_star);
            else
                vh.star.setImageResource(R.drawable.no_star);
            vh.star.setOnClickListener(this);
            vh.star.setTag(pList.get(position).presentationID + ";" + position);

            if (pList.get(position).recommended.compareTo("yes") == 0)
                vh.t2.setText(Html.fromHtml(pList.get(position).title + "<font color=\"#ff0000\"> &lt;Recommended&gt; </font>"));
            else
                vh.t2.setText(pList.get(position).title);
            vh.t2.setTag(position);
            vh.t3.setText(pList.get(position).authors);
            vh.type.setText(pList.get(position).type);
            return convertView;
        }

        public void onClick(View v) {
            // TODO Auto-generated method stub
            //ImageButton ib;
            TextView tv;
            int index;
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
                        pList.get(pos).starred = "yes";
                        this.notifyDataSetChanged();

                    } else {
                        ib.setImageResource(R.drawable.no_star);
                        updateUserPaperStatus(paperID, "no", "star");
                        deleteMyStarredPaper(paperID);
                        pList.get(pos).starred = "no";
                        this.notifyDataSetChanged();

                    }

                    break;
                case R.id.title:
                    tv = (TextView) v;
                    index = Integer.parseInt(tv.getTag().toString());
                    PaperInSession.this.finish();
                    Intent in = new Intent(PaperInSession.this, PaperDetail.class);
                    in.putExtra("id", pList.get(index).id);
                    in.putExtra("title", pList.get(index).title);
                    in.putExtra("authors", pList.get(index).authors);
                    in.putExtra("date", pList.get(index).date);
                    in.putExtra("abstract", pList.get(index).paperAbstract);
                    in.putExtra("contentlink", pList.get(index).contentlink);
                    in.putExtra("sessionName", sessionName);
                    in.putExtra("room", room);
                    in.putExtra("bTime", pList.get(index).exactbeginTime);
                    in.putExtra("eTime", pList.get(index).exactendTime);
                    in.putExtra("presentationID", pList.get(index).presentationID);
                    in.putExtra("activity", "PaperInSession");
                    in.putExtra("key", papersessionID + "%" + sessionName + "%" + sessionBTime + "%" + sessionETime + "%" + sessionDate + "%" + room);
                    startActivity(in);
                    break;
                default:
                    break;
            }
        }

    }

}
