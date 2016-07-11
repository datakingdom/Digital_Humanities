package activity;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import edu.pitt.is.HT2015.R;

public class Proceedings extends TabActivity {

    private final int MENU_HOME = Menu.FIRST;
    private final int MENU_TRACK = Menu.FIRST + 1;
    private final int MENU_STAR = Menu.FIRST + 2;
    private final int MENU_SCHEDULE = Menu.FIRST + 3;

    private int tabno;
    private TabHost host;

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

        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.getString("no") != null)
                tabno = Integer.parseInt(b.getString("no"));
            else
                tabno = 1;
        }
        Intent intent;

        // Set up the tabs
        host = getTabHost();//(TabHost) findViewById(R.id.tabhost);
        //host.setup();

        // 1st day tab
        intent = new Intent().setClass(this, ProceedingsByAuthor.class);
        TabSpec day1 = host.newTabSpec("Author");
        day1.setIndicator("Author");
        day1.setContent(intent);
        host.addTab(day1);

        //2nd day tab
        intent = new Intent().setClass(this, ProceedingsByName.class);
        TabSpec day2 = host.newTabSpec("Title");
        day2.setIndicator("Title");
        day2.setContent(intent);
        host.addTab(day2);

        // 3rd day tab
        intent = new Intent().setClass(this, ProceedingsByType.class);
        TabSpec day3 = host.newTabSpec("Type");
        day3.setIndicator("Type");
        day3.setContent(intent);
        host.addTab(day3);
        //set up default tab
        Changetab(tabno);

    }

    private void Changetab(int request) {
        switch (request) {
            case 1:
                host.setCurrentTabByTag("Author");
                break;
            case 2:
                host.setCurrentTabByTag("Title");
                break;
            case 3:
                host.setCurrentTabByTag("Type");
                break;
            default:
                host.setCurrentTabByTag("Author");
                break;
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_HOME, 0, "Home").setIcon(R.drawable.home);
        menu.add(0, MENU_TRACK, 0, "Schedule").setIcon(R.drawable.session);
        menu.add(0, MENU_STAR, 0, "My Favorite").setIcon(R.drawable.star);
        menu.add(0, MENU_SCHEDULE, 0, "My Schedule").setIcon(R.drawable.schedule);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent itemintent = new Intent();
        switch (item.getItemId()) {
            case MENU_HOME:
                this.finish();
                itemintent.setClass(Proceedings.this, MainInterface.class);
                startActivity(itemintent);
                return true;
            case MENU_STAR:
                this.finish();
                itemintent.setClass(Proceedings.this, MyStaredPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_TRACK:
                this.finish();
                itemintent.setClass(Proceedings.this, ProgramByDay.class);
                startActivity(itemintent);
                return true;
            case MENU_SCHEDULE:
                this.finish();
                itemintent.setClass(Proceedings.this, MyScheduledPapers.class);
                startActivity(itemintent);
                return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            //this.finish(); this.onStop(); this.onDestroy();
            //this.finish();
            this.finish();

            Intent in = new Intent(Proceedings.this, MainInterface.class);
            startActivity(in);
        }

        return super.onKeyDown(keyCode, event);
    }
}