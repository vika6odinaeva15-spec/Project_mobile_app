package com.socialnavigator.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.socialnavigator.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    /**
     * Callback с выбранными типами (список).
     */
    public interface OnFilterApplyListener {
        void onFilterApplied(List<String> selectedTypes);
    }

    private static final String ARG_TYPES = "types";
    private static final String ARG_SELECTED = "selected";

    private OnFilterApplyListener listener;

    public static FilterBottomSheet newInstance(List<String> allTypes, List<String> selectedTypes) {
        FilterBottomSheet sheet = new FilterBottomSheet();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_TYPES, new ArrayList<>(allTypes));
        args.putStringArrayList(ARG_SELECTED, new ArrayList<>(selectedTypes));
        sheet.setArguments(args);
        return sheet;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFilterApplyListener) {
            listener = (OnFilterApplyListener) context;
        } else if (getParentFragment() instanceof OnFilterApplyListener) {
            listener = (OnFilterApplyListener) getParentFragment();
        } else {
            throw new RuntimeException(context + " must implement OnFilterApplyListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout llTypes = view.findViewById(R.id.llTypes);
        Button btnReset = view.findViewById(R.id.btnReset);
        Button btnApply = view.findViewById(R.id.btnApply);

        Bundle args = getArguments();
        List<String> types = args != null
                ? args.getStringArrayList(ARG_TYPES)
                : Collections.emptyList();
        List<String> selected = args != null
                ? args.getStringArrayList(ARG_SELECTED)
                : Collections.emptyList();

        if (types == null) types = Collections.emptyList();
        if (selected == null) selected = Collections.emptyList();

        // Программно добавляем CheckBox'ы
        for (String type : types) {
            CheckBox cb = new CheckBox(requireContext());
            cb.setText(type);
            cb.setPadding(8, 16, 8, 16);
            cb.setTag(type);
            cb.setChecked(selected.contains(type));

            // При клике — снимаем выделение со всех, если выбрано больше 1
            // (опционально — можно разрешить сколько угодно)

            llTypes.addView(cb);
        }

        // Сбросить — снять все галочки
        btnReset.setOnClickListener(v -> {
            for (int i = 0; i < llTypes.getChildCount(); i++) {
                View child = llTypes.getChildAt(i);
                if (child instanceof CheckBox) {
                    ((CheckBox) child).setChecked(false);
                }
            }
        });

        // Применить — собрать выбранные типы
        btnApply.setOnClickListener(v -> {
            List<String> result = new ArrayList<>();
            for (int i = 0; i < llTypes.getChildCount(); i++) {
                View child = llTypes.getChildAt(i);
                if (child instanceof CheckBox) {
                    CheckBox cb = (CheckBox) child;
                    if (cb.isChecked()) {
                        result.add(cb.getTag().toString());
                    }
                }
            }

            if (listener != null) {
                listener.onFilterApplied(result);
            }
            dismiss();
        });
    }
}