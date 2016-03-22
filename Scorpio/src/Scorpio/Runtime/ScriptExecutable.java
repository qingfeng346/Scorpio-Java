package Scorpio.Runtime;

import Scorpio.*;

//指令执行列表
public class ScriptExecutable {
    private java.util.ArrayList<ScriptInstruction> m_listScriptInstructions; //指令列表
    private int m_count; //指令数量
    private ScriptInstruction[] m_arrayScriptInstructions; //指令列表
    public ScriptExecutable(Script script, Executable_Block block) {
        setScript(script);
        setBlock(block);
        m_listScriptInstructions = new java.util.ArrayList<ScriptInstruction>();
    }
    private Executable_Block privateBlock = Executable_Block.forValue(0);
    public final Executable_Block getBlock() {
        return privateBlock;
    }
    private void setBlock(Executable_Block value) {
        privateBlock = value;
    }
    private Script privateScript;
    public final Script getScript() {
        return privateScript;
    }
    private void setScript(Script value) {
        privateScript = value;
    }
    //添加一条指令
    public final void AddScriptInstruction(ScriptInstruction val) {
        m_listScriptInstructions.add(val);
    }
    //指令添加完成
    public final void EndScriptInstruction() {
        m_count = m_listScriptInstructions.size();
        m_arrayScriptInstructions = m_listScriptInstructions.toArray(new ScriptInstruction[]{});
    }
    //指令数量
    public final int getCount() {
        return m_count;
    }
    //获得一条指令
    public final ScriptInstruction getItem(int index) {
        return m_arrayScriptInstructions[index];
    }
}