package activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
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
import java.util.Hashtable;

import data.UserScheduledToServer;
import data.db.DBAdapter;
import data.model.Conference;
import data.model.Paper;
import edu.pitt.is.DH2016.R;

public class MyStaredPapers extends Activity implements Runnable {
    private final int MENU_HOME = Menu.FIRST;
    private final int MENU_TRACK = Menu.FIRST + 1;
    private final int MENU_SESSION = Menu.FIRST + 2;
    private final int MENU_SCHEDULE = Menu.FIRST + 3;
    private final int MENU_RECOMMEND = Menu.FIRST + 4;
    private ArrayList<Paper> pList;
    private DBAdapter db;
    private ListView lv;
    private TextView tx2;
    private UserScheduledToServer us2s;
    private String paperStatus;
    private int show;
    private ProgressDialog pd;
    private ImageButton ib;
    private String paperID;
    private Hashtable idTotrackName;
    private Hashtable<String, Drawable> idTotrackImage;
    //private Hashtable pTable;
    private ListViewAdapter adapter;
    private int pos;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pd.dismiss();
            // update interface here

            if (paperStatus.compareTo("yes") == 0) {
                updateUserPaperStatus(paperID, "yes", "schedule");
                insertMyScheduledPaper(paperID);
                pList.get(pos).scheduled = "yes";
                adapter.notifyDataSetChanged();
            } else if (paperStatus.compareTo("no") == 0) {
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

        setContentView(R.layout.mystaredpapers);

        tx2 = (TextView) findViewById(R.id.TextView02);

        us2s = new UserScheduledToServer();

        pList = getData();

        adapter = new ListViewAdapter(pList);
        lv = (ListView) findViewById(R.id.ListView01);
        lv.setAdapter(adapter);

        if (pList.size() != 0) {
            show += 1;
        }
        if (show == 0) {
            tx2.setVisibility(View.VISIBLE);
        } else
            tx2.setVisibility(View.GONE);
    }

    public ArrayList<Paper> getData() {
        ArrayList<Paper> p;
        db = new DBAdapter(this);
        db.open();
        p = db.getPapersByStared();
        db.close();

        return p;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_HOME, 0, "Home").setIcon(R.drawable.home);
        menu.add(0, MENU_TRACK, 0, "Proceedings").setIcon(R.drawable.proceedings);
        menu.add(0, MENU_SESSION, 0, "Schedule").setIcon(R.drawable.session);
        menu.add(0, MENU_SCHEDULE, 0, "My Schedule").setIcon(R.drawable.schedule);
        menu.add(0, MENU_RECOMMEND, 0, "Recommendation").setIcon(R.drawable.recommends);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent itemintent = new Intent();
        switch (item.getItemId()) {
            case MENU_HOME:
                this.finish();
                itemintent.setClass(MyStaredPapers.this, MainInterface.class);
                startActivity(itemintent);
                return true;
            case MENU_SESSION:
                this.finish();
                itemintent.setClass(MyStaredPapers.this, ProgramByDay.class);
                startActivity(itemintent);
                return true;
            case MENU_TRACK:
                this.finish();
                itemintent.setClass(MyStaredPapers.this, Proceedings.class);
                startActivity(itemintent);
                return true;
            case MENU_SCHEDULE:
                this.finish();
                itemintent.setClass(MyStaredPapers.this, MyScheduledPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_RECOMMEND:
                this.finish();
                itemintent.setClass(MyStaredPapers.this, MyRecommendedPapers.class);
                startActivity(itemintent);
                return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            //this.finish(); this.onStop(); this.onDestroy();
            this.finish();
            Intent in = new Intent(MyStaredPapers.this, MainInterface.class);
            startActivity(in);
        }

        return super.onKeyDown(keyCode, event);
    }

