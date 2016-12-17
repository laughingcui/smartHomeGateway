package com.everyoo.smartgateway.everyoolocaldata.sql.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chaos on 2016/6/24.
 */
@DatabaseTable(tableName = "devAttribute")
public class DefineAttriBean {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "device_type")
    private int deviceType;
    @DatabaseField(columnName = "manufacture_id")
    private int manufactureId;
    @DatabaseField(columnName = "product_type")
    private int productType;
    @DatabaseField(columnName = "product_id")
    private int productId;

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getManufactureId() {
        return manufactureId;
    }

    public void setManufactureId(int manufactureId) {
        this.manufactureId = manufactureId;
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }


    public DefineAttriBean(int deviceType, int productType, int manufactureId, int productId) {
        this.deviceType = deviceType;
        this.productType = productType;
        this.manufactureId = manufactureId;
        this.productId = productId;
    }
    public DefineAttriBean(){

    }
}
