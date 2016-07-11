package activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import data.db.DBAdapter;
import data.model.Conference;
import edu.pitt.is.HT2015.R;

public class iConference extends Activity {
    public int alpha = 255;
    private Handler mHandler = new Handler();
    private TextView status;
    private ImageView imageview;
    private DBAdapter db;
    private Handler handler = new Handler();

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

    /**
     * Called when the activity is first created.
     */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        imageview = (ImageView) this.findViewById(R.id.Logo);
        imageview.setAlpha(alpha);
        status = (TextView) this.findViewById(R.id.status);

        new Thread(new Runnable() {
            public void run() {
                try {
                    if (alpha > 0) {
                        alpha -= 5;
                        mHandler.sendMessage(mHandler.obtainMessage());
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                loadData();
            }
        }).start();

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                imageview.setAlpha(alpha);
                //status.invalidate();
            }
        };
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

    public void loadData() {
        Conference.userID = getUserID();
        db = new DBAdapter(this).open();
        db.getConferenceInfo();
        db.close();
        if (Conference.title != null && Conference.title.equals("")) { // first launch
            finish();
            Intent intent = new Intent(iConference.this, FirstLaunchUpdate.class);
            startActivity(intent);
        } else {
            finish();
            Intent in = new Intent(this, MainInterface.class);
            startActivity(in);
        }
    }

    public void showToast(final String s) {
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), s,
                        Toast.LENGTH_LONG).show();

            }
        });
    }
}