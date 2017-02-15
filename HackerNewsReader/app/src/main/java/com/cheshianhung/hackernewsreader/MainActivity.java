package com.cheshianhung.hackernewsreader;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView titleList;
    ArrayList<String> titleAry;
    ArrayList<String> idAry;
    ArrayList<String> urlAry;

    ArrayAdapter<String> arrayAdapter;



    public class DownloadWebContent extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {

            return download(params);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONArray jsonArray = new JSONArray(s);
                idAry = new ArrayList<String>();

                for(int i = 0; i < jsonArray.length(); i++){
                    idAry.add(jsonArray.get(i).toString());
                }

            }
            catch(Exception e) {
                e.printStackTrace();
            }

            for(int i = 0; i < 30; i++){
                DownloadTitleFromID downloadTitleFromID = new DownloadTitleFromID();
                downloadTitleFromID.execute("https://hacker-news.firebaseio.com/v0/item/" + idAry.get(i) + ".json?print=pretty");
            }

        }
    }

    public class DownloadTitleFromID extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return download(params);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

//            Log.i("Item JSON", s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                String title = jsonObject.getString("title");
                String url = jsonObject.getString("url");
                titleAry.add(title);
                urlAry.add(url);
                arrayAdapter.notifyDataSetChanged();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleList = (ListView) findViewById(R.id.titleList);
        titleAry = new ArrayList<String>();
        idAry = new ArrayList<String>();
        urlAry = new ArrayList<String>();

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,titleAry);

        titleList.setAdapter(arrayAdapter);

        DownloadWebContent downloadWebContent = new DownloadWebContent();
        downloadWebContent.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");

        titleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                intent.putExtra("url", urlAry.get(position));
                startActivity(intent);
            }
        });

    }

    private String download(String... params) {
        String result = "";
        String data;

        try {
            URL url = new URL(params[0]);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            data = bufferedReader.readLine();

            while(data != null) {
                result += data;
                data = bufferedReader.readLine();
            }
            return result;


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
