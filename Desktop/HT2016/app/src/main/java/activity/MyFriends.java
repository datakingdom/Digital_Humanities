package activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;

import data.model.Conference;
import data.model.Friend;
import data.parser.UserFriendParse;
import edu.pitt.is.DH2016.R;

public class MyFriends extends Activity {

    private final int MENU_HOME = Menu.FIRST;
    private final int MENU_TRACK = Menu.FIRST + 1;
    private final int MENU_STAR = Menu.FIRST + 2;
    private final int MENU_SCHEDULE = Menu.FIRST + 3;
    private ArrayList<Friend> FriendList;
    private String paperID;
    private ListView list, listview;
    private MyListAdapter adapter;
    private UserFriendParse uap;
    private ProgressDialog pd;
    private String userID;
    private TextView tv1, tv2;
    private Hashtable<String, String> idToStatus;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.mystaredpapers);
        this.idtostatus();

        tv1 = (TextView) this.findViewById(R.id.TextView01);
        tv1.setText("My Friends");
        tv2 = (TextView) this.findViewById(R.id.TextView02);
        tv2.setText("No friends information available");

			/*listview = (ListView)this.findViewById(R.id.ListView01); //side ListView
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,R.layout.textview,py);
	        listview.setAdapter(adapter1);	        
	        listview.setDivider(null);*/
        //listview.setOnItemClickListener(this);
        userID = Conference.userID;
        if (Conference.userSignin) {
            FriendList = null;
            FriendList = new ArrayList<Friend>();
            new AsyncFriend().execute(FriendList);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Please log in first",
                    Toast.LENGTH_LONG)
                    .show();
            CallSignin();
        }

        list = (ListView) this.findViewById(R.id.ListView01); // real ListView

        //list.setOnScrollListener(this);

    }

    private void CallSignin() {
        this.finish();
        Intent in = new Intent(MyFriends.this, Signin.class);
        in.putExtra("activity", "MyFriends");
        startActivity(in);
    }

    private void idtostatus() {
        // TODO Auto-generated method stub
        idToStatus = new Hashtable<String, String>();
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

    //private String py[]={"A","B","C","D","E","F","G","H","I","J","K","L","M","N","P","R","S","V","W","X","Y"};
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_HOME, 0, "Home").setIcon(R.drawable.home);
        menu.add(0, MENU_TRACK, 0, "Sessions").setIcon(R.drawable.session);
        menu.add(0, MENU_STAR, 0, "Starred Papers").setIcon(R.drawable.star);
        menu.add(0, MENU_SCHEDULE, 0, "My Schedule").setIcon(R.drawable.star);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent itemintent = new Intent();
        switch (item.getItemId()) {
            case MENU_HOME:
                this.finish();
                itemintent.setClass(MyFriends.this, MainInterface.class);
                startActivity(itemintent);
                return true;
            case MENU_STAR:
                this.finish();
                itemintent.setClass(MyFriends.this, MyStaredPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_TRACK:
                this.finish();
                itemintent.setClass(MyFriends.this, ProgramByDay.class);
                startActivity(itemintent);
                return true;
            case MENU_SCHEDULE:
                this.finish();
                itemintent.setClass(MyFriends.this, MyScheduledPapers.class);
                startActivity(itemintent);
                return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            //this.finish(); this.onStop(); this.onDestroy();
            this.finish();
            Intent in = new Intent(MyFriends.this, MainInterface.class);
            startActivity(in);
        }

        return super.onKeyDown(keyCode, event);
    }

    public final class ViewHolder {
        public TextView firstCharHintTextView, username, status;
        public ImageView photo;
    }

    private class MyListAdapter extends BaseAdapter implements OnItemClickListener {
        private ArrayList<Friend> aul;

        public MyListAdapter(ArrayList<Friend> ul) {
            this.aul = ul;
        }

        public int getCount() {
            return aul.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.attending_list_item, null);
                holder = new ViewHolder();
                holder.firstCharHintTextView = (TextView) convertView.findViewById(R.id.text_first_char_hint);
                holder.username = (TextView) convertView.findViewById(R.id.username);
                holder.status = (TextView) convertView.findViewById(R.id.status);
                holder.status.setVisibility(View.VISIBLE);
                //holder.photo = (ImageView)convertView.findViewById(R.id.photo);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.username.setText(aul.get(position).getname());
            holder.status.setText(idToStatus.get(aul.get(position).getstatus()));
                /*int idx = position - 1;

	            char previewChar = idx >= 0 ? pList.get(idx).authors.charAt(0) : ' ';
	            char currentChar = pList.get(position).authors.charAt(0);

	            char newPreviewChar = Character.toUpperCase(previewChar);
	            char newCurrentChar = Character.toUpperCase(currentChar);
	            if (newCurrentChar != newPreviewChar) {
	                holder.firstCharHintTextView.setVisibility(View.VISIBLE);
	                holder.firstCharHintTextView.setText(String.valueOf(newCurrentChar));
	            } else {

	                holder.firstCharHintTextView.setVisibility(View.GONE);
	            }*/
            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                long arg3) {
            // TODO Auto-generated method stub
            Intent in = new Intent(MyFriends.this, FriendsDetail.class);
            in.putExtra("id", aul.get(pos).getid());
            in.putExtra("status", aul.get(pos).getstatus());
            startActivity(in);
        }

    }

    private class AsyncFriend extends AsyncTask<ArrayList<Friend>, Void, Integer> {

        @Override
        protected void onPreExecute() {
            showDialog("Loading...");
        }

        @Override
        protected Integer doInBackground(ArrayList<Friend>... params) {
            // TODO Auto-generated method stub
            uap = new UserFriendParse();
            FriendList = params[0];
            FriendList = uap.getIdList(userID);
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            int state = result.intValue();
            switch (state) {
                case 1:
                    dismissDialog();
                    Toast.makeText(getApplicationContext(),
                            "Done",
                            Toast.LENGTH_LONG)
                            .show();
                    if (FriendList.size() != 0) {
                        tv2.setVisibility(View.GONE);
                    } else
                        tv2.setVisibility(View.VISIBLE);
                    adapter = new MyListAdapter(FriendList);
                    list.setAdapter(adapter);
                    list.setOnItemClickListener(adapter);
                    break;
                default:
                    dismissDialog();
                    Toast.makeText(getApplicationContext(),
                            "Error",
                            Toast.LENGTH_LONG)
                            .show();
                    if (FriendList.size() != 0) {
                        tv2.setVisibility(View.GONE);
                    } else
                        tv2.setVisibility(View.VISIBLE);
                    adapter = new MyListAdapter(FriendList);
                    list.setAdapter(adapter);
                    list.setOnItemClickListener(adapter);
                    break;
            }
        }
    }
}