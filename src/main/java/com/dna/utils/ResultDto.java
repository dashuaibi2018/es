package com.dna.utils;

import lombok.Data;

@Data
public class ResultDto {
    private String resultCode;
    private String resultMessage;
    private Object data;
    private String Tag;

}