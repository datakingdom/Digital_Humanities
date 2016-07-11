package activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
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

import data.db.DBAdapter;
import data.model.Keynote;
import edu.pitt.is.HT2015.R;

public class KeyNote extends Activity {

    private final int MENU_HOME = Menu.FIRST;
    private final int MENU_TRACK = Menu.FIRST + 1;
    private final int MENU_SESSION = Menu.FIRST + 2;
    private final int MENU_STAR = Menu.FIRST + 3;
    private final int MENU_SCHEDULE = Menu.FIRST + 4;
    private final int MENU_RECOMMEND = Menu.FIRST + 5;
    ArrayList<Keynote> kList;
    private ListView lv1;
    private DBAdapter db;

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

        db = new DBAdapter(this);
        kList = new ArrayList<Keynote>();
        db.open();
        kList = db.getKeynoteByDay();
        db.close();


        lv1 = (ListView) findViewById(R.id.ListView01);
        ListViewAdapter adapter = new ListViewAdapter(kList);
        lv1.setAdapter(adapter);
        lv1.setOnItemClickListener(adapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.finish();
            Intent in = new Intent(KeyNote.this, MainInterface.class);
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
        menu.add(0, MENU_RECOMMEND, 0, "Recommendation").setIcon(R.drawable.recommends);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent itemintent = new Intent();
        switch (item.getItemId()) {
            case MENU_HOME:
                this.finish();
                itemintent.setClass(KeyNote.this, MainInterface.class);
                startActivity(itemintent);
                return true;
            case MENU_SESSION:
                this.finish();
                itemintent.setClass(KeyNote.this, ProgramByDay.class);
                startActivity(itemintent);
                return true;
            case MENU_STAR:
                this.finish();
                itemintent.setClass(KeyNote.this, MyStaredPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_TRACK:
                this.finish();
                itemintent.setClass(KeyNote.this, Proceedings.class);
                startActivity(itemintent);
                return true;
            case MENU_SCHEDULE:
                this.finish();
                itemintent.setClass(KeyNote.this, MyScheduledPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_RECOMMEND:
                this.finish();
                itemintent.setClass(KeyNote.this, MyRecommendedPapers.class);
                startActivity(itemintent);
                return true;
        }
        return false;
    }

    public final class ViewHolder {
        public TextView firstCharHintTextView, title, location, time;
    }

    private class ListViewAdapter extends BaseAdapter implements OnItemClickListener {
        private ArrayList<Keynote> s;

        public ListViewAdapter(ArrayList<Keynote> sList) {
            this.s = sList;
        }

        public int getCount() {
            return s.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            SimpleDateFormat sdfSource = new SimpleDateFormat("HH:mm");
            SimpleDateFormat sdfDestination = new SimpleDateFormat("h:mm a");
            Date beginDate, endDate;
            String begTime, endTime;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.sessionitem, null);
                holder = new ViewHolder();
                holder.firstCharHintTextView = (TextView) convertView.findViewById(R.id.text_first_char_hint);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.location = (TextView) convertView.findViewById(R.id.location);
                holder.time = (TextView) convertView.findViewById(R.id.time);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                beginDate = sdfSource.parse(s.get(position).beginTime);
                endDate = sdfSource.parse(s.get(position).endTime);
                begTime = sdfDestination.format(beginDate);
                endTime = sdfDestination.format(endDate);
                holder.time.setVisibility(View.VISIBLE);
                holder.time.setText(begTime + " - " + endTime);
            } catch (Exception e) {
                System.out.println("Date Exception");
            }
            holder.title.setText(s.get(position).title);
            if (s.get(position).room.compareToIgnoreCase("NULL") == 0)
                holder.location.setVisibility(View.GONE);
            else {
                holder.location.setVisibility(View.VISIBLE);
                holder.location.setText("At " + s.get(position).room);
            }
            int idx = position - 1;

            String previewb = idx >= 0 ? s.get(idx).date : "";
            String currentb = s.get(position).date;

            if (currentb.compareTo(previewb) == 0) {
                holder.firstCharHintTextView.setVisibility(View.GONE);
            } else {

                holder.firstCharHintTextView.setVisibility(View.VISIBLE);
                holder.firstCharHintTextView.setText(s.get(position).date);
            }
            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                long arg3) {
            // TODO Auto-generated method stub
            Intent in = new Intent(KeyNote.this, KeynoteDetail.class);
            in.putExtra("keynoteName", s.get(pos).title);
            in.putExtra("keynoteDate", s.get(pos).date);
            in.putExtra("keynoteBegintime", s.get(pos).beginTime);
            in.putExtra("keynoteEndtime", s.get(pos).endTime);
            in.putExtra("keynoteRoom", s.get(pos).room);
            in.putExtra("keynoteSpeaker", s.get(pos).speakerName);
            in.putExtra("keynoteAffiliation", s.get(pos).speakerAffiliation);
            in.putExtra("keynoteDes", s.get(pos).description);
            startActivity(in);
        }

    }
}
