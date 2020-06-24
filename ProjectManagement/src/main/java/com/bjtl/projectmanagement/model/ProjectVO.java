package com.bjtl.projectmanagement.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 项目实体展示类
 * @Author: leitianquan
 * @Date: 2020/06/21
 **/
public class ProjectVO {
    /**
     * 项目编号
     */
    private Integer projectId;

    /**
     * 单位名称
     */
    private String unitName;

    /**
     * 单位编号
     */
    private Integer unitId;

    /**
     * 建设性质编号
     */
    private String buildName;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date projectDate;

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getBuildName() {
        return buildName;
    }

    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public ProjectVO() {
    }

    public ProjectVO(Integer projectId, String unitName, Integer unitId, String buildName, Integer buildId, String projectName, Double planValue, Date projectDate) {
        this.projectId = projectId;
        this.unitName = unitName;
        this.unitId = unitId;
        this.buildName = buildName;
        this.buildId = buildId;
        this.projectName = projectName;
        this.planValue = planValue;
        this.projectDate = projectDate;
    }

    @Override
    public String toString() {
        return "ProjectVO{" +
                "projectId=" + projectId +
                ", unitName='" + unitName + '\'' +
                ", unitId=" + unitId +
                ", buildName='" + buildName + '\'' +
                ", buildId=" + buildId +
                ", projectName='" + projectName + '\'' +
                ", planValue=" + planValue +
                ", projectDate=" + projectDate +
                '}';
    }
}
