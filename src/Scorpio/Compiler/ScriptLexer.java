package Scorpio.Compiler;

import Scorpio.*;
import Scorpio.Exception.*;

public class ScriptLexer {
    private LexState m_lexState = LexState.forValue(0); //当前解析状态
    private String m_strToken = null; //字符串token
    private int m_iSourceLine; //当前解析行数
    private int m_iSourceChar; //当前解析字符
    private String m_strBreviary; //字符串的摘要 取第一行字符串的前20个字符
    private java.util.ArrayList<String> m_listSourceLines; //所有行
    private java.util.ArrayList<Token> m_listTokens; //解析后所得Token
    private char ch; //当前的解析的字符
    public ScriptLexer(String buffer) {
        m_listSourceLines = new java.util.ArrayList<String>();
        m_listTokens = new java.util.ArrayList<Token>();
        String strSource = buffer.replace("\r\n", "\r");
        String[] strLines = strSource.split("[\\r]", -1);
        m_strBreviary = strLines.length > 0 ? strLines[0] : "";
        if (m_strBreviary.length() > BREVIARY_CHAR) {
            m_strBreviary = m_strBreviary.substring(0, BREVIARY_CHAR);
        }
        for (String strLine : strLines) {
            m_listSourceLines.add(strLine + "\r\n");
        }
        m_iSourceLine = 0;
        m_iSourceChar = 0;
        setlexState(LexState.None);
    }
    /**  获得整段字符串的摘要 
    */
    public final String GetBreviary() {
        return m_strBreviary;
    }
    /**  解析字符串 
    */
    public final java.util.ArrayList<Token> GetTokens() {
        m_iSourceLine = 0;
        m_iSourceChar = 0;
        setlexState(LexState.None);
        m_listTokens.clear();
        while (!getEndOfSource()) {
            if (getEndOfLine()) {
                IgnoreLine();
                continue;
            }
            ch = ReadChar();
            switch (getlexState()) {
                case None:
                    switch (ch) {
                        case ' ':
                        case '\t':
                        case '\n':
                        case '\r':
                            break;
                        case '(':
                            AddToken(TokenType.LeftPar);
                            break;
                        case ')':
                            AddToken(TokenType.RightPar);
                            break;
                        case '[':
                            AddToken(TokenType.LeftBracket);
                            break;
                        case ']':
                            AddToken(TokenType.RightBracket);
                            break;
                        case '{':
                            AddToken(TokenType.LeftBrace);
                            break;
                        case '}':
                            AddToken(TokenType.RightBrace);
                            break;
                        case ',':
                            AddToken(TokenType.Comma);
                            break;
                        case ':':
                            AddToken(TokenType.Colon);
                            break;
                        case ';':
                            AddToken(TokenType.SemiColon);
                            break;
                        case '?':
                            AddToken(TokenType.QuestionMark);
                            break;
                        case '.':
                            setlexState(LexState.PeriodOrParams);
                            break;
                        case '+':
                            setlexState(LexState.PlusOrIncrementOrAssignPlus);
                            break;
                        case '-':
                            setlexState(LexState.MinusOrDecrementOrAssignMinus);
                            break;
                        case '*':
                            setlexState(LexState.MultiplyOrAssignMultiply);
                            break;
                        case '/':
                            setlexState(LexState.CommentOrDivideOrAssignDivide);
                            break;
                        case '%':
                            setlexState(LexState.ModuloOrAssignModulo);
                            break;
                        case '=':
                            setlexState(LexState.AssignOrEqual);
                            break;
                        case '&':
                            setlexState(LexState.AndOrCombine);
                            break;
                        case '|':
                            setlexState(LexState.OrOrInclusiveOr);
                            break;
                        case '!':
                            setlexState(LexState.NotOrNotEqual);
                            break;
                        case '>':
                            setlexState(LexState.GreaterOrGreaterEqual);
                            break;
                        case '<':
                            setlexState(LexState.LessOrLessEqual);
                            break;
                        case '^':
                            setlexState(LexState.XorOrAssignXor);
                            break;
                        case '@':
                            setlexState(LexState.SimpleStringStart);
                            break;
                        case '\"':
                            setlexState(LexState.String);
                            break;
                        case '\'':
                            setlexState(LexState.SingleString);
                            break;
                        default:
                            if (ch == '_' || Character.isLetter(ch)) {
                                setlexState(LexState.Identifier);
                                m_strToken = "" + ch;
                            }
                            else if (ch == '0') {
                                setlexState(LexState.NumberOrHexNumber);
                                m_strToken = "";
                            }
                            else if (Character.isDigit(ch)) {
                                setlexState(LexState.Number);
                                m_strToken = "" + ch;
                            }
                            else {
                                ThrowInvalidCharacterException(ch);
                            }
                            break;
                    }
                    break;
                case PeriodOrParams:
                    if (ch == '.') {
                        setlexState(LexState.Params);
                    }
                    else {
                        AddToken(TokenType.Period, ".");
                        UndoChar();
                    }
                    break;
                case Params:
                    if (ch == '.') {
                        AddToken(TokenType.Params, "...");
                    }
                    else {
                        ThrowInvalidCharacterException(ch);
                    }
                    break;
                case PlusOrIncrementOrAssignPlus:
                    if (ch == '+') {
                        AddToken(TokenType.Increment, "++");
                    }
                    else if (ch == '=') {
                        AddToken(TokenType.AssignPlus, "+=");
                    }
                    else {
                        AddToken(TokenType.Plus, "+");
                        UndoChar();
                    }
                    break;
                case MinusOrDecrementOrAssignMinus:
                    if (ch == '-') {
                        AddToken(TokenType.Decrement, "--");
                    }
                    else if (ch == '=') {
                        AddToken(TokenType.AssignMinus, "-=");
                    }
                    else {
                        AddToken(TokenType.Minus, "-");
                        UndoChar();
                    }
                    break;
                case MultiplyOrAssignMultiply:
                    if (ch == '=') {
                        AddToken(TokenType.AssignMultiply, "*=");
                    }
                    else {
                        AddToken(TokenType.Multiply, "*");
                        UndoChar();
                    }
                    break;
                case CommentOrDivideOrAssignDivide:
                    switch (ch) {
                        case '/':
                            setlexState(LexState.LineComment);
                            break;
                        case '*':
                            setlexState(LexState.BlockCommentStart);
                            break;
                        case '=':
                            AddToken(TokenType.AssignDivide, "/=");
                            break;
                        default:
                            AddToken(TokenType.Divide, "/");
                            UndoChar();
                            break;
                    }
                    break;
                case ModuloOrAssignModulo:
                    if (ch == '=') {
                        AddToken(TokenType.AssignModulo, "%=");
                    }
                    else {
                        AddToken(TokenType.AssignModulo, "%");
                        UndoChar();
                    }
                    break;
                case LineComment:
                    if (ch == '\n') {
                        setlexState(LexState.None);
                    }
                    break;
                case BlockCommentStart:
                    if (ch == '*') {
                        setlexState(LexState.BlockCommentEnd);
                    }
                    break;
                case BlockCommentEnd:
                    if (ch == '/') {
                        setlexState(LexState.None);
                    }
                    else {
                        setlexState(LexState.BlockCommentStart);
                    }
                    break;
                case AssignOrEqual:
                    if (ch == '=') {
                        AddToken(TokenType.Equal, "==");
                    }
                    else {
                        AddToken(TokenType.Assign, "=");
                        UndoChar();
                    }
                    break;
                case AndOrCombine:
                    if (ch == '&') {
                        AddToken(TokenType.And, "&&");
                    }
                    else if (ch == '=') {
                        AddToken(TokenType.AssignCombine, "&=");
                    }
                    else {
                        AddToken(TokenType.Combine, "&");
                        UndoChar();
                    }
                    break;
                case OrOrInclusiveOr:
                    if (ch == '|') {
                        AddToken(TokenType.Or, "||");
                    }
                    else if (ch == '=') {
                        AddToken(TokenType.AssignInclusiveOr, "|=");
                    }
                    else {
                        AddToken(TokenType.InclusiveOr, "|");
                        UndoChar();
                    }
                    break;
                case XorOrAssignXor:
                    if (ch == '=') {
                        AddToken(TokenType.AssignXOR, "^=");
                    }
                    else {
                        AddToken(TokenType.XOR, "^");
                        UndoChar();
                    }
                    break;
                case GreaterOrGreaterEqual:
                    if (ch == '=') {
                        AddToken(TokenType.GreaterOrEqual, ">=");
                    }
                    else if (ch == '>') {
                        setlexState(LexState.ShrOrAssignShr);
                    }
                    else {
                        AddToken(TokenType.Greater, ">");
                        UndoChar();
                    }
                    break;
                case LessOrLessEqual:
                    if (ch == '=') {
                        AddToken(TokenType.LessOrEqual, "<=");
                    }
                    else if (ch == '<') {
                        setlexState(LexState.ShiOrAssignShi);
                    }
                    else {
                        AddToken(TokenType.Less, "<");
                        UndoChar();
                    }
                    break;
                case ShrOrAssignShr:
                    if (ch == '=') {
                        AddToken(TokenType.AssignShr, ">>=");
                    }
                    else {
                        AddToken(TokenType.Shr, ">>");
                        UndoChar();
                    }
                    break;
                case ShiOrAssignShi:
                    if (ch == '=') {
                        AddToken(TokenType.AssignShi, "<<=");
                    }
                    else {
                        AddToken(TokenType.Shi, "<<");
                        UndoChar();
                    }
                    break;
                case NotOrNotEqual:
                    if (ch == '=') {
                        AddToken(TokenType.NotEqual, "!=");
                    }
                    else {
                        AddToken(TokenType.Not, "!");
                        UndoChar();
                    }
                    break;
                case String:
                    if (ch == '\"') {
                        AddToken(TokenType.String, m_strToken);
                    }
                    else if (ch == '\\') {
                        setlexState(LexState.StringEscape);
                    }
                    else if (ch == '\r' || ch == '\n') {
                        ThrowInvalidCharacterException(ch);
                    }
                    else {
                        m_strToken += ch;
                    }
                    break;
                case StringEscape:
                    if (ch == '\\' || ch == '\"') {
                        m_strToken += ch;
                        setlexState(LexState.String);
                    }
                    else if (ch == 't') {
                        m_strToken += '\t';
                        setlexState(LexState.String);
                    }
                    else if (ch == 'r') {
                        m_strToken += '\r';
                        setlexState(LexState.String);
                    }
                    else if (ch == 'n') {
                        m_strToken += '\n';
                        setlexState(LexState.String);
                    }
                    else {
                        ThrowInvalidCharacterException(ch);
                    }
                    break;
                case SingleString:
                    if (ch == '\'') {
                        AddToken(TokenType.String, m_strToken);
                    }
                    else if (ch == '\\') {
                        setlexState(LexState.SingleStringEscape);
                    }
                    else if (ch == '\r' || ch == '\n') {
                        ThrowInvalidCharacterException(ch);
                    }
                    else {
                        m_strToken += ch;
                    }
                    break;
                case SingleStringEscape:
                    if (ch == '\\' || ch == '\'') {
                        m_strToken += ch;
                        setlexState(LexState.SingleString);
                    }
                    else if (ch == 't') {
                        m_strToken += '\t';
                        setlexState(LexState.SingleString);
                    }
                    else if (ch == 'r') {
                        m_strToken += '\r';
                        setlexState(LexState.SingleString);
                    }
                    else if (ch == 'n') {
                        m_strToken += '\n';
                        setlexState(LexState.SingleString);
                    }
                    else {
                        ThrowInvalidCharacterException(ch);
                    }
                    break;
                case SimpleStringStart:
                    if (ch == '\"') {
                        setlexState(LexState.SimpleString);
                    }
                    else if (ch == '\'') {
                        setlexState(LexState.SingleSimpleString);
                    }
                    else {
                        ThrowInvalidCharacterException(ch);
                    }
                    break;
                case SimpleString:
                    if (ch == '\"') {
                        setlexState(LexState.SimpleStringQuotationMarkOrOver);
                    }
                    else {
                        m_strToken += ch;
                    }
                    break;
                case SimpleStringQuotationMarkOrOver:
                    if (ch == '\"') {
                        m_strToken += '\"';
                        setlexState(LexState.SimpleString);
                    }
                    else {
                        AddToken(TokenType.String, m_strToken);
                        UndoChar();
                    }
                    break;
                case SingleSimpleString:
                    if (ch == '\'') {
                        setlexState(LexState.SingleSimpleStringQuotationMarkOrOver);
                    }
                    else {
                        m_strToken += ch;
                    }
                    break;
                case SingleSimpleStringQuotationMarkOrOver:
                    if (ch == '\'') {
                        m_strToken += '\'';
                        setlexState(LexState.SingleSimpleString);
                    }
                    else {
                        AddToken(TokenType.String, m_strToken);
                        UndoChar();
                    }
                    break;
                case NumberOrHexNumber:
                    if (ch == 'x') {
                        setlexState(LexState.HexNumber);
                    }
                    else {
                        m_strToken = "0";
                        setlexState(LexState.Number);
                        UndoChar();
                    }
                    break;
                case Number:
                    if (Character.isDigit(ch) || ch == '.') {
                        m_strToken += ch;
                    }
                    else if (ch == 'L') {
                        long value = Long.parseLong(m_strToken);
                        AddToken(TokenType.Number, value);
                    }
                    else {
                        double value = Double.parseDouble(m_strToken);
                        AddToken(TokenType.Number, value);
                        UndoChar();
                    }
                    break;
                case HexNumber:
                    if (IsHexDigit(ch)) {
                        m_strToken += ch;
                    }
                    else {
                        if (Util.IsNullOrEmpty(m_strToken)) {
                            ThrowInvalidCharacterException(ch);
                        }
                        long value = Long.parseLong(m_strToken, 16);
                        AddToken(TokenType.Number, value);
                        UndoChar();
                    }
                    break;
                case Identifier:
                    if (ch == '_' || Character.isLetterOrDigit(ch)) {
                        m_strToken += ch;
                    }
                    else {
                        TokenType tokenType;
//C# TO JAVA CONVERTER NOTE: The following 'switch' operated on a string member and was converted to Java 'if-else' logic:
//                        switch (m_strToken)
//ORIGINAL LINE: case "eval":
                        if (m_strToken.equals("eval")) {
                                tokenType = TokenType.Eval;
                        }
//ORIGINAL LINE: case "var":
                        else if (m_strToken.equals("var") || m_strToken.equals("local")) {
                                tokenType = TokenType.Var;
                        }
//ORIGINAL LINE: case "function":
                        else if (m_strToken.equals("function")) {
                                tokenType = TokenType.Function;
                        }
//ORIGINAL LINE: case "if":
                        else if (m_strToken.equals("if")) {
                                tokenType = TokenType.If;
                        }
//ORIGINAL LINE: case "elseif":
                        else if (m_strToken.equals("elseif") || m_strToken.equals("elif")) {
                                tokenType = TokenType.ElseIf;
                        }
//ORIGINAL LINE: case "else":
                        else if (m_strToken.equals("else")) {
                                tokenType = TokenType.Else;
                        }
//ORIGINAL LINE: case "while":
                        else if (m_strToken.equals("while")) {
                                tokenType = TokenType.While;
                        }
//ORIGINAL LINE: case "for":
                        else if (m_strToken.equals("for")) {
                                tokenType = TokenType.For;
                        }
//ORIGINAL LINE: case "foreach":
                        else if (m_strToken.equals("foreach")) {
                                tokenType = TokenType.Foreach;
                        }
//ORIGINAL LINE: case "in":
                        else if (m_strToken.equals("in")) {
                                tokenType = TokenType.In;
                        }
//ORIGINAL LINE: case "switch":
                        else if (m_strToken.equals("switch")) {
                                tokenType = TokenType.Switch;
                        }
//ORIGINAL LINE: case "case":
                        else if (m_strToken.equals("case")) {
                                tokenType = TokenType.Case;
                        }
//ORIGINAL LINE: case "default":
                        else if (m_strToken.equals("default")) {
                                tokenType = TokenType.Default;
                        }
//ORIGINAL LINE: case "try":
                        else if (m_strToken.equals("try")) {
                                tokenType = TokenType.Try;
                        }
//ORIGINAL LINE: case "catch":
                        else if (m_strToken.equals("catch")) {
                                tokenType = TokenType.Catch;
                        }
//ORIGINAL LINE: case "throw":
                        else if (m_strToken.equals("throw")) {
                                tokenType = TokenType.Throw;
                        }
//ORIGINAL LINE: case "continue":
                        else if (m_strToken.equals("continue")) {
                                tokenType = TokenType.Continue;
                        }
//ORIGINAL LINE: case "break":
                        else if (m_strToken.equals("break")) {
                                tokenType = TokenType.Break;
                        }
//ORIGINAL LINE: case "return":
                        else if (m_strToken.equals("return")) {
                                tokenType = TokenType.Return;
                        }
//ORIGINAL LINE: case "null":
                        else if (m_strToken.equals("null") || m_strToken.equals("nil")) {
                                tokenType = TokenType.Null;
                        }
//ORIGINAL LINE: case "true":
                        else if (m_strToken.equals("true") || m_strToken.equals("false")) {
                                tokenType = TokenType.Boolean;
                        }
                        else {
                                tokenType = TokenType.Identifier;
                        }
                        if (tokenType == TokenType.Boolean) {
                            m_listTokens.add(new Token(tokenType, m_strToken.equals("true"), m_iSourceLine, m_iSourceChar));
                        }
                        else if (tokenType == TokenType.Null) {
                            m_listTokens.add(new Token(tokenType, null, m_iSourceLine, m_iSourceChar));
                        }
                        else {
                            m_listTokens.add(new Token(tokenType, m_strToken, m_iSourceLine, m_iSourceChar));
                        }
                        UndoChar();
                        setlexState(LexState.None);
                    }
                    break;
            }
        }
        m_listTokens.add(new Token(TokenType.Finished, "", m_iSourceLine, m_iSourceChar));
        return m_listTokens;
    }


