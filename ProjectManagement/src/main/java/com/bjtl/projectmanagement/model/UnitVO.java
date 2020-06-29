package com.bjtl.projectmanagement.model;

/**
 * @Description: 单位存在ES中
 * @Author: leitianquan
 * @Date: 2020/06/27
 **/
public class UnitVO {
    private Integer id;
    private String text;
    private Integer parentId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "UnitVO{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", parentId=" + parentId +
                '}';
    }
}
