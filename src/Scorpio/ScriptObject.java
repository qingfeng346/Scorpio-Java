package Scorpio;

import Scorpio.Exception.*;

//脚本数据类型
public abstract class ScriptObject {
    private static final ScriptObject[] NOPARAMETER = new ScriptObject[0]; // 没有参数
    public ScriptObject Assign() { // 赋值
        return this;
    }
    //设置变量
    public void SetValue(Object key, ScriptObject value) throws Exception {
        throw new ExecutionException(getScript(), "类型[" + getType() + "]不支持设置变量");
    }
    //获取变量
    public ScriptObject GetValue(Object key) throws Exception {
        throw new ExecutionException(getScript(), "类型[" + getType() + "]不支持获取变量");
    }
    //调用无参函数
    public final Object Call() throws Exception {
        return Call(NOPARAMETER);
    }
    //调用函数
    public Object Call(ScriptObject[] parameters) throws Exception {
        throw new ExecutionException(getScript(), "类型[" + getType() + "]不支持函数调用");
    }
    public ScriptObject clone() { // 复制一个变量
        return this;
    }
    public String ToJson() { // ToJson
        return getObjectValue().toString();
    }
    @Override
    public String toString() { // ToString
        return getObjectValue().toString();
    }
    @Override
    public boolean equals(Object obj) {                                       // Equals
        if (obj == null) return false;
        if (!(obj instanceof ScriptObject)) return false;
        return ((ScriptObject)obj).getObjectValue().equals(getObjectValue());
    }
    public ScriptObject(Script script) { // 构图函数
        setScript(script);
    }
    private Script privateScript;
    public final Script getScript() {
        return privateScript;
    }
    protected final void setScript(Script value) {
        privateScript = value;
    }
    public abstract ObjectType getType();
    public int getBranchType() {
        return 0;
    }
    public Object getObjectValue() {
        return this;
    }
    public final boolean getIsPrimitive() {
        return getIsBoolean() || getIsNumber() || getIsString();
    }
    public final boolean getIsNull() {
        return (getType() == ObjectType.Null);
    }
    public final boolean getIsBoolean() {
        return (getType() == ObjectType.Boolean);
    }
    public final boolean getIsNumber() {
        return (getType() == ObjectType.Number);
    }
    public final boolean getIsString() {
        return (getType() == ObjectType.String);
    }
    public final boolean getIsFunction() {
        return (getType() == ObjectType.Function);
    }
    public final boolean getIsArray() {
        return (getType() == ObjectType.Array);
    }
    public final boolean getIsTable() {
        return (getType() == ObjectType.Table);
    }
    public final boolean getIsEnum() {
        return (getType() == ObjectType.Enum);
    }
    public final boolean getIsUserData() {
        return (getType() == ObjectType.UserData);
    }
}