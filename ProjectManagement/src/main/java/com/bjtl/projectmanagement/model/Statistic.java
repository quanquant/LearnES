package com.bjtl.projectmanagement.model;

/**
 * @Description: 统计数据
 * @Author: leitianquan
 * @Date: 2020/06/29
 **/
public class Statistic {
    private String name;
    private String value;

    public Statistic(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Statistic{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
