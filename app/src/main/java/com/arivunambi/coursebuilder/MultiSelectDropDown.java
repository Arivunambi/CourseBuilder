package com.arivunambi.coursebuilder;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by arivu on 6/4/2016.
 */
public class MultiSelectDropDown {

    private ArrayList<String> dItems = new ArrayList<String>();
    private PopupWindow dPw;
    private ViewGroup dPopup;
    private boolean expanded;
    public  boolean[] dCheckSelected; //was static
    private TextView dTv;
    private RelativeLayout dContainer;
    private Context dContext;

    public MultiSelectDropDown(Context context, ArrayList<String> items, RelativeLayout container,
                               TextView tv, ViewGroup popup, boolean[] checkSelected){
        dCheckSelected = checkSelected;
        dTv = tv;
        dContainer = container;
        dItems = items;
        dContext =context;
        dPopup = popup;
        //initialize();

    }

    public void initialize(){
        for (int i = 0; i < dCheckSelected.length; i++) {
            dCheckSelected[i] = false;
        }

        dTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!expanded) {
                    //display all selected values
                    String selected = "";
                    int flag = 0;
                    for (int i = 0; i < dItems.size(); i++) {
                        if (dCheckSelected[i] == true) {
                            selected += dItems.get(i);
                            selected += ", ";
                            flag = 1;
                        }
                    }
                    if (flag == 1)
                        dTv.setText(selected);
                        Log.d("userSelected", selected);
                    expanded = true;
                } else {
                    //display shortened representation of selected values
                    dTv.setText(DropDownListAdapter.getSelected());
                    expanded = false;
                }
            }
        });

        //onClickListener to initiate the dropDown list - For initiator see below the definition
        View.OnClickListener dropdown_icon = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopUp(dItems, dTv);
            }
        };

        dContainer.setOnClickListener(dropdown_icon);
        dTv.setOnClickListener(dropdown_icon);
    }

    private void initiatePopUp(ArrayList<String> dItems, TextView dTv){
        LayoutInflater inflater = (LayoutInflater)dContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.dropdown_popup, dPopup);

        dPw = new PopupWindow(layout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        dPw.setBackgroundDrawable(new BitmapDrawable());
        dPw.setTouchable(true);
        //inform pop-up the touch event outside its window
        dPw.setOutsideTouchable(true);
        //the pop-up will be dismissed if touch event occurs anywhere outside its window
        dPw.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dPw.dismiss();
                    return true;
                }
                return false;
            }
        });

        // Set the content of pop-up window as layout and anchor the pop-up to the bottom-left corner of the desired view.
        dPw.setContentView(layout);
        RelativeLayout layout1 = dContainer;
        dPw.showAsDropDown(layout1);

        // finally set the drop-down listview with the data source items:
        final ListView list = (ListView) layout.findViewById(R.id.dropdown_listView);
        DropDownListAdapter adapter = new DropDownListAdapter(dContext, dItems, dTv, dCheckSelected);
        //’items’ is the values’ list and ‘tv’ is the textview where the selected values are displayed
        list.setAdapter(adapter);
    }
}
