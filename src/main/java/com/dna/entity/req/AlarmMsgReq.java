package com.dna.entity.req;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class AlarmMsgReq extends BaseQueryReq {

    @NotEmpty(message = "xx不允许为空")
    private String handle_status;

    @NotNull(message = "")
    private List<String> objIdList;

    private List<String> alarmTypeList;

}
