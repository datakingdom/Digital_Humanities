package activity;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.pitt.is.DH2016.R;

public class KeynoteDetail extends Activity {
    private final int MENU_HOME = Menu.FIRST;
    private final int MENU_TRACK = Menu.FIRST + 1;
    private final int MENU_SESSION = Menu.FIRST + 2;
    private final int MENU_STAR = Menu.FIRST + 3;
    private final int MENU_SCHEDULE = Menu.FIRST + 4;
    private final int MENU_RECOMMEND = Menu.FIRST + 5;
    private TextView tw1, tw2, tw3, tw6, tw7;
    private Button googlemap;
    private WebView wv;
    private RelativeLayout rl;
    private String title, date, beginTime, kendTime, room, description, speaker, Affiliation;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.conference_general);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            title = b.getString("keynoteName");
            date = b.getString("keynoteDate");
            beginTime = b.getString("keynoteBegintime");
            kendTime = b.getString("keynoteEndtime");
            room = b.getString("keynoteRoom");
            description = b.getString("keynoteDes");
            speaker = b.getString("keynoteSpeaker");
            Affiliation = b.getString("keynoteAffiliation");
        }

        tw1 = (TextView) this.findViewById(R.id.TextView01);
        tw1.setText(title);

        SimpleDateFormat sdfSource = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdfDestination = new SimpleDateFormat("h:mm a");
        Date beginDate, endDate;
        String begTime, endTime;
        try {
            beginDate = sdfSource.parse(beginTime);
            endDate = sdfSource.parse(kendTime);
            begTime = sdfDestination.format(beginDate);
            endTime = sdfDestination.format(endDate);
            tw2 = (TextView) this.findViewById(R.id.TextView02);
            tw2.setText(date + "\t" + begTime + "-" + endTime);
        } catch (Exception e) {
            System.out.println("Date Exception");
        }

        tw3 = (TextView) this.findViewById(R.id.TextView04);
        if (room == null || "null".compareToIgnoreCase(room) == 0 || "".compareTo(room) == 0 || "N/A".compareToIgnoreCase(room) == 0)
            tw3.setVisibility(View.GONE);
        else {
            tw3.setText(room);
        }

        rl = (RelativeLayout) this.findViewById(R.id.LinearLayout02);
        rl.setVisibility(View.VISIBLE);
        tw6 = (TextView) this.findViewById(R.id.TextView06);
        tw6.setText(speaker);
        tw7 = (TextView) this.findViewById(R.id.TextView07);
        if (Affiliation.compareTo("") != 0)
            tw7.setText(Affiliation);
        else {
            tw7.setVisibility(View.GONE);
        }

        wv = (WebView) this.findViewById(R.id.WebView01);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadData(description, "text/html", "utf-8");

        googlemap = (Button) findViewById(R.id.map);
        googlemap.setOnClickListener(new View.OnClickListener() {

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
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.finish();
            Intent in = new Intent(KeynoteDetail.this, KeyNote.class);
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
                itemintent.setClass(KeynoteDetail.this, MainInterface.class);
                startActivity(itemintent);
                return true;
            case MENU_SESSION:
                this.finish();
                itemintent.setClass(KeynoteDetail.this, ProgramByDay.class);
                startActivity(itemintent);
                return true;
            case MENU_STAR:
                this.finish();
                itemintent.setClass(KeynoteDetail.this, MyStaredPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_TRACK:
                this.finish();
                itemintent.setClass(KeynoteDetail.this, Proceedings.class);
                startActivity(itemintent);
                return true;
            case MENU_SCHEDULE:
                this.finish();
                itemintent.setClass(KeynoteDetail.this, MyScheduledPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_RECOMMEND:
                this.finish();
                itemintent.setClass(KeynoteDetail.this, MyRecommendedPapers.class);
                startActivity(itemintent);
                return true;
        }
        return false;
    }
}
