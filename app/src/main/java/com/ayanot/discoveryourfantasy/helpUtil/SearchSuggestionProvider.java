package com.ayanot.discoveryourfantasy.helpUtil;

import android.content.SearchRecentSuggestionsProvider;

public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.ayanot.discoveryourfantasy.helpUtil.SearchSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;// | DATABASE_MODE_2LINES;;

    public SearchSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

}
