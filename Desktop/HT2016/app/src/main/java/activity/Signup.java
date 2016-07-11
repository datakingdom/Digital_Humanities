package activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.Authorization;
import data.UserScheduledToServer;
import data.db.DBAdapter;
import data.model.Conference;
import data.parser.UserScheduleParse;
import edu.pitt.is.DH2016.R;

public class Signup extends Activity implements Runnable {

    private static final int ERRORDIALOG = 1; // Signup error
    private static final int PERRORDIALOG = 2; // password not match
    private static final int NERRORDIALOG = 3; // no name
    private static final int EERRORDIALOG = 4; // no email
    private static final int NPERRORDIALOG = 5; // no password
    private static final int NOINTERNET = 6; // no internet
    private static boolean isConnected = true;
    private EditText nameText, emailText, passwordText, rePasswordText, citeULikeText;
    private Button registerButton, signinButton;
    private String name, email, password, rPassword;
    private String citeULike = "null";
    private boolean registerOK = false;
    private ProgressDialog pd;
    private Authorization au;
    private String activityName = "";
    private String papersessionID, sessionName, sessionBTime, sessionETime,
            sessionDate, paperID = "";
    private String paperTitle, paperbTime, papereTime, paperAbstract, paperAuthors, room = "";
    private String workshopID, workshopTitle, content, workshopDate, eventSessionIDList = "";
    private String trackID, trackName = "";
    private DBAdapter db;
    private String date = "";
    private String presentationID = "";
    private String contentID = "";
    private String wbtime = "";
    private String wetime = "";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pd.dismiss();
            showSignupResult();
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

        setContentView(R.layout.registration);

        Bundle b = getIntent().getExtras();
        if (b != null) {

            activityName = b.getString("activity");
            if (activityName.compareTo("PaperInSession") == 0) {
                papersessionID = b.getString("papersessionID");
                sessionName = b.getString("sessionName");
                sessionBTime = b.getString("bTime");
                sessionETime = b.getString("eTime");
                sessionDate = b.getString("date");
                paperID = b.getString("paperID");
            } else if (activityName.compareTo("PaperInSchedule") == 0) {
                papersessionID = b.getString("papersessionID");
                sessionName = b.getString("sessionName");
                sessionBTime = b.getString("bTime");
                sessionETime = b.getString("eTime");
                sessionDate = b.getString("date");
                paperID = b.getString("paperID");
            } else if (activityName.compareTo("PaperInfo") == 0) {
                paperID = b.getString("paperID");
                paperTitle = b.getString("title");
                paperbTime = b.getString("bTime");
                papereTime = b.getString("eTime");
                paperAbstract = b.getString("abstract");
                paperAuthors = b.getString("authors");
                date = b.getString("date");
                room = b.getString("room");
                presentationID = b.getString("presentationID");
            } else if (activityName.compareTo("PaperSimilar") == 0) {
                paperID = b.getString("paperID");
                contentID = b.getString("id");
                paperTitle = b.getString("title");
                paperbTime = b.getString("bTime");
                papereTime = b.getString("eTime");
                paperAbstract = b.getString("abstract");
                paperAuthors = b.getString("authors");
                date = b.getString("date");
                room = b.getString("room");
                presentationID = b.getString("presentationID");
            } else if (activityName.compareTo("MyStaredPapers") == 0) {
                paperID = b.getString("paperID");
            } else if (activityName.compareTo("MyRecommendedPapers") == 0) {
                paperID = b.getString("paperID");
            } else if (activityName.compareTo("ProceedingsByAuthor") == 0) {
                paperID = b.getString("paperID");
            } else if (activityName.compareTo("ProceedingsByName") == 0) {
                paperID = b.getString("paperID");
            } else if (activityName.compareTo("ProceedingsByType") == 0) {
                paperID = b.getString("paperID");
            } else if (activityName.compareTo("WorkshopDetail") == 0) {
                paperID = b.getString("paperID");
                workshopID = b.getString("id");
                workshopTitle = b.getString("wtitle");
                room = b.getString("room");
                eventSessionIDList = b.getString("eventSessionIDList");
            } else if (activityName.compareTo("PosterDetail") == 0) {
                workshopID = b.getString("wid");
                workshopTitle = b.getString("wtitle");
                room = b.getString("room");
                eventSessionIDList = b.getString("eventSessionIDList");
            } else {

            }
        }

