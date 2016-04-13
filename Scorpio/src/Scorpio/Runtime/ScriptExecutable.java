package Scorpio.Runtime;

//指令执行列表
public class ScriptExecutable {
    private java.util.ArrayList<ScriptInstruction> m_listScriptInstructions; //指令列表
    private ScriptInstruction[] m_arrayScriptInstructions; //指令列表
    public Executable_Block m_Block = Executable_Block.forValue(0);
    public ScriptExecutable(Executable_Block block) {
        m_Block = block;
        m_listScriptInstructions = new java.util.ArrayList<ScriptInstruction>();
    }
    //添加一条指令
    public final void AddScriptInstruction(ScriptInstruction val) {
        m_listScriptInstructions.add(val);
    }
    //指令添加完成
    public final void EndScriptInstruction() {
        m_arrayScriptInstructions = m_listScriptInstructions.toArray(new ScriptInstruction[]{});
    }
    public final ScriptInstruction[] getScriptInstructions() {
        return m_arrayScriptInstructions;
    }
}