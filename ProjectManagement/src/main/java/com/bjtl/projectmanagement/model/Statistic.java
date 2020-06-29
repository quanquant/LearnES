package com.bjtl.projectmanagement.model;

/**
 * @Description: 统计数据
 * @Author: leitianquan
 * @Date: 2020/06/29
 **/
public class Statistic {
    private String unitName;
    private String planValue;

    public Statistic(String unitName, String planValue) {
        this.unitName = unitName;
        this.planValue = planValue;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getPlanValue() {
        return planValue;
    }

    public void setPlanValue(String planValue) {
        this.planValue = planValue;
    }

    @Override
    public String toString() {
        return "Statistic{" +
                "unitName='" + unitName + '\'' +
                ", planValue='" + planValue + '\'' +
                '}';
    }
}
