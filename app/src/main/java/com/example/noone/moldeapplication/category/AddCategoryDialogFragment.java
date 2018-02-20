package com.example.noone.moldeapplication.category;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.noone.moldeapplication.R;
import com.example.noone.moldeapplication.db.MoldeDBHelper;
import com.example.noone.moldeapplication.models.CategoryModel;

public class AddCategoryDialogFragment extends DialogFragment implements View.OnClickListener {
    private AddCategoryCallback mAddCategoryCallback;

    public void setAddCategoryCallback(AddCategoryCallback categoryCallback) {
        this.mAddCategoryCallback = categoryCallback;
    }

    public interface AddCategoryCallback {
        void onCategoryAdded();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle("DialogFragment Demo");
        return inflater.inflate(R.layout.dialog_fragment_add_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setDimAmount(new Float(0.65));
        getDialog().setCanceledOnTouchOutside(true);
        setCancelable(true);

        // set dialog width
        Rect displayRectangle = new Rect();
        Window window = getDialog().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        getDialog().getWindow().setLayout((int) (displayRectangle.width() * .88), WindowManager.LayoutParams.WRAP_CONTENT);

        // on cancel click
        view.findViewById(R.id.cancel_tv).setOnClickListener(this);

        // on add click
        view.findViewById(R.id.add_tv).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_tv:
                dismiss();
                break;

            case R.id.add_tv:
                actionOnAddButtonClick();
                break;
        }
    }

    private void actionOnAddButtonClick() {
        if (getView() != null) {
            EditText editText = getView().findViewById(R.id.input_et);
            String text = editText.getText().toString().trim();

            if (!TextUtils.isEmpty(text)) {
                if (MoldeDBHelper.getInstance(getActivity()).isCategoryExists(text)) {
                    getView().findViewById(R.id.error_tv).setVisibility(View.VISIBLE);
                } else {
                    getView().findViewById(R.id.error_tv).setVisibility(View.GONE);

                    // store into db
                    MoldeDBHelper.getInstance(getActivity()).insertCategory(new CategoryModel(text));

                    // update using callback
                    if (mAddCategoryCallback != null) {
                        mAddCategoryCallback.onCategoryAdded();
                    }

                    // dismiss
                    dismiss();
                }
            }
        }
    }
}
