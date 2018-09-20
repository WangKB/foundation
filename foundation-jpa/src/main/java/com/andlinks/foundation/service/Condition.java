package com.andlinks.foundation.service;

import java.util.List;

public class Condition<AT> {

    public enum ConditionType {
        EQUAL,
        UNEQUAL,
        GREATER,
        GE,//greaterThanOrEqualTo
        LESS,
        LE,//lessThanOrEqualTo
        LIKE,
        SEARCH//keyword for multiple attributes
    }

    private ConditionType type;//条件类型

    private String attribute;//条件比较属性

    private Object value;//相等比较值

    private Comparable comparableValue;//条件比较值,比较大小的值必须实现Comparable接口

    private String[] searchAttributes;//关键词搜索的多个检索属性

    public ConditionType getType() {
        return type;
    }

    public void setType(ConditionType type) {
        this.type = type;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Comparable getComparableValue() {
        return comparableValue;
    }

    public void setComparableValue(Comparable comparableValue) {
        this.comparableValue = comparableValue;
    }

    public String[] getSearchAttributes() {
        return searchAttributes;
    }

    public void setSearchAttributes(String[] searchAttributes) {
        this.searchAttributes = searchAttributes;
    }
}