    public void updateUserPaperStatus(String paperID, String status,
                                      String which) {
        db = new DBAdapter(this);
        db.open();
        if (which.compareTo("schedule") == 0)
            db.updatePaperBySchedule(paperID, status);
        if (which.compareTo("star") == 0)
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

    private void CallSignin() {
        Intent in = new Intent(MyStaredPapers.this, Signin.class);
        in.putExtra("activity", "MyStaredPapers");
        in.putExtra("paperID", paperID);
        startActivity(in);
    }

    public String getUserID() {
        String id = "";

		/*
        db = new DBAdapter(this);
		db.open();
		id = db.getUserID();
		db.close();
		*/

        try {
            SharedPreferences getUserID = getSharedPreferences("userinfo", 0);
            id = getUserID.getString("userID", "");
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
        TextView t1, t2, t3, tr, type, date, location;
        ImageButton star, schedule;
    }

    private class ListViewAdapter extends BaseAdapter implements
            OnClickListener {

        ArrayList<Paper> paperList;

        public ListViewAdapter(ArrayList pList) {
            this.paperList = pList;
        }

        public int getCount() {
            return paperList.size();
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
                convertView = li.inflate(R.layout.list_item, null);
                vh = new ViewHolder();
                vh.t1 = (TextView) convertView.findViewById(R.id.time);
                vh.t2 = (TextView) convertView.findViewById(R.id.title);

                vh.t3 = (TextView) convertView.findViewById(R.id.author);
                vh.type = (TextView) convertView.findViewById(R.id.type);
                //vh.tr = (TextView) convertView.findViewById(R.id.trackimage);
                vh.date = (TextView) convertView.findViewById(R.id.text_first_char_hint);

                vh.schedule = (ImageButton) convertView
                        .findViewById(R.id.ImageButton01);

                vh.star = (ImageButton) convertView
                        .findViewById(R.id.ImageButton02);

                vh.location = (TextView) convertView.findViewById(R.id.location);
                vh.location.setVisibility(View.VISIBLE);
                String room = paperList.get(position).room;
                if (room == null || "null".compareToIgnoreCase(room) == 0 || "".compareTo(room) == 0 || "N/A".compareToIgnoreCase(room) == 0) {
                    vh.location.setVisibility(View.GONE);
                    //vh.location.setText("AT N/A");
                } else {
                    vh.location.setText("AT " + room);
                }

                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            if (pList.get(position).scheduled.compareTo("yes") == 0) {
                vh.schedule.setImageResource(R.drawable.yes_schedule);
            } else
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
            vh.t2.setOnClickListener(this);
            vh.t2.setTag(position);
            vh.t3.setText(pList.get(position).authors);
            vh.type.setText(pList.get(position).type);
            //vh.tr.setText(idTotrackName.get(pList.get(position).trackID).toString());
            //vh.tr.setBackgroundDrawable(getResources().getDrawable(R.drawable.track));

	/*
     * vh.t1.setText(pList.get(position).exactbeginTime + " - " +
	 * pList.get(position).exactendTime);
	 */

            try {
                beginDate = sdfSource.parse(pList.get(position).exactbeginTime);
                endDate = sdfSource.parse(pList.get(position).exactendTime);
                begTime = sdfDestination.format(beginDate);
                endTime = sdfDestination.format(endDate);
                vh.t1.setText(begTime + " - " + endTime);
            } catch (Exception e) {
                System.out.println("Date Exception");
            }
            int idx = position - 1;
            String previewChar;
            if (idx >= 0) {
                previewChar = pList.get(idx).date;
            } else
                previewChar = " ";

            String currentChar = pList.get(position).date;

            String newPreviewChar = previewChar;
            String newCurrentChar = currentChar;
            if (newCurrentChar.compareTo(newPreviewChar) != 0) {
                vh.date.setVisibility(View.VISIBLE);
                vh.date.setText(newCurrentChar.toString());
            } else {

                vh.date.setVisibility(View.GONE);
            }
            return convertView;
        }

        public void onClick(View v) {
            // TODO Auto-generated method stub
            TextView tv;
            int index;
            switch (v.getId()) {
                case R.id.title:
                    tv = (TextView) v;
                    index = Integer.parseInt(tv.getTag().toString());
                    MyStaredPapers.this.finish();
                    Intent in = new Intent(MyStaredPapers.this, PaperDetail.class);
                    in.putExtra("id", paperList.get(index).id);
                    in.putExtra("title", paperList.get(index).title);
                    in.putExtra("authors", paperList.get(index).authors);
                    in.putExtra("date", paperList.get(index).date);
                    in.putExtra("abstract", paperList.get(index).paperAbstract);
                    in.putExtra("contentlink", paperList.get(index).contentlink);

                    in.putExtra("room", paperList.get(index).room);
                    in.putExtra("bTime", paperList.get(index).exactbeginTime);
                    in.putExtra("eTime", paperList.get(index).exactendTime);
                    in.putExtra("presentationID", paperList.get(index).presentationID);
                    in.putExtra("activity", "MyStaredPapers");
                    in.putExtra("key", "no");
                    startActivity(in);
                    break;
                case R.id.ImageButton01:
                    ib = (ImageButton) v;
                    String s = ib.getTag().toString();
                    String[] st = s.split(";");
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
                    String[] st1 = s1.split(";");
                    paperID = st1[0];
                    pos = Integer.parseInt(st1[1]);

                    if (getPaperStarred(paperID).compareTo("no") == 0) {
                        ib.setImageResource(R.drawable.yes_star);
                        updateUserPaperStatus(paperID, "yes", "star");
                        insertMyStarredPaper(paperID);
                        paperList = getData();
                        pList = paperList;
                        adapter.notifyDataSetChanged();
                    } else {
                        ib.setImageResource(R.drawable.no_star);
                        updateUserPaperStatus(paperID, "no", "star");
                        deleteMyStarredPaper(paperID);
                        paperList = getData();
                        pList = paperList;
                        adapter.notifyDataSetChanged();
                    }

                    break;
                default:
                    break;
            }

        }
    }
}
