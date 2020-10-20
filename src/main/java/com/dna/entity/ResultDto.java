package com.dna.entity;

public class ResultDto {
    private String resultCode;
    private String resultMessage;
    private Object data;
    private String Tag;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    @Override
    public String toString() {
        return "ResultDto{" +
                "resultCode='" + resultCode + '\'' +
                ", resultMessage='" + resultMessage + '\'' +
                ", data=" + data +
                ", Tag='" + Tag + '\'' +
                '}';
    }
}