package com.spreys.lesmillsnz.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.spreys.lesmillsnz.activities.ArticleDetailsActivity;
import com.spreys.lesmillsnz.adapters.NewsAdapter;
import com.spreys.lesmillsnz.R;
import com.spreys.lesmillsnz.data.DataContract.*;
import com.spreys.lesmillsnz.sync.GetNewsService;
import com.spreys.lesmillsnz.utils.DisplayFragmentInterface;
import com.spreys.lesmillsnz.utils.PreferenceProvider;
import com.spreys.lesmillsnz.utils.UiUtils;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 27/09/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class NewsFragment extends Fragment implements DisplayFragmentInterface,
        LoaderManager.LoaderCallbacks<Cursor>{
    private int NEWS_LOADER = 0;
    private IntentFilter filter;
    private BroadcastReceiver receiver;
    private NewsAdapter adapter;
    private int currentClubId = -1;

    public static final String[] NEWS_COLUMNS = {
            NewsArticleEntry.TABLE_NAME + "." + NewsArticleEntry._ID,
            NewsArticleEntry.COLUMN_CLUB_ID,
            NewsArticleEntry.COLUMN_HEADLINE,
            NewsArticleEntry.COLUMN_DESC,
            NewsArticleEntry.COLUMN_CONT,
            NewsArticleEntry.COLUMN_IMAGE
    };

    public static final int COL_ART_ID = 0;
    public static final int COL_CLUB_ID = 1;
    public static final int COL_HEADLINE = 2;
    public static final int COL_DESC = 3;
    public static final int COL_CONT = 4;
    public static final int COL_IMAGE = 5;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        adapter = new NewsAdapter(getActivity(), null, 0);

        filter = new IntentFilter();
        filter.addAction(GetNewsService.ACTION_NEWS_UPDATED);
        filter.addAction(GetNewsService.ACTION_UNABLE_TO_GET_NEWS);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleNewsResponse(intent);
            }
        };

        return inflater.inflate(R.layout.news_fragment, container, false);
    }

    private void attachListViewAdapter(){

        ListView listViewNews = (ListView) getActivity().findViewById(R.id.listViewNews);
        //Set adapter
        listViewNews.setAdapter(adapter);

        //OnClick listener
        listViewNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsAdapter adapter = (NewsAdapter)parent.getAdapter();
                Cursor cursor = adapter.getCursor();
                int articleId = cursor.getInt(COL_ART_ID);

                if(isW700dp()){
                    displayDetailFragment(articleId);
                } else {
                    Intent detailActivityIntent = new Intent(getActivity(), ArticleDetailsActivity.class);
                    detailActivityIntent.putExtra(ArticleDetailsActivity.KEY_ARTICLE_ID, articleId);
                    startActivity(detailActivityIntent);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(NEWS_LOADER, null, this);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    /**
     * Handles the response from the news service.
     *      ACTION_NEWS_UPDATED creates a new list view adapter with retrieved news
     * @param intent returned from the service intent
     */
    private void handleNewsResponse(Intent intent){
        String action = intent.getAction();

        UiUtils.HideLoadingSpinner(getActivity());

        if(action.equals(GetNewsService.ACTION_NEWS_UPDATED)){
            attachListViewAdapter();
        }
    }

    @Override
    public void fragmentDisplayed() {
        if(isAdded() && currentClubId != PreferenceProvider.GetPreferredClub(getActivity())){
            getLoaderManager().restartLoader(NEWS_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        currentClubId = PreferenceProvider.GetPreferredClub(getActivity());
        return new CursorLoader(
                getActivity(),
                NewsArticleEntry.buildArticlesByClubId(currentClubId),
                NEWS_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursor, Cursor data) {
        if(isW700dp() && isAdded()){
            data.moveToFirst();
            int articleId = data.getInt(COL_ART_ID);
            displayDetailFragment(articleId);
        }
        adapter.swapCursor(data);
        attachListViewAdapter();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(NEWS_LOADER, null, this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private void displayDetailFragment(int articleId){
        View news_details_view = getActivity().findViewById(R.id.news_details_container);

        if(news_details_view != null){
            news_details_view.setVisibility(View.VISIBLE);
        }

        ArticleDetailsFragment fragment = new ArticleDetailsFragment ();

        Bundle args = new Bundle();
        args.putInt(ArticleDetailsFragment.ARG_ART_ID, articleId);
        args.putBoolean(ArticleDetailsFragment.ARG_DISPLAY_IMAGE_VIEW, !isW700dp());
        fragment.setArguments(args);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.news_details_container, fragment)
                .commit();
    }

    private boolean isW700dp(){
        return getResources().getBoolean(R.bool.isW700dp);
    }
}
