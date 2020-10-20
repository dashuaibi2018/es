package com.dna.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author sj
 * @since 2020-10-19
 */
public class City implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 市ID
     */
    private Long areaId;

    /**
     * 市区域名称
     */
    private String areaName;

    /**
     * 简称
     */
    private String shortName;

    /**
     * 拼音
     */
    private String pinyin;

    /**
     * 经度
     */
    private BigDecimal lng;

    /**
     * 纬度
     */
    private BigDecimal lat;

    /**
     * 级别
     */
    private Integer level;

    /**
     * 上级区域编码ID
     */
    private Long parentAreaId;

    /**
     * 状态 0无效 1有效
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sortKey;

    /**
     * 备注
     */
    private String memo;

    @Override
    public String toString() {
        return "City{" +
                "areaId=" + areaId +
                ", areaName='" + areaName + '\'' +
                ", shortName='" + shortName + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", lng=" + lng +
                ", lat=" + lat +
                ", level=" + level +
                ", parentAreaId=" + parentAreaId +
                ", status=" + status +
                ", sortKey=" + sortKey +
                ", memo='" + memo + '\'' +
                '}';
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Long getParentAreaId() {
        return parentAreaId;
    }

    public void setParentAreaId(Long parentAreaId) {
        this.parentAreaId = parentAreaId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSortKey() {
        return sortKey;
    }

    public void setSortKey(Integer sortKey) {
        this.sortKey = sortKey;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
