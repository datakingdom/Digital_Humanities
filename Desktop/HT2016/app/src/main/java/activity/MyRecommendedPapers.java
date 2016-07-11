package activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import data.UserScheduledToServer;
import data.db.DBAdapter;
import data.model.Conference;
import data.model.Paper;
import data.parser.RecommendParse;
import edu.pitt.is.DH2016.R;

public class MyRecommendedPapers extends Activity implements Runnable {
    private final int MENU_HOME = Menu.FIRST;
    private final int MENU_TRACK = Menu.FIRST + 1;
    private final int MENU_SESSION = Menu.FIRST + 2;
    private final int MENU_STAR = Menu.FIRST + 3;
    private final int MENU_SCHEDULE = Menu.FIRST + 4;
    private final int MENU_RECOMMEND = Menu.FIRST + 5;
    private DBAdapter db;
    private ListView lv;
    private TextView tx1, tx2, tx3, tx4;
    private ImageButton syncB;
    private UserScheduledToServer us2s;
    private ListViewAdapter adapter;
    private ProgressDialog pd;
    private String paperStatus;
    private int show;
    private int threadid = 0;
    private String paperID;
    private ImageButton ib;
    private int cdate;
    private String day;
    private ArrayList<Paper> pList;
    private int pos;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pd.dismiss();

