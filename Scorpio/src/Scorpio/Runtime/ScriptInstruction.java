package Scorpio.Runtime;

import Scorpio.CodeDom.*;

//一条指令
public class ScriptInstruction {
    public ScriptInstruction(Opcode opcode, CodeObject operand0) {
        this(opcode, operand0, null);
    }
    public ScriptInstruction(Opcode opcode, CodeObject operand0, CodeObject operand1) {
        this.opcode = opcode;
        this.operand0 = operand0;
        this.operand1 = operand1;
    }
    public ScriptInstruction(Opcode opcode, String opvalue) {
        this.opcode = opcode;
        this.opvalue = opvalue;
    }
    public Opcode opcode = Opcode.forValue(0); //指令类型
    public CodeObject operand0; //指令值1
    public CodeObject operand1; //指令值2
    public String opvalue; //指令值
}