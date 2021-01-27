package com.dna.entity.req;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(callSuper = false)
public class oilWearReq extends BaseQueryReq {

    @NotEmpty(message = "xx不允许为空")
    private String startTime;

    private String endTime;

    private String is_contract;

    private String operator_corp_id;

    private String corp_id;

}
