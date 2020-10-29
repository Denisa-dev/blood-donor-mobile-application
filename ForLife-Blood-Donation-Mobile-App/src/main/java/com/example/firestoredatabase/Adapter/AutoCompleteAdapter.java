package com.example.firestoredatabase.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.android.libraries.places.api.model.TypeFilter.ESTABLISHMENT;

public class AutoCompleteAdapter extends ArrayAdapter<AutocompletePrediction> implements Filterable {

    private List<AutocompletePrediction> mResultList;
    private PlacesClient placesClient;

    public AutoCompleteAdapter(Context context, PlacesClient placesClient) {
        super(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1);
        this.placesClient = placesClient;
    }

    @Override
    public int getCount() {
        return mResultList.size();
    }

    @Nullable
    @Override
    public AutocompletePrediction getItem(int position) {
        return mResultList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = super.getView(position, convertView, parent);

        AutocompletePrediction item = getItem(position);

        TextView textView1 = row.findViewById(android.R.id.text1);
        TextView textView2 = row.findViewById(android.R.id.text2);

        if (item != null) {
            textView1.setText(item.getPrimaryText(null));
            textView2.setText(item.getSecondaryText(null));
        }
        return row;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();

                List<AutocompletePrediction> filterData = new ArrayList<>();

                if (charSequence != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    filterData = getAutoComplete(charSequence);
                }

                results.values = filterData;
                if (filterData != null) {
                    results.count = filterData.size();
                } else {
                    results.count = 0;
                }
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                try {
                    if (filterResults != null && filterResults.count > 0) {
                        // The API returned at least one result, update the data.
                        mResultList = (List<AutocompletePrediction>) filterResults.values;
                        notifyDataSetChanged();

                    } else {
                        // The API did not return any results, invalidate the data set.
                        notifyDataSetInvalidated();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                // Override this method to display a readable result in the AutocompleteTextView
                // when clicked.
                if (resultValue instanceof AutocompletePrediction) {
                    return ((AutocompletePrediction) resultValue).getFullText(null);
                } else {
                    return super.convertResultToString(resultValue);
                }
            }
        };
    }

    private List<AutocompletePrediction> getAutoComplete(CharSequence constraint){
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(43.59703, 20.24181),
                new LatLng(48.28633, 30.27896));

        final FindAutocompletePredictionsRequest.Builder requestBuilder =
                FindAutocompletePredictionsRequest.builder()
                .setQuery(constraint.toString())
                .setCountry("RO")
                .setLocationBias(bounds)
                .setSessionToken(AutocompleteSessionToken.newInstance())
                .setTypeFilter(ESTABLISHMENT);

        Task<FindAutocompletePredictionsResponse> result =
                placesClient.findAutocompletePredictions(requestBuilder.build());

        try {
            Tasks.await(result, 2000, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        if(result.isSuccessful()){
            if(result.getResult() != null){
                return result.getResult().getAutocompletePredictions();
            }
            return null;
        }else
        {
            return null;
        }
    }
}
