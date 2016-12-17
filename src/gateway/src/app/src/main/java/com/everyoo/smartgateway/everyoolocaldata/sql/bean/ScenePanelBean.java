package com.everyoo.smartgateway.everyoolocaldata.sql.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Administrator on 2016/11/1.
 */
@DatabaseTable(tableName = "scene_panel")
public class ScenePanelBean {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "ctrl_id")
    private String ctrlId;
    @DatabaseField(columnName = "key_id")
    private int keyId;
    @DatabaseField(columnName = "scene_id")
    private String sceneId;

    public String getCtrlId() {
        return ctrlId;
    }

    public void setCtrlId(String ctrlId) {
        this.ctrlId = ctrlId;
    }

    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
}
