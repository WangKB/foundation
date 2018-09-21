package com.andlinks.foundation.service;

public class ConditionFactory {

    public static Condition equal(String attribute, Object value) {

        Condition condition = new Condition();
        condition.setType(Condition.ConditionType.EQUAL);
        condition.setAttribute(attribute);
        condition.setValue(value);
        return condition;
    }

    public static Condition unequal(String attribute, Object value) {

        Condition condition = new Condition();
        condition.setType(Condition.ConditionType.UNEQUAL);
        condition.setAttribute(attribute);
        condition.setValue(value);
        return condition;
    }
}
