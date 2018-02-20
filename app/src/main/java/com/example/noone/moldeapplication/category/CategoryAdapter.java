package com.example.noone.moldeapplication.category;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.noone.moldeapplication.R;
import com.example.noone.moldeapplication.db.MoldeDBHelper;
import com.example.noone.moldeapplication.models.CategoryModel;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<CategoryModel> mCategoryModels;

    private CategoryCallback mCategoryCallback;

    public void setCategoryCallback(CategoryCallback categoryCallback) {
        this.mCategoryCallback = categoryCallback;
    }

    public interface CategoryCallback {
        void OnItemClick(CategoryModel categoryModel);
    }

    public void setCategoryModels(List<CategoryModel> categoryModels) {
        this.mCategoryModels = categoryModels;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_category, parent, false));
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        if (holder != null) {
            holder.bindData(position, mCategoryModels.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mCategoryModels == null ? 0 : mCategoryModels.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;
        private TextView titleTv;
        private TextView subTitleTv;
        CategoryViewHolder(View itemView) {
            super(itemView);

            mContext = itemView.getContext();
            titleTv = itemView.findViewById(R.id.title);
            subTitleTv = itemView.findViewById(R.id.sub_title);
        }

        void bindData(int position, final CategoryModel categoryModel) {
            if (categoryModel != null) {
                titleTv.setText(categoryModel.getCategoryName());

                int categoryItemsCount = MoldeDBHelper.getInstance(mContext).getCategoryItemCount(categoryModel.getCategoryName());
                subTitleTv.setText(String.valueOf(categoryItemsCount));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCategoryCallback != null) {
                        mCategoryCallback.OnItemClick(categoryModel);
                    }
                }
            });
        }
    }
}