    private enum LexState {
        /**  没有关键字 
        */
        None(0),
        /**  = 等于或者相等 
        */
        AssignOrEqual(1),
        /**  / 注释或者除号 
        */
        CommentOrDivideOrAssignDivide(2),
        /**  行注释 
        */
        LineComment(3),
        /**  区域注释开始 
        */
        BlockCommentStart(4),
        /**  区域注释结束 
        */
        BlockCommentEnd(5),
        /**  .或者多参符(...) 
        */
        PeriodOrParams(6),
        /**  多参符(...) 
        */
        Params(7),
        /**  + 或者 ++ 或者 += 
        */
        PlusOrIncrementOrAssignPlus(8),
        /**  - 或者 -= 
        */
        MinusOrDecrementOrAssignMinus(9),
        /**  * 或者 *= 
        */
        MultiplyOrAssignMultiply(10),
        /**  % 或者 %= 
        */
        ModuloOrAssignModulo(11),
        /**  & 或者 &= 或者 && 
        */
        AndOrCombine(12),
        /**  | 或者 |= 或者 || 
        */
        OrOrInclusiveOr(13),
        /**  ^ 或者 ^= 
        */
        XorOrAssignXor(14),
        /**  << 或者 <<= 
        */
        ShiOrAssignShi(15),
        /**  >> 或者 >>= 
        */
        ShrOrAssignShr(16),
        /**  ! 非或者不等于 
        */
        NotOrNotEqual(17),
        /**  > 大于或者大于等于 
        */
        GreaterOrGreaterEqual(18),
        /**  < 小于或者小于等于 
        */
        LessOrLessEqual(19),
        /**  " 字符串 </summary>
        */
        String(20),
        /**  \ 格式符 
        */
        StringEscape(21),
        /**  ' 字符串 单引号开始结束
        */
        SingleString(22),
        /**  \ 格式符
        */
        SingleStringEscape(23),
        /**  @ 开始字符串 
        */
        SimpleStringStart(24),
        /**  @" 不格式化的字符串 类似c# @符号 </summary>
        */
        SimpleString(25),
        /**  字符串内出现"是引号还是结束符 </summary>
        */
        SimpleStringQuotationMarkOrOver(26),
        /**  @" 不格式化的字符串 类似c# @符号 </summary>
        */
        SingleSimpleString(27),
        /**  字符串内出现"是引号还是结束符 </summary>
        */
        SingleSimpleStringQuotationMarkOrOver(28),
        /**  十进制数字或者十六进制数字 
        */
        NumberOrHexNumber(29),
        /**  十进制数字 
        */
        Number(30),
        /**  十六进制数字 
        */
        HexNumber(31),
        /**  描述符 
        */
        Identifier(32);

