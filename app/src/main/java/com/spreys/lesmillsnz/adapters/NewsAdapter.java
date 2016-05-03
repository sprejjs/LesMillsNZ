package com.spreys.lesmillsnz.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spreys.lesmillsnz.R;
import com.spreys.lesmillsnz.fragments.NewsFragment;
import com.spreys.lesmillsnz.model.NewsArticle;

import java.util.List;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 19/10/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class NewsAdapter extends CursorAdapter {

    /**
     * Default constructor
     * @param context application context
     * @param c data base cursor
     * @param flags list of flags
     */
    public NewsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_news_listitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Read article from DB
        int clubId = cursor.getInt(NewsFragment.COL_CLUB_ID);
        String headline = cursor.getString(NewsFragment.COL_HEADLINE);
        String description = cursor.getString(NewsFragment.COL_DESC);
        String content = cursor.getString(NewsFragment.COL_CONT);
        String base64 = cursor.getString(NewsFragment.COL_IMAGE);

        NewsArticle article = new NewsArticle(headline, content, clubId);
        article.setShortDescription(description);
        article.setImage(base64);

        //Set the data
        ViewHolder viewHolder = (ViewHolder)view.getTag();
        viewHolder.imgNewsImage.setImageBitmap(article.getImage());
        viewHolder.txtNewsHeadline.setText(article.getHeadline());
        viewHolder.txtNewsShortDesc.setText(article.getShortDescription());
    }

    /**
     * Cache for children views for the news list item
     */
    public static class ViewHolder {
        public final ImageView imgNewsImage;
        public final TextView txtNewsHeadline;
        public final TextView txtNewsShortDesc;

        public ViewHolder(View view){
            imgNewsImage = (ImageView)view.findViewById(R.id.imgNewsImage);
            txtNewsHeadline = (TextView)view.findViewById(R.id.txtNewsHeadline);
            txtNewsShortDesc = (TextView)view.findViewById(R.id.txtNewsShortDesc);
        }
    }
}
