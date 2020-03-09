package com.example.krittest3.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.krittest3.R;
import com.example.krittest3.models.City;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityHolder> implements Filterable {

    private static final String LOG_TAG = CityAdapter.class.getName();

    private View.OnClickListener onItemClickListener;

    private Context context;
    private ArrayList<City> cities;
    private ArrayList<City> citiesSearch;

    public CityAdapter(Context context, ArrayList<City> cities) {
        this.context = context;
        this.cities = cities;
        this.citiesSearch = new ArrayList<City>(cities);
    }

    @Override
    public CityHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);

        return new CityHolder(view);
    }

    @Override
    public void onBindViewHolder(CityHolder holder, int position) {
        City city = cities.get(position);

        holder.setDetails(city);
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    @Override
    public Filter getFilter() {
        return cityFilter;
    }

    private Filter cityFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<City> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(citiesSearch);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (City city : citiesSearch) {
                    if (city.getName().toLowerCase().startsWith(filterPattern)) {
                        filteredList.add(city);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            cities.clear();
            cities.addAll((ArrayList<City>) results.values);
            notifyDataSetChanged();
        }
    };

    class CityHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private TextView forecastTV;

        CityHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_card_view);
            forecastTV = itemView.findViewById(R.id.forecast_card_view);
            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
        }

        void setDetails(City city) {
            textView.setText(city.getName());
            DecimalFormat df = new DecimalFormat("#.#");
            String temperature = "? C˚";
            try {
                temperature = df.format(city.getForecast().getTemperatureCel()) + " C˚";
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            } finally {
                forecastTV.setText(temperature);
            }
        }
    }

    public void updateWithForecast() {
        citiesSearch.clear();
        citiesSearch.addAll(cities);
    }

    public void setItemClickListener(View.OnClickListener clickListener) {
        onItemClickListener = clickListener;
    }
}