        private int intValue;
        private static java.util.HashMap<Integer, LexState> mappings;
        private synchronized static java.util.HashMap<Integer, LexState> getMappings() {
            if (mappings == null) {
                mappings = new java.util.HashMap<Integer, LexState>();
            }
            return mappings;
        }

        private LexState(int value) {
            intValue = value;
            LexState.getMappings().put(value, this);
        }

        public int getValue() {
            return intValue;
        }

        public static LexState forValue(int value) {
            return getMappings().get(value);
        }
    }
    private static final int BREVIARY_CHAR = 20; //摘要的字符数
    private LexState getlexState() {
        return m_lexState;
    }
    private void setlexState(LexState value) {
        m_lexState = value;
        if (m_lexState == LexState.None) {
            m_strToken = "";
        }
    }
    private boolean getEndOfSource() {
        return m_iSourceLine >= m_listSourceLines.size();
    }
    private boolean getEndOfLine() {
        return m_iSourceChar >= m_listSourceLines.get(m_iSourceLine).length();
    }
    private char ReadChar() {
        if (getEndOfSource()) {
            throw new LexerException("End of source reached.", m_iSourceLine);
        }
        char ch = m_listSourceLines.get(m_iSourceLine).charAt(m_iSourceChar++);
        if (m_iSourceChar >= m_listSourceLines.get(m_iSourceLine).length()) {
            m_iSourceChar = 0;
            ++m_iSourceLine;
        }
        return ch;
    }
    private void UndoChar() {
        if (m_iSourceLine == 0 && m_iSourceChar == 0) {
            throw new LexerException("Cannot undo char beyond start of source.", m_iSourceLine);
        }
        --m_iSourceChar;
        if (m_iSourceChar < 0) {
            --m_iSourceLine;
            m_iSourceChar = m_listSourceLines.get(m_iSourceLine).length() - 1;
        }
    }
    private void IgnoreLine() {
        ++m_iSourceLine;
        m_iSourceChar = 0;
    }
    private void ThrowInvalidCharacterException(char ch) {
        throw new ScriptException("Unexpected character [" + ch + "]  Line:" + (m_iSourceLine + 1) + " Column:" + m_iSourceChar + " [" + m_listSourceLines.get(m_iSourceLine) + "]");
    }
    private void AddToken(TokenType type) {
        AddToken(type, ch);
    }
    private void AddToken(TokenType type, Object lexeme) {
        m_listTokens.add(new Token(type, lexeme, m_iSourceLine, m_iSourceChar));
        setlexState(LexState.None);
    }
    private boolean IsHexDigit(char c) {
        if(Character.isDigit(c)) {
            return true;
        }
        if('a' <= c && c <= 'f') {
            return true;
        }
        if('A' <= c && c <= 'F') {
            return true;
        }
        return false;
    }
}