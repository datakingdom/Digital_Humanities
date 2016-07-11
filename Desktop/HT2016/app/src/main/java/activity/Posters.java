package activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import data.db.DBAdapter;
import data.model.Paper;
import data.model.Poster;
import data.model.Session;
import edu.pitt.is.HT2015.R;

public class Posters extends Activity {
    private final int MENU_HOME = Menu.FIRST;
    private final int MENU_TRACK = Menu.FIRST + 1;
    private final int MENU_SESSION = Menu.FIRST + 2;
    private final int MENU_STAR = Menu.FIRST + 3;
    private final int MENU_SCHEDULE = Menu.FIRST + 4;
    private ArrayList<Session> sList;
    private ArrayList<Paper> pList;
    private DBAdapter db;
    private ListView lv;
    private HashMap<String, String> sessionMap = new HashMap<String, String>();
    private ArrayList<Session> list = new ArrayList<Session>();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.track);

        ListViewAdapter adapter;
        db = new DBAdapter(this);
        db.open();

        ArrayList<Poster> poList = new ArrayList<Poster>();
        poList = db.getPoster();


        // get Poster session list
        sList = new ArrayList<Session>();
        HashSet<String> eventSessionIDSet = new HashSet<String>();

        for (int i = 0; i < poList.size(); i++) {
            String id = poList.get(i).eventSessionID;
            if (!eventSessionIDSet.contains(id)) {
                eventSessionIDSet.add(id);
            }
        }

        // get session by dayid
        for (int i = 0; i < 5; i++) {
            ArrayList<Session> tmpList = db.getSessionBydayID(String.valueOf(i));
            for (int j = 0; j < tmpList.size(); j++) {
                String id = tmpList.get(j).ID;
                if (eventSessionIDSet.contains(id)) {
                    sList.add(tmpList.get(j));
                }
            }
        }
        db.close();

        for (Session session : sList) {
            if (sessionMap.containsKey(session.name)) {
                String eventSessionIDList = sessionMap.get(session.name);
                StringBuilder sb = new StringBuilder(eventSessionIDList);
                sb.append(";" + session.ID);
                sessionMap.put(session.name, sb.toString());
            } else {
                sessionMap.put(session.name, session.ID);
                list.add(session);
            }
        }

        adapter = new ListViewAdapter(sList);

        TextView tv = (TextView) findViewById(R.id.TextView01);
        tv.setText("Posters");

        lv = (ListView) findViewById(R.id.ListView01);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView av, View v, int pos, long arg) {

                Intent in = new Intent(Posters.this, PosterDetail.class);
                String sessionName = list.get(pos).name;
                String eventSessionIDList = sessionMap.get(sessionName);
                in.putExtra("eventSessionIDList", eventSessionIDList);

                startActivity(in);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.finish();
            Intent in = new Intent(Posters.this, MainInterface.class);
            startActivity(in);
        }

        return super.onKeyDown(keyCode, event);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_HOME, 0, "Home").setIcon(R.drawable.home);
        menu.add(0, MENU_TRACK, 0, "Proceedings").setIcon(R.drawable.proceedings);
        menu.add(0, MENU_SESSION, 0, "Schedule").setIcon(R.drawable.session);
        menu.add(0, MENU_STAR, 0, "My Favourite").setIcon(R.drawable.star);
        menu.add(0, MENU_SCHEDULE, 0, "My Schedule").setIcon(R.drawable.schedule);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent itemintent = new Intent();
        switch (item.getItemId()) {
            case MENU_HOME:
                this.finish();
                itemintent.setClass(Posters.this, MainInterface.class);
                startActivity(itemintent);
                return true;
            case MENU_SESSION:
                this.finish();
                itemintent.setClass(Posters.this, ProgramByDay.class);
                startActivity(itemintent);
                return true;
            case MENU_STAR:
                this.finish();
                itemintent.setClass(Posters.this, MyStaredPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_TRACK:
                this.finish();
                itemintent.setClass(Posters.this, Proceedings.class);
                startActivity(itemintent);
                return true;
            case MENU_SCHEDULE:
                this.finish();
                itemintent.setClass(Posters.this, MyScheduledPapers.class);
                startActivity(itemintent);
                return true;
        }
        return false;
    }

    static class ViewHolder {
        TextView t1, t2, t3, firstCharHintTextView;
    }

    private class ListViewAdapter extends BaseAdapter {
        ArrayList<Session> list;

        public ListViewAdapter(ArrayList w) {
            this.list = w;
        }

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder v = null;
            SimpleDateFormat sdfSource = new SimpleDateFormat("HH:mm");
            SimpleDateFormat sdfDestination = new SimpleDateFormat("h:mm a");
            Date beginDate, endDate;
            String begTime, endTime;
            if (convertView == null) {
                LayoutInflater li = getLayoutInflater();
                convertView = li.inflate(R.layout.sessionitem, null);
                v = new ViewHolder();
                v.t1 = (TextView) convertView.findViewById(R.id.title);
                v.t2 = (TextView) convertView.findViewById(R.id.time);
                v.t3 = (TextView) convertView.findViewById(R.id.location);
                v.firstCharHintTextView = (TextView) convertView.findViewById(R.id.text_first_char_hint);
                convertView.setTag(v);
            } else {
                v = (ViewHolder) convertView.getTag();
            }
            try {
                beginDate = sdfSource.parse(list.get(position).beginTime);
                endDate = sdfSource.parse(list.get(position).endTime);
                begTime = sdfDestination.format(beginDate);
                endTime = sdfDestination.format(endDate);
                v.t2.setVisibility(View.VISIBLE);
                v.t2.setText(begTime + " - " + endTime);
            } catch (Exception e) {
                System.out.println("Date Exception");
            }
            v.t1.setText(list.get(position).name);
            if (list.get(position).room.compareToIgnoreCase("NULL") == 0)
                v.t3.setVisibility(View.GONE);
            else {
                v.t3.setVisibility(View.VISIBLE);
                v.t3.setText("At " + list.get(position).room);
            }
            int idx = position - 1;

            String preview = idx >= 0 ? list.get(idx).date : "";
            String current = list.get(position).date;

            if (current.compareTo(preview) == 0) {
                v.firstCharHintTextView.setVisibility(View.GONE);
            } else {

                v.firstCharHintTextView.setVisibility(View.VISIBLE);
                v.firstCharHintTextView.setText(list.get(position).date);
            }
            return convertView;
        }
    }
}


