package activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class ProceedingsByType extends Activity implements OnItemClickListener, OnScrollListener, Runnable {

    /**
     * Called when the activity is first created.
     */
    private MyListAdapter adapter;
    private ArrayList<Paper> pList;
    private WindowManager windowManager;
    private TextView txtOverlay;   //put in windows manager to show char hint
    private Handler handler1;
    private DisapearThread disapearThread;
    private int scrollState;
    private ListView list, listview;
    private DBAdapter db;
    private ImageButton ib;
    private String paperID, paperStatus;
    private ProgressDialog pd;
    private UserScheduledToServer us2s;
    private int pos;
    private String py[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
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

    public static int binSearch(ArrayList<Paper> p, String s) {
        for (int i = 0; i < p.size(); i++) {
            if (s.equalsIgnoreCase("" + p.get(i).type.charAt(0))) {
                return i;
            }
        }
        return -1;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proceedingsby);

        txtOverlay = (TextView) LayoutInflater.from(this).inflate(R.layout.list_popup_char_hint, null);
        txtOverlay.setVisibility(View.INVISIBLE);
        //WindowManager settings
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION,
                //set to no focus state
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                //translucent
                PixelFormat.TRANSLUCENT);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(txtOverlay, lp);

        us2s = new UserScheduledToServer();


        handler1 = new Handler();
        disapearThread = new DisapearThread();

        db = new DBAdapter(this);
        db.open();
        pList = new ArrayList<Paper>();
        pList = db.getPapersBypresentationType();
        db.close();


        list = (ListView) this.findViewById(R.id.list); // real ListView
        listview = (ListView) this.findViewById(R.id.listview); //side ListView

        adapter = new MyListAdapter(this);
        list.setAdapter(adapter);
        list.setOnScrollListener(this);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, (R.layout.textview), py);
        listview.setAdapter(adapter1);

        listview.setDivider(null);
        listview.setOnItemClickListener(this);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            //this.finish(); this.onStop(); this.onDestroy();
            this.finish();

            Intent in = new Intent(ProceedingsByType.this, MainInterface.class);
            startActivity(in);
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        txtOverlay.setVisibility(View.INVISIBLE);
        windowManager.removeView(txtOverlay);
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

    public void callThread() {

        pd = ProgressDialog.show(this, "Synchronization", "Please Wait...",
                true, false);
        Thread thread = new Thread(this);
        thread.start();

    }

    private void CallSignin() {
        Intent in = new Intent(ProceedingsByType.this, Signin.class);
        in.putExtra("activity", "ProceedingsByType");
        in.putExtra("paperID", paperID);
        startActivity(in);
    }

    public String getPaperStarred(String paperID) {
        String status;
        db = new DBAdapter(this);
        db.open();

        status = db.getPaperStarredStatus(paperID);

        db.close();

        return status;
    }

    public String getPaperScheduled(String paperID) {
        String status;
        db = new DBAdapter(this);
        db.open();

        status = db.getPaperScheduledStatus(paperID);

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

    public void insertMyScheduledPaper(String paperID) {
        db = new DBAdapter(this);
        db.open();
        db.insertMyScheduledPaper(paperID);
        db.close();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        txtOverlay.setText(String.valueOf(pList.get(firstVisibleItem + (visibleItemCount >> 1)).type.charAt(0)).toUpperCase());
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;
        if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
            handler1.removeCallbacks(disapearThread);

            handler1.postDelayed(disapearThread, 1500);
        } else {
            txtOverlay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        String s = ((TextView) view).getText().toString().charAt(0) + "";
        txtOverlay.setText(s);
        txtOverlay.setVisibility(View.VISIBLE);
        handler1.removeCallbacks(disapearThread);

        handler1.postDelayed(disapearThread, 1500);

        int localPosition = binSearch(pList, s);

        if (localPosition != -1) {
            txtOverlay.setVisibility(View.INVISIBLE);
            list.setSelection(localPosition);
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        if (getPaperScheduled(paperID).compareTo("yes") == 0)
            paperStatus = us2s.DeleteScheduledPaper2Sever(paperID);
        else if (getPaperScheduled(paperID).compareTo("no") == 0)
            paperStatus = us2s.addScheduledPaper2Sever(paperID);
        handler.sendEmptyMessage(0);
    }

    private class DisapearThread implements Runnable {
        @Override
        public void run() {
            // to avoid invisible in 1.5second
            if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
                txtOverlay.setVisibility(View.INVISIBLE);
            }
        }
    }

    public final class ViewHolder {
        public TextView firstCharHintTextView;
        public TextView nameTextView;
        public TextView time, author, type, recommend;
        public LinearLayout l;
        ImageButton star, schedule;
    }

    private class MyListAdapter extends BaseAdapter implements
            OnClickListener {
        SimpleDateFormat sdfSource = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdfDestination = new SimpleDateFormat("h:mm a");
        Date beginDate, endDate;
        String begTime, endTime;
        private LayoutInflater inflater;

        public MyListAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return pList.size();
        }

        @Override
        public Object getItem(int position) {
            return pList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item, null);
                holder = new ViewHolder();

                holder.firstCharHintTextView = (TextView) convertView.findViewById(R.id.text_first_char_hint);
                holder.nameTextView = (TextView) convertView.findViewById(R.id.title);
                holder.nameTextView.setOnClickListener(this);

                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.author = (TextView) convertView.findViewById(R.id.author);
                holder.type = (TextView) convertView.findViewById(R.id.type);
                //holder.recommend = (TextView) convertView.findViewById(R.id.recommend);
                holder.schedule = (ImageButton) convertView
                        .findViewById(R.id.ImageButton01);

                holder.star = (ImageButton) convertView
                        .findViewById(R.id.ImageButton02);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //holder.orderTextView.setText(String.valueOf(position + 1) + ".");
            if (pList.get(position).scheduled.compareTo("yes") == 0)
                holder.schedule.setImageResource(R.drawable.yes_schedule);
            else
                holder.schedule.setImageResource(R.drawable.no_schedule);

            holder.schedule.setOnClickListener(this);
            holder.schedule.setTag(pList.get(position).id + ";" + position);
            if (pList.get(position).starred.compareTo("yes") == 0)
                holder.star.setImageResource(R.drawable.yes_star);
            else
                holder.star.setImageResource(R.drawable.no_star);
            /*if (pList.get(position).recommended.compareTo("yes") == 0)
                holder.recommend.setVisibility(View.VISIBLE);
    		else
    			holder.recommend.setVisibility(View.GONE);*/
            holder.star.setOnClickListener(this);
            holder.star.setTag(pList.get(position).presentationID + ";" + position);
            if (pList.get(position).recommended.compareTo("yes") == 0)
                holder.nameTextView.setText(Html.fromHtml(pList.get(position).title + "<font color=\"#ff0000\"> &lt;Recommended&gt; </font>"));
            else
                holder.nameTextView.setText(pList.get(position).title);
            holder.nameTextView.setTag(position);
            holder.author.setText(pList.get(position).authors);
            holder.type.setTextColor(R.drawable.Red);
            holder.type.setText(pList.get(position).type);
            try {
                beginDate = sdfSource.parse(pList.get(position).exactbeginTime);
                endDate = sdfSource.parse(pList.get(position).exactendTime);
                begTime = sdfDestination.format(beginDate);
                endTime = sdfDestination.format(endDate);
                holder.time.setText(pList.get(position).date + "\t" + begTime + " - " + endTime);
            } catch (Exception e) {
                System.out.println("Date Exception");
            }
            int idx = position - 1;

            String previewChar = idx >= 0 ? pList.get(idx).type : " ";
            String currentChar = pList.get(position).type;

            if (currentChar.compareToIgnoreCase(previewChar) != 0) {
                holder.firstCharHintTextView.setVisibility(View.VISIBLE);
                holder.firstCharHintTextView.setText(pList.get(position).type);
            } else {

                holder.firstCharHintTextView.setVisibility(View.GONE);
            }
            return convertView;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            TextView tv;
            int index;
            switch (v.getId()) {
                case (R.id.title):
                    tv = (TextView) v;
                    index = Integer.parseInt(tv.getTag().toString());
//                    ProceedingsByType.this.finish();
                    windowManager.removeView(txtOverlay);
                    Intent in = new Intent(ProceedingsByType.this, PaperDetail.class);
                    in.putExtra("id", pList.get(index).id);
                    in.putExtra("title", pList.get(index).title);
                    in.putExtra("authors", pList.get(index).authors);
                    in.putExtra("date", pList.get(index).date);
                    in.putExtra("abstract", pList.get(index).paperAbstract);
                    in.putExtra("contentlink", pList.get(index).contentlink);
                    in.putExtra("bTime", pList.get(index).exactbeginTime);
                    in.putExtra("eTime", pList.get(index).exactendTime);
                    in.putExtra("room", pList.get(index).room);
                    in.putExtra("presentationID", pList.get(index).presentationID);
                    in.putExtra("activity", "ProceedingsByType");
                    in.putExtra("key", "no");
                    startActivity(in);
                    break;
                case (R.id.ImageButton01):
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
                case (R.id.ImageButton02):
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
                        pList.get(pos).starred = "yes";
                        adapter.notifyDataSetChanged();

                    }

                    break;
                default:
                    break;
            }

        }
    }
}