package Scorpio.CodeDom;

//成员类型
public enum MEMBER_TYPE {
    VALUE, //Value类型
    OBJECT; //变量类型

    public int getValue() {
        return this.ordinal();
    }

    public static MEMBER_TYPE forValue(int value) {
        return values()[value];
    }
}