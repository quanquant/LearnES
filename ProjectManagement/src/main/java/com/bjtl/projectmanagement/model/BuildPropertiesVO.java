package com.bjtl.projectmanagement.model;

/**
 * @Description: 建设性质展示对象
 * @Author: leitianquan
 * @Date: 2020/06/22
 **/
public class BuildPropertiesVO {
    private Integer id;
    private String text;

    public BuildPropertiesVO(Integer id, String text) {
        this.id = id;
        this.text = text;
    }

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

    @Override
    public String toString() {
        return "BuildPropertiesVO{" +
                "id=" + id +
                ", text='" + text + '\'' +
                '}';
    }
}
