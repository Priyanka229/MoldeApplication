package com.example.noone.moldeapplication.categoryitem;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.noone.moldeapplication.R;
import com.example.noone.moldeapplication.db.MoldeDBHelper;
import com.example.noone.moldeapplication.models.CategoryItemModel;

import java.util.List;

public class CategoryItemFragment extends Fragment implements View.OnClickListener,
        CategoryItemAdapter.CategoryItemCallback {
    private String mCategoryName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set up toolbar
        if (getActivity() != null) {
            if (getArguments() != null) {
                mCategoryName = getArguments().getString("category_name");
                if (!TextUtils.isEmpty(mCategoryName)) {
                    ((TextView) getActivity().findViewById(R.id.toolbar_title)).setText(mCategoryName);
                }
            }
            ((ImageView) getActivity().findViewById(R.id.toolbar_left_img)).setImageResource(R.drawable.back);
            getActivity().findViewById(R.id.toolbar_left_img).setOnClickListener(this);
            getActivity().findViewById(R.id.toolbar_left_img).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.toolbar_right_text_1).setVisibility(View.GONE);
            getActivity().findViewById(R.id.toolbar_right_text_2).setVisibility(View.GONE);
        }


        if (!TextUtils.isEmpty(mCategoryName)) {
            view.findViewById(R.id.fab).setOnClickListener(this);

            // fetch categories from db
            RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            CategoryItemAdapter categoryItemAdapter = new CategoryItemAdapter();
            List<CategoryItemModel> categoryModelList = MoldeDBHelper.getInstance(getActivity()).getCategoryItems(mCategoryName);
            categoryItemAdapter.setCategoryItemList(categoryModelList);
            categoryItemAdapter.setCategoryItemCallback(this);
            categoryItemAdapter.setRecyclerView(recyclerView);

            recyclerView.setAdapter(categoryItemAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                CategoryItemDetailFragment fragment = new CategoryItemDetailFragment();

                Bundle bundle = new Bundle();
                bundle.putBoolean("is_existing_item", false);

                if (getArguments() != null) {
                    String categoryName = getArguments().getString("category_name");
                    bundle.putString("category_item_parent_name", categoryName);
                }

                fragment.setArguments(bundle);

                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
                break;

            case R.id.toolbar_left_img:
                popFragment();
                break;
        }
    }

    private void popFragment() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void OnItemClick(CategoryItemModel categoryItemModel) {
        CategoryItemDetailFragment fragment = new CategoryItemDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean("is_existing_item", true);
        bundle.putString("category_item_primary_key", categoryItemModel.getPrimaryKey());

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
