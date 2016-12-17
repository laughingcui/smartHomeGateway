package com.everyoo.smartgateway.everyoocore.message.impl;


import com.everyoo.smartgateway.everyoocore.bean.SceneBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.LinkageBean;
import com.everyoo.smartgateway.everyoolocaldata.sql.bean.TimingBean;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/11/17.
 */
public interface FilterMessageImpl  {
    String controlFilter(JSONObject jsonObject, String userId);
    String clusionFilter(JSONObject jsonObject, String userId, String actionId);
    String multiLevelSwitchSet(String ctrlId, String devValue);
    LinkageBean createLinkageFilter(JSONObject jsonObject, String userId);
    LinkageBean updateLinkageFilter(JSONObject jsonObject, String userId);
    LinkageBean deleteLinkageFilter(JSONObject jsonObject, String userId);
    LinkageBean enableLinkageFilter(JSONObject jsonObject, String userId);
    SceneBean createSceneFilter(JSONObject jsonObject, String userId);
    SceneBean updateSceneFilter(JSONObject jsonObject, String userId);
    SceneBean deleteSceneFilter(JSONObject jsonObject, String userId);
    SceneBean startFilter(JSONObject jsonObject, String userId);
    SceneBean createScenePanelFilter(JSONObject jsonObject, String userId);
    SceneBean updateScenePanelFilter(JSONObject jsonObject, String userId);
    SceneBean performScenePanelFilter(JSONObject jsonObject, String userId);
    SceneBean deleteScenePanelFilter(JSONObject jsonObject, String userId);
    SceneBean controlAllDev(int value, String userId);
    TimingBean createTimingFilter(JSONObject jsonObject, String userId);
    TimingBean createSceneTimeFilter(JSONObject jsonObject, String userId);
    TimingBean updateTimingFilter(JSONObject jsonObject, String userId);
    TimingBean updateSceneTimeFilter(JSONObject jsonObject, String userId);
    TimingBean deleteTimingFilter(JSONObject jsonObject, String userId, int timerType);
    TimingBean startTimingFilter(JSONObject jsonObject, String userId);
    TimingBean closeTimingFilter(JSONObject jsonObject, String userId);

}
