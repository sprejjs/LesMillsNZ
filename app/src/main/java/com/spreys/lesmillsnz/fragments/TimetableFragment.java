package com.spreys.lesmillsnz.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.spreys.lesmillsnz.R;
import com.spreys.lesmillsnz.adapters.TimetableAdapter;
import com.spreys.lesmillsnz.data.DataContract;
import com.spreys.lesmillsnz.data.DataContract.ClassEntry;
import com.spreys.lesmillsnz.model.Class;
import com.spreys.lesmillsnz.sync.GetTimetableService;
import com.spreys.lesmillsnz.utils.DisplayFragmentInterface;
import com.spreys.lesmillsnz.utils.PreferenceProvider;
import com.spreys.lesmillsnz.utils.TabManager;
import com.spreys.lesmillsnz.utils.UiUtils;
import com.spreys.lesmillsnz.utils.Utils;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 27/09/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class TimetableFragment extends Fragment implements DisplayFragmentInterface,
        LoaderManager.LoaderCallbacks<Cursor> {
    private IntentFilter filter;
    private BroadcastReceiver receiver;
    private int selectedDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    private int currentClubId = -1;
    private TimetableAdapter mTimetableAdapter;
    private int TIMETABLE_LOADER = 3;

    private static final String[] CLASS_COLUMNS = {
            ClassEntry.TABLE_NAME + "." + ClassEntry._ID,
            ClassEntry.COLUMN_CLUB_ID,
            ClassEntry.COLUMN_INTENSITY,
            ClassEntry.COLUMN_DURATION,
            ClassEntry.COLUMN_NAME,
            ClassEntry.COLUMN_IMAGE,
            ClassEntry.COLUMN_WEEK_DAY,
            ClassEntry.COLUMN_TIME,
            ClassEntry.COLUMN_DESC,
            ClassEntry.COLUMN_LOC
    };

    public static final int COL_CLASS_ID = 0;
    public static final int COL_CLASS_CLUB_ID = 1;
    public static final int COL_CLASS_INTENSITY = 2;
    public static final int COL_CLASS_DURATION = 3;
    public static final int COL_CLASS_NAME = 4;
    public static final int COL_CLASS_IMAGE = 5;
    public static final int COL_CLASS_WEEK_DAY = 6;
    public static final int COL_CLASS_TIME = 7;
    public static final int COL_CLASS_DESC = 8;
    public static final int COL_CLASS_LOC = 9;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mTimetableAdapter = new TimetableAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.timetable_fragment, container, false);
        Button btnPreviousDay = (Button)rootView.findViewById(R.id.btnPreviousDay);
        btnPreviousDay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                changeDate(false);
            }
        });
        Button btnNextDay = (Button)rootView.findViewById(R.id.btnNextDay);
        btnNextDay.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                changeDate(true);
            }
        });
        filter = new IntentFilter();
        filter.addAction(GetTimetableService.ACTION_CLASSES_UPDATED);
        filter.addAction(GetTimetableService.ACTION_UNABLE_TO_GET_CLASSES);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleBroadcast(intent);
            }
        };

        getActivity().registerReceiver(receiver, filter);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    /**
     * Changes the current day, either moving one day forward or back
     * @param moveForward boolean, moves day forward if set to true
     */
    private void changeDate(boolean moveForward){
        if(moveForward){
            if(selectedDay < 7){
                selectedDay ++;
            } else {
                selectedDay = 1;
            }
        } else {
            if(selectedDay > 1){
                selectedDay --;
            } else {
                selectedDay = 7;
            }
        }

        getLoaderManager().restartLoader(TIMETABLE_LOADER, null, this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(TIMETABLE_LOADER, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(receiver, filter);
        getLoaderManager().restartLoader(TIMETABLE_LOADER, null, this);

        //Do not do anything if view is not visible to the user
        if(!getUserVisibleHint()){
            return;
        }

        //If application opened for the fist time, then open preferences for user to review
        if(PreferenceProvider.IsOpenedFirstTime(getActivity())){
            ((TabManager)getActivity()).openTab(2);
        }

    }

    /**
     * Fills up the list view with the classes for a selected day
     */
    private void attachListViewAdapter(){
        if(getActivity() != null){
            ListView listViewTimetable = (ListView) getActivity().findViewById(R.id.listViewTimetable);
            //Set adapter
            listViewTimetable.setAdapter(mTimetableAdapter);

            //OnClick listener
            listViewTimetable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TimetableAdapter adapter = (TimetableAdapter)parent.getAdapter();

                    Cursor cursor = adapter.getCursor();

                    String intensity = cursor.getString(COL_CLASS_INTENSITY);
                    String duration = cursor.getString(COL_CLASS_DURATION);
                    int clubId = cursor.getInt(COL_CLASS_CLUB_ID);
                    String name = cursor.getString(COL_CLASS_NAME);
                    String image = cursor.getString(COL_CLASS_IMAGE);
                    String weekDay = cursor.getString(COL_CLASS_WEEK_DAY);
                    String classTime = cursor.getString(COL_CLASS_TIME);
                    DateFormat formatter = new SimpleDateFormat("hh:mm:ss");
                    Time time = null;
                    try {
                        time = new Time(formatter.parse(classTime).getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String description = cursor.getString(COL_CLASS_DESC);
                    String location = cursor.getString(COL_CLASS_LOC);

                    Class selectedClass = new Class(name, weekDay, time, clubId);
                    selectedClass.setIntensity(intensity);
                    selectedClass.setDuration(duration);
                    selectedClass.setImage(image);
                    selectedClass.setDescription(description);
                    selectedClass.setLocation(location);

                    openPopUpView(selectedClass);
                }
            });

            //Set day
            String weekDayAsString = Utils.DayOfTheWeekFromInt(selectedDay);
            ((TextView) getActivity().findViewById(R.id.txtDayOfTheWeek)).setText(weekDayAsString);
        }
    }

    /**
     * Opens a pop up view with class details.
     * @param selectedClass reference to a Class object, details of which will be displayed
     */
    private void openPopUpView(final Class selectedClass){
        LayoutInflater inflater = (LayoutInflater)
                getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Prepare the view
        final View popupView = inflater.inflate(R.layout.class_popup, null, false);
        ((TextView)popupView.findViewById(R.id.txtPopUpClassName)).setText(selectedClass.getName());
        ((TextView)popupView.findViewById(R.id.txtPopUpDescription)).setText(selectedClass.getDescription());
        ((TextView)popupView.findViewById(R.id.txtPopUpDuration)).setText(selectedClass.getDuration());
        ((TextView)popupView.findViewById(R.id.txtPopUpIntensity)).setText(selectedClass.getIntensity());

        //Get screen width
        int screenWidth = getActivity().getResources().getDisplayMetrics().widthPixels;

        //Create a pop up
        final PopupWindow pw = new PopupWindow(
                popupView,
                (int)(screenWidth * 0.8),//80% of the screen width
                LayoutParams.WRAP_CONTENT,
                true);
        pw.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));

        //Close button
        popupView.findViewById(R.id.btnPopupClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });

        //Share button
        popupView.findViewById(R.id.btnPopupShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareClass(selectedClass);
            }
        });

        //Calendar button
        popupView.findViewById(R.id.btnPopupCalendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClassToCalendar(selectedClass);
            }
        });

        //Display pop-up
        pw.showAtLocation(getActivity().findViewById(R.id.timetable_fragment), Gravity.CENTER, 0, 0);
    }

    /**
     * Handles TimetableServiceResponse
     *      ACTION_CLASSES_UPDATED updates UI
     * @param intent Intent received from the service
     */
    private void handleBroadcast(Intent intent){
        String action = intent.getAction();

        if(action.equals(GetTimetableService.ACTION_CLASSES_UPDATED)){
            UiUtils.HideLoadingSpinner(getActivity());
            attachListViewAdapter();
        }
    }

    /**
     * Creates an intent to add a class to the calendar.
     * @param classToBeAdded reference to the class
     */
    private void addClassToCalendar(Class classToBeAdded){

        if (Build.VERSION.SDK_INT >= 14) {
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, classToBeAdded.getDate().getTimeInMillis())
                    .putExtra(
                            CalendarContract.EXTRA_EVENT_END_TIME,
                            classToBeAdded.getDate().getTimeInMillis() + classToBeAdded.getDurationInMills()
                    )
                    .putExtra(Events.TITLE, classToBeAdded.getName())
                    .putExtra(Events.DESCRIPTION, classToBeAdded.getDescription())
                    .putExtra(Events.EVENT_LOCATION, classToBeAdded.getLocation())
                    .putExtra(Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("beginTime", classToBeAdded.getDate().getTimeInMillis());
            intent.putExtra("endTime", classToBeAdded.getDate().getTimeInMillis()
                    + classToBeAdded.getDurationInMills());
            intent.putExtra("title", classToBeAdded.getName());
            intent.putExtra("description", classToBeAdded.getDescription());
            startActivity(intent);
        }
    }

    /**
     * Users the standard sharing dialog to share the details of the class
     * @param classToShare reference to the shared class
     */
    private void shareClass(Class classToShare){

        Cursor mCursor = getActivity().getContentResolver().query(
                DataContract.ClubEntry.CONTENT_URI,
                PreferencesFragment.CLUB_COLUMNS,
                DataContract.ClubEntry._ID + " = ?",
                new String[]{String.valueOf(classToShare.getClubId())},
                null
        );

        String clubName = null;
        if (mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            clubName = mCursor.getString(PreferencesFragment.COL_CLUB_NAME);
        }

        String classDate = new SimpleDateFormat("HH:mm, dd MMMM").format(classToShare.getDate().getTime());

        Intent intent=new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        if (Build.VERSION.SDK_INT >= 21) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }

        // Add data to the intent, the receiving app will decide what to do with it.
        intent.putExtra(
                Intent.EXTRA_SUBJECT,
                String.format("%s at Les Mills %s", classToShare.getName(), clubName)
        );

        intent.putExtra(
                Intent.EXTRA_TEXT,
                String.format(
                    "Keen to join me for %s? %s. Les Mills %s #lesmillsapp.",
                    classToShare.getName(),
                    classDate,
                    clubName
                )
        );

        startActivity(Intent.createChooser(intent, "Select application to share the class details"));
    }

    @Override
    public void fragmentDisplayed() {
        if(isAdded() && currentClubId != PreferenceProvider.GetPreferredClub(getActivity())) {
            getLoaderManager().restartLoader(TIMETABLE_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        currentClubId = PreferenceProvider.GetPreferredClub(getActivity());
        return new android.support.v4.content.CursorLoader(
                getActivity(),
                ClassEntry.buildClassesForClubAndWeekDayUri(
                        currentClubId,
                        selectedDay),
                CLASS_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mTimetableAdapter.swapCursor(cursor);
        attachListViewAdapter();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mTimetableAdapter.swapCursor(null);
    }
}
