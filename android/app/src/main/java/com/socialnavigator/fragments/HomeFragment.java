package com.socialnavigator.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.socialnavigator.R;
import com.socialnavigator.activities.DetailActivity;
import com.socialnavigator.adapters.DiscountAdapter;
import com.socialnavigator.models.Discount;
import com.socialnavigator.storage.DiscountRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvDiscounts;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvEmpty;

    private DiscountAdapter adapter;
    private final List<Discount> discounts = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvDiscounts = view.findViewById(R.id.rvDiscounts);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        rvDiscounts.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new DiscountAdapter(discounts, discount -> {
            Intent intent = new Intent(requireActivity(), DetailActivity.class);
            intent.putExtra("discount_id", discount.getId());
            startActivity(intent);
        });
        rvDiscounts.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(() -> {
            DiscountRepository.getInstance().loadTestData();
            refreshList();
            swipeRefresh.setRefreshing(false);
        });

        // Загружаем данные при первом показе
        DiscountRepository.getInstance().loadTestData();
        refreshList();
    }

    private void refreshList() {
        discounts.clear();
        discounts.addAll(DiscountRepository.getInstance().getAll());
        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(discounts.isEmpty() ? View.VISIBLE : View.GONE);
    }
}