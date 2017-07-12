package com.test.tedlin.leadertechapp8;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            ListView listview = (ListView)findViewById(R.id.MyListView);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    listview.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    listview.setVisibility(View.INVISIBLE);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    listview.setVisibility(View.INVISIBLE);
                    return true;
            }
            return false;
        }

    };
    public void gotoSecondActivity(View view){
                 //创建一个意图
                 Intent intent = new Intent(MainActivity.this,ForwardTarget.class);

                 startActivity(intent);
             }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute();

    }




    private class DownloadTask extends AsyncTask<String, Object, ArrayList<Map<String,Object>>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<Map<String,Object>> doInBackground(String... params) {
            ArrayList<Map<String,Object>> lists;
            JSONObject jsonObject = getfile();
            Log.i("OKOK", String.valueOf(jsonObject));
            lists = new ArrayList<>();
            if(jsonObject!=null){
                try {
                    JSONArray jarr = jsonObject.getJSONArray("ItemList");
                    for(int i=0;i<jarr.length();i++){
                        Map<String,Object> map = new HashMap<String,Object>();
                        JSONObject product = jarr.getJSONObject(i);
                        map.put("ItemSKU",product.getString("ItemSKU"));
                        map.put("Desc",product.getString("Desc"));
                        map.put("ItemID",product.getString("ItemID"));
                        String imgurl = "https://www.leadertechusa.com/showThumb.aspx?maxsize=100&img=/products/"+product.get("Img");
                        Bitmap bitmap=getUrlImage(imgurl);
                        if (bitmap!=null){
                            map.put("Img",bitmap);
                        }else{
                            map.put("Img","");
                        }
                        lists.add(map);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.i("CHECK", String.valueOf(lists));
            return lists;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate();

        }

        @Override
        protected void onPostExecute(ArrayList<Map<String,Object>> lists) {
            super.onPostExecute(lists);
            Log.i("onpost", String.valueOf(lists));



            ListView listView = (ListView) findViewById(R.id.MyListView);
            MySimpleAdapter mSchedule = new   MySimpleAdapter(MainActivity.this,
                    lists,//数据来源
                    R.layout.item,//ListItem的XML实现

                    //动态数组与ListItem对应的子项
                    new String[] {"ItemSKU", "Desc","Img","ItemID"},

                    //ListItem的XML文件里面的两个TextView ID
                    new int[] {R.id.ItemSKU,R.id.VendorCode,R.id.imageView,R.id.ItemId});
            //添加并且显示
            mSchedule.setViewBinder(new SimpleAdapter.ViewBinder() {
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    if (view instanceof ImageView && data instanceof Bitmap) {
                        ImageView image = (ImageView) view;
                        image.setImageBitmap((Bitmap) data);
                        return true;
                    }
                    return false;
                }
            });
            listView.setAdapter(mSchedule);

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

        }

        private JSONObject getfile(){
            String objectUrl = "http://www.leadertechusa.com/api/getNewItems.asp"+"?lmt=10";
            JSONObject jsonObject = null;
            HttpURLConnection connection;
            try{
                URL myurl = new URL(objectUrl);
                connection = (HttpURLConnection) myurl.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                if(connection.getResponseCode()==200){
                    Log.i("COnnected","good");
                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line=reader.readLine())!=null){
                        response.append(line);
                    }
                    jsonObject = new JSONObject(response.toString());
                    is.close();
                    reader.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            Log.i("HELP", String.valueOf(jsonObject));
            new ProductInfo(jsonObject);
            return jsonObject;
        }

        private Bitmap getUrlImage(String url){
            HttpURLConnection conn;
            Bitmap bitmap=null;
            Log.i("try to connect","nuinui");
            try{
                URL imgUrl = new URL(url);
                conn = (HttpURLConnection) imgUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setReadTimeout(5000);
                if(conn.getResponseCode()==200){

                    InputStream in = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(in);
                    in.close();
                }else{
                }
                conn.disconnect();

            }catch (Exception e){
                e.printStackTrace();
            }
            return bitmap;
        }

    }

}
