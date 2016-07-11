package activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import data.db.CheckDBUpdate;
import data.db.DBAdapter;
import data.model.Author;
import data.model.Conference;
import data.model.Keynote;
import data.model.Paper;
import data.model.PaperContent;
import data.model.Poster;
import data.model.Session;
import data.model.Workshop;
import data.parser.ConferenceInfoParser;
import data.parser.KeynoteWorkshopParser;
import data.parser.LoadPaperFromDB;
import data.parser.LoadSessionFromDB;
import data.parser.PaperContentParse;
import edu.pitt.is.HT2015.R;

public class FirstLaunchUpdate extends Activity {


    private ProgressDialog pd;
    private DBAdapter db;
    private TextView session, keynote, presentation, paper, success, sync;
    private int count = 0;

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

        setContentView(R.layout.first_launch_update);
        db = new DBAdapter(this);

        new AsyncUpdate().execute();
    }

    public void showDialog(String s) {
        pd = ProgressDialog.show(this, "Synchronization", s, true, false);
    }

    public void dismissDialog() {
        pd.dismiss();
    }

    private class AsyncUpdate extends AsyncTask<Void, Integer, Integer> {
        protected void onPreExcute() {
        }

        @Override
        protected Integer doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            count++;
            int state = 2;

            if (!isConnect(getApplicationContext())) {
                state = 3;
                return state;
            }

            // get timestamp
            String timestamp = new CheckDBUpdate().getTimestamp();
//            String timestamp = "2015-08-24 08:35:21.0";
            System.out.println("++++++++++++++++++++FirstLaunch++ " + timestamp);

            //execute update

            ArrayList<Session> sList = new ArrayList<Session>();
            ArrayList<Paper> pList = new ArrayList<Paper>();
            ArrayList<PaperContent> pcList = new ArrayList<PaperContent>();
            ArrayList<Keynote> knList = new ArrayList<Keynote>();
            ArrayList<Workshop> wsList = new ArrayList<Workshop>();
            ArrayList<Poster> poList = new ArrayList<Poster>();
            HashMap<String, Author> authorList = new HashMap<String, Author>();

            //Update keynote and workshop info
            publishProgress(14);
            KeynoteWorkshopParser knp = new KeynoteWorkshopParser();
            knp.getData();

            knList = knp.getKenotes();
            wsList = knp.getWorkshops();
            poList = knp.getPosters();
            if (poList.size() != 0) {
                publishProgress(12);
            } else {
                publishProgress(13);
                state = 2;
                return state;
            }

            //Update session info
            publishProgress(6);
            LoadSessionFromDB sdbr = new LoadSessionFromDB();
            sList = sdbr.getSessionData();
            if (sList.size() != 0) {
                publishProgress(0);
            } else {
                publishProgress(1);
                state = 2;
                return state;
            }

            //Update presentation info
            publishProgress(7);
            LoadPaperFromDB pdbr = new LoadPaperFromDB();
            pList = pdbr.getPaperData();
            if (pList.size() != 0) {
                publishProgress(2);
            } else {
                publishProgress(3);
                state = 2;
                return state;
            }

            //Update paper content info
            publishProgress(8);
            PaperContentParse pcp = new PaperContentParse();
            pcp.getData();
            pcList = pcp.getPaperContentList();
            authorList = pcp.getAuthors();
            if (pcList.size() != 0 && authorList.size() != 0) {
                publishProgress(4);
            } else {
                publishProgress(5);
                state = 2;
                return state;
            }

            // update conference
            ConferenceInfoParser.getConferenceInfo("143");
            db.open();
            db.deleteConference();
            long errorr = db.insertConference(Conference.id, Conference.title, Conference.startDate,
                    Conference.endDate, Conference.location, Conference.description, timestamp);
            if (errorr == -1)
                System.out.println("Insertion ConferenceInfo Failed");
            db.close();

            if (sList.size() != 0 && pList.size() != 0 && pcList.size() != 0) {
                try {
                    db.open();
                    db.deleteKeynote();
                    db.deleteWorkshopDes();
                    db.deletePoster();
                    db.deleteSession();
                    db.deletePaper();
                    db.deletePaperContent();
                    db.deleteAuthorToPaper();
                    db.deleteAllAuthor();

                    for (int i = 0; i < wsList.size(); i++) {
                        long error = db.insertWorkshopDes(wsList.get(i));
                        if (error == -1)
                            System.out.println("error occured");
                    }

                    for (int i = 0; i < knList.size(); i++) {
                        long error = db.insertKeynote(knList.get(i));
                        if (error == -1)
                            System.out.println("error occured");
                    }

                    for (int i = 0; i < poList.size(); i++) {
                        long error = db.insertPoster(poList.get(i));
                        if (error == -1)
                            System.out.println("error occured");
                    }

                    for (int i = 0; i < sList.size(); i++) {
                        long error = db.insertSession(sList.get(i));
                        if (error == -1)
                            System.out.println("Insertion Failed session");
                    }
                    for (int i = 0; i < pList.size(); i++) {
                        long error = db.insertPaper(pList.get(i));
                        if (error == -1)
                            System.out.println("Insertion Failed session");
                    }
                    for (int i = 0; i < pcList.size(); i++) {
                        if (pcList.get(i).authors == null || pcList.get(i).authors == "") {
                            pcList.get(i).authors = "No author information available";
                        }
                        if (pcList.get(i).type == null || pcList.get(i).type == "") {
                            pcList.get(i).type = "No type information available";
                        }
                        if (pcList.get(i).title == null || pcList.get(i).title == "") {
                            pcList.get(i).title = "No title information available";
                        }
                        if (pcList.get(i).paperAbstract == null || pcList.get(i).paperAbstract == "") {
                            pcList.get(i).paperAbstract = "No abstract information available";
                        }
                        for (String autorID : pcList.get(i).authorIDList) {
                            long error = db.insertAuthorToPaper(autorID, pcList.get(i).id);
                            if (error == -1)
                                System.out.println("Insert authorToPaper error occured");
                        }
                        long error = db.insertPaperContent(pcList.get(i));
                        if (error == -1)
                            System.out.println("Insert paper content error occured");
                    }
                    for (Author author : authorList.values()) {
                        long error = db.insertAuthor(author.id, author.name);
                        if (error == -1)
                            System.out.println("Insertion author Failed session");
                    }
                    db.close();
                } catch (Exception e) {
                    System.out.print(e.getMessage());
                }
                state = 1;//success
            } else {
                state = 2;//error
            }

            return state;
        }

        protected void onProgressUpdate(Integer... progress) {
            session = (TextView) findViewById(R.id.sessionupdate);
            presentation = (TextView) findViewById(R.id.presentationupdate);
            paper = (TextView) findViewById(R.id.paperupdate);
            keynote = (TextView) findViewById(R.id.keynoteupdate);
            int command = progress[0];
            switch (command) {
                case 0:
                    session.setCompoundDrawablesWithIntrinsicBounds(R.drawable.accept, 0, 0, 0);
                    session.setText("Update introduction,keynote,workshop information: success!");
                    break;
                case 1:
                    session.setCompoundDrawablesWithIntrinsicBounds(R.drawable.error, 0, 0, 0);
                    session.setText("Fail to update introduction,keynote,workshop information");
                    break;
                case 2:
                    presentation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.accept, 0, 0, 0);
                    presentation.setText("Update presentation information: success!");
                    break;
                case 3:
                    presentation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.error, 0, 0, 0);
                    presentation.setText("Fail to update presentation information");
                    break;
                case 4:
                    paper.setCompoundDrawablesWithIntrinsicBounds(R.drawable.accept, 0, 0, 0);
                    paper.setText("Update paper information: success!");
                    break;
                case 5:
                    paper.setCompoundDrawablesWithIntrinsicBounds(R.drawable.error, 0, 0, 0);
                    paper.setText("Fail to update paper information");
                    break;
                case 6:
                    session.setCompoundDrawablesWithIntrinsicBounds(R.drawable.db_refresh, 0, 0, 0);
                    session.setText("Updating introduction,keynote,workshop information ...");
                    break;
                case 7:
                    presentation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.db_refresh, 0, 0, 0);
                    presentation.setText("Updating presentation information ...");
                    break;
                case 8:
                    paper.setCompoundDrawablesWithIntrinsicBounds(R.drawable.db_refresh, 0, 0, 0);
                    paper.setText("Updating paper information ...");
                    break;
                case 12:
                    keynote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.accept, 0, 0, 0);
                    keynote.setText("Update keynote & workshop information: success!");
                    break;
                case 13:
                    keynote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.error, 0, 0, 0);
                    keynote.setText("Fail to update keynote & workshop information");
                    break;
                case 14:
                    keynote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.db_refresh, 0, 0, 0);
                    keynote.setText("Updating keynote & workshop information ...");
                    break;
                default:
                    break;

            }

        }

        protected void onPostExecute(Integer state) {
            success = (TextView) findViewById(R.id.success);
            Intent intent = new Intent(FirstLaunchUpdate.this, MainInterface.class);
            switch (state) {
                case 1:
                    Toast.makeText(getApplicationContext(),
                            "UMAP 2015 has Updated!",
                            Toast.LENGTH_LONG)
                            .show();

                    success.setText("Update Success!");
                    finish();
                    startActivity(intent);
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(),
                            "Server error, restart again.",
                            Toast.LENGTH_SHORT)
                            .show();

//                    success.setText("Update Fail!");
                    if (count < 3) {
                        new AsyncUpdate().execute();
                        success.setText("Restart update!");
                    } else {
                        success.setText("Server crashed, please try it later!");
                    }
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(),
                            "Please check your internet connection",
                            Toast.LENGTH_LONG)
                            .show();
                    success.setText("No Internet Connection!");
                default:
                    break;
            }
        }
    }
}
