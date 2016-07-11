package activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
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
import edu.pitt.is.HT2015.R;

public class AuthorDetail extends Activity implements Runnable {
    private final int MENU_HOME = Menu.FIRST;
    private final int MENU_TRACK = Menu.FIRST + 1;
    private final int MENU_SESSION = Menu.FIRST + 2;
    private final int MENU_STAR = Menu.FIRST + 3;
    private final int MENU_SCHEDULE = Menu.FIRST + 4;
    private String wtitle, wid, room, eventSessionIDList;
    private TextView tv, t1;
    private ListView lv;
    private DBAdapter db = new DBAdapter(this);
    private UserScheduledToServer us2s;
    private String paperStatus;
    private ProgressDialog pd;
    private ImageButton ib;
    private String paperID;
    private int Pos, pos;
    private MyListViewAdapter adapter;
    private ArrayList<Paper> pList = new ArrayList<Paper>();
    private String authorID = "";
    private String authorName = "";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pd.dismiss();

            if (paperStatus.compareTo("yes") == 0) {
                ib.setImageResource(R.drawable.yes_schedule);
                updateUserPaperStatus(paperID, "yes", "schedule");
                insertMyScheduledPaper(paperID);

            }
            if (paperStatus.compareTo("no") == 0) {
                ib.setImageResource(R.drawable.no_schedule);
                updateUserPaperStatus(paperID, "no", "schedule");
                deleteMyScheduledPaper(paperID);
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.workshopdetail);

        us2s = new UserScheduledToServer();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            authorID = b.getString("authorID");
            db.open();
            pList = db.getPapersByAuthorID(authorID);
            db.close();
            authorName = b.getString("authorName");
        }

        tv = (TextView) findViewById(R.id.TextView);
        tv.setText("Authors");

        t1 = (TextView) findViewById(R.id.TextView01);
        t1.setText(authorName);

        findViewById(R.id.LinearLayout01).setVisibility(View.GONE);
//        findViewById(R.id.TextView04).setVisibility(View.GONE);

        lv = (ListView) findViewById(R.id.ListView01);
        adapter = new MyListViewAdapter(pList);
        lv.setAdapter(adapter);
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
        menu.add(0, MENU_STAR, 0, "My Favorite").setIcon(R.drawable.star);
        menu.add(0, MENU_SCHEDULE, 0, "My Schedule").setIcon(R.drawable.schedule);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent itemintent = new Intent();
        switch (item.getItemId()) {
            case MENU_HOME:
                this.finish();
                itemintent.setClass(AuthorDetail.this, MainInterface.class);
                startActivity(itemintent);
                return true;
            case MENU_SESSION:
                this.finish();
                itemintent.setClass(AuthorDetail.this, ProgramByDay.class);
                startActivity(itemintent);
                return true;
            case MENU_STAR:
                this.finish();
                itemintent.setClass(AuthorDetail.this, MyStaredPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_TRACK:
                this.finish();
                itemintent.setClass(AuthorDetail.this, Proceedings.class);
                startActivity(itemintent);
                return true;
            case MENU_SCHEDULE:
                this.finish();
                itemintent.setClass(AuthorDetail.this, MyScheduledPapers.class);
                startActivity(itemintent);
                return true;
        }
        return false;
    }

    private void CallSignin() {
        Intent in = new Intent(AuthorDetail.this, Signin.class);
        in.putExtra("authorID", authorID);
        in.putExtra("authorName", authorName);
        in.putExtra("activity", "AuthorDetail");
        startActivity(in);
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

    public void callThread() {

        pd = ProgressDialog.show(this, "Synchronization", "Please Wait...",
                true, false);
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {


        if (getPaperScheduled(paperID).compareTo("yes") == 0) {
            paperStatus = us2s.DeleteScheduledPaper2Sever(paperID);
        } else {
            paperStatus = us2s.addScheduledPaper2Sever(paperID);
        }
        handler.sendEmptyMessage(0);
    }

    public final class ViewHolder {
        TextView firstCharHintTextView, title, location;
        TextView t1, t2, t3, type;
        ImageButton star, schedule;

    }

    private class MyListViewAdapter extends BaseAdapter implements
            OnClickListener {
        //        private ArrayList<Session> parents;
        private ArrayList<Paper> childs;

        public MyListViewAdapter(ArrayList<Paper> child) {
//            this.parents = parent;
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
//                    pos = Integer.parseInt(st[2]);
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
//                    pos = Integer.parseInt(st1[2]);

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
                    int idx, idxs;
                    tv = (TextView) v;
                    String s2 = tv.getTag().toString();
                    String st2[] = s2.split(";");
                    idx = Integer.parseInt(st2[0]);
//                    idxs = Integer.parseInt(st2[1]);

                    AuthorDetail.this.finish();
                    Intent in = new Intent(AuthorDetail.this, PaperDetail.class);
                    in.putExtra("id", childs.get(idx).id);
                    in.putExtra("title", childs.get(idx).title);
                    in.putExtra("authors", childs.get(idx).authors);
                    in.putExtra("date", childs.get(idx).date);
                    in.putExtra("abstract", childs.get(idx).paperAbstract);
                    in.putExtra("room", childs.get(idx).room);
                    in.putExtra("contentlink", childs.get(idx).contentlink);
                    in.putExtra("bTime", childs.get(idx).exactbeginTime);
                    in.putExtra("eTime", childs.get(idx).exactendTime);
                    in.putExtra("presentationID", childs.get(idx).presentationID);
                    in.putExtra("activity", "AuthorDetail");
                    in.putExtra("key", authorID + "%" + authorName);
                    startActivity(in);
                    break;
                default:
                    break;
            }
        }


    }
}
