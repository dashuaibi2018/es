package com.dna.entity.req;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class pushMsgReq extends BaseQueryReq {

    @NotEmpty(message = "xx不允许为空")
    private String app_name;

    private String push_mode;

    private String user_id;

    private String clean_flag;

    @NotNull(message = "")
    private List<String> msgTypeList;

    private List<String> receivedList;

}
