package com.example.focibajnoksag;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TeamAdapter extends BaseAdapter {
    private Context context;
    private int[] teamLogos;
    private String[] teamNames;

    public TeamAdapter(Context context, int[] teamLogos, String[] teamNames) {
        this.context = context;
        this.teamLogos = teamLogos;
        this.teamNames = teamNames;
    }

    @Override
    public int getCount() {
        return teamLogos.length;
    }

    @Override
    public Object getItem(int position) {
        return teamLogos[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout container;
        if (convertView == null) {

            container = new LinearLayout(context);
            container.setOrientation(LinearLayout.VERTICAL);
            container.setGravity(Gravity.CENTER);

            int imageSize = (int)(120 * context.getResources().getDisplayMetrics().density);
            container.setPadding(8, 8, 8, 8);

            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(imageSize, imageSize);
            imageView.setLayoutParams(imageParams);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            container.addView(imageView);

            TextView textView = new TextView(context);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(14);
            LinearLayout.LayoutParams textParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
            textParams.topMargin = 4;
            textView.setLayoutParams(textParams);
            container.addView(textView);
        } else {
            container = (LinearLayout) convertView;
        }

        ImageView imageView = (ImageView) container.getChildAt(0);
        TextView textView = (TextView) container.getChildAt(1);
        imageView.setImageResource(teamLogos[position]);
        textView.setText(teamNames[position]);

        container.clearAnimation();
        container.startAnimation(AnimationUtils.loadAnimation(context, R.anim.homeactivityanimation));

        return container;
    }
}