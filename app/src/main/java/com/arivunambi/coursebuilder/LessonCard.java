package com.arivunambi.coursebuilder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by arivu on 6/3/2016.
 */
public class LessonCard extends LinearLayout {

    public static ArrayList<String> lessonArray = new ArrayList<String>( Arrays.asList("Introduction", "Human Cognition", " Interface Design",
            "Usability Analysis", "Conceptual Model", "+"));

    private int dpToPixels(int dps){
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }

    private void inflateLayout(final Context context) {
        //LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View view = layoutInflater.inflate(R.layout.card_view, this);
        this.setOrientation(LinearLayout.VERTICAL);
        for(int i = 0; i < lessonArray.size(); i++) {
            LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, this.dpToPixels(100));
            params.setMargins(this.dpToPixels(5), this.dpToPixels(10), this.dpToPixels(5), this.dpToPixels(0));
            params.height = this.dpToPixels(100);

            CardView card = new CardView(context, null, 0);
            card.setLayoutParams(params);
            card.setCardBackgroundColor(Color.parseColor("#303F9F"));
            //card.setId(i);

            LinearLayout cardInner = new LinearLayout(context);
            //cardInner.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            cardInner.setOrientation(LinearLayout.VERTICAL);

            TextView card_title = new TextView(context);
            card_title.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
            ));
            card_title.setHeight(this.dpToPixels(45));
            card_title.setPadding(0, this.dpToPixels(3), 0, 0);
            card_title.setText(lessonArray.get(i));
            card_title.setTextSize((float) this.dpToPixels(15));
            card_title.setGravity(Gravity.CENTER);
            card_title.setTextColor(Color.parseColor("#ffffff"));

            TextView card_desc = new TextView(context);
            card_desc.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
            ));
            card_desc.setHeight(this.dpToPixels(55));
            card_desc.setPadding(0, this.dpToPixels(5), 0, 0);
            if (i==lessonArray.size()-1){
                card_desc.setText("Add Lesson");
            }else {
                card_desc.setText("Description");
            }
            card_desc.setTextSize((float) this.dpToPixels(12));
            card_desc.setGravity(Gravity.CENTER);
            card_desc.setTextColor(Color.parseColor("#ffffff"));
            card_desc.setBackgroundColor(Color.parseColor("#dee0e2"));

            cardInner.addView(card_title);
            cardInner.addView(card_desc);
            card.addView(cardInner);

            if (i==lessonArray.size()-1){
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // item clicked
                        Log.d("Clicked: ", Integer.toString(v.getId()));
                        context.startActivity(new Intent(context.getApplicationContext(), CreateLesson.class));

                    }
                });
            }else {
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // item clicked
                        Log.d("Clicked: ", Integer.toString(v.getId()));
                        context.startActivity(new Intent(context.getApplicationContext(), ArticleScreen.class));
                    }
                });
            }

            this.addView(card);
        }
    }

    public LessonCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateLayout(context);
    }

    public LessonCard(Context context) {
        super(context);
        inflateLayout(context);
    }

}

