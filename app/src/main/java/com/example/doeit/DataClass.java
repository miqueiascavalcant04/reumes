package com.example.doeit;

public class DataClass {

    private String dataName;
    private String dataDesc;
    private String dataImage;

    public String getDataName() {
        return dataName;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public String getDataImage() {
        return dataImage;
    }

    public DataClass(String dataName, String dataDesc, String dataImage) {
        this.dataName = dataName;
        this.dataDesc = dataDesc;
        this.dataImage = dataImage;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public void setDataDesc(String dataDesc) {
        this.dataDesc = dataDesc;
    }

    public void setDataImage(String dataImage) {
        this.dataImage = dataImage;
    }

    public DataClass(){

    }
}
