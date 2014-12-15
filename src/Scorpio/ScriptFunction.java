package Scorpio;

import Scorpio.Runtime.*;
import Scorpio.Variable.*;

//脚本函数类型
public class ScriptFunction extends ScriptObject {
    private String privateName;
    public final String getName() {
        return privateName;
    }
    private void setName(String value) {
        privateName = value;
    }
    private FunstionType privateFunctionType = FunstionType.forValue(0);
    public final FunstionType getFunctionType() {
        return privateFunctionType;
    }
    private void setFunctionType(FunstionType value) {
        privateFunctionType = value;
    }
    private boolean privateIsStatic;
    public final boolean getIsStatic() {
        return privateIsStatic;
    }
    private void setIsStatic(boolean value) {
        privateIsStatic = value;
    }

    private ScorpioScriptFunction m_ScriptFunction; //脚本函数
    private ScorpioHandle m_Handle; //程序函数执行类
    private ScorpioMethod m_Method; //程序函数
    public final ScorpioMethod getMethod() {
        return m_Method;
    }
    private java.util.HashMap<String, ScriptObject> m_stackObject = new java.util.HashMap<String, ScriptObject>(); //函数变量
    @Override
    public ObjectType getType() {
        return ObjectType.Function;
    }
    public ScriptFunction(Script script, ScorpioHandle handle) {
        this(script, handle.getClass().getName(), handle);
    }
    public ScriptFunction(Script script, String strName, ScorpioHandle handle) {
        super(script);
        this.m_Handle = handle;
        Initialize(strName, FunstionType.Handle);
    }
    public ScriptFunction(Script script, ScorpioMethod method) {
        this(script, method.getMethodName(), method);
    }
    public ScriptFunction(Script script, String strName, ScorpioMethod method) {
        super(script);
        this.m_Method = method;
        Initialize(strName, FunstionType.Method);
    }
    public ScriptFunction(Script script, String strName, ScorpioScriptFunction function) {
        super(script);
        this.setIsStatic(true);
        this.m_ScriptFunction = function;
        Initialize(strName, FunstionType.Script);
    }
    private void Initialize(String strName, FunstionType funcType) {
        setName(strName);
        setFunctionType(funcType);
    }
    public final void SetTable(ScriptTable table) {
        if (getFunctionType() == FunstionType.Script) {
            setIsStatic(false);
            m_stackObject.put("this", table);
            m_stackObject.put("self", table);
        }
    }
    public final void SetParentContext(ScriptContext context) {
        if (getFunctionType() == FunstionType.Script) {
            m_ScriptFunction.SetParentContext(context);
        }
    }
    public final Object call(Object... args) throws Exception {
        int length = args.length;
        ScriptObject[] parameters = new ScriptObject[length];
        for (int i = 0; i < length; ++i) {
            parameters[i] = getScript().CreateObject(args[i]);
        }
        return Call(parameters);
    }
    @Override
    public Object Call(ScriptObject[] parameters) throws Exception {
        if (getFunctionType() == FunstionType.Script) {
            return m_ScriptFunction.Call(m_stackObject, parameters);
        }
        else {
            if (getFunctionType() == FunstionType.Handle) {
                return m_Handle.Call(parameters);
            }
            else if (getFunctionType() == FunstionType.Method) {
                return m_Method.Call(parameters);
            }
        }
        return null;
    }
    @Override
    public ScriptObject clone() {
        if (getFunctionType() != FunstionType.Script) {
            return super.clone();
        }
        ScriptFunction ret = new ScriptFunction(getScript(), getName(), m_ScriptFunction);
        ret.setIsStatic(getIsStatic());
        return ret;
    }
    @Override
    public String toString() {
        return "Function(" + getName() + ")";
    }
    @Override
    public String ToJson() {
        return "\"Function\"";
    }
}