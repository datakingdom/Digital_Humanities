package activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Hashtable;

import data.UserBeFriend;
import data.model.Conference;
import data.model.User;
import data.parser.UserProfileParse;
import data.parser.UserScheduleParse;
import edu.pitt.is.HT2015.R;

public class FriendsDetail extends Activity {

    private String userID, status;
    private TextView name, position, organization, linkedin, country, statusdis;
    private Button action;
    private ProgressDialog pd;
    private UserProfileParse upp;
    private UserScheduleParse usp;
    private UserBeFriend ubf;
    private User u;
    private Hashtable<String, String> idToStatus;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.userdetail);

        this.idtostatus();
        Bundle b = getIntent().getExtras();
        if (b != null) {
            userID = b.getString("id");
            status = b.getString("status");
        }
        u = new User();

        name = (TextView) findViewById(R.id.name);
        position = (TextView) findViewById(R.id.position);
        organization = (TextView) findViewById(R.id.organization);
        country = (TextView) findViewById(R.id.country);
        linkedin = (TextView) findViewById(R.id.linkedin);
        action = (Button) findViewById(R.id.action);
        statusdis = (TextView) findViewById(R.id.status);
        action.setVisibility(View.VISIBLE);
        statusdis.setVisibility(View.VISIBLE);

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
        Intent in = new Intent(FriendsDetail.this, Signin.class);
        in.putExtra("activity", "FriendsDetail");
        startActivity(in);
    }

    private void idtostatus() {
        // TODO Auto-generated method stub
        idToStatus = new Hashtable<String, String>();
        idToStatus.put("0", "Error occurs during processing");
        idToStatus.put("1", "waiting for confirmation from");
        idToStatus.put("2", "Rejected by");
        idToStatus.put("3", "Already connected with");
        idToStatus.put("4", "Already removed");
    }

    public void showDialog(String s) {
        pd = ProgressDialog.show(this, "Synchronization", s, true, false);
    }

    public void dismissDialog() {
        pd.dismiss();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            //this.finish(); this.onStop(); this.onDestroy();
            this.finish();
            Intent in = new Intent(FriendsDetail.this, MyFriends.class);
            startActivity(in);
        }

        return super.onKeyDown(keyCode, event);
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
                    statusdis.setText(idToStatus.get(status));
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
                    if (status.compareTo("1") == 0 || status.compareTo("3") == 0)
                        action.setText("Remove this connection");
                    else if (status.compareTo("2") == 0 || status.compareTo("4") == 0)
                        action.setText("Reconnect to this person");
                    action.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            if (status.compareTo("1") == 0 || status.compareTo("3") == 0) {
                                ubf = new UserBeFriend();
                                status = ubf.getStatus(userID, "4");
                                statusdis.setText(idToStatus.get(status));
                                action.setText("Reconnect to this person");
                            } else if (status.compareTo("2") == 0 || status.compareTo("4") == 0) {
                                ubf = new UserBeFriend();
                                status = ubf.getStatus(userID, "1");
                                statusdis.setText(idToStatus.get(status));
                                action.setText("Remove this connection");

                            }
                        }

                    });
                    break;
                default:
                    dismissDialog();
                    Toast.makeText(getApplicationContext(),
                            "Error!",
                            Toast.LENGTH_LONG)
                            .show();
                    statusdis.setText(idToStatus.get(status));
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
                    if (status.compareTo("1") == 0 || status.compareTo("3") == 0)
                        action.setText("Remove this connection");
                    else if (status.compareTo("2") == 0 || status.compareTo("4") == 0)
                        action.setText("Reconnect to this person");
                    action.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            if (status.compareTo("1") == 0 || status.compareTo("3") == 0) {
                                ubf = new UserBeFriend();
                                status = ubf.getStatus(userID, "4");
                                statusdis.setText(idToStatus.get(status));
                                action.setText("Reconnect to this person");
                            } else if (status.compareTo("2") == 0 || status.compareTo("4") == 0) {
                                ubf = new UserBeFriend();
                                status = ubf.getStatus(userID, "1");
                                statusdis.setText(idToStatus.get(status));
                                action.setText("Remove this connection");
                            }
                        }

                    });
                    break;
            }
        }
    }
}

