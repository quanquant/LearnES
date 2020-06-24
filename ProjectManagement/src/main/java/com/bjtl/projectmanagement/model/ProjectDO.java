package com.bjtl.projectmanagement.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 *  @Description: 项目实体类
 *  @Author: leitianquan
 *  @Date: 2020/06/20
 */
public class ProjectDO {
    /**
     * 项目编号
     */
    private Integer projectId;

    /**
     * 单位编号
     */
    private Integer unitId;

    /**
     * 建设性质编号
     */
    private Integer buildId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目计划值
     */
    private Double planValue;

    /**
     * 创建项目日期
     */
    private Date projectDate;

    public ProjectDO() {
    }

    public ProjectDO(Integer projectId, Integer unitId, Integer buildId, String projectName, Double planValue, Date projectDate) {
        this.projectId = projectId;
        this.unitId = unitId;
        this.buildId = buildId;
        this.projectName = projectName;
        this.planValue = planValue;
        this.projectDate = projectDate;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public Integer getBuildId() {
        return buildId;
    }

    public void setBuildId(Integer buildId) {
        this.buildId = buildId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName == null ? null : projectName.trim();
    }

    public Double getPlanValue() {
        return planValue;
    }

    public void setPlanValue(Double planValue) {
        this.planValue = planValue;
    }

    public Date getProjectDate() {
        return projectDate;
    }

    public void setProjectDate(Date projectDate) {
        this.projectDate = projectDate;
    }

    @Override
    public String toString() {
        return "ProjectDO{" +
                "projectId=" + projectId +
                ", unitId=" + unitId +
                ", buildId=" + buildId +
                ", projectName='" + projectName + '\'' +
                ", planValue=" + planValue +
                ", projectDate=" + projectDate +
                '}';
    }
}