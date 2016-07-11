package activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import java.util.Hashtable;

import data.UserScheduledToServer;
import data.db.DBAdapter;
import data.model.Conference;
import data.model.Paper;
import data.parser.UserPaperSimilar;
import edu.pitt.is.HT2015.R;

public class PaperSimilar extends Activity implements Runnable {
    private ArrayList<Paper> pList;
    private ArrayList<String> pidList;
    private DBAdapter db;
    private ListView lv;
    private TextView tx1, tx2;
    private boolean starClicked, scheduleClicked;
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
    private UserPaperSimilar ups;
    private String contentID, activity, key, title, authors, pAbstract, pContent, pRoom, bTime, eTime, date, presentationID;
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
                scheduleClicked = true;
            }
            if (paperStatus.compareTo("no") == 0) {
                ib.setImageResource(R.drawable.no_schedule);
                updateUserPaperStatus(paperID, "no", "schedule");
                deleteMyScheduledPaper(paperID);
                pList.get(pos).scheduled = "no";
                adapter.notifyDataSetChanged();
                scheduleClicked = false;
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

        setContentView(R.layout.mystaredpapers);

        tx1 = (TextView) findViewById(R.id.TextView01);
        tx1.setText("People also scheduled following papers");
        tx2 = (TextView) findViewById(R.id.TextView02);
        tx2.setText("No similar paper available");

        Bundle b = getIntent().getExtras();
        if (b != null) {
            contentID = b.getString("id");
            activity = b.getString("activity");
            key = b.getString("key");
            title = b.getString("title");
            authors = b.getString("authors");
            pAbstract = b.getString("abstract");
            pContent = b.getString("contentlink");
            //tbColor = b.getInt("color");
            pRoom = b.getString("room");
            bTime = b.getString("bTime");
            eTime = b.getString("eTime");
            date = b.getString("date");
            presentationID = b.getString("presentationID");

        }
        us2s = new UserScheduledToServer();


        pidList = null;
        pidList = new ArrayList<String>();
        if (isConnect(this)) {
            new AsyncSimilarList().execute(pidList);
        } else
            new AlertDialog.Builder(this)
                    .setMessage("This porcess requires internet connection, please check your internet connection.")
                    .setPositiveButton("close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int i) {
                                    dialoginterface.cancel();
                                }
                            })
                    .show();
    }

    public ArrayList<Paper> getData() {
        ArrayList<Paper> p;
        db = new DBAdapter(this);
        db.open();
        p = db.getPapersByPPlist(pidList);
        db.close();

        return p;
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

    private void CallSignin() {
        Intent in = new Intent(PaperSimilar.this, Signin.class);
        in.putExtra("activity", "PaperSimilar");
        in.putExtra("paperID", paperID);
        in.putExtra("id", contentID);
        in.putExtra("title", title);
        in.putExtra("bTime", bTime);
        in.putExtra("eTime", eTime);
        in.putExtra("authors", authors);
        in.putExtra("Abstract", pAbstract);
        in.putExtra("contentlink", pContent);
        in.putExtra("room", pRoom);
        in.putExtra("date", date);
        in.putExtra("presentationID", presentationID);
        startActivity(in);
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

    public void showDialog(String s) {
        pd = ProgressDialog.show(this, "Synchronization", s, true, false);
    }

    public void dismissDialog() {
        pd.dismiss();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            // Do something.
            if (activity.compareToIgnoreCase("PaperInSession") == 0) {
                this.finish();
                String[] s = key.split("%");
                Intent in = new Intent(this, PaperInSession.class);
                in.putExtra("papersessionID", s[0]);
                in.putExtra("sessionName", s[1]);
                in.putExtra("bTime", s[2]);
                in.putExtra("eTime", s[3]);
                in.putExtra("date", s[4]);
                in.putExtra("room", s[5]);
                startActivity(in);
            } else if (activity.compareToIgnoreCase("MyRecommendedPapers") == 0) {
                this.finish();
                Intent in = new Intent(this, MyRecommendedPapers.class);
                startActivity(in);
            } else if (activity.compareToIgnoreCase("MyScheduledPapers") == 0) {
                this.finish();
                Intent in = new Intent(this, MyScheduledPapers.class);
                in.putExtra("day", key);
                startActivity(in);
            } else if (activity.compareToIgnoreCase("MyStaredPapers") == 0) {
                this.finish();
                Intent in = new Intent(this, MyStaredPapers.class);
                startActivity(in);
            } else if (activity.compareToIgnoreCase("ProceedingsByAuthor") == 0) {
                this.finish();
                Intent in = new Intent(this, Proceedings.class);
                in.putExtra("no", "1");
                startActivity(in);
            } else if (activity.compareToIgnoreCase("ProceedingsByName") == 0) {
                this.finish();
                Intent in = new Intent(this, Proceedings.class);
                in.putExtra("no", "2");
                startActivity(in);
            } else if (activity.compareToIgnoreCase("ProceedingsByType") == 0) {
                this.finish();
                Intent in = new Intent(this, Proceedings.class);
                in.putExtra("no", "3");
                startActivity(in);
            } else if (activity.compareToIgnoreCase("WorkshopDetail") == 0) {
                this.finish();
                String[] s = key.split("%");
                Intent in = new Intent(this, WorkshopDetail.class);
                in.putExtra("id", s[0]);
                in.putExtra("title", s[1]);
                in.putExtra("room", s[2]);
                in.putExtra("eventSessionID", s[3]);
                in.putExtra("eventSessionIDList", s[4]);
                startActivity(in);
            } else if (activity.compareToIgnoreCase("PosterDetail") == 0) {
                this.finish();
                String[] s = key.split("%");
                Intent in = new Intent(this, PosterDetail.class);
                in.putExtra("id", s[0]);
                in.putExtra("title", s[1]);
                in.putExtra("room", s[2]);
                in.putExtra("eventSessionID", s[3]);
                in.putExtra("eventSessionIDList", s[4]);
                startActivity(in);
            } else if (activity.compareToIgnoreCase("Tutorials") == 0) {
                this.finish();
                String[] s = key.split("%");
                Intent in = new Intent(this, Tutorials.class);
                in.putExtra("id", s[0]);
                in.putExtra("title", s[1]);
                in.putExtra("room", s[2]);
                startActivity(in);
            } else if (activity.compareToIgnoreCase("AuthorDetail") == 0) {
                this.finish();
                String[] s = key.split("%");
                Intent in = new Intent(this, AuthorDetail.class);
                in.putExtra("authorID", s[0]);
                in.putExtra("authorName", s[1]);
                startActivity(in);
            } else {
                return false;
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);

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
                //vh.tr = (TextView) convertView.findViewById(R.id.trackimage);
                vh.date = (TextView) convertView.findViewById(R.id.text_first_char_hint);

                vh.schedule = (ImageButton) convertView
                        .findViewById(R.id.ImageButton01);

                vh.star = (ImageButton) convertView
                        .findViewById(R.id.ImageButton02);


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
                    Intent in = new Intent(PaperSimilar.this, PaperDetail.class);
                    in.putExtra("id", pList.get(index).id);
                    in.putExtra("title", pList.get(index).title);
                    in.putExtra("authors", pList.get(index).authors);
                    in.putExtra("date", pList.get(index).date);
                    // in.putExtra("affilication", pList.get(index).affiliation);
                    in.putExtra("abstract", pList.get(index).paperAbstract);
                    in.putExtra("contentlink", pList.get(index).contentlink);
                    in.putExtra("room", pList.get(index).room);
                    in.putExtra("bTime", pList.get(index).exactbeginTime);
                    in.putExtra("eTime", pList.get(index).exactendTime);
                    in.putExtra("presentationID", pList.get(index).presentationID);
                    in.putExtra("activity", "PaperSimilar");
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
                        //paperStatus = us2s.addScheduledPaper2Sever(paperID);
                        // if (getPaperScheduled(paperID).compareTo("no") == 0) {
            /*
			if (paperStatus.compareTo("yes") == 0) {
				ib.setImageResource(R.drawable.yes_schedule);
				updateUserPaperStatus(paperID, "yes", "schedule");
				insertMyScheduledPaper(paperID);
				scheduleClicked = true;
			}
			if (paperStatus.compareTo("no") == 0) {
				ib.setImageResource(R.drawable.no_schedule);
				updateUserPaperStatus(paperID, "no", "schedule");
				deleteMyScheduledPaper(paperID);
				scheduleClicked = false;
			}
			*/
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
                        pList = getData();
                        adapter.notifyDataSetChanged();
                        starClicked = true;
                    } else {
                        ib.setImageResource(R.drawable.no_star);
                        updateUserPaperStatus(paperID, "no", "star");
                        deleteMyStarredPaper(paperID);
                        pList = getData();
                        adapter.notifyDataSetChanged();
                        starClicked = false;
                    }

                    break;
                default:
                    break;
            }

        }
    }

    private class AsyncSimilarList extends AsyncTask<ArrayList<String>, Void, Integer> {

        @Override
        protected void onPreExecute() {
            showDialog("Loading...");
        }

        @Override
        protected Integer doInBackground(ArrayList<String>... params) {
            // TODO Auto-generated method stub
            ups = new UserPaperSimilar();
            pidList = params[0];
            pidList = ups.getSimi(contentID);
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            int state = result.intValue();
            switch (state) {
                case 1:
                    dismissDialog();
                    Toast.makeText(getApplicationContext(),
                            "Done!",
                            Toast.LENGTH_SHORT)
                            .show();
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
                    break;
                default:
                    dismissDialog();
                    Toast.makeText(getApplicationContext(),
                            "Error!",
                            Toast.LENGTH_LONG)
                            .show();
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
                    break;
            }
        }
    }


}
