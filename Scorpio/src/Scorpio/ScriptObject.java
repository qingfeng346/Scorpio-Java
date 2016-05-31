package Scorpio;

import Scorpio.Exception.*;
import Scorpio.Compiler.*;

//脚本数据类型
public abstract class ScriptObject {
    // 无参                            
    private static final ScriptObject[] NOPARAMETER = new ScriptObject[0];
    // Object名字
    private String privateName;
    public final String getName() {
        return privateName;
    }
    public final void setName(String value) {
        privateName = value;
    }
    // 赋值
    public ScriptObject Assign() {
        return this;
    }
    //设置变量
    public void SetValue(Object key, ScriptObject value) {
        throw new ExecutionException(getScript(), "类型[" + getType() + "]不支持设置变量[" + key + "]");
    }
    //获取变量
    public ScriptObject GetValue(Object key) {
        throw new ExecutionException(getScript(), "类型[" + getType() + "]不支持获取变量[" + key + "]");
    }
    public final Object call(Object... args) {
        int length = args.length;
        ScriptObject[] parameters = new ScriptObject[length];
        for (int i = 0; i < length; ++i) {
            parameters[i] = getScript().CreateObject(args[i]);
        }
        return Call(parameters);
    }
    //调用无参函数
    public final Object Call() {
        return Call(NOPARAMETER);
    }
    //调用函数
    public Object Call(ScriptObject[] parameters) {
        throw new ExecutionException(getScript(), "类型[" + getType() + "]不支持函数调用[" + getName() + "]");
    }
    //两个数值比较 > >= < <=
    public boolean Compare(TokenType type, ScriptObject obj) {
        throw new ExecutionException(getScript(), "类型[" + getType() + "]不支持值比较[" + type + "]");
    }
    //运算符或者位运算 + - * / % | & ^ >> <<
    public ScriptObject Compute(TokenType type, ScriptObject obj) {
        throw new ExecutionException(getScript(), "类型[" + getType() + "]不支持运算符[" + type + "]");
    }
    //运算符或者位运算赋值运算 += -= *= /= %= |= &= ^= >>= <<=
    public ScriptObject AssignCompute(TokenType type, ScriptObject obj) {
        throw new ExecutionException(getScript(), "类型[" + getType() + "]不支持赋值运算符[" + type + "]");
    }
    //逻辑运算符 逻辑运算时 Object 算 true 或者 false
    public boolean LogicOperation() {
        return true;
    }
    // 复制一个变量
    public ScriptObject clone() {
        return this;
    }
    // ToJson
    public String ToJson() {
        return getObjectValue().toString();
    }
    // ToString
    @Override
    public String toString() {
        return getObjectValue().toString();
    }
    // Equals
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ScriptObject)) {
            return false;
        }
        if (getObjectValue() == this) {
            return obj == this;
        }
        return getObjectValue().equals(((ScriptObject)obj).getObjectValue());
    }
    // GetHashCode
    @Override
    public int hashCode() {
        return super.hashCode();
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
    public Object getKeyValue() {
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