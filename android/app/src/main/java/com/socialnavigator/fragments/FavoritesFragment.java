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

import com.socialnavigator.R;
import com.socialnavigator.activities.DetailActivity;
import com.socialnavigator.adapters.DiscountAdapter;
import com.socialnavigator.models.Discount;
import com.socialnavigator.storage.DiscountRepository;
import com.socialnavigator.storage.FavoritesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FavoritesFragment extends Fragment {

    private RecyclerView rvFavorites;
    private TextView tvEmpty;

    private DiscountAdapter adapter;
    private final List<Discount> favorites = new ArrayList<>();

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFavorites = view.findViewById(R.id.rvFavorites);
        tvEmpty = view.findViewById(R.id.tvEmptyFavorites);

        rvFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new DiscountAdapter(favorites, discount -> {
            Intent intent = new Intent(requireActivity(), DetailActivity.class);
            intent.putExtra("discount_id", discount.getId());
            startActivity(intent);
        });
        rvFavorites.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Обновляем список при каждом возврате на вкладку
        loadFavorites();
    }

    /**
     * Загружает избранные скидки.
     * Берёт ID из FavoritesManager, находит скидки в DiscountRepository.
     */
    private void loadFavorites() {
        favorites.clear();

        FavoritesManager favoritesManager = FavoritesManager.getInstance(requireContext());
        DiscountRepository repository = DiscountRepository.getInstance();

        Set<String> ids = favoritesManager.getAllIds();
        for (String idStr : ids) {
            try {
                int id = Integer.parseInt(idStr);
                Discount discount = repository.getById(id);
                if (discount != null) {
                    favorites.add(discount);
                }
            } catch (NumberFormatException e) {
                // Пропускаем некорректный ID
            }
        }

        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(favorites.isEmpty() ? View.VISIBLE : View.GONE);
    }
}