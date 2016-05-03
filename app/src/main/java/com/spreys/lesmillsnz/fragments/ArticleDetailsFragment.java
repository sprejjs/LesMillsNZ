package com.spreys.lesmillsnz.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.spreys.lesmillsnz.R;
import com.spreys.lesmillsnz.data.DataContract.*;
import com.spreys.lesmillsnz.model.NewsArticle;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 19/10/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class ArticleDetailsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>{
    private static final int ARTICLE_LOADER = 4;
    public static final String ARG_DISPLAY_IMAGE_VIEW = "article_details_fragment_display_image";
    public static final String ARG_ART_ID = "article_details_fragment_art_id";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(ARTICLE_LOADER, null, this);

        return inflater.inflate(R.layout.article_details_fragment, container, false);
    }

    private int getArticleId(){
        if(getArguments() != null){
           return getArguments().getInt(ARG_ART_ID);
        }

        return -1;
    }

    private boolean getDisplayImageView(){
        return getArguments() == null || getArguments().getBoolean(ARG_DISPLAY_IMAGE_VIEW);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri articleDetailsUri = NewsArticleEntry.buildArticleUri(getArticleId());

        return new CursorLoader(
                getActivity(),
                articleDetailsUri,
                NewsFragment.NEWS_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if(cursor.moveToFirst()){
            int club_id = cursor.getInt(NewsFragment.COL_CLUB_ID);
            String headline = cursor.getString(NewsFragment.COL_HEADLINE);
            String shortDescription = cursor.getString(NewsFragment.COL_DESC);
            String content = cursor.getString(NewsFragment.COL_CONT);
            String base64Image = cursor.getString(NewsFragment.COL_IMAGE);

            NewsArticle article = new NewsArticle(headline, content, club_id);
            article.setShortDescription(shortDescription);
            article.setImage(base64Image);

            ImageView imgNewsDetailsImage = (ImageView)getActivity()
                    .findViewById(R.id.imgNewsDetailsImage);

            if(!getDisplayImageView()){
                imgNewsDetailsImage.setVisibility(View.GONE);
            } else {
                imgNewsDetailsImage.setImageBitmap(article.getImage());
            }

            WebView webView = (WebView)getActivity().findViewById(R.id.webViewArticleContent);
            webView.loadData(article.getContent(), "text/html", null);
            webView.setBackgroundColor(0x00FFFFFF);

            //Open links in the default browser
            webView.setWebViewClient(new WebViewClient(){
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url != null && url.startsWith("http://")) {
                        view.getContext().startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        getLoaderManager().restartLoader(ARTICLE_LOADER, null, this);
    }
}
