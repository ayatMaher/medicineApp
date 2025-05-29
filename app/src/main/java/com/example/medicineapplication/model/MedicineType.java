package com.example.medicineapplication.model;

public class MedicineType {
    String id;
    int imageType;
    String nameType;

    public MedicineType(String id, int imageType, String nameType) {
        this.id = id;
        this.imageType = imageType;
        this.nameType = nameType;
    }

    public String getId() {
        return id;
    }

    public int getImageType() {
        return imageType;
    }

    public String getNameType() {
        return nameType;
    }
}
