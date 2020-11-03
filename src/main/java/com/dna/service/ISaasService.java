package com.dna.service;

import com.dna.entity.ResultDto;

import java.io.IOException;
import java.util.HashMap;

public interface ISaasService {

    /**
     * @description: biz_alarm_msg 查询
     */
    ResultDto alarmMsgSearch(HashMap<String, Object> reqMap) throws IOException;

    /**
     * @description: push_msg 查询
     */
    ResultDto pushMsgSearch(HashMap<String, Object> reqMap) throws IOException;

    /**
     * @description: service_objs_join_vehicle 车牌号查询
     */
    ResultDto carNoSearch(HashMap<String, Object> reqMap) throws IOException;

    /**
     * @description: device_status_vehicle_summary 查询统计表的油耗
     */
    ResultDto oilWearSearch(HashMap<String, Object> reqMap) throws IOException;

}
