package Scorpio.Serialize;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import Scorpio.Util;
import Scorpio.Compiler.*;

public class ScorpioMaker {
    private static byte LineFlag = Byte.MAX_VALUE;
    public static byte[] Serialize(String breviary, String data) {
        java.util.ArrayList<Token> tokens = new ScriptLexer(data, breviary).GetTokens();
        if (tokens.isEmpty()) {
            return new byte[0];
        }
        int sourceLine = 0;
        ByteBuffer writer = ByteBuffer.allocate(8192).order(ByteOrder.LITTLE_ENDIAN);
        writer.put((byte)0); //第一个字符写入一个null 以此判断文件是二进制文件还是字符串文件
        writer.putInt(tokens.size());
        for (int i = 0; i < tokens.size(); ++i) {
            Token token = tokens.get(i);
            if (sourceLine != token.getSourceLine()) {
                sourceLine = token.getSourceLine();
                writer.put(LineFlag);
                writer.putInt(token.getSourceLine());
            }
            writer.put((byte)token.getType().ordinal());
            switch (token.getType()) {
            case Boolean:
                writer.put((boolean)token.getLexeme() ? (byte)1 : (byte)0);
                break;
            case String:
            case SimpleString:
                Util.WriteString(writer, (String)token.getLexeme());
                break;
            case Identifier:
                Util.WriteString(writer, (String)token.getLexeme());
                break;
            case Number:
                if (token.getLexeme() instanceof Double) {
                    writer.put((byte)1);
                    writer.putDouble((double)token.getLexeme());
                }
                else {
                    writer.put((byte)2);
                    writer.putLong((long)token.getLexeme());
                }
                break;
			default :
				break;
            }
        }
        byte[] ret = new byte[writer.remaining()];
        writer.get(ret, 0, ret.length);
        writer.clear();
        return ret;
    }
    public static java.util.ArrayList<Token> Deserialize(byte[] data) {
        java.util.ArrayList<Token> tokens = new java.util.ArrayList<Token>();
        ByteBuffer reader = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        reader.get();
        int count = reader.getInt();
        int sourceLine = 0;
        for (int i = 0; i < count; ++i) {
            byte flag = reader.get();
            if (flag == LineFlag) {
                sourceLine = reader.getInt();
                flag = reader.get();
            }
            TokenType type = TokenType.forValue(flag);
            Object value = null;
            switch (type) {
            case Boolean:
                value = (reader.get() == 1);
                break;
            case String:
            case SimpleString:
                value = Util.ReadString(reader);
                break;
            case Identifier:
                value = Util.ReadString(reader);
                break;
            case Number:
                if (reader.get() == 1) {
                    value = reader.getDouble();
                }
                else {
                    value = reader.getLong();
                }
                break;
            default:
                value = type.toString();
                break;
            }
            tokens.add(new Token(type, value, sourceLine - 1, 0));
        }
        return tokens;
    }
    public static String DeserializeToString(byte[] data) {
        StringBuilder builder = new StringBuilder();
        ByteBuffer reader = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        reader.get();
        int count = reader.getInt();
        for (int i = 0; i < count; ++i) {
            byte flag = reader.get();
            if (flag == LineFlag) {
                int line = reader.getInt();
                flag = reader.get();
                int sourceLine = builder.toString().split("\n").length;
                for (int j = sourceLine; j < line; ++j) {
                    builder.append('\n');
                }
            }
            TokenType type = TokenType.forValue(flag);
            Object value = null;
            switch (type) {
            case Boolean:
                value = (reader.get() == 1) ? "true" : "false";
                break;
            case String:
                value = "\"" + Util.ReadString(reader).replace("\n", "\\n") + "\"";
                break;
            case SimpleString:
                value = "@\"" + Util.ReadString(reader) + "\"";
                break;
            case Identifier:
                value = Util.ReadString(reader);
                break;
            case Number:
                if (reader.get() == 1) {
                    value = reader.getDouble();
                }
                else {
                    value = reader.getLong() + "L";
                }
                break;
            default:
                value = GetTokenString(type);
                break;
            }
            builder.append(value + " ");
        }
        return builder.toString();
    }
    private static String GetTokenString(TokenType type) {
        switch (type) {
        case LeftBrace:
            return "{";
        case RightBrace:
            return "}";
        case LeftBracket:
            return "[";
        case RightBracket:
            return "]";
        case LeftPar:
            return "(";
        case RightPar:
            return ")";

        case Period:
            return ".";
        case Comma:
            return ",";
        case Colon:
            return ":";
        case SemiColon:
            return ";";
        case QuestionMark:
            return "?";
        case Sharp:
            return "#";

        case Plus:
            return "+";
        case Increment:
            return "++";
        case AssignPlus:
            return "+=";
        case Minus:
            return "-";
        case Decrement:
            return "--";
        case AssignMinus:
            return "-=";
        case Multiply:
            return "*";
        case AssignMultiply:
            return "*=";
        case Divide:
            return "/";
        case AssignDivide:
            return "/=";
        case Modulo:
            return "%";
        case AssignModulo:
            return "%=";
        case InclusiveOr:
            return "|";
        case AssignInclusiveOr:
            return "|=";
        case Or:
            return "||";
        case Combine:
            return "&";
        case AssignCombine:
            return "&=";
        case And:
            return "&&";
        case XOR:
            return "^";
        case Negative:
            return "~";
        case AssignXOR:
            return "^=";
        case Shi:
            return "<<";
        case AssignShi:
            return "<<=";
        case Shr:
            return ">>";
        case AssignShr:
            return ">>=";
        case Not:
            return "!";
        case Assign:
            return "=";
        case Equal:
            return "==";
        case NotEqual:
            return "!=";
        case Greater:
            return ">";
        case GreaterOrEqual:
            return ">=";
        case Less:
            return "<";
        case LessOrEqual:
            return "<=";

        case Eval:
            return "eval";
        case Var:
            return "var";
        case Function:
            return "function";
        case If:
            return "if";
        case ElseIf:
            return "elif";
        case Else:
            return "else";
        case While:
            return "while";
        case For:
            return "for";
        case Foreach:
            return "foreach";
        case In:
            return "in";
        case Switch:
            return "switch";
        case Case:
            return "case";
        case Default:
            return "default";
        case Try:
            return "try";
        case Catch:
            return "catch";
        case Throw:
            return "throw";
        case Continue:
            return "continue";
        case Break:
            return "break";
        case Return:
            return "return";
        case Define:
            return "define";
        case Ifndef:
            return "ifndef";
        case Endif:
            return "endif";
        case Null:
            return "null";
        case Params:
            return "...";
        default:
            return "";
        }
    }
}