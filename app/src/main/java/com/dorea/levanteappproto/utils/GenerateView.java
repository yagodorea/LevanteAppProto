package com.dorea.levanteappproto.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.dorea.levanteappproto.R;

import java.util.List;

public class GenerateView {

    private static final String TAG = "GenerateView";

    public static LinearLayout generateTitleDescription(String title, String description, Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);

        TextView viewTitle = new TextView(context);
        viewTitle.setLayoutParams(layoutParams);
        viewTitle.setText(title);
        viewTitle.setTextColor(Color.BLACK);
        viewTitle.setTextSize(16);
        viewTitle.setTypeface(Typeface.DEFAULT_BOLD);

        TextView viewDescription = new TextView(context);
        LayoutParams layoutParams2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams2.setMargins(10,0,10,20);
        viewDescription.setLayoutParams(layoutParams2);
        viewDescription.setTextColor(Color.BLACK);
        viewDescription.setText(description);
        viewDescription.setTextSize(14);

        layout.addView(viewTitle);
        layout.addView(viewDescription);
        return layout;
    }

    public static LinearLayout generateList(List<String> elements, Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);

        return layout;
    }
}
