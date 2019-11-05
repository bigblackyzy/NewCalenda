package com.example.myapplication;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NewCalenda extends LinearLayout {

    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grdi;
    private Calendar curDate = Calendar.getInstance();
    private String displayFormat;

    public NewCalendaListener listener;

    public NewCalenda(Context context) {
        super(context);
    }

    public NewCalenda(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context,attrs);
    }

    public NewCalenda(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context,attrs);
    }

    //实现时间绑定，渲染
    private void initControl(Context context,AttributeSet attrs){
        bindControl(context);
        bindControlEvent();
        renderCalendar();

        /*TypedArray ta = getContext().obtainStyledAttributes(attrs,R.styleable.NewCalenda);
        try {
            String format = ta.getString(R.styleable.NewCalenda_dateFormat);
            displayFormat = format;
            if (displayFormat == null){
                displayFormat = "MMM yyy";
            }
        }finally {
            ta.recycle();
        }
        renderCalendar();*/
    }

    private void bindControlEvent() {
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                 curDate.add(Calendar.MONTH,-1);
                 renderCalendar();
            }
        });
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                curDate.add(Calendar.MONTH,1);
                renderCalendar();
            }
        });
    }

    private void bindControl(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.calenda_view,this);
        btnPrev = (ImageView)findViewById(R.id.btnPrev);
        btnNext = (ImageView)findViewById(R.id.btnNext);
        txtDate = (TextView)findViewById(R.id.txtDate);
        grdi = (GridView)findViewById(R.id.calendar_grid);
    }

    private void renderCalendar() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyy");
        txtDate.setText(sdf.format(curDate.getTime()));

        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar) curDate.clone();

        calendar.set(Calendar.DAY_OF_MONTH,1);
        int prevDays = calendar.get(Calendar.DAY_OF_WEEK)-1;
        calendar.add(Calendar.DAY_OF_MONTH,-prevDays);

        int maxCellCount = 6*7;
        while (cells.size()<maxCellCount){
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH,1);
        }
        grdi.setAdapter(new CalendarAdapter(getContext(),cells));
        grdi.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener == null) {
                    return false;
                } else {
                    listener.onItemLongPress((Date) parent.getItemAtPosition(position));
                    return true;
                }
            }
        });
    }

    private class CalendarAdapter extends ArrayAdapter<Date>{

        LayoutInflater inflater;

        public CalendarAdapter(Context context, ArrayList<Date> days) {
            super(context, R.layout.calenda_text_day,days);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Date date = getItem(position);
            if (convertView == null){
                convertView = inflater.inflate(R.layout.calenda_text_day,parent,false);
            }

            int day = date.getDate();
            ((TextView)convertView).setText(String.valueOf(day));

            Date now = new Date();
            Boolean isTheSameMonth = false;
            if (date.getMonth()==now.getMonth()){
                isTheSameMonth = true;
            }
            /*Calendar calendar = (Calendar) curDate.clone();
            calendar.set(Calendar.DAY_OF_MONTH,1);
            int daysInMonth = calendar.getActualMaximum(Calendar.DATE);*/
            if (isTheSameMonth){
                ((TextView)convertView).setTextColor(Color.parseColor("#000000"));
            }else {
                ((TextView)convertView).setTextColor(Color.parseColor("#9d9d9d"));
            }

            if (now.getDate() == date.getDate() && now.getMonth() == date.getMonth()
                    && now.getYear() == date.getYear()){
                ((TextView)convertView).setTextColor(Color.parseColor("#ff0000"));
                ((Calenda_day_textview)convertView).isToday = true;
            }

            return convertView;
        }
    }

    public interface NewCalendaListener{
        void onItemLongPress(Date day);
    }
}
