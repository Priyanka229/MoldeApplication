package com.example.noone.moldeapplication.categoryitem;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.noone.moldeapplication.R;
import com.example.noone.moldeapplication.db.MoldeDBHelper;
import com.example.noone.moldeapplication.models.CategoryItemModel;

import static android.app.Activity.RESULT_OK;

public class CategoryItemDetailFragment extends Fragment implements View.OnClickListener {
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;
    private static final int PICK_IMAGE_SINGLE_FROM_GALLERY = 1;

    private boolean isExistingItem;
    private CategoryItemModel mCategoryItemModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_item_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set up values from arguments
        setUpValuesFromArguments();

        // set up toolbar
        setUpToolbar();

        // add photo text
        String addPhotoText = isExistingItem ? getString(R.string.category_item_detail_change_photo_text) : getString(R.string.category_item_detail_add_photo_text);
        ((TextView) view.findViewById(R.id.photo_tv)).setText(addPhotoText);

        String title = "";
        String description = "";
        String imagePath = "";

        if (mCategoryItemModel != null) {
            title = TextUtils.isEmpty(mCategoryItemModel.getItemName()) ? "" : mCategoryItemModel.getItemName();
            description = TextUtils.isEmpty(mCategoryItemModel.getItemDescription()) ? "" : mCategoryItemModel.getItemDescription();
            imagePath = TextUtils.isEmpty(mCategoryItemModel.getPhotoPath()) ? "" : mCategoryItemModel.getPhotoPath();
        }

        // set image
        if (!TextUtils.isEmpty(imagePath)) {
            Glide.with(this)
                    .load(imagePath)
                    .into((ImageView) view.findViewById(R.id.image_iv));
        }

        // set title
        EditText titleEt = view.findViewById(R.id.title_et);
        titleEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCategoryItemModel.setItemName(s.toString());
            }
        });
        titleEt.setText(title);


        // set description
        EditText descriptionEt = view.findViewById(R.id.description_et);
        descriptionEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCategoryItemModel.setItemDescription(s.toString());
            }
        });
        descriptionEt.setText(description);


        // task status
        SwitchCompat switchCompat = view.findViewById(R.id.switchButton);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCategoryItemModel.setItemCompleted(isChecked);
            }
        });
        switchCompat.setChecked(mCategoryItemModel.isItemCompleted());


        // click listener on add or change photo click
        view.findViewById(R.id.photo_tv).setOnClickListener(this);

        if (getActivity() != null) {
            getActivity().findViewById(R.id.toolbar_right_text_1).setOnClickListener(this);
            getActivity().findViewById(R.id.toolbar_right_text_2).setOnClickListener(this);
        }
    }

    private void setUpValuesFromArguments() {
        if (getArguments() != null) {
            isExistingItem = getArguments().getBoolean("is_existing_item", false);

            String categoryItemName = getArguments().getString("category_item_primary_key");
            if (!TextUtils.isEmpty(categoryItemName)) {
                mCategoryItemModel = MoldeDBHelper.getInstance(getActivity()).getCategoryItem(categoryItemName);
            } else {
                mCategoryItemModel = new CategoryItemModel("",
                        getArguments().getString("category_item_parent_name"),
                        "", "", false);
            }
        }
    }

    private void setUpToolbar() {
        if (getActivity() != null) {
            ((TextView) getActivity().findViewById(R.id.toolbar_title)).setText(
                    isExistingItem ? getString(R.string.edit_item_text) : getString(R.string.add_item_text));

            ((ImageView) getActivity().findViewById(R.id.toolbar_left_img)).setImageResource(R.drawable.cross);
            getActivity().findViewById(R.id.toolbar_left_img).setOnClickListener(this);
            getActivity().findViewById(R.id.toolbar_left_img).setVisibility(View.VISIBLE);

            getActivity().findViewById(R.id.toolbar_right_text_1).setVisibility(isExistingItem ? View.VISIBLE : View.GONE);
            getActivity().findViewById(R.id.toolbar_right_text_2).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo_tv:
                openGallery();
                break;

            case R.id.toolbar_right_text_1:
                actionOnDeleteClick();
                break;

            case R.id.toolbar_right_text_2:
                actionOnSaveClick();
                break;

            case R.id.toolbar_left_img:
                popFragment();
                break;
        }
    }

    private void actionOnDeleteClick() {
        if (getActivity() != null) {
            MoldeDBHelper.getInstance(getActivity()).deleteCategoryItem(mCategoryItemModel.getPrimaryKey());

            popFragment();
        }
    }

    private void actionOnSaveClick() {
        if (getView() != null) {
            String categoryItemName = mCategoryItemModel.getItemName();
            String categoryItemParentName = mCategoryItemModel.getParentName();
            String categoryItemDescription = mCategoryItemModel.getItemDescription();
            String categoryItemPhotoPath = mCategoryItemModel.getPhotoPath();
            boolean categoryItemStatus = mCategoryItemModel.isItemCompleted();

            if (isExistingItem) {
                MoldeDBHelper.getInstance(getActivity()).updateCategoryItem(
                        new CategoryItemModel(mCategoryItemModel.getPrimaryKey(), categoryItemName, categoryItemParentName, categoryItemDescription,
                                categoryItemPhotoPath, categoryItemStatus));
            } else {
                MoldeDBHelper.getInstance(getActivity()).insertCategoryItem(
                        new CategoryItemModel(categoryItemName, categoryItemParentName, categoryItemDescription,
                                categoryItemPhotoPath, categoryItemStatus));
            }
            popFragment();
        }
    }

    private void popFragment() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void openGallery() {
        if (isStorageReadPermissionGranted()) {
            openGalleryForSingleImageSelection();
        } else {
            askForStoragePermission();
        }
    }

    private void askForStoragePermission() {
        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
    }

    private void openGalleryForSingleImageSelection() {
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, PICK_IMAGE_SINGLE_FROM_GALLERY);
    }

    private boolean isStorageReadPermissionGranted() {
        boolean returnValue;
        if (Build.VERSION.SDK_INT >= 23 && getActivity() != null) {
            returnValue = getActivity().checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED;
        }
        else {
            returnValue = true;
        }

        return returnValue;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case WRITE_EXTERNAL_STORAGE_REQUEST_CODE:
                if (isStorageReadPermissionGranted()) {
                    openGalleryForSingleImageSelection();
                } else {
                    Toast.makeText(getActivity(), "External Storage permission allows us to do load images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_SINGLE_FROM_GALLERY && resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imagePath = cursor.getString(columnIndex);

                mCategoryItemModel.setPhotoPath(imagePath);

                // add to ui list
                if (!TextUtils.isEmpty(imagePath) && getView() != null) {
                    ImageView imageView = getView().findViewById(R.id.image_iv);
                    Glide.with(this).load(imagePath).into(imageView);
                }

                cursor.close();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);

    }
}
