package Scorpio;

import Scorpio.Compiler.*;
import Scorpio.Exception.*;

//脚本字符串类型
public class ScriptString extends ScriptObject {
    private String m_Value;
    public ScriptString(Script script, String value) {
        super(script);
        this.m_Value = value;
    }
    public final String getValue() {
        return m_Value;
    }
    @Override
    public ObjectType getType() {
        return ObjectType.String;
    }
    @Override
    public Object getObjectValue() {
        return m_Value;
    }
    @Override
    public Object getKeyValue() {
        return m_Value;
    }
    @Override
    public ScriptObject Assign() {
        return m_Script.CreateString(m_Value);
    }
    @Override
    public ScriptObject GetValue(Object index) {
        if (!(index instanceof Double || index instanceof Integer || index instanceof Long)) {
            throw new ExecutionException(m_Script, "String GetValue只支持Number类型");
        }
        return m_Script.CreateString(m_Value.charAt(Util.ToInt32(index)) + "");
    }
    @Override
    public boolean Compare(TokenType type, ScriptObject obj) {
        ScriptString val = (ScriptString)((obj instanceof ScriptString) ? obj : null);
        if (val == null) {
            throw new ExecutionException(m_Script, "字符串比较 右边必须为字符串类型");
        }
        switch (type) {
            case Greater:
                return m_Value.compareTo(val.m_Value) > 0;
            case GreaterOrEqual:
                return m_Value.compareTo(val.m_Value) >= 0;
            case Less:
                return m_Value.compareTo(val.m_Value) < 0;
            case LessOrEqual:
                return m_Value.compareTo(val.m_Value) <= 0;
            default:
                throw new ExecutionException(m_Script, "String类型 操作符[" + type + "]不支持");
        }
    }
    @Override
    public ScriptObject AssignCompute(TokenType type, ScriptObject obj) {
        if (type == TokenType.AssignPlus) {
            m_Value += obj.toString();
            return this;
        }
        throw new ExecutionException(m_Script, "String类型 操作符[" + type + "]不支持");
    }
    @Override
    public ScriptObject clone() {
        return m_Script.CreateString(m_Value);
    }
    @Override
    public String ToJson() {
        return "\"" + m_Value.replace("\"", "\\\"") + "\"";
    }
}