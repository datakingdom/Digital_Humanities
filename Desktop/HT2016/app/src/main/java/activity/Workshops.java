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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import data.db.DBAdapter;
import data.model.Session;
import data.model.Workshop;
import edu.pitt.is.DH2016.R;

public class Workshops extends Activity {
    private final int MENU_HOME = Menu.FIRST;
    private final int MENU_TRACK = Menu.FIRST + 1;
    private final int MENU_SESSION = Menu.FIRST + 2;
    private final int MENU_STAR = Menu.FIRST + 3;
    private final int MENU_SCHEDULE = Menu.FIRST + 4;
    private ArrayList<Session> sList;
    private DBAdapter db;
    private ListView lv;
    private TextView t1;
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

        t1 = (TextView) findViewById(R.id.TextView01);
        t1.setText("Workshops");

        ListViewAdapter adapter;
        db = new DBAdapter(this);
        db.open();

        ArrayList<Workshop> wList = new ArrayList<Workshop>();
        wList = db.getWorkshopsDes();


        // get workshop session list
        sList = new ArrayList<Session>();
        HashSet<String> eventSessionIDSet = new HashSet<String>();

        for (int i = 0; i < wList.size(); i++) {
            String id = wList.get(i).eventSessionID;
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

        adapter = new ListViewAdapter(list);

        lv = (ListView) findViewById(R.id.ListView01);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView av, View v, int pos, long arg) {

                Intent in = new Intent(Workshops.this, WorkshopDetail.class);
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
            Intent in = new Intent(Workshops.this, MainInterface.class);
            startActivity(in);
        }

        return super.onKeyDown(keyCode, event);
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
                itemintent.setClass(Workshops.this, MainInterface.class);
                startActivity(itemintent);
                return true;
            case MENU_SESSION:
                this.finish();
                itemintent.setClass(Workshops.this, ProgramByDay.class);
                startActivity(itemintent);
                return true;
            case MENU_STAR:
                this.finish();
                itemintent.setClass(Workshops.this, MyStaredPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_TRACK:
                this.finish();
                itemintent.setClass(Workshops.this, Proceedings.class);
                startActivity(itemintent);
                return true;
            case MENU_SCHEDULE:
                this.finish();
                itemintent.setClass(Workshops.this, MyScheduledPapers.class);
                startActivity(itemintent);
                return true;
        }
        return false;
    }

    static class ViewHolder {
        TextView t1, t2, t3, firstCharHintTextView;
    }

    private class ListViewAdapter extends BaseAdapter {
        ArrayList<Session> sList;

        public ListViewAdapter(ArrayList s) {
            this.sList = s;
        }

        public int getCount() {
            return sList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder v = null;
            if (convertView == null) {
                LayoutInflater li = getLayoutInflater();
                convertView = li.inflate(R.layout.sessionitem, null);
                v = new ViewHolder();
                v.t1 = (TextView) convertView.findViewById(R.id.title);
                v.t3 = (TextView) convertView.findViewById(R.id.location);
                v.firstCharHintTextView = (TextView) convertView.findViewById(R.id.text_first_char_hint);
                convertView.setTag(v);
            } else {
                v = (ViewHolder) convertView.getTag();
            }
            v.t1.setText(sList.get(position).name);
            System.out.println(sList.get(position).room + ")))))))))))))))))))))))");
            if (sList.get(position).room == null || "null".compareToIgnoreCase(sList.get(position).room) == 0 || "".equals(sList.get(position).room))
                v.t3.setVisibility(View.GONE);
            else {
                v.t3.setText("At " + sList.get(position).room);
            }

            int idx = position - 1;

            String preview = idx >= 0 ? sList.get(idx).date : "";
            String current = sList.get(position).date;

            if (current.compareTo(preview) == 0) {
                v.firstCharHintTextView.setVisibility(View.GONE);
            } else {

                v.firstCharHintTextView.setVisibility(View.VISIBLE);
                v.firstCharHintTextView.setText(sList.get(position).date);
            }
            return convertView;
        }
    }
}
