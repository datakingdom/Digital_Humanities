package activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import data.model.Conference;
import data.model.User;
import data.parser.UserProfileParse;
import data.parser.UserScheduleParse;
import edu.pitt.is.DH2016.R;

public class UserDetail extends Activity {

    private final int MENU_HOME = Menu.FIRST;
    private final int MENU_TRACK = Menu.FIRST + 1;
    private final int MENU_SESSION = Menu.FIRST + 2;
    private final int MENU_STAR = Menu.FIRST + 3;
    private final int MENU_SCHEDULE = Menu.FIRST + 4;
    private final int MENU_RECOMMEND = Menu.FIRST + 5;
    private String userID;
    private TextView name, position, organization, linkedin, country;
    private ProgressDialog pd;
    private UserProfileParse upp;
    private UserScheduleParse usp;
    private User u;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.userdetail);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            userID = b.getString("id");
        }
        u = new User();

        name = (TextView) findViewById(R.id.name);
        position = (TextView) findViewById(R.id.position);
        organization = (TextView) findViewById(R.id.organization);
        country = (TextView) findViewById(R.id.country);
        linkedin = (TextView) findViewById(R.id.linkedin);
        if (Conference.userSignin) {
            new AsyncUserProfile().execute(u);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Please log in first",
                    Toast.LENGTH_LONG)
                    .show();
            CallSignin();
        }


    }

    private void CallSignin() {
        this.finish();
        Intent in = new Intent(UserDetail.this, Signin.class);
        in.putExtra("activity", "UserDetail");
        startActivity(in);
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
                itemintent.setClass(UserDetail.this, MainInterface.class);
                startActivity(itemintent);
                return true;
            case MENU_SESSION:
                this.finish();
                itemintent.setClass(UserDetail.this, ProgramByDay.class);
                startActivity(itemintent);
                return true;
            case MENU_STAR:
                this.finish();
                itemintent.setClass(UserDetail.this, MyStaredPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_TRACK:
                this.finish();
                itemintent.setClass(UserDetail.this, Proceedings.class);
                startActivity(itemintent);
                return true;
            case MENU_SCHEDULE:
                this.finish();
                itemintent.setClass(UserDetail.this, MyScheduledPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_RECOMMEND:
                this.finish();
                itemintent.setClass(UserDetail.this, MyRecommendedPapers.class);
                startActivity(itemintent);
                return true;
        }
        return false;
    }

    public void showDialog(String s) {
        pd = ProgressDialog.show(this, "Synchronization", s, true, false);
    }

    public void dismissDialog() {
        pd.dismiss();
    }

    private class AsyncUserProfile extends AsyncTask<User, Void, Integer> {

        @Override
        protected void onPreExecute() {
            showDialog("Loading...");
        }

        @Override
        protected Integer doInBackground(User... params) {
            // TODO Auto-generated method stub
            upp = new UserProfileParse();
            u = params[0];
            u = upp.getUser(userID);

            //usp = new UserScheduleParse();
            //paperidList= usp.getData(userID);

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
                            Toast.LENGTH_LONG)
                            .show();
                    if (u.getname() != null)
                        name.setText(u.getname());
                    if (u.getposition() == null || u.getposition().compareTo("") == 0 || u.getposition().compareToIgnoreCase("null") == 0)
                        position.setText("No position information available");
                    else
                        position.setText(u.getposition());
                    if (u.getorganization() == null || u.getorganization().compareTo("") == 0 || u.getorganization().compareToIgnoreCase("null") == 0)
                        organization.setText("No organization information available");
                    else
                        organization.setText(u.getorganization());
                    if (u.getcountry() == null || u.getcountry().compareTo("") == 0 || u.getcountry().compareToIgnoreCase("null") == 0)
                        country.setText("No country information available");
                    else
                        country.setText(u.getcountry());
                    if (u.getlinkedin() == null || u.getlinkedin().compareTo("") == 0 || u.getlinkedin().compareToIgnoreCase("null") == 0)
                        linkedin.setText("No linkedin information available");
                    else
                        linkedin.setText(u.getlinkedin());
                    break;
                default:
                    dismissDialog();
                    Toast.makeText(getApplicationContext(),
                            "Error!",
                            Toast.LENGTH_LONG)
                            .show();
                    if (u.getname() != null)
                        name.setText(u.getname());
                    if (u.getposition() == null || u.getposition().compareTo("") == 0 || u.getposition().compareToIgnoreCase("null") == 0)
                        position.setText("No position information available");
                    else
                        position.setText(u.getposition());
                    if (u.getorganization() == null || u.getorganization().compareTo("") == 0 || u.getorganization().compareToIgnoreCase("null") == 0)
                        organization.setText("No organization information available");
                    else
                        organization.setText(u.getorganization());
                    if (u.getcountry() == null || u.getcountry().compareTo("") == 0 || u.getcountry().compareToIgnoreCase("null") == 0)
                        country.setText("No country information available");
                    else
                        country.setText(u.getcountry());
                    if (u.getlinkedin() == null || u.getlinkedin().compareTo("") == 0 || u.getlinkedin().compareToIgnoreCase("null") == 0)
                        linkedin.setText("No linkedin information available");
                    else
                        linkedin.setText(u.getlinkedin());
                    break;
            }
        }
    }
}
