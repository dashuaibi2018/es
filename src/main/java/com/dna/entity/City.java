package com.dna.entity;

import lombok.Data;

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

@Data
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

}
