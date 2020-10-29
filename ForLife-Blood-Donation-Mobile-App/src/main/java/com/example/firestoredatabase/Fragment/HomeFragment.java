package com.example.firestoredatabase.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestoredatabase.Adapter.HomeListCardAdapter;
import com.example.firestoredatabase.Model.HomeFragmentData;
import com.example.firestoredatabase.Model.HomeListCards;
import com.example.firestoredatabase.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<HomeListCards> cards;
    private RecyclerView.LayoutManager layoutManager;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.home_list);

        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        //doesn't working X_X_X
        /*DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(getContext(),
                R.drawable.divider));*/
        cards = new ArrayList<>();
        loadCards();
        return view;
    }

    private void loadCards() {
        for (int i = 0; i < HomeFragmentData.listArray.length; i++)
        {
            cards.add(new HomeListCards(HomeFragmentData.drawableArray[i],
                    HomeFragmentData.listArray[i]));

            adapter = new HomeListCardAdapter(cards, getContext());
            recyclerView.setAdapter(adapter);
        }
    }
}
