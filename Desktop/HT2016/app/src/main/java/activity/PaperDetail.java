package activity;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import edu.pitt.is.DH2016.R;

public class PaperDetail extends TabActivity {
    private final int MENU_HOME = Menu.FIRST;
    private final int MENU_TRACK = Menu.FIRST + 1;
    private final int MENU_SESSION = Menu.FIRST + 2;
    private final int MENU_STAR = Menu.FIRST + 3;
    private final int MENU_SCHEDULE = Menu.FIRST + 4;
    private final int MENU_RECOMMEND = Menu.FIRST + 5;
    private String key, activity, id, title, authors, pAbstract, pContent, pRoom, bTime, eTime, date, presentationID;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.proceedings);

        Intent intent;

        Bundle b = getIntent().getExtras();
        if (b != null) {
            id = b.getString("id");
            title = b.getString("title");
            authors = b.getString("authors");
            pAbstract = b.getString("abstract");
            pContent = b.getString("contentlink");
            pRoom = b.getString("room");
            bTime = b.getString("bTime");
            eTime = b.getString("eTime");
            date = b.getString("date");
            presentationID = b.getString("presentationID");
            if (b.getString("activity") != null)
                activity = b.getString("activity");
            else
                activity = "no";
            if (b.getString("key") != null)
                key = b.getString("key");
            else
                key = "no";
        }
        // Set up the tabs
        TabHost host = getTabHost();//(TabHost) findViewById(R.id.tabhost);
        //host.setup();

        //Detail tab
        intent = new Intent().setClass(this, PaperInfo.class);
        Bundle b1 = new Bundle();
        b1.putString("id", id);
        b1.putString("title", title);
        b1.putString("authors", authors);
        b1.putString("abstract", pAbstract);
        b1.putString("contentlink", pContent);
        b1.putString("room", pRoom);
        b1.putString("bTime", bTime);
        b1.putString("eTime", eTime);
        b1.putString("date", date);
        b1.putString("presentationID", presentationID);
        b1.putString("activity", activity);
        b1.putString("key", key);
        intent.putExtras(b1);
        TabSpec detail = host.newTabSpec("Detail");
        detail.setIndicator("Detail");
        detail.setContent(intent);
        host.addTab(detail);

        //Attending tab
        intent = new Intent().setClass(this, PaperAttending.class);
        intent.putExtras(b1);
        TabSpec attending = host.newTabSpec("Attending");
        attending.setIndicator("Attending");
        attending.setContent(intent);
        host.addTab(attending);

        //Similar tab
        intent = new Intent().setClass(this, PaperSimilar.class);
        intent.putExtras(b1);
        TabSpec similar = host.newTabSpec("Similar");
        similar.setIndicator("Similar");
        similar.setContent(intent);
        host.addTab(similar);
        //set up default tab
        host.setCurrentTabByTag("Detail");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_HOME, 0, "Home").setIcon(R.drawable.home);
        menu.add(0, MENU_TRACK, 0, "Proceedings").setIcon(R.drawable.proceedings);
        menu.add(0, MENU_SESSION, 0, "Schedule").setIcon(R.drawable.session);
        menu.add(0, MENU_STAR, 0, "My Favorite").setIcon(R.drawable.star);
        menu.add(0, MENU_SCHEDULE, 0, "My Schedule").setIcon(R.drawable.schedule);
        menu.add(0, MENU_RECOMMEND, 0, "Recommendation").setIcon(R.drawable.recommends);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent itemintent = new Intent();
        switch (item.getItemId()) {
            case MENU_HOME:
                this.finish();
                itemintent.setClass(PaperDetail.this, MainInterface.class);
                startActivity(itemintent);
                return true;
            case MENU_SESSION:
                this.finish();
                itemintent.setClass(PaperDetail.this, ProgramByDay.class);
                startActivity(itemintent);
                return true;
            case MENU_STAR:
                this.finish();
                itemintent.setClass(PaperDetail.this, MyStaredPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_TRACK:
                this.finish();
                itemintent.setClass(PaperDetail.this, Proceedings.class);
                startActivity(itemintent);
                return true;
            case MENU_SCHEDULE:
                this.finish();
                itemintent.setClass(PaperDetail.this, MyScheduledPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_RECOMMEND:
                this.finish();
                itemintent.setClass(PaperDetail.this, MyRecommendedPapers.class);
                startActivity(itemintent);
                return true;
        }
        return false;
    }

}