        nameText = (EditText) findViewById(R.id.NameText);
        emailText = (EditText) findViewById(R.id.REmailText);
        passwordText = (EditText) findViewById(R.id.RPasswordText);
        rePasswordText = (EditText) findViewById(R.id.RePasswordText);
        citeULikeText = (EditText) findViewById(R.id.CiteULikeUser);
        registerButton = (Button) findViewById(R.id.RegisterButton);
        signinButton = (Button) findViewById(R.id.SigninButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                register();
            }
        });

        signinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toSignIn();
            }
        });

    }

    private void updatePaperStatus(String id) {
        UserScheduledToServer us2s = new UserScheduledToServer();
        String paperStatus = us2s.addScheduledPaper2Sever(id);
        if (paperStatus.compareTo("no") == 0)
            paperStatus = us2s.addScheduledPaper2Sever(id);
    }

    public void run() {
        /* add authorization method below
         *
		 *
		 *
		 */
        postToServer();
        handler.sendEmptyMessage(0);
    }

    private boolean checkEmail(String mail) {
        String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(mail);
        return m.find();
    }

    private void register() {
        name = nameText.getText().toString();
        email = emailText.getText().toString();
        password = passwordText.getText().toString();
        rPassword = rePasswordText.getText().toString();
        citeULike = citeULikeText.getText().toString();

        if (name.compareTo("") == 0) {
            showDialog(NERRORDIALOG);
        } else if (!checkEmail(email)) {
            showDialog(EERRORDIALOG);
        } else if (password.compareTo("") == 0) {
            showDialog(NPERRORDIALOG);
        } else if (password.compareTo(rPassword) != 0) {
            showDialog(PERRORDIALOG);
        } else {
            pd = ProgressDialog.show(this, "Signing up", "Please Wait...",
                    true, false);
            Thread thread = new Thread(this);
            thread.start();
        }
    }

    private void toSignIn() {
        Intent in = new Intent();
        in.setClass(Signup.this, Signin.class);

        in.putExtra("activity", activityName);
        in.putExtra("paperID", paperID);
        if (activityName.compareTo("PaperInSession") == 0) {
            in.putExtra("papersessionID", papersessionID);
            in.putExtra("sessionName", sessionName);
            in.putExtra("bTime", sessionBTime);
            in.putExtra("eTime", sessionETime);
            in.putExtra("date", sessionDate);
        } else if (activityName.compareTo("PaperInfo") == 0) {
            in.putExtra("title", paperTitle);
            in.putExtra("bTime", paperbTime);
            in.putExtra("eTime", papereTime);
            in.putExtra("abstract", paperAbstract);
            in.putExtra("authors", paperAuthors);
            in.putExtra("room", room);
            in.putExtra("date", date);
            in.putExtra("presentationID", presentationID);
        } else if (activityName.compareTo("PaperSimilar") == 0) {
            in.putExtra("id", contentID);
            in.putExtra("title", paperTitle);
            in.putExtra("bTime", paperbTime);
            in.putExtra("eTime", papereTime);
            in.putExtra("abstract", paperAbstract);
            in.putExtra("authors", paperAuthors);
            in.putExtra("room", room);
            in.putExtra("date", date);
            in.putExtra("presentationID", presentationID);
        } else if (activityName.compareTo("MyStaredPapers") == 0) {
        } else if (activityName.compareTo("MyRecommendedPapers") == 0) {
        } else if (activityName.compareTo("PaperInSchedule") == 0) {
            in.putExtra("papersessionID", papersessionID);
            in.putExtra("sessionName", sessionName);
            in.putExtra("bTime", sessionBTime);
            in.putExtra("eTime", sessionETime);
            in.putExtra("date", sessionDate);
        } else if (activityName.compareTo("ProceedingsByAuthor") == 0) {

        } else if (activityName.compareTo("ProceedingsByName") == 0) {

        } else if (activityName.compareTo("ProceedingsByType") == 0) {

        } else if (activityName.compareTo("WorkshopDetail") == 0) {
            in.putExtra("id", workshopID);
            in.putExtra("wtitle", workshopTitle);
            in.putExtra("paperID", paperID);
            in.putExtra("room", room);
            in.putExtra("eventSessionIDList", eventSessionIDList);
        } else if (activityName.compareTo("PosterDetail") == 0) {
            in.putExtra("paperID", workshopID);
            in.putExtra("title", workshopTitle);
            in.putExtra("room", room);
            in.putExtra("eventSessionIDList", eventSessionIDList);
        } else {

        }
        startActivity(in);
    }

    private void syncDB() {
        ArrayList<String> pidList = new ArrayList<String>();
        UserScheduleParse usp = new UserScheduleParse();
        pidList = usp.getData();
        db = new DBAdapter(this);
        db.open();
        db.updatePaperScheduleToDefault();
        db.deleteUserScheduled();

        for (int i = 0; i < pidList.size(); i++) {
            db.insertMyScheduledPaper(pidList.get(i).toString());
            db.updatePaperBySchedule(pidList.get(i).toString(), "yes");
        }

        db.close();
    }

    private void postToServer() {
        // add authorization method below
        if (isConnect(this)) {
            au = new Authorization();
            au.signUp(name, email, password, rPassword, citeULike);
            registerOK = au.isSignUp;
            isConnected = true;
        } else {
            isConnected = false;
        }
    }

    private void showSignupResult() {
        if (!isConnected) {
            showDialog(NOINTERNET);
        } else if (!registerOK) {
            showDialog(ERRORDIALOG);
        } else {
            /***
             * after successfully register, a file should be created to store email and password
             * then call authorization to login
             */


            SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
            userinfo.edit().putString("userID", au.userID).commit();
            Conference.userSignin = true;
            Conference.userID = au.userID;

            Intent in;

            if (activityName.compareTo("PaperInSession") == 0) {
                updatePaperStatus(paperID);
                syncDB();
                in = new Intent(Signup.this, PaperInSession.class);
                in.putExtra("papersessionID", papersessionID);
                in.putExtra("sessionName", sessionName);
                in.putExtra("bTime", sessionBTime);
                in.putExtra("eTime", sessionETime);
                in.putExtra("date", sessionDate);
                //in.putExtra("firstLogin", "yes");

            } else if (activityName.compareTo("PaperSimilar") == 0) {
                updatePaperStatus(paperID);
                syncDB();
                in = new Intent(Signup.this, PaperDetail.class);
                in.putExtra("id", contentID);
                in.putExtra("title", paperTitle);
                in.putExtra("bTime", paperbTime);
                in.putExtra("eTime", papereTime);
                in.putExtra("abstract", paperAbstract);
                in.putExtra("authors", paperAuthors);
                in.putExtra("room", room);
                in.putExtra("date", date);
                in.putExtra("presentationID", presentationID);
                //in.putExtra("firstLogin", "yes");
            } else if (activityName.compareTo("PaperInfo") == 0) {
                updatePaperStatus(paperID);
                syncDB();
                in = new Intent(Signup.this, PaperDetail.class);
                in.putExtra("id", paperID);
                in.putExtra("title", paperTitle);
                in.putExtra("bTime", paperbTime);
                in.putExtra("eTime", papereTime);
                in.putExtra("abstract", paperAbstract);
                in.putExtra("authors", paperAuthors);
                in.putExtra("room", room);
                in.putExtra("date", date);
                in.putExtra("presentationID", presentationID);
            } else if (activityName.compareTo("ProceedingsByAuthor") == 0) {
                updatePaperStatus(paperID);
                syncDB();
                in = new Intent(Signup.this, Proceedings.class);
                in.putExtra("no", "1");
            } else if (activityName.compareTo("ProceedingsByName") == 0) {
                updatePaperStatus(paperID);
                syncDB();
                in = new Intent(Signup.this, Proceedings.class);
                in.putExtra("no", "2");
            } else if (activityName.compareTo("ProceedingsByType") == 0) {
                updatePaperStatus(paperID);
                syncDB();
                in = new Intent(Signup.this, Proceedings.class);
                in.putExtra("no", "2");
            } else if (activityName.compareTo("WorkshopDetail") == 0) {
                updatePaperStatus(paperID);
                syncDB();
                in = new Intent(Signup.this, WorkshopDetail.class);
                in.putExtra("id", workshopID);
                in.putExtra("wtitle", workshopTitle);
                in.putExtra("paperID", paperID);
                in.putExtra("room", room);
                in.putExtra("eventSessionIDList", eventSessionIDList);
            } else if (activityName.compareTo("PosterDetail") == 0) {
                updatePaperStatus(paperID);
                syncDB();
                in = new Intent(Signup.this, PosterDetail.class);
                in.putExtra("paperID", paperID);
                in.putExtra("id", workshopID);
                in.putExtra("title", workshopTitle);
                in.putExtra("date", workshopDate);
                in.putExtra("room", room);
                in.putExtra("bTime", wbtime);
                in.putExtra("eTime", wetime);
            } else if (activityName.compareTo("MyStaredPapers") == 0) {
                updatePaperStatus(paperID);
                syncDB();
                in = new Intent(Signup.this, MyStaredPapers.class);
            } else if (activityName.compareTo("MyRecommendedPapers") == 0) {
                updatePaperStatus(paperID);
                syncDB();
                in = new Intent(Signup.this, MyRecommendedPapers.class);
            } else {
                updatePaperStatus(paperID);
                syncDB();
                in = new Intent(Signup.this, MainInterface.class);
            }

            startActivity(in);
            this.finish();
        }
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ERRORDIALOG:
                return errorDialog(Signup.this);
            case PERRORDIALOG:
                return pErrorDialog(Signup.this);
            case NERRORDIALOG:
                return nErrorDialog(Signup.this);
            case EERRORDIALOG:
                return eErrorDialog(Signup.this);
            case NPERRORDIALOG:
                return npErrorDialog(Signup.this);
            case NOINTERNET:
                return internetErrorDialog(this);
        }
        return null;
    }

    protected void onPrepareDialog(int id, Dialog dialog) {

    }

    private Dialog errorDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.alert_dialog_icon);
        builder.setTitle("Signup");
        builder.setMessage("Signup Error");
        builder.setPositiveButton("ok", null);
        return builder.create();
    }

    private Dialog pErrorDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.alert_dialog_icon);
        builder.setTitle("Signup");
        builder.setMessage("Passwords do not match");
        builder.setPositiveButton("ok", null);
        return builder.create();
    }

    private Dialog npErrorDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.alert_dialog_icon);
        builder.setTitle("Signup");
        builder.setMessage("Please input a password");
        builder.setPositiveButton("ok", null);
        return builder.create();
    }

    private Dialog nErrorDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.alert_dialog_icon);
        builder.setTitle("Signup");
        builder.setMessage("Please input a valid screen name");
        builder.setPositiveButton("ok", null);
        return builder.create();
    }

    private Dialog eErrorDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.alert_dialog_icon);
        builder.setTitle("Signup");
        builder.setMessage("Please input a valid email address");
        builder.setPositiveButton("ok", null);
        return builder.create();
    }

    private Dialog internetErrorDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.alert_dialog_icon);
        builder.setTitle("Signup");
        builder.setMessage("No Internet connection, please sign up again when Internet is available");
        builder.setPositiveButton("ok", null);
        return builder.create();
    }
}


