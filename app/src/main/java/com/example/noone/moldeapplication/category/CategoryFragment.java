package com.example.noone.moldeapplication.category;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.noone.moldeapplication.R;
import com.example.noone.moldeapplication.categoryitem.CategoryItemFragment;
import com.example.noone.moldeapplication.db.MoldeDBHelper;
import com.example.noone.moldeapplication.models.CategoryModel;

import java.util.List;

public class CategoryFragment extends Fragment implements View.OnClickListener,
        AddCategoryDialogFragment.AddCategoryCallback, CategoryAdapter.CategoryCallback {
    private CategoryAdapter mCategoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set up toolbar
        if (getActivity() != null) {
            getActivity().findViewById(R.id.toolbar_left_img).setVisibility(View.GONE);
            ((TextView) getActivity().findViewById(R.id.toolbar_title)).setText(R.string.category_text);
            getActivity().findViewById(R.id.toolbar_right_text_1).setVisibility(View.GONE);
            getActivity().findViewById(R.id.toolbar_right_text_2).setVisibility(View.GONE);
        }


        view.findViewById(R.id.fab).setOnClickListener(this);

        // fetch categories from db
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mCategoryAdapter = new CategoryAdapter();
        List<CategoryModel> categoryModelList = MoldeDBHelper.getInstance(getActivity()).getCategories();
        mCategoryAdapter.setCategoryModels(categoryModelList);
        mCategoryAdapter.setCategoryCallback(this);

        recyclerView.setAdapter(mCategoryAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                AddCategoryDialogFragment fragment = new AddCategoryDialogFragment();
                fragment.setAddCategoryCallback(this);

                if (getActivity() != null) {
                    fragment.show(getActivity().getSupportFragmentManager(), "addCategoryFragment");
                }

                break;
        }
    }

    @Override
    public void onCategoryAdded() {
        List<CategoryModel> categoryModelList = MoldeDBHelper.getInstance(getActivity()).getCategories();

        if (mCategoryAdapter != null) {
            mCategoryAdapter.setCategoryModels(categoryModelList);
            mCategoryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void OnItemClick(CategoryModel categoryModel) {
        CategoryItemFragment fragment = new CategoryItemFragment();

        Bundle bundle = new Bundle();
        bundle.putString("category_name", categoryModel.getCategoryName());

        fragment.setArguments(bundle);

        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
