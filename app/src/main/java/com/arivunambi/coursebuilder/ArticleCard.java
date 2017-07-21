package com.arivunambi.coursebuilder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by arivu on 6/3/2016.
 */
public class ArticleCard extends LinearLayout {

    public static ArrayList<String> articleArray = new ArrayList<String>( Arrays.asList("Graphical Interface", "Web Interface", "Mobile Interface",
            "+"));

    public String course_id="";
    public String article_name="";
    public String article_id="";

    private int dpToPixels(int dps){
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }

    private void inflateLayout(final Context context) {
        //LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View view = layoutInflater.inflate(R.layout.card_view, this);
        course_id = ArticleScreen.course_id;
        final DataAPI dataAPI = new DataAPI(context.getApplicationContext());
        JSONArray article_list = dataAPI.get_articles(course_id);
        Log.d("MyLogger article_list :",article_list.toString());

        this.setOrientation(LinearLayout.VERTICAL);
        Integer article_list_length = article_list.length();
        if (MainActivity.privilege<2){
            article_list_length = article_list.length()-1;
        }
        for(int i = 0; i <= article_list_length; i++) {
            try{
                article_name = article_list.getJSONObject(i).getString("name");
                article_id = article_list.getJSONObject(i).getString("id");
            } catch (JSONException e) {
                Log.d("Mylogger :",e.toString());
            }
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
            card_title.setPadding(0, this.dpToPixels(0), 0, 0);// was card_title.setPadding(0, this.dpToPixels(3), 0, 0);
            card_title.setTextSize((float) this.dpToPixels(12));// was 15
            card_title.setGravity(Gravity.CENTER);
            card_title.setTextColor(Color.parseColor("#ffffff"));

            TextView card_desc = new TextView(context);
            card_desc.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
            ));
            card_desc.setHeight(this.dpToPixels(55));
            card_desc.setPadding(0, this.dpToPixels(5), 0, 0);
            if (i==article_list.length()) {
                //if (MainActivity.privilege > 1) {
                    card_title.setText("+");
                    card_desc.setText("Add Article");
                //}
            }else {
                card_title.setText(article_name);
                card_desc.setText("Description");
            }
            card_desc.setTextSize((float) this.dpToPixels(12));
            card_desc.setGravity(Gravity.CENTER);
            card_desc.setTextColor(Color.parseColor("#ffffff"));
            card_desc.setBackgroundColor(Color.parseColor("#dee0e2"));

            cardInner.addView(card_title);
            cardInner.addView(card_desc);
            card.addView(cardInner);

            final String final_article_id =article_id;//specially for call
            final String final_article_name =article_name;//specially for call

            if (i==article_list.length()){
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // item clicked
                        Log.d("Clicked: ", Integer.toString(v.getId()));
                        context.startActivity(new Intent(context.getApplicationContext(), CreateArticle.class));
                    }
                });
            }else {
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // item clicked
                        Log.d("Clicked: ", Integer.toString(v.getId()));
                        //dataAPI.get_articles_content(final_article_id);
                        Intent intent = new Intent(context.getApplicationContext(), ArticleContent.class);
                        intent.putExtra("article_id",final_article_id);
                        intent.putExtra("article_name",final_article_name);
                        ArticleContent.recreateCount =0;
                        context.startActivity(intent);
                    }
                });
            }

            this.addView(card);
        }
    }

    public ArticleCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateLayout(context);
    }

    public ArticleCard(Context context) {
        super(context);
        inflateLayout(context);
    }

}

