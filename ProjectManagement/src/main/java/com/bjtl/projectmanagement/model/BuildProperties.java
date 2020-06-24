package com.bjtl.projectmanagement.model;

/**
 *  @Description: 建设性质实体类
 *  @Author: leitianquan
 *  @Date: 2020/06/20
 */
public class BuildProperties {
    /**
     * 建设性质编号
     */
    private Integer buildId;

    /**
     * 建设性质名称
     */
    private String buildName;

    public Integer getBuildId() {
        return buildId;
    }

    public void setBuildId(Integer buildId) {
        this.buildId = buildId;
    }

    public String getBuildName() {
        return buildName;
    }

    public void setBuildName(String buildName) {
        this.buildName = buildName == null ? null : buildName.trim();
    }
}