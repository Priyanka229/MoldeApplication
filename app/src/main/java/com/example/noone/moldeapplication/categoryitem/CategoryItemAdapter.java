package com.example.noone.moldeapplication.categoryitem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.noone.moldeapplication.R;
import com.example.noone.moldeapplication.db.MoldeDBHelper;
import com.example.noone.moldeapplication.models.CategoryItemModel;

import java.util.List;

public class CategoryItemAdapter extends RecyclerView.Adapter<CategoryItemAdapter.CategoryItemViewHolder> {
    private List<CategoryItemModel> mCategoryModels;
    private RecyclerView mRecyclerView;

    private CategoryItemCallback mCategoryItemCallback;

    public void setCategoryItemCallback(CategoryItemCallback categoryItemCallback) {
        this.mCategoryItemCallback = categoryItemCallback;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    public interface CategoryItemCallback {
        void OnItemClick(CategoryItemModel categoryItemModel);
    }

    public void setCategoryItemList(List<CategoryItemModel> categoryModels) {
        this.mCategoryModels = categoryModels;
    }

    @Override
    public CategoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CategoryItemViewHolder holder, int position) {
        if (holder != null) {
            holder.bindData(position, mCategoryModels.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mCategoryModels == null ? 0 : mCategoryModels.size();
    }

    class CategoryItemViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        private Context mContext;

        private static final int MIN_DISTANCE = 200;
        private static final int MIN_LOCK_DISTANCE = 30;
        private boolean motionInterceptDisallowed = false;
        private float downX, upX;
        private long clickTimeInMillis;

        private ViewGroup mMainView;
        private ViewGroup mUnDoneView;
        private ViewGroup mDoneView;

        private ImageView imageIv;
        private TextView titleTv;
        private TextView subTitleTv;

        private CategoryItemModel mCategoryItemModel;
        CategoryItemViewHolder(View itemView) {
            super(itemView);

            mContext = itemView.getContext();
            imageIv = itemView.findViewById(R.id.image_iv);
            titleTv = itemView.findViewById(R.id.title_tv);
            subTitleTv = itemView.findViewById(R.id.sub_title_tv);

            mMainView = itemView.findViewById(R.id.main_view);
            mUnDoneView = itemView.findViewById(R.id.undone_view);
            mDoneView = itemView.findViewById(R.id.done_view);
        }

        void bindData(int position, final CategoryItemModel categoryItemModel) {
            if (categoryItemModel != null) {

                mCategoryItemModel = categoryItemModel;

                // set image
                if (!TextUtils.isEmpty(categoryItemModel.getPhotoPath())) {
                    Glide.with(mContext)
                            .load(categoryItemModel.getPhotoPath())
                            .into(imageIv);
                } else {
                    Glide.with(mContext)
                            .load(R.drawable.default_image)
                            .into(imageIv);
                }

                titleTv.setText(categoryItemModel.getItemName());

                setUpSubTitleText();

                itemView.setOnTouchListener(this);
            }
        }

        private void setUpSubTitleText() {
            if (mCategoryItemModel.isItemCompleted()) {
                subTitleTv.setText(mContext.getResources().getString(R.string.category_item_done_text));
                subTitleTv.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            } else {
                subTitleTv.setText(mContext.getResources().getString(R.string.category_item_pending_text));
                subTitleTv.setTextColor(mContext.getResources().getColor(R.color.grey_69));
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    downX = event.getX();
                    clickTimeInMillis = System.currentTimeMillis();
                    return true; // allow other events like Click to be processed
                }

                case MotionEvent.ACTION_MOVE: {
                    upX = event.getX();
                    float deltaX = downX - upX;

                    if (Math.abs(deltaX) > MIN_LOCK_DISTANCE && mRecyclerView != null && !motionInterceptDisallowed) {
                        mRecyclerView.requestDisallowInterceptTouchEvent(true);
                        motionInterceptDisallowed = true;
                    }

                    if (deltaX > 0) {
                        mUnDoneView.setVisibility(View.GONE);
                        mDoneView.setVisibility(View.VISIBLE);
                    } else {
                        mUnDoneView.setVisibility(View.VISIBLE);
                        mDoneView.setVisibility(View.GONE);
                    }

                    swipe(-(int) deltaX);
                    return true;
                }

                case MotionEvent.ACTION_UP:
                    upX = event.getX();
                    float deltaX = upX - downX;
                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        boolean isDone = deltaX < 0;
                        swipeRemove(isDone);
                    }
                    swipe(0);

                    if (mRecyclerView != null) {
                        mRecyclerView.requestDisallowInterceptTouchEvent(false);
                        motionInterceptDisallowed = false;
                    }

                    mDoneView.setVisibility(View.GONE);
                    mUnDoneView.setVisibility(View.GONE);

                    if (System.currentTimeMillis() - clickTimeInMillis > 0 && System.currentTimeMillis() - clickTimeInMillis < 200) {
                        mCategoryItemCallback.OnItemClick(mCategoryItemModel);
                    }

                    return true;

                case MotionEvent.ACTION_CANCEL:
                    swipe(0);

                    mDoneView.setVisibility(View.GONE);
                    mUnDoneView.setVisibility(View.GONE);
                    return false;
            }

            return true;
        }

        private void swipe(int distance) {
            View animationView = mMainView;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) animationView.getLayoutParams();
            params.rightMargin = -distance;
            params.leftMargin = distance;
            animationView.setLayoutParams(params);
        }

        private void swipeRemove(boolean isDone) {
            mCategoryItemModel.setItemCompleted(isDone);

            String categoryItemName = mCategoryItemModel.getItemName();
            String categoryItemParentName = mCategoryItemModel.getParentName();
            String categoryItemDescription = mCategoryItemModel.getItemDescription();
            String categoryItemPhotoPath = mCategoryItemModel.getPhotoPath();
            boolean categoryItemStatus = mCategoryItemModel.isItemCompleted();

            MoldeDBHelper.getInstance(mContext).updateCategoryItem(
                    new CategoryItemModel(mCategoryItemModel.getPrimaryKey(), categoryItemName, categoryItemParentName, categoryItemDescription,
                            categoryItemPhotoPath, categoryItemStatus));

            setUpSubTitleText();
        }
    }
}
