package com.dna.service;

import com.dna.entity.ResultDto;

import java.io.IOException;
import java.util.Map;

public interface ISaasService {

    /**
     * @description: biz_alarm_msg 查询
     */
    ResultDto alarmMsgSearch(Map<String, Object> reqMap) throws IOException;

    /**
     * @description: push_msg 查询
     */
    ResultDto pushMsgSearch(Map<String, Object> reqMap) throws IOException;

    /**
     * @description: service_objs_join_vehicle 车牌号查询
     */
    ResultDto carNoSearch(Map<String, Object> reqMap) throws IOException;

    /**
     * @description: device_status_vehicle_summary 查询统计表的油耗
     */
    ResultDto oilWearSearch(Map<String, Object> reqMap) throws IOException;

}
