package com.ayanot.discoveryourfantasy;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ayanot.discoveryourfantasy.helpUtil.SearchSuggestionProvider;

/**
 * <h3>Класс-активити, строится на основании {@link R.layout#activity_search_results}
 * Предназачен для отображения результатов поиска изображения на disk</h3>
 *
 * <p>Поиск осуществляется с помощью регулярного выражения, проверяющего названия
 * изображения на вхождения введенной последовательности букв</p>
 *
 * @author ivan
 * @version 0.0.1
 */
public class SearchResultsActivity extends AppCompatActivity {

    Toolbar toolbar;
    private String query;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        handleIntent(getIntent());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * <p>Перехват запроса поиска, если Intent = ACTION_SEARCH
     * Сохраняет историю запросов с помощью
     * {@link SearchRecentSuggestions}</p>
     *
     * @param intent - интент прищедший с {@link MainActivity}, и содержащий
     *               query для поиска
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            addFragment();
        }
    }

    private void addFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(SearchResultsActivity.class.getSimpleName(), query);
        ContentImageForSearchFragment fragment = new ContentImageForSearchFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.search_results_activity, fragment).commit();
    }
}
