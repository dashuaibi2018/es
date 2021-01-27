package com.dna.controller;


import com.dna.entity.ResultDto;
import com.dna.entity.req.AlarmMsgReq;
import com.dna.entity.req.carNoReq;
import com.dna.entity.req.oilWearReq;
import com.dna.entity.req.pushMsgReq;
import com.dna.service.ISaasService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/saas")
public class SaasController {

    @Autowired
    ISaasService saasService;

    /**
     * @param req
     * @description: biz_alarm_msg 查询
     * @return: com.dna.entity.ResultDto
     * @author: SUJUN
     * @time: 2020/10/20 14:33
     */
    @PostMapping("/alarmMsgSearch")
    public ResultDto alarmMsgSearch(@RequestBody @Validated AlarmMsgReq req) throws IOException {

        Map<String, Object> reqMap = new HashMap<>();
        //模拟传参
//        ArrayList<String> objIdList = new ArrayList<>();
//        Collections.addAll(objIdList, "18061110594379843", "18061209415874994", "18062912132930390", "18062914000298718", "18061110541928130", "66f55f543904437e98bce96a96903702", "677e464ec96d4cebb7903d902ae7186f", "edb2748cf51f42a5accf83bded7d3ec3", "1647b0c7b5ae43e781d2dc6234747c49", "19031415052563108", "22638356f9fb4468b5ed9c91bd442a9a", "26805c2ce6e848a4969feaafdd2b2f4b", "4e3b82c4db164eacb08322f249484704", "5b134d658f8e4863b5c6f03c3645ed55", "6ba3bc809a02499d9204ec334a87421d", "a1b87f86758c41e6b05ae176aa684de2", "18061110550768395", "18061209445476031", "e5d89ee8a0cd481ab57277ece65e1337", "18090518224140332", "aecdae7d7055476dac0a193729407923", "882f572381914d57b1a62657ca9ae764", "c6371ea71ba641aab74dcfed13c9499c", "ff6eafbd52194c539c9d0924496b4e71", "0385772f9123490eb624b9607357e3a3", "165f0c86981547629ceb5a7bfa112cdf", "18061209455676438", "19052318241678680", "19052318251978892", "1b5e29819502466ea3a53b42fd0c58e6", "1c985e7f56124d2bbf1f2baffa035ba4", "20011412550798793", "20011413100890083", "2d8a2922868244f094d27050431f6cbb", "2f2f407762324d66968e44fe5c8169bd", "3204d4163c39418c84568c8d21c7d7da", "4ae51c30712c4fe8ad7709bbe43a4d87", "4f73fc4d968f4e7ebc7bb3dd0bae3e5f", "57c978529848477ea782c9f4437bb4dc", "718a0e99ec95459ba93b385032721757", "766e53803697416bb3264c6f44917ee0", "7991114af1bc479c87cd40c27a1b23cf", "85a950ae03f8424aac237171e1721312", "92fd976f83a048a7a6b0a8d4941b44ff", "93b21abae55547169e46a23fb21b6174", "98cde790ca6f4442b0352a3c54e1862c", "a442cc46580f4d07ae5f35eb154a4b4b", "a6b06be796ab47e0b37fde04c008aba0", "ab2a4720cf924817be706e39fcb9325c", "bc6cefefdeca4d9ba656a407b3b2da10", "e596aa0aaa2f4bdfa6adf7078e3dbcfc", "fdda47eb8ce7473489fe165618a2c7a3");
//        ArrayList<String> alarmTypeList = new ArrayList<>();
//        Collections.addAll(alarmTypeList, "200", "201", "202", "203");
//        reqMap.put("objIdList", objIdList);
//        reqMap.put("alarmTypeList", alarmTypeList);
//        reqMap.put("handle_status", "0");
//        reqMap.put("pageFrom", "0");
//        reqMap.put("pageSize", "20");

        reqMap.put("objIdList", req.getObjIdList()); //18082117150715259
        reqMap.put("alarmTypeList", req.getAlarmTypeList());
        reqMap.put("handle_status", req.getHandle_status());
        reqMap.put("pageFrom", req.getPageFrom());
        reqMap.put("pageSize", req.getPageSize());

        ResultDto resultDto = saasService.alarmMsgSearch(reqMap);
        return resultDto;
    }


