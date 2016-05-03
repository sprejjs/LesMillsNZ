package com.spreys.lesmillsnz.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.spreys.lesmillsnz.R;
import com.spreys.lesmillsnz.data.DataContract;
import com.spreys.lesmillsnz.fragments.ArticleDetailsFragment;
import com.spreys.lesmillsnz.fragments.NewsFragment;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 19/10/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class ArticleDetailsActivity extends ActionBarActivity {
    public static final String KEY_ARTICLE_ID = "key_article_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            int article_id = getIntent().getIntExtra(KEY_ARTICLE_ID, -1);
            if(article_id == -1){
                throw new IllegalArgumentException("Article ID is not supplied");
            }

            Cursor mCursor = getContentResolver().query(
                    DataContract.NewsArticleEntry.buildArticleUri(article_id),
                    NewsFragment.NEWS_COLUMNS,
                    null,
                    null,
                    null
            );

            String headline = null;
            if (mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                headline = mCursor.getString(NewsFragment.COL_HEADLINE);
            }
            setTitle(headline);

            ArticleDetailsFragment fragment = new ArticleDetailsFragment();

            Bundle args = new Bundle();
            args.putInt(ArticleDetailsFragment.ARG_ART_ID, article_id);
            args.putBoolean(ArticleDetailsFragment.ARG_DISPLAY_IMAGE_VIEW, true);
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.article_detail_activity_container, fragment)
                    .commit();
        }
    }
}
