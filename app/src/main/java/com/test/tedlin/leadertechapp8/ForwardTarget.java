package com.test.tedlin.leadertechapp8;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tedlin on 6/8/2017.
 */

public class ForwardTarget  extends Activity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productoverview);

        JSONObject js =ProductInfo.myinfo;
        Intent intent = getIntent();
        String myid=intent.getStringExtra("tid");



        TextView productid = (TextView) findViewById(R.id.RelDesc);
        productid.setText(myid);

        GetOverview getOverview = new GetOverview();
        getOverview.execute(myid);

    }

    public interface Mymap<K,V>{
        public K getKey();
        public V getValue();
    }
    public class MymapImp<K,V> implements Mymap<K,V>{
        public K key;
        public V value;

        public MymapImp(K key, V value){
            this.key = key;
            this.value = value;
        }

        public K getKey() {return key;}
        public V getValue(){return value;}

    }
    private class GetOverview extends AsyncTask<String, Integer, Map> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Map doInBackground(String... params) {
            ArrayList lists;
            Map<String , Object> map = null;
            String introduction="";
            map= new HashMap<>();
            lists = new ArrayList();
            Log.i("woshiparmas", String.valueOf(params));
            for(String itemId :params){
                System.out.print(itemId);
                JSONObject overviewOj=getoverview(itemId);
                try {
                    JSONArray jarr = overviewOj.getJSONArray("ItemDetail");
                    Log.d("bvbv", String.valueOf(jarr));
                    JSONObject detail = jarr.getJSONObject(0);
                    JSONArray jsonlist=detail.getJSONArray("Img");
                    Log.i("CHECK-Connection",jsonlist.getString(0));
                    //获取图片并放入map
                    Bitmap showimg = getUrlImage(jsonlist.getString(0));
                    if (showimg!=null){
                        map.put("showImg",showimg);
                    }else{
                        map.put("showImg","");
                    }
                    //获取RelDesc
                    String relDesc="" ;
                    jsonlist = detail.getJSONArray("RelDesc");
                    for (int i = 0 ; i<jsonlist.length();i++){
                        relDesc = relDesc+ jsonlist.getString(i)+"\n";
                    }
                    map.put("relDesc",relDesc);
                    //获取introduction

                    introduction = detail.getString("Overview");
//                    introduction = overViewjson.toString();
                    String REX= "<style[^<]*</style>";
                    String REX2= "<script[^<]*</script>";
                    String replace = "";
                    Pattern p = Pattern.compile(REX);
                    Matcher m = p.matcher(introduction);

                    introduction = m.replaceAll(replace);
                    p = Pattern.compile(REX2);
                    m = p.matcher(introduction);
                    introduction = m.replaceAll(replace);
                    Log.i("res-A",introduction);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("log_tag", "Error parsing data "+e.toString());
                }
            }
            Spanned sp = Html.fromHtml(introduction, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    InputStream is = null;
                    try {
                        is = (InputStream) new URL(source).getContent();
                        Drawable d = Drawable.createFromStream(is, "src");
                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int width = size.x;
                        int height = size.y;

                        int fwidth = width;
                        int fheight = d.getIntrinsicHeight()*width/d.getIntrinsicWidth();
                        if(d.getIntrinsicWidth()>width){
                        d.setBounds(0, 0, fwidth, fheight);
                        }else{
                            d.setBounds(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
                        }
                        is.close();
                        return d;
                    } catch (Exception e) {
                        return null;
                    }
                }
            }, null);
            map.put("overView",sp);
            Log.i("cilun", String.valueOf(map));
            return map;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {



        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(Map as) {
            super.onPostExecute(as);
            Log.i("zuizhong", String.valueOf(as));
            TextView overview = (TextView)findViewById(R.id.overviews);
            ImageView imageView = (ImageView)findViewById(R.id.imageView2);
            Spanned sp = (Spanned) as.get("overView");
            Bitmap bitmap = (Bitmap) as.get("showImg");
            Log.i("kpi", String.valueOf(bitmap.getClass()));
            imageView.setImageBitmap(bitmap);
            overview.setText(sp);

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

        }

        private JSONObject getoverview (String itemid){
            String url = "http://www.leadertechusa.com/api/prodOverview.asp?i="+itemid;
            JSONObject jsonObject = null;
            HttpURLConnection connection;
            try{
                URL myurl = new URL(url);
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
            Log.i("overview", String.valueOf(jsonObject));
            return jsonObject;
        }

        private Bitmap getUrlImage(String url){
            HttpURLConnection conn;
            Bitmap bitmap=null;
            String imgurl;
            Log.i("try to connect","nuinui");
            imgurl = "https://www.leadertechusa.com/showThumb.aspx?maxsize=300&img=/products/"+url;
            try{
                URL imgUrl = new URL(imgurl);
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