    /**
     * @param req
     * @description: push_msg 查询
     * @return: com.dna.entity.ResultDto
     * @author: SUJUN
     * @time: 2020/10/20 14:33
     */
    @PostMapping("/pushMsgSearch")
    public ResultDto pushMsgSearch(@RequestBody @Validated pushMsgReq req) throws IOException {
        Map<String, Object> reqMap = new HashMap<>();

//        ArrayList<String> receivedList = new ArrayList<>();
//        Collections.addAll(receivedList, "0", "1", "2");
//        ArrayList<String> msgTypeList = new ArrayList<>();
//        Collections.addAll(msgTypeList, "17");
//        reqMap.put("receivedList", receivedList);
//        reqMap.put("msgTypeList", msgTypeList);
//        reqMap.put("user_id", "16072415330002514");
//        reqMap.put("app_name", "HeXieQiChe");
//        reqMap.put("push_mode", "0");
//        reqMap.put("clean_flag", "1");
//        reqMap.put("pageFrom", "0");
//        reqMap.put("pageSize", "20");

        reqMap.put("receivedList", req.getReceivedList());
        reqMap.put("msgTypeList", req.getMsgTypeList());
        reqMap.put("user_id", req.getUser_id());
        reqMap.put("app_name", req.getApp_name());
        reqMap.put("push_mode", req.getPush_mode());
        reqMap.put("clean_flag", req.getClean_flag());
        reqMap.put("pageFrom", req.getPageFrom());
        reqMap.put("pageSize", req.getPageSize());

        return saasService.pushMsgSearch(reqMap);
    }


    /**
     * @param req
     * @description: service_objs_join_vehicle 车牌号查询
     * @return: com.dna.entity.ResultDto
     * @author: SUJUN
     * @time: 2020/10/20 14:33
     */
    @PostMapping("/carNoSearch")
    public ResultDto carNoSearch(@RequestBody @Validated carNoReq req) throws IOException {
        Map<String, Object> reqMap = new HashMap<>();

//        reqMap.put("status", "1");
//        reqMap.put("rec_status", "1");
//        reqMap.put("license_plate_no", "苏A6A5M0"); //前缀查询
////        reqMap.put("license_plate_no", "*6A5*"); //通配符查询
//        reqMap.put("sortField", "creat_time");
//        reqMap.put("sortOrder", "desc");
//
//        reqMap.put("pageFrom", "0");
//        reqMap.put("pageSize", "20");

        reqMap.put("status", req.getStatus());
        reqMap.put("rec_status", req.getRec_status());
        reqMap.put("license_plate_no", req.getLicense_plate_no()); //前缀查询
//        reqMap.put("license_plate_no", "*6A5*"); //通配符查询
        reqMap.put("sortField", req.getSortField());
        reqMap.put("sortOrder", req.getSortOrder());
        reqMap.put("pageFrom", req.getPageFrom());
        reqMap.put("pageSize", req.getPageSize());

        return saasService.carNoSearch(reqMap);

    }

    /**
     * @param req
     * @description: device_status_vehicle_summary 查询统计表的油耗
     * @return: com.dna.entity.ResultDto
     * @author: SUJUN
     * @time: 2020/10/20 14:33
     */
    @PostMapping("/oilWearSearch")
    public ResultDto oilWearSearch(@RequestBody @Validated oilWearReq req) throws IOException {
        Map<String, Object> reqMap = new HashMap<>();

//        reqMap.put("startTime", "2019-10-01 00:00:00");
//        reqMap.put("endTime", "2020-09-25 23:59:59");
//        reqMap.put("is_contract", "1");
//        reqMap.put("operator_corp_id", "dinacarrier");
//        reqMap.put("corp_id", "12120710341890007");

        reqMap.put("startTime", req.getStartTime());
        reqMap.put("endTime", req.getEndTime());
        reqMap.put("is_contract", req.getIs_contract());
        reqMap.put("operator_corp_id", req.getOperator_corp_id());
        reqMap.put("corp_id", req.getCorp_id());

        ResultDto resultDto = saasService.oilWearSearch(reqMap);
        return resultDto;

    }

}