            // update interface here
            if (threadid == 1) {
                pList = getDataRecommended();
                if (pList.size() == 0)
                    tx2.setVisibility(View.VISIBLE);
                else
                    tx2.setVisibility(View.GONE);
                adapter = new ListViewAdapter(pList);
                lv.setAdapter(adapter);
            } else {
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
        }
    };

    public static boolean isConnect(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {

            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null) {

                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.myscheduledpapers);

        tx1 = (TextView) findViewById(R.id.TextView01);
        tx2 = (TextView) findViewById(R.id.TextView02);

        show = 0;

        us2s = new UserScheduledToServer();

        tx1.setText("Recommendation");
        tx2.setText("Click the refresh button to get recommendation.");

		/*if(Conference.userID.compareTo("")!=0){
        pList = getDataRecommended();
		}
		else
			pList = new ArrayList<Paper>();
		adapter = new ListViewAdapter(pList);*/

        lv = (ListView) findViewById(R.id.ListView01);

        //lv.setAdapter(adapter);

        if (Conference.userSignin) {
            pList = getDataRecommended();
            adapter = new ListViewAdapter(pList);
            lv.setAdapter(adapter);
            if (pList.size() != 0) {
                show += 1;
            }
            if (show == 0)
                tx2.setVisibility(View.VISIBLE);
            else
                tx2.setVisibility(View.GONE);


        } else {
            new AlertDialog.Builder(MyRecommendedPapers.this)
                    .setMessage("Recommendation function requires login, do you want to sign in first?")
                    .setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialoginterface, int i) {
                                    dialoginterface.cancel();
                                    MyRecommendedPapers.this.finish();
                                    try {
                                        CallSignin();
                                    } catch (Exception e) {
                                    }
                                }
                            })
                    .setNegativeButton("no",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Intent in = new Intent(MyRecommendedPapers.this, MainInterface.class);
                                    startActivity(in);
                                }
                            }).show();
        }

        syncB = (ImageButton) findViewById(R.id.ImageButton01);

        syncB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (isConnect(MyRecommendedPapers.this)) {
                    try {
                        //Conference.userID = getUserID();
                        if (Conference.userID.compareTo("") != 0) {
                            Conference.userSignin = true;
                            threadid = 1;
                            callThread();
                        } else {
                            CallSignin();
                        }
                    } catch (Exception e) {
                    }
                } else
                    new AlertDialog.Builder(MyRecommendedPapers.this)
                            .setMessage("This porcess requires internet connection, please check your internet connection.")
                            .setPositiveButton("close",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialoginterface, int i) {
                                            dialoginterface.cancel();
                                        }
                                    })
                            .show();
            }
        });
    }

    public String getUserID() {
        String id = "";
        try {
            SharedPreferences getUserID = getSharedPreferences("userinfo", 0);
            id = getUserID.getString("userID", "");
        } catch (Exception e) {
        }
        return id;
    }

    public boolean isSignin() {
        SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
        boolean signin = userinfo.getBoolean("userSignin", false);
        return signin;

    }

    public ArrayList<Paper> getDataRecommended() {
        ArrayList<Paper> p;
        db = new DBAdapter(this);

        db.open();
        p = db.getPapersByRecommended();

        db.close();
        return p;
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
                itemintent.setClass(MyRecommendedPapers.this, MainInterface.class);
                startActivity(itemintent);
                return true;
            case MENU_SESSION:
                this.finish();
                itemintent.setClass(MyRecommendedPapers.this, ProgramByDay.class);
                startActivity(itemintent);
                return true;
            case MENU_STAR:
                this.finish();
                itemintent.setClass(MyRecommendedPapers.this, MyStaredPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_TRACK:
                this.finish();
                itemintent.setClass(MyRecommendedPapers.this, Proceedings.class);
                startActivity(itemintent);
                return true;
            case MENU_SCHEDULE:
                this.finish();
                itemintent.setClass(MyRecommendedPapers.this, MyScheduledPapers.class);
                startActivity(itemintent);
                return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.finish();
            Intent in = new Intent(MyRecommendedPapers.this, MainInterface.class);
            startActivity(in);
        }

        return super.onKeyDown(keyCode, event);
    }

    private void CallSignin() {
        this.finish();
        Intent in = new Intent(MyRecommendedPapers.this, Signin.class);
        in.putExtra("activity", "MyRecommendedPapers");
        in.putExtra("paperID", paperID);
        startActivity(in);
    }

    public void callThread() {

        pd = ProgressDialog.show(this, "Synchronization", "Please Wait...",
                true, false);
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {

        if (threadid == 1) {
            synchronizeScheduledPaper();

        } else if (threadid == 2)
            paperStatus = us2s.DeleteScheduledPaper2Sever(paperID);
        else {
            paperStatus = us2s.addScheduledPaper2Sever(paperID);
        }


        handler.sendEmptyMessage(0);
    }

    public void synchronizeScheduledPaper() {
        ArrayList<String> pidList = new ArrayList<String>();
        RecommendParse usp = new RecommendParse();

        pidList = usp.getRecom();

        if (pidList.size() == 0) {
            showToast("No recommendation. In order to receive recommendations, please schedule some presentations that you feel interested in.");
        } else {
            db = new DBAdapter(this);
            db.open();
            db.updatePaperRecommendToDefault();
            db.deleteUserRecommended();

            for (int i = 0; i < pidList.size(); i++) {
                db.insertMyRecommendedPaper(pidList.get(i).toString());
                db.updatePaperByRecommend(pidList.get(i).toString(), "yes");
            }

            db.close();
        }
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

    public void showToast(final String s) {
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), s,
                        Toast.LENGTH_LONG).show();

            }
        });
    }

    static class ViewHolder {
        TextView t1, t2, t3, tr, type, date;
        ImageButton star, schedule;
    }

    private class ListViewAdapter extends BaseAdapter implements
            OnClickListener {

        ArrayList<Paper> pList;

        public ListViewAdapter(ArrayList pList) {
            this.pList = pList;
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
                convertView = li.inflate(R.layout.list_item, null);
                vh = new ViewHolder();
                vh.t1 = (TextView) convertView.findViewById(R.id.time);
                vh.t2 = (TextView) convertView.findViewById(R.id.title);

                vh.t3 = (TextView) convertView.findViewById(R.id.author);
                vh.type = (TextView) convertView.findViewById(R.id.type);
                vh.date = (TextView) convertView.findViewById(R.id.text_first_char_hint);

                vh.schedule = (ImageButton) convertView
                        .findViewById(R.id.ImageButton01);

                vh.star = (ImageButton) convertView
                        .findViewById(R.id.ImageButton02);
                //vh.recommend= (TextView) convertView.findViewById(R.id.recommend);


                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
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
            if (pList.get(position).recommended.compareTo("yes") == 0)
                vh.t2.setText(Html.fromHtml(pList.get(position).title + "<font color=\"#ff0000\"> &lt;Recommended&gt; </font>"));
            else
                vh.t2.setText(pList.get(position).title);
            vh.star.setOnClickListener(this);
            vh.star.setTag(pList.get(position).presentationID + ";" + position);
            vh.t2.setOnClickListener(this);
            vh.t2.setTag(position);
            vh.t3.setText(pList.get(position).authors);
            vh.type.setText(pList.get(position).type);

            try {
                beginDate = sdfSource.parse(pList.get(position).exactbeginTime);
                endDate = sdfSource.parse(pList.get(position).exactendTime);
                begTime = sdfDestination.format(beginDate);
                endTime = sdfDestination.format(endDate);
                vh.t1.setText(pList.get(position).date + "\t" + begTime + " - " + endTime);
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

            if (currentChar.compareTo(previewChar) != 0) {
                vh.date.setVisibility(View.VISIBLE);
                vh.date.setText(currentChar.toString());
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
                    MyRecommendedPapers.this.finish();
                    Intent in = new Intent(MyRecommendedPapers.this, PaperDetail.class);
                    in.putExtra("id", pList.get(index).id);
                    in.putExtra("title", pList.get(index).title);
                    in.putExtra("authors", pList.get(index).authors);
                    in.putExtra("date", pList.get(index).date);
                    in.putExtra("room", pList.get(index).room);
                    in.putExtra("abstract", pList.get(index).paperAbstract);
                    in.putExtra("contentlink", pList.get(index).contentlink);

                    in.putExtra("bTime", pList.get(index).exactbeginTime);
                    in.putExtra("eTime", pList.get(index).exactendTime);
                    in.putExtra("presentationID", pList.get(index).presentationID);
                    in.putExtra("activity", "MyRecommendedPapers");
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
                        if (getPaperScheduled(paperID).compareTo("yes") == 0)
                            threadid = 2;
                        else
                            threadid = 3;
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
                        pList.get(pos).starred = "yes";
                        adapter.notifyDataSetChanged();

                    } else {
                        ib.setImageResource(R.drawable.no_star);
                        updateUserPaperStatus(paperID, "no", "star");
                        deleteMyStarredPaper(paperID);
                        pList.get(pos).starred = "no";
                        adapter.notifyDataSetChanged();

                    }

                    break;
                default:
                    break;
            }

        }
    }
}
