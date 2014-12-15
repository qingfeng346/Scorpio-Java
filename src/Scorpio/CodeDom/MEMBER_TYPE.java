package Scorpio.CodeDom;

import Scorpio.*;

//成员类型
public enum MEMBER_TYPE {
    STRING, //String
    NUMBER, //long类型
    INDEX, //double类型（自动转成int类型）
    OBJECT; //变量类型

    public int getValue() {
        return this.ordinal();
    }

    public static MEMBER_TYPE forValue(int value) {
        return values()[value];
    }
}