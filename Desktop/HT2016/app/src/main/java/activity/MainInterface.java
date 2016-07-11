package activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import data.db.CheckDBUpdate;
import data.db.DBAdapter;
import data.model.Conference;
import edu.pitt.is.HT2015.R;

public class MainInterface extends Activity {

    private ImageButton syncB;
    private DBAdapter db;

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

    private void checkUpdate() {
        CheckDBUpdate checkDBUpdate = new CheckDBUpdate();
        syncB = (ImageButton) findViewById(R.id.ImageButton01);
        if (checkDBUpdate.check()) {
            syncB.setImageResource(R.drawable.need_update);
        } else {
            syncB.setImageResource(R.drawable.update);
        }
    }

    @Override
    protected void onResume() {
        System.out.println("+++++++++++onResume: " + Conference.timstamp);
        super.onResume();
        if (isConnect(this)) {
            checkUpdate();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main_interface);
        //setContentView(R.layout.spinner);

//        spinner2 = (Spinner) findViewById(R.id.spinner1);

        db = new DBAdapter(this);
//        adapter2 = ArrayAdapter.createFromResource(this, R.array.conference_array, android.R.layout.simple_spinner_item);

        db.open().getConferenceInfo();

        syncB = (ImageButton) findViewById(R.id.ImageButton01);
        //syncC = (ImageButton) findViewById(R.id.button1);
        syncB.setOnClickListener(new View.OnClickListener() {
                                     public void onClick(View view) {
                                         Intent in = new Intent(MainInterface.this, UpdateOption.class);
                                         startActivity(in);
                                     }
                                 }
        );


        //Row 1
        GridView gv1 = (GridView) findViewById(R.id.GridView01);
        Integer[] i1 = {R.drawable.about, R.drawable.keynote, R.drawable.sessionbig, R.drawable.proceeding, R.drawable.workshop, R.drawable.tutorial, R.drawable.poster, R.drawable.author};
        String[] t1 = {"About", "Keynotes", "Schedule", "Proceedings", "Workshops", "Tutorials", "Posters", "Authors"};
        gv1.setAdapter(new ImageViewAdapter(this, i1, t1));

        gv1.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView av, View v, int index, long arg) {
                Intent in;
                switch (index) {
                    // Conference General
                    case 0:
                        in = new Intent(MainInterface.this, ConferenceInfo.class);
                        startActivity(in);
                        break;
                    // Keynotes
                    case 1:
                        in = new Intent(MainInterface.this, KeyNote.class);
                        startActivity(in);
                        break;
                    // Schedule
                    case 2:
                        in = new Intent(MainInterface.this, ProgramByDay.class);
                        startActivity(in);
                        break;
                    // Proceedings
                    case 3:
                        in = new Intent(MainInterface.this, Proceedings.class);
                        startActivity(in);
                        break;
                    // Workshop
                    case 4:
                        in = new Intent(MainInterface.this, Workshops.class);
                        startActivity(in);
                        break;
                    // Tutorial
                    case 5:
                        in = new Intent(MainInterface.this, Tutorials.class);
                        startActivity(in);
                        break;
                    // Poster
                    case 6:
                        in = new Intent(MainInterface.this, Posters.class);
                        startActivity(in);
                        break;
                    // Author
                    case 7:
                        in = new Intent(MainInterface.this, Authors.class);
                        startActivity(in);
                        break;
                    default:
                        break;
                }
            }
        });

        //Row 2
        GridView gv4 = (GridView) findViewById(R.id.GridView04);

        if (Conference.userID.compareTo("") != 0) {
            System.out.println("------------" + Conference.userID);
            Integer[] i4 = {R.drawable.starbig, R.drawable.schedulebig, R.drawable.recommend, R.drawable.logout};
            String[] t4 = {"Favorite", "Schedule", "Recommends", "Log Out"};
            gv4.setAdapter(new ImageViewAdapter(this, i4, t4));

            gv4.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView av, View v, int index, long arg) {
                    Intent in;
                    switch (index) {
                        // Starred Papers
                        case 0:
                            in = new Intent(MainInterface.this, MyStaredPapers.class);
                            startActivity(in);
                            break;
                        // Sessions
                        case 1:
                            in = new Intent(MainInterface.this, MyScheduledPapers.class);
                            startActivity(in);
                            break;
                        // Recommendation
                        case 2:
                            in = new Intent(MainInterface.this, MyRecommendedPapers.class);
                            startActivity(in);
                            break;
                        case 3:
                            Conference.userID = "";
                            Conference.userSignin = false;
                            SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
                            SharedPreferences.Editor editor = userinfo.edit();
                            editor.putString("userID", "");
                            //
                            editor.putBoolean("userSignin", false);
                            editor.commit();
                            CharSequence msg = "You've signed out successfully.";
                            int dur = Toast.LENGTH_SHORT;
                            Toast t = Toast.makeText(getApplicationContext(), msg, dur);
                            t.show();
                            finish();//
                            startActivity(getIntent());//
                            break;
                        default:
                            break;

                    }
                }
            });

        } else {
            Integer[] i4 = {R.drawable.starbig, R.drawable.schedulebig, R.drawable.recommend, R.drawable.login};
            String[] t4 = {"Favorite", "Schedule", "Recommends", "Log In"};
            gv4.setAdapter(new ImageViewAdapter(this, i4, t4));

            gv4.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView av, View v, int index, long arg) {
                    Intent in;
                    switch (index) {
                        // Starred Papers
                        case 0:
                            in = new Intent(MainInterface.this, MyStaredPapers.class);
                            startActivity(in);
                            break;
                        // Sessions
                        case 1:
                            in = new Intent(MainInterface.this, MyScheduledPapers.class);
                            startActivity(in);
                            break;
                        // Recommendation
                        case 2:
                            in = new Intent(MainInterface.this, MyRecommendedPapers.class);
                            startActivity(in);
                            break;
                        case 3:
                            CallSignin();
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        if (isConnect(this)) {
            checkUpdate();
        }
    }

    private void CallSignin() {
        this.finish();
        Intent in = new Intent(MainInterface.this, Signin.class);
        in.putExtra("activity", "MainInterface");
        startActivity(in);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    // Fires after the OnStop() state
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
        }

        return super.onKeyDown(keyCode, event);
    }

    private class ImageViewAdapter extends BaseAdapter {
        private Context mContext;
        private Integer[] mThumbIds;
        private String[] mText;

        public ImageViewAdapter(Context c, Integer[] i, String[] t) {
            mContext = c;
            mThumbIds = i;
            mText = t;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            if (convertView == null) {
                LayoutInflater li = getLayoutInflater();
                v = li.inflate(R.layout.imagetext, null);
                TextView tv = (TextView) v.findViewById(R.id.TextView01);
                tv.setText(mText[position]);
//                tv.setTextSize(12);
                ImageView iv = (ImageView) v.findViewById(R.id.ImageView01);
                iv.setImageResource(mThumbIds[position]);
            } else {
                v = convertView;
            }

            return v;
        }
    }
}
