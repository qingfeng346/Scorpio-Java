package Scorpio.Variable;

import Scorpio.*;
import Scorpio.Runtime.*;

/**  脚本函数 
*/
public class ScorpioScriptFunction {
    private Script m_Script; //脚本系统
    private String[] m_ListParameters; //参数
    private ScriptExecutable m_ScriptExecutable; //函数执行命令
    private int m_ParameterCount; //参数个数
    private boolean m_Params; //是否是不定参函数
    public final int GetParameterCount() {
        return m_ParameterCount;
    }
    public final boolean IsParams() {
        return m_Params;
    }
    public final ScriptArray GetParameters() {
        ScriptArray ret = m_Script.CreateArray();
        for (String par : m_ListParameters) {
            ret.Add(m_Script.CreateString(par));
        }
        return ret;
    }
    public ScorpioScriptFunction(Script script, java.util.ArrayList<String> listParameters, ScriptExecutable scriptExecutable, boolean bParams) {
        this.m_Script = script;
        this.m_ListParameters = listParameters.toArray(new String[]{});
        this.m_ScriptExecutable = scriptExecutable;
        this.m_ParameterCount = listParameters.size();
        this.m_Params = bParams;
    }
    public final ScriptObject Call(ScriptContext parentContext, java.util.HashMap<String, ScriptObject> objs, ScriptObject[] parameters) {
        int length = parameters.length;
        if (m_Params) {
            ScriptArray paramsArray = m_Script.CreateArray();
            for (int i = 0; i < m_ParameterCount - 1; ++i) {
                objs.put(m_ListParameters[i], (parameters != null && length > i) ? parameters[i] : m_Script.getNull());
            }
            for (int i = m_ParameterCount - 1; i < length; ++i) {
                paramsArray.Add(parameters[i]);
            }
            objs.put(m_ListParameters[m_ParameterCount - 1], paramsArray);
        }
        else {
            for (int i = 0; i < m_ParameterCount; ++i) {
                objs.put(m_ListParameters[i], (parameters != null && length > i) ? parameters[i] : m_Script.getNull());
            }
        }
        ScriptContext context = new ScriptContext(m_Script, m_ScriptExecutable, parentContext, Executable_Block.Function);
        context.Initialize(objs);
        return context.Execute();
    }
}