package Scorpio.Variable;

import Scorpio.*;
import Scorpio.Runtime.*;

/**  脚本函数 
*/
public class ScorpioScriptFunction {
    private Script m_Script; //脚本系统
    private java.util.ArrayList<String> m_ListParameters; //参数
    private ScriptExecutable m_ScriptExecutable; //函数执行命令
    private ScriptContext m_ParentContext; //父级上下文
    private ScriptContext m_Context; //执行上下文
    private int m_ParameterCount; //参数个数
    private ScriptArray m_ParamsArray; //不定参数组
    private boolean m_Params; //是否是不定参函数
    public final boolean getParams() {
        return m_Params;
    }
    public final int getParameterCount() {
        return m_ParameterCount;
    }
    public ScorpioScriptFunction(Script script, java.util.ArrayList<String> listParameters, ScriptExecutable scriptExecutable, boolean bParams) {
        this.m_Script = script;
        this.m_ListParameters = new java.util.ArrayList<String>(listParameters);
        this.m_ScriptExecutable = scriptExecutable;
        this.m_ParameterCount = listParameters.size();
        this.m_Params = bParams;
        this.m_ParamsArray = bParams ? script.CreateArray() : null;
        this.m_Context = new ScriptContext(m_Script, m_ScriptExecutable, null, Executable_Block.Function);
    }
    public final void SetParentContext(ScriptContext context) {
        m_ParentContext = context;
    }
    public final ScriptObject Call(java.util.HashMap<String, ScriptObject> objs, ScriptObject[] parameters) throws Exception {
        int length = parameters.length;
        if (m_Params) {
            m_ParamsArray.Clear();
            for (int i = 0; i < m_ParameterCount - 1; ++i) {
                objs.put(m_ListParameters.get(i), (parameters != null && length > i) ? parameters[i] : m_Script.Null);
            }
            for (int i = m_ParameterCount - 1; i < length; ++i) {
                m_ParamsArray.Add(parameters[i]);
            }
            objs.put(m_ListParameters.get(m_ParameterCount - 1), m_ParamsArray);
        }
        else {
            for (int i = 0; i < m_ParameterCount; ++i) {
                objs.put(m_ListParameters.get(i), (parameters != null && length > i) ? parameters[i] : m_Script.Null);
            }
        }
        m_Context.Initialize(m_ParentContext, objs);
        return m_Context.Execute();
    }
}