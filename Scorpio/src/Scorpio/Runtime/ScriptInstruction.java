package Scorpio.Runtime;

import Scorpio.CodeDom.*;

//一条指令
public class ScriptInstruction {
    public ScriptInstruction(Opcode opcode) {
        this(opcode, null, null);
    }
    public ScriptInstruction(Opcode opcode, CodeObject operand0) {
        this(opcode, operand0, null);
    }
    public ScriptInstruction(Opcode opcode, CodeObject operand0, CodeObject operand1) {
        setOpcode(opcode);
        setOperand0(operand0);
        setOperand1(operand1);
    }
    public ScriptInstruction(Opcode opcode, Object value) {
        setOpcode(opcode);
        setValue(value);
    }
    private Opcode privateOpcode = Opcode.forValue(0);
    public final Opcode getOpcode() {
        return privateOpcode;
    }
    private void setOpcode(Opcode value) {
        privateOpcode = value;
    }
    private CodeObject privateOperand0;
    public final CodeObject getOperand0() {
        return privateOperand0;
    }
    private void setOperand0(CodeObject value) {
        privateOperand0 = value;
    }
    private CodeObject privateOperand1;
    public final CodeObject getOperand1() {
        return privateOperand1;
    }
    private void setOperand1(CodeObject value) {
        privateOperand1 = value;
    }
    private Object privateValue;
    public final Object getValue() {
        return privateValue;
    }
    private void setValue(Object value) {
        privateValue = value;
    }
}