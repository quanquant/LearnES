package com.bjtl.projectmanagement.model;

/**
 *  @Description: 单位实体类
 *  @Author: leitianquan
 *  @Date: 2020/06/20
 */
public class UnitDO {
    /**
     * 单位编号
     */
    private Integer unitId;

    /**
     * 单位名称
     */
    private String unitName;

    /**
     * 单位父级编号
     */
    private Integer unitParentId;

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName == null ? null : unitName.trim();
    }

    public Integer getUnitParentId() {
        return unitParentId;
    }

    public void setUnitParentId(Integer unitParentId) {
        this.unitParentId = unitParentId;
    }
}