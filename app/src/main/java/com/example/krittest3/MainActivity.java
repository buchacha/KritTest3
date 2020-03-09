package com.example.krittest3;

import androidx.appcompat.app.AppCompatActivity;
import android.app.LoaderManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.krittest3.adapter.CityAdapter;
import com.example.krittest3.models.City;

import java.io.InputStream;
import java.util.ArrayList;

//        1. На экране должен быть доступен текстовый поиск городов
//        с отображением текущей погоды в них (можно использовать
//        любой бесплатный сервис, например https://developer.accuweather.com/).
//        2. После выбора города, на экране так же должна отображаться погода
//        в выбранном городе, в формате: "+18, облачно с прояснениями".
//        Приветствуется вывод дополнительной информации.
//        Особенности:
//        - На время загрузок данных в UI должны отображаться прогрессы.
//        - Нужна корректная поддержка поворотов экрана.
//        - Плюсом будет предусмотрение кэша городов и погод городов,
//        на случай отсутствия интернета и выводом соответствующей информации
//        (предпочтительно использовать локальную БД).
//        https://developer.tech.yandex.ru/services/3 - geo coder
//        https://darksky.net/dev/account - погода

//https://www.youtube.com/watch?v=sJ-Z9G0SDhc
// https://stackoverflow.com/questions/30398247/how-to-filter-a-recyclerview-with-a-searchview

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<City>>{

    private static final String LOG_TAG = MainActivity.class.getName();

    private int LOADER_ID = 1;

    private RecyclerView mRecyclerView;
    private CityAdapter mAdapter;
    private ArrayList<City> mCitiesArrayList;
    private SearchView mSearchView;
    private ProgressBar mProgressBar;

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            City city = mCitiesArrayList.get(position);
            try {
                FrameLayout frameLayout = viewHolder.itemView.findViewById(R.id.frame_layout);
                if (frameLayout.getVisibility() == View.GONE) {
                    frameLayout.setVisibility(View.VISIBLE);
                    String forecastSummary = city.getForecast().getSummary();
                    String toastText = forecastSummary;
                    TextView description = frameLayout.findViewById(R.id.desc_text_card);
                    description.setText(toastText);
                } else if (frameLayout.getVisibility() == View.VISIBLE) {
                    frameLayout.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                Log.e(LOG_TAG, "forecast isn't exist for this city", e);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        readCsvCities();
        mAdapter = new CityAdapter(this, mCitiesArrayList);
        mAdapter.setItemClickListener(onItemClickListener);
        mRecyclerView.setAdapter(mAdapter);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        startLoading();

        getSupportActionBar().setTitle("Прогноз погоды");
    }

//    Loader callback override methods

    @Override
    public Loader<ArrayList<City>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(QueryUtils.BASE_URL);

        return new CityForecastsLoader(this, baseUri.toString(), mCitiesArrayList);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<City>> loader, ArrayList<City> citiesWithForecast) {
        mCitiesArrayList.clear();
        mCitiesArrayList.addAll(citiesWithForecast);
        mAdapter.updateWithForecast();
        mAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<City>> loader) {}

//    Loader init

    private void startLoading() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            mProgressBar.setVisibility(View.VISIBLE);
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(LOADER_ID, null, this);
        }
    }

//    Init procedures

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchView = (SearchView)findViewById(R.id.search_view);
        mProgressBar = (ProgressBar)findViewById(R.id.loading_indicator);
    }

    private void readCsvCities() {
        mCitiesArrayList = new ArrayList<>();
        InputStream inputStream = getResources().openRawResource(R.raw.cities);
        CityCSVFile cityCsvFile = new CityCSVFile(inputStream);
        mCitiesArrayList.addAll(cityCsvFile.read());
    }
}