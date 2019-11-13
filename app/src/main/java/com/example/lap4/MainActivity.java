package com.example.lap4;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.*;

public class MainActivity extends AppCompatActivity{

    private List<Restaurant> list = new ArrayList<Restaurant>();
    private Button btn_save;
    private EditText ed_name,ed_address;
    private ListView listView;
    private RestaurantAdapter adapter = null;
    private DatabaseHanding helper = null;
    private Cursor cursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new DatabaseHanding(this);
        btn_save = (Button) findViewById(R.id.save);
        btn_save.setOnClickListener(onSave);

        listView = (ListView)findViewById(R.id.restaurants);


        cursor = helper.getAll();
        startManagingCursor(cursor);
        adapter = new RestaurantAdapter(cursor);
        listView.setAdapter(adapter);

        TabHost tabs = (TabHost) findViewById(R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec("tag1");
        spec.setContent(R.id.List);
        spec.setIndicator("List",getResources().getDrawable(R.drawable.list));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tag2");
        spec.setContent(R.id.Details);
        spec.setIndicator("Details",getResources().getDrawable(R.drawable.restaurant));
        tabs.addTab(spec);

    }


    private View.OnClickListener onSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Restaurant r = new Restaurant();
            ed_name= (EditText)findViewById(R.id.name);
            ed_address = (EditText) findViewById(R.id.addr);

            listView.setOnItemClickListener(onListClick);
            r.setName(ed_name.getText().toString());
            r.setAddress(ed_address.getText().toString());

            RadioGroup type = (RadioGroup) findViewById(R.id.type);
            switch (type.getCheckedRadioButtonId())
            {
                case R.id.take_out:
                    r.setType("Take out");
                    break;
                case R.id.sit_down:
                    r.setType("Sit down");
                    break;
                case R.id.delivery:
                    r.setType("Delivery");
                    break;

                    default:
                        break;
            }
            helper.Add(r.getName(),r.getAddress(),r.getType());
            cursor.requery();
            list.add(r);
            listView.setAdapter(adapter);
            Toast.makeText(MainActivity.this, ed_name.getText() +" "+ ed_address.getText()+" "+ r.getType() ,Toast.LENGTH_LONG).show();

        }
    };

    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            cursor.moveToPosition(position);
            TabHost tabs = (TabHost) findViewById(R.id.tabhost);
//            Restaurant r = list.get(position);
            EditText name;
            EditText address;
            RadioGroup types;

            name = (EditText) findViewById(R.id.name);
            address = (EditText) findViewById(R.id.addr);
            types = (RadioGroup) findViewById(R.id.type);

            name.setText(helper.getName(cursor));
            address.setText(helper.getAddress(cursor));
            if (helper.getType(cursor).equals("Sit down"))
                types.check(R.id.sit_down);
            else if (helper.getType(cursor).equals("Take out"))
                types.check(R.id.take_out);
            else
                types.check(R.id.delivery);
            tabs.setCurrentTab(1);

        }
    };
    class RestaurantAdapter extends CursorAdapter {
        public  RestaurantAdapter(Cursor c){
            super(MainActivity.this, c);
        }
        public RestaurantAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row,parent,false);
            return row;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            View row = view;
            ((TextView)row.findViewById(R.id.title)).setText(helper.getName(cursor));
            ((TextView)row.findViewById(R.id.address)).setText(helper.getAddress(cursor));
            ImageView icon = (ImageView) row.findViewById(R.id.icon);

            String type = helper.getType(cursor);
//
            if(type.equals("Take out"))
                icon.setImageResource(R.drawable.ic_intersection);
            else if(type.equals("Sit down"))
                icon.setImageResource(R.drawable.ic_manager);
            else icon.setImageResource(R.drawable.ic_delivery_truck);
        }

//        public RestaurantAdapter(@NonNull Context context, int resource) {
//            super(context, resource);
//        }
//        public  RestaurantAdapter(){
//            super(MainActivity.this,android.R.layout.simple_list_item_1,list);
//        }
//
//        @NonNull
//        @Override
//        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            View row = convertView;
//            if(row == null){
//                LayoutInflater inflater = getLayoutInflater();
//                row = inflater.inflate(R.layout.row,null);
//            }
//            Restaurant r = list.get(position);
//            ((TextView)row.findViewById(R.id.title)).setText(r.getName());
//            ((TextView)row.findViewById(R.id.address)).setText(r.getAddress());
//            ImageView icon = (ImageView) row.findViewById(R.id.icon);
//            row.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(MainActivity.this,DetailActivity.class);
//                    intent.putExtra("Name",list.get(position).getName());
//                    intent.putExtra("Address",list.get(position).getAddress());
//                    intent.putExtra("Type",list.get(position).getType());
//                    startActivity(intent);
//                }
//            });

//            String type = r.getType();
//
//            if(type.equals("Take out"))
//                icon.setImageResource(R.drawable.ic_intersection);
//            else if(type.equals("Sit down"))
//                icon.setImageResource(R.drawable.ic_manager);
//            else icon.setImageResource(R.drawable.ic_delivery_truck);
//
//            return row;
//        }
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();
        helper.close();
    }
}
