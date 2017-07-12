package com.test.tedlin.leadertechapp8;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Created by tedlin on 6/13/2017.
 */

public class MySimpleAdapter extends SimpleAdapter {
    /**
     * Constructor
     *
     * @param context  The context where the View associated with this SimpleAdapter is running
     * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
     *                 Maps contain the data for each row, and should include all the entries specified in
     *                 "from"
     * @param resource Resource identifier of a view layout that defines the views for this list
     *                 item. The layout file should include at least those named views defined in "to"
     * @param from     A list of column names that will be added to the Map associated with each
     *                 item.
     * @param to       The views that should display column in the "from" parameter. These should all be
     *                 TextViews. The first N views in this list are given the values of the first N columns
     */
    private Context context; /*运行环境*/
    ArrayList<Map<String, Object>> listItem;  /*数据源*/
    private LayoutInflater listContainer; // 视图容器





    class ListItemView { // 自定义控件集合
        public TextView ItemSKU;
        public TextView  VendorCode;
        public TextView ItemId;
        public ImageView imageView;
        public RelativeLayout listitem;

    }


    public MySimpleAdapter(Context context, ArrayList<Map<String, Object>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
        this.context=context;
        listItem=data;
    }


    public int getCount() {

        return listItem.size();
    }


    public Object getItem(int position) {

        return listItem.get(position);
    }

    public long getItemId(int position) {

        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final int mPosition = position;
        ListItemView listItemView = null;
        if (convertView == null) {
            convertView = listContainer.inflate(R.layout.item, null);//加载布局
            listItemView = new ListItemView();
                /*初始化控件容器集合*/
            listItemView.ItemSKU=(TextView) convertView
                    .findViewById(R.id.ItemSKU);
            listItemView.VendorCode=(TextView) convertView
                    .findViewById(R.id.VendorCode);
            listItemView.ItemId=(TextView) convertView
                    .findViewById(R.id.ItemId);
            listItemView.imageView=(ImageView) convertView
                    .findViewById(R.id.imageView);
            listItemView.listitem=(RelativeLayout) convertView.findViewById(R.id.listitem);
            // 设置控件集到convertView
            convertView.setTag(listItemView);

        }else{
            listItemView=(ListItemView)convertView.getTag();//利用缓存的View
        }


        final String itemid = (String) listItem.get(mPosition).get("ItemID");
        listItemView.listitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context.getApplicationContext(),ForwardTarget.class);
                intent.putExtra("tid",itemid);
                context.startActivity(intent);

            }
        });

        //设置text的值（用position标记来看的更清楚点儿）
        listItemView.ItemId.setText(" "+listItem.get(mPosition).get("ItemID"));
        listItemView.ItemSKU.setText((String)(listItem.get(mPosition).get("ItemSKU")));
        if(listItem.get(mPosition).get("Img") instanceof java.lang.String){
            System.out.println(listItem.get(mPosition).get("Img"));
        }else{
            listItemView.imageView.setImageBitmap((Bitmap) listItem.get(mPosition).get("Img"));
        }
        listItemView.VendorCode.setText((String)(listItem.get(mPosition).get("Desc")));
        return convertView;
    }

}
