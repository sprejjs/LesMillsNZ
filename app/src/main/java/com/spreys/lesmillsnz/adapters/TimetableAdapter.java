package com.spreys.lesmillsnz.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spreys.lesmillsnz.R;
import com.spreys.lesmillsnz.fragments.TimetableFragment;
import com.spreys.lesmillsnz.model.Class;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 12/10/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class TimetableAdapter extends CursorAdapter {

    /**
     * Default construction
     * @param context application context
     * @param c data cursor
     * @param flags flags
     */
    public TimetableAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_class_listitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String classTime = cursor.getString(TimetableFragment.COL_CLASS_TIME);
        DateFormat formatter = new SimpleDateFormat("hh:mm:ss");
        Time time = null;
        try {
            time = new Time(formatter.parse(classTime).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String className = cursor.getString(TimetableFragment.COL_CLASS_NAME);
        String classWeekDay = cursor.getString(TimetableFragment.COL_CLASS_WEEK_DAY);
        int clubId = cursor.getInt(TimetableFragment.COL_CLASS_CLUB_ID);

        Class newClass = new Class(className, classWeekDay, time, clubId);
        ViewHolder viewHolder = (ViewHolder)view.getTag();
        viewHolder.txtClassName.setText(newClass.getName());
        viewHolder.txtClassTime.setText(newClass.getTime().toString());
    }

    public static class ViewHolder {
        public final TextView txtClassTime;
        public final TextView txtClassName;

        public ViewHolder(View view){
            txtClassTime = (TextView)view.findViewById(R.id.txtClassTime);
            txtClassName = (TextView)view.findViewById(R.id.txtClassName);
        }
    }
}
