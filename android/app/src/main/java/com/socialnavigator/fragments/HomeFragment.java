package com.socialnavigator.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.socialnavigator.R;
import com.socialnavigator.activities.DetailActivity;
import com.socialnavigator.adapters.DiscountAdapter;
import com.socialnavigator.models.Discount;
import com.socialnavigator.storage.DiscountRepository;
import com.socialnavigator.ui.FilterBottomSheet;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements FilterBottomSheet.OnFilterApplyListener {

    // UI
    private RecyclerView rvDiscounts;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvEmpty;
    private ProgressBar progressBar;

    // Data
    private DiscountAdapter adapter;
    private final List<Discount> discounts = new ArrayList<>();
    private final List<String> currentFilterTypes = new ArrayList<>();

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

        // Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbarHome);
        toolbar.setTitle("Social Navigator");

        // Город в Toolbar
        TextView tvToolbarCity = view.findViewById(R.id.tvToolbarCity);
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String city = prefs.getString("city", "Москва");
        tvToolbarCity.setText(city);

        // Кнопка фильтра
        ImageButton btnFilter = view.findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(v -> openFilterBottomSheet());

        // View
        rvDiscounts = view.findViewById(R.id.rvDiscounts);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        progressBar = view.findViewById(R.id.progressBar);

        rvDiscounts.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Адаптер
        adapter = new DiscountAdapter(discounts, discount -> {
            Intent intent = new Intent(requireActivity(), DetailActivity.class);
            intent.putExtra("discount_id", discount.getId());
            startActivity(intent);
        });
        rvDiscounts.setAdapter(adapter);

        // Swipe to refresh
        swipeRefresh.setOnRefreshListener(() -> {
            swipeRefresh.setRefreshing(false);
            loadFromApi();
        });

        // Первая загрузка
        loadFromApi();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Обновить данные при возврате (если уже загружены)
        if (!discounts.isEmpty()) {
            applyFilters();
        }
    }

    /**
     * Загружает скидки с API.
     */
    private void loadFromApi() {
        showLoading(true);
        tvEmpty.setVisibility(View.GONE);

        DiscountRepository.getInstance().loadFromApi(new DiscountRepository.LoadCallback() {
            @Override
            public void onSuccess(List<Discount> result) {
                if (!isAdded()) return;

                showLoading(false);
                applyFilters();
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;

                showLoading(false);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Ошибка загрузки:\n" + message);
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Применить фильтры (город + категория + типы).
     */
    private void applyFilters() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        String city = prefs.getString("city", "");
        String role = prefs.getString("role", "");
        String category = roleToCategory(role);

        List<Discount> filtered = DiscountRepository.getInstance()
                .getFiltered(city, category, currentFilterTypes);

        discounts.clear();
        discounts.addAll(filtered);
        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(discounts.isEmpty() ? View.VISIBLE : View.GONE);
    }

    /**
     * Преобразовать код роли в категорию API.
     */
    private String roleToCategory(String role) {
        if (role == null) return "";
        switch (role) {
            case "student":   return "Студенты";
            case "pensioner": return "Пенсионеры";
            case "svo":       return "Участники СВО";
            default:          return "";
        }
    }

    /**
     * Открыть BottomSheet с фильтром.
     */
    private void openFilterBottomSheet() {
        List<String> allTypes = DiscountRepository.getInstance().getAllTypes();
        FilterBottomSheet sheet = FilterBottomSheet.newInstance(allTypes, currentFilterTypes);
        sheet.show(getChildFragmentManager(), "filter");
    }

    /**
     * Callback из BottomSheet — выбранные типы.
     */
    @Override
    public void onFilterApplied(List<String> selectedTypes) {
        currentFilterTypes.clear();
        currentFilterTypes.addAll(selectedTypes);
        applyFilters();
    }

    /**
     * Показать/скрыть индикатор загрузки.
     */
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (rvDiscounts != null) {
            rvDiscounts.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}