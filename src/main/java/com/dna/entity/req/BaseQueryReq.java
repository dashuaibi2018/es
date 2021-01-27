package com.dna.entity.req;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseQueryReq implements Serializable {

	private Integer pageFrom = 0;
	private Integer pageSize = 10;

}
