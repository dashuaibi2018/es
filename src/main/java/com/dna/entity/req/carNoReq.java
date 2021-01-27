package com.dna.entity.req;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(callSuper = false)
public class carNoReq extends BaseQueryReq {

    @NotEmpty(message = "xx不允许为空")
    private String status;

    private String rec_status;

    private String license_plate_no;

    private String sortField;

    private String sortOrder;

}
