package com.example.noone.moldeapplication.models;

public class CategoryItemModel {
    private String primaryKey;
    private String itemName;
    private String parentName;
    private String itemDescription;
    private String photoPath;
    private boolean isItemCompleted;

    public CategoryItemModel(String itemName, String parentName, String itemDescription, String photoPath, boolean isItemCompleted) {
        this.itemName = itemName;
        this.parentName = parentName;
        this.itemDescription = itemDescription;
        this.photoPath = photoPath;
        this.isItemCompleted = isItemCompleted;
    }

    public CategoryItemModel(String primaryKey, String itemName, String parentName, String itemDescription, String photoPath, boolean isItemCompleted) {
        this.primaryKey = primaryKey;
        this.itemName = itemName;
        this.parentName = parentName;
        this.itemDescription = itemDescription;
        this.photoPath = photoPath;
        this.isItemCompleted = isItemCompleted;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public boolean isItemCompleted() {
        return isItemCompleted;
    }

    public void setItemCompleted(boolean itemCompleted) {
        isItemCompleted = itemCompleted;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
