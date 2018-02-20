package com.example.noone.moldeapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.noone.moldeapplication.models.CategoryItemModel;
import com.example.noone.moldeapplication.models.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class MoldeDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CROWD_FIRE_IMAGE_DB";

    private static final String CATEGORY_TABLE_NAME = "category_table_info";
    private static final String CATEGORY_COLUMN_PRIMARY_KEY = "category_primary_id";
    private static final String CATEGORY_COLUMN_NAME = "category_name";

    private static final String CATEGORY_ITEM_TABLE_NAME = "category_item_table_info";
    private static final String CATEGORY_ITEM_COLUMN_PRIMARY_KEY = "category_item_primary_id";
    private static final String CATEGORY_ITEM_COLUMN_KEY = "category_item_key";
    private static final String CATEGORY_ITEM_COLUMN_NAME = "category_item_name";
    private static final String CATEGORY_ITEM_PARENT_COLUMN_NAME = "category_item_parent_name";
    private static final String CATEGORY_ITEM_COLUMN_DESCRIPTION = "category_item_description";
    private static final String CATEGORY_ITEM_COLUMN_IMAGE_PATH = "category_item_image_path";
    private static final String CATEGORY_ITEM_COLUMN_IS_COMPLETED = "category_item_is_completed";

    private static MoldeDBHelper mDBHelper;
    public static MoldeDBHelper getInstance(Context context) {
        if (mDBHelper == null) {
            mDBHelper = new MoldeDBHelper(context);
        }

        return mDBHelper;
    }

    private MoldeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        tableCreateStatements(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_ITEM_TABLE_NAME);

        onCreate(db);
    }

    private void tableCreateStatements(SQLiteDatabase db) {
        createCategoryTable(db);
        createCategoryItemTable(db);
    }

    private void createCategoryTable(SQLiteDatabase db) {
        try {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS "
                            + CATEGORY_TABLE_NAME + "("
                            + CATEGORY_COLUMN_PRIMARY_KEY + " INTEGER PRIMARY KEY, "
                            + CATEGORY_COLUMN_NAME + " VARCHAR(20) UNIQUE"
                            + ")"
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createCategoryItemTable(SQLiteDatabase db) {
        try {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS "
                            + CATEGORY_ITEM_TABLE_NAME + "("
                            + CATEGORY_ITEM_COLUMN_PRIMARY_KEY + " INTEGER PRIMARY KEY, "
                            + CATEGORY_ITEM_COLUMN_NAME + " VARCHAR(20), "
                            + CATEGORY_ITEM_PARENT_COLUMN_NAME + " VARCHAR(20), "
                            + CATEGORY_ITEM_COLUMN_DESCRIPTION + " VARCHAR(20), "
                            + CATEGORY_ITEM_COLUMN_IMAGE_PATH + " VARCHAR(20), "
                            + CATEGORY_ITEM_COLUMN_IS_COMPLETED + " INTEGER"
                            + ")"
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isCategoryExists(String categoryName) {
        boolean returnValue = false;

        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT * FROM "
                            + CATEGORY_TABLE_NAME
                            + " WHERE " + CATEGORY_COLUMN_NAME + " = ?",
                    new String[]{categoryName});

            returnValue = cursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null)
                cursor.close();
        }

        return returnValue;
    }

    public List<CategoryModel> getCategories() {
        List<CategoryModel> returnValue = new ArrayList<>();

        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT * FROM "
                            + CATEGORY_TABLE_NAME,
                    new String[]{});

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String categoryName = cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_NAME));
                    returnValue.add(new CategoryModel(categoryName));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null)
                cursor.close();
        }

        return returnValue;
    }


    public void insertCategory(CategoryModel categoryModel) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(CATEGORY_COLUMN_NAME, categoryModel.getCategoryName());

            db.insert(CATEGORY_TABLE_NAME, null, contentValues);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCategoryItemCount(String categoryName) {
        int returnValue = 0;

        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT * FROM "
                            + CATEGORY_ITEM_TABLE_NAME
                            + " WHERE " + CATEGORY_ITEM_PARENT_COLUMN_NAME + " = ?",
                    new String[]{categoryName});

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    returnValue++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null)
                cursor.close();
        }

        return returnValue;
    }

    public boolean isCategoryItemExists(String categoryItemName) {
        boolean returnValue = false;

        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT * FROM "
                            + CATEGORY_ITEM_TABLE_NAME
                            + " WHERE " + CATEGORY_ITEM_COLUMN_NAME + " = ?",
                    new String[]{categoryItemName});

            returnValue = cursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null)
                cursor.close();
        }

        return returnValue;
    }

    public CategoryItemModel getCategoryItem(String categoryItemPK) {
        CategoryItemModel returnValue = null;

        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT * FROM "
                            + CATEGORY_ITEM_TABLE_NAME
                    + " WHERE " + CATEGORY_ITEM_COLUMN_PRIMARY_KEY + " = ?",
                    new String[]{categoryItemPK});

            if (cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    String categoryItemPrimaryKey = cursor.getString(cursor.getColumnIndex(CATEGORY_ITEM_COLUMN_PRIMARY_KEY));
                    String categoryItemName = cursor.getString(cursor.getColumnIndex(CATEGORY_ITEM_COLUMN_NAME));
                    String categoryItemParentName = cursor.getString(cursor.getColumnIndex(CATEGORY_ITEM_PARENT_COLUMN_NAME));
                    String categoryItemDescription = cursor.getString(cursor.getColumnIndex(CATEGORY_ITEM_COLUMN_DESCRIPTION));
                    String categoryItemImagePath = cursor.getString(cursor.getColumnIndex(CATEGORY_ITEM_COLUMN_IMAGE_PATH));
                    boolean categoryItemIsCompleted = cursor.getInt(cursor.getColumnIndex(CATEGORY_ITEM_COLUMN_IS_COMPLETED)) == 1;

                    returnValue = new CategoryItemModel(categoryItemPrimaryKey, categoryItemName, categoryItemParentName, categoryItemDescription, categoryItemImagePath, categoryItemIsCompleted);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null)
                cursor.close();
        }

        return returnValue;
    }

    public List<CategoryItemModel> getCategoryItems(String categoryName) {
        List<CategoryItemModel> returnValue = new ArrayList<>();

        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT * FROM "
                            + CATEGORY_ITEM_TABLE_NAME
                    + " WHERE " + CATEGORY_ITEM_PARENT_COLUMN_NAME + " = ?",
                    new String[]{categoryName});

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String categoryItemPrimaryKey = cursor.getString(cursor.getColumnIndex(CATEGORY_ITEM_COLUMN_PRIMARY_KEY));
                    String categoryItemName = cursor.getString(cursor.getColumnIndex(CATEGORY_ITEM_COLUMN_NAME));
                    String categoryItemParentName = cursor.getString(cursor.getColumnIndex(CATEGORY_ITEM_PARENT_COLUMN_NAME));
                    String categoryItemDescription = cursor.getString(cursor.getColumnIndex(CATEGORY_ITEM_COLUMN_DESCRIPTION));
                    String categoryItemImagePath = cursor.getString(cursor.getColumnIndex(CATEGORY_ITEM_COLUMN_IMAGE_PATH));
                    boolean categoryItemIsCompleted = cursor.getInt(cursor.getColumnIndex(CATEGORY_ITEM_COLUMN_IS_COMPLETED)) == 1;

                    returnValue.add(new CategoryItemModel(categoryItemPrimaryKey, categoryItemName, categoryItemParentName, categoryItemDescription, categoryItemImagePath, categoryItemIsCompleted));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null)
                cursor.close();
        }

        return returnValue;
    }

    public void insertCategoryItem(CategoryItemModel categoryItemModel) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(CATEGORY_ITEM_COLUMN_NAME, categoryItemModel.getItemName());
            contentValues.put(CATEGORY_ITEM_PARENT_COLUMN_NAME, categoryItemModel.getParentName());
            contentValues.put(CATEGORY_ITEM_COLUMN_DESCRIPTION, categoryItemModel.getItemDescription());
            contentValues.put(CATEGORY_ITEM_COLUMN_IMAGE_PATH, categoryItemModel.getPhotoPath());
            contentValues.put(CATEGORY_ITEM_COLUMN_IS_COMPLETED, categoryItemModel.isItemCompleted() ? 1 : 0);

            db.insert(CATEGORY_ITEM_TABLE_NAME, null, contentValues);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCategoryItem(CategoryItemModel categoryItemModel) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(CATEGORY_ITEM_COLUMN_NAME, categoryItemModel.getItemName());
            contentValues.put(CATEGORY_ITEM_PARENT_COLUMN_NAME, categoryItemModel.getParentName());
            contentValues.put(CATEGORY_ITEM_COLUMN_DESCRIPTION, categoryItemModel.getItemDescription());
            contentValues.put(CATEGORY_ITEM_COLUMN_IMAGE_PATH, categoryItemModel.getPhotoPath());
            contentValues.put(CATEGORY_ITEM_COLUMN_IS_COMPLETED, categoryItemModel.isItemCompleted() ? 1 : 0);

            long x = db.update(CATEGORY_ITEM_TABLE_NAME,  contentValues, CATEGORY_ITEM_COLUMN_PRIMARY_KEY + "=?", new String[]{categoryItemModel.getPrimaryKey()});
            System.out.println(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteCategoryItem(String primaryKey) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            long x = db.delete(CATEGORY_ITEM_TABLE_NAME, CATEGORY_ITEM_COLUMN_PRIMARY_KEY + "=?", new String[]{primaryKey});
            System.out.println(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
