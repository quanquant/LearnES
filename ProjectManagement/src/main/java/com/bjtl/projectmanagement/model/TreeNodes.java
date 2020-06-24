package com.bjtl.projectmanagement.model;

import javax.swing.tree.TreeNode;
import java.util.List;

/**
 * @Description: 单位树形实体类
 * @Author: leitianquan
 * @Date: 2020/06/21
 **/
public class TreeNodes {

    private Integer id;

    private String text;

    private List<TreeNodes> children;

    public TreeNodes() {
    }

    public TreeNodes(Integer id, String text, List<TreeNodes> children) {
        this.id = id;
        this.text = text;
        this.children = children;
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

    public List<TreeNodes> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNodes> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "TreeNodes{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", children=" + children +
                '}';
    }
}
