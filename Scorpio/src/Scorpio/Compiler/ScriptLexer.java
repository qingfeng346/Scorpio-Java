package Scorpio.Compiler;

import Scorpio.*;
import Scorpio.Exception.*;

public class ScriptLexer {
    private LexState m_lexState = LexState.forValue(0); //当前解析状态
    private LexState m_cacheLexState = LexState.forValue(0); //缓存解析状态
    private boolean m_SingleString; //是否是单引号的字符串
    private String m_strToken = null; //字符串token
    private int m_iCacheLine; //解析@字符串的时候记录其实行数
    private int m_iSourceLine; //当前解析行数
    private int m_iSourceChar; //当前解析字符
    private boolean m_bFormatString; //是否正在格式化字符串
    private String m_strBreviary; //字符串的摘要 取第一行字符串的前20个字符
    private java.util.ArrayList<String> m_listSourceLines; //所有行
    private java.util.ArrayList<Token> m_listTokens; //解析后所得Token
    private char ch; //当前的解析的字符
    public ScriptLexer(String buffer, String strBreviary) {
        m_listSourceLines = new java.util.ArrayList<String>();
        m_listTokens = new java.util.ArrayList<Token>();
        String[] strLines = buffer.split("[\\n]", -1);
        if (Util.IsNullOrEmpty(strBreviary)) {
            m_strBreviary = strLines.length > 0 ? strLines[0] : "";
            if (m_strBreviary.length() > BREVIARY_CHAR) {
                m_strBreviary = m_strBreviary.substring(0, BREVIARY_CHAR);
            }
        }
        else {
            m_strBreviary = strBreviary;
        }
        for (String strLine : strLines) {
            m_listSourceLines.add(strLine + '\n');
        }
        m_iSourceLine = 0;
        m_iSourceChar = 0;
        m_bFormatString = false;
        m_SingleString = false;
        setlexState(LexState.None);
        m_cacheLexState = LexState.None;
    }
    /**  获得整段字符串的摘要 
    */
    public final String GetBreviary() {
        return m_strBreviary;
    }
    private boolean IsIdentifier(char ch) {
        return (ch == '_' || Character.isLetterOrDigit(ch));
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
                    if (m_bFormatString) {
                        m_bFormatString = false;
                        AddToken(TokenType.RightPar, ")");
                        AddToken(TokenType.Plus, "+");
                        setlexState(LexState.SimpleString);
                        m_strToken = "";
                    }
                    else {
                        AddToken(TokenType.RightBrace);
                    }
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
                case '#':
                    AddToken(TokenType.Sharp);
                    break;
                case '~':
                    AddToken(TokenType.Negative);
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
                    m_SingleString = false;
                    setlexState(LexState.String);
                    break;
                case '\'':
                    m_SingleString = true;
                    setlexState(LexState.String);
                    break;
                default:
                    if (ch == '0') {
                        setlexState(LexState.NumberOrHexNumber);
                        m_strToken = "";
                    }
                    else if (Character.isDigit(ch)) {
                        setlexState(LexState.Number);
                        m_strToken = "" + ch;
                    }
                    else if (IsIdentifier(ch)) {
                        setlexState(LexState.Identifier);
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
                if (ch == '/') {
                    setlexState(LexState.LineComment);
                }
                else if (ch == '*') {
                    setlexState(LexState.BlockCommentStart);
                }
                else if (ch == '=') {
                    AddToken(TokenType.AssignDivide, "/=");
                }
                else {
                    AddToken(TokenType.Divide, "/");
                    UndoChar();
                }
                break;
            case ModuloOrAssignModulo:
                if (ch == '=') {
                    AddToken(TokenType.AssignModulo, "%=");
                }
                else {
                    AddToken(TokenType.Modulo, "%");
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
                if (ch == (m_SingleString ? '\'' : '\"')) {
                    m_cacheLexState = LexState.None;
                    AddToken(TokenType.String, m_strToken);
                }
                else if (ch == '\\') {
                    setlexState(LexState.StringEscape);
                }
                else if (ch == '\r' || ch == '\n') {
                    ThrowInvalidCharacterException(ch);
                }
                else if (ch == '$') {
                    m_cacheLexState = LexState.String;
                    setlexState(LexState.StringFormat);
                }
                else {
                    m_strToken += ch;
                }
                break;
            case StringEscape:
                if (ch == '\\' || ch == '\"') {
                    m_strToken += ch;
                }
                else if (ch == 't') {
                    m_strToken += '\t';
                }
                else if (ch == 'r') {
                    m_strToken += '\r';
                }
                else if (ch == 'n') {
                    m_strToken += '\n';
                }
                else {
                    m_strToken += ch;
                }
                setlexState(LexState.String);
                break;
            case SimpleStringStart:
                if (ch != '\"' && ch != '\'') {
                    ThrowInvalidCharacterException(ch);
                }
                m_iCacheLine = m_iSourceLine;
                setlexState(LexState.SimpleString);
                m_SingleString = (ch == '\'');
                break;
            case SimpleString:
                if (ch == (m_SingleString ? '\'' : '\"')) {
                    setlexState(LexState.SimpleStringQuotationMarkOrOver);
                }
                else if (ch == '$') {
                    m_cacheLexState = LexState.SimpleString;
                    setlexState(LexState.StringFormat);
                }
                else {
                    m_strToken += ch;
                }
                break;
            case SimpleStringQuotationMarkOrOver:
                char c = m_SingleString ? '\'' : '\"';
                if (ch == c) {
                    m_strToken += c;
                    setlexState(LexState.SimpleString);
                }
                else {
                    m_listTokens.add(new Token(TokenType.SimpleString, m_strToken, m_iCacheLine, m_iSourceChar));
                    setlexState(LexState.None);
                    UndoChar();
                }
                break;
            case StringFormat:
                if (ch == '$') {
                    m_strToken += "$";
                    setlexState(m_cacheLexState);
                }
                else if (ch == '{') {
                    m_listTokens.add(new Token(TokenType.SimpleString, m_strToken, m_iCacheLine, m_iSourceChar));
                    AddToken(TokenType.Plus, "+");
                    AddToken(TokenType.LeftPar, "(");
                    m_bFormatString = true;
                    m_strToken = "";
                }
                else {
                    m_strToken += "$";
                    setlexState(m_cacheLexState);
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
                if (IsIdentifier(ch)) {
                    m_strToken += ch;
                }
                else {
                    TokenType tokenType;
//                    switch (m_strToken)
                    if (m_strToken.equals("eval")) {
                        tokenType = TokenType.Eval;
                    }
                    else if (m_strToken.equals("var") || m_strToken.equals("local")) {
                        tokenType = TokenType.Var;
                    }
                    else if (m_strToken.equals("function")) {
                        tokenType = TokenType.Function;
                    }
                    else if (m_strToken.equals("if")) {
                        tokenType = TokenType.If;
                    }
                    else if (m_strToken.equals("elseif") || m_strToken.equals("elif")) {
                        tokenType = TokenType.ElseIf;
                    }
                    else if (m_strToken.equals("else")) {
                        tokenType = TokenType.Else;
                    }
                    else if (m_strToken.equals("while")) {
                        tokenType = TokenType.While;
                    }
                    else if (m_strToken.equals("for")) {
                        tokenType = TokenType.For;
                    }
                    else if (m_strToken.equals("foreach")) {
                        tokenType = TokenType.Foreach;
                    }
                    else if (m_strToken.equals("in")) {
                        tokenType = TokenType.In;
                    }
                    else if (m_strToken.equals("switch")) {
                        tokenType = TokenType.Switch;
                    }
                    else if (m_strToken.equals("case")) {
                        tokenType = TokenType.Case;
                    }
                    else if (m_strToken.equals("default")) {
                        tokenType = TokenType.Default;
                    }
                    else if (m_strToken.equals("try")) {
                        tokenType = TokenType.Try;
                    }
                    else if (m_strToken.equals("catch")) {
                        tokenType = TokenType.Catch;
                    }
                    else if (m_strToken.equals("throw")) {
                        tokenType = TokenType.Throw;
                    }
                    else if (m_strToken.equals("continue")) {
                        tokenType = TokenType.Continue;
                    }
                    else if (m_strToken.equals("break")) {
                        tokenType = TokenType.Break;
                    }
                    else if (m_strToken.equals("return")) {
                        tokenType = TokenType.Return;
                    }
                    else if (m_strToken.equals("define")) {
                        tokenType = TokenType.Define;
                    }
                    else if (m_strToken.equals("ifndef")) {
                        tokenType = TokenType.Ifndef;
                    }
                    else if (m_strToken.equals("endif")) {
                        tokenType = TokenType.Endif;
                    }
                    else if (m_strToken.equals("null") || m_strToken.equals("nil")) {
                        tokenType = TokenType.Null;
                    }
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
        /**  字符串 双引号 单引号 字符串都可以
        */
        String(20),
        /**  \ 格式符 
        */
        StringEscape(21),
        /**  @ 开始字符串 
        */
        SimpleStringStart(22),
        /**  @" 不格式化的字符串 类似c# @符号 </summary>
        */
        SimpleString(23),
        /**  字符串内出现"是引号还是结束符 </summary>
        */
        SimpleStringQuotationMarkOrOver(24),
        /**  ${} 格式化字符串 
        */
        StringFormat(25),
        /**  十进制数字或者十六进制数字 
        */
        NumberOrHexNumber(26),
        /**  十进制数字 
        */
        Number(27),
        /**  十六进制数字 
        */
        HexNumber(28),
        /**  描述符 
        */
        Identifier(29);

        private static java.util.HashMap<Integer, LexState> mappings;
        private synchronized static java.util.HashMap<Integer, LexState> getMappings() {
            if (mappings == null) {
                mappings = new java.util.HashMap<Integer, LexState>();
            }
            return mappings;
        }

        private LexState(int value) {
            LexState.getMappings().put(value, this);
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
            throw new LexerException("End of source reached.");
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
            throw new LexerException("Cannot undo char beyond start of source.");
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
        throw new LexerException(m_strBreviary + ":" + (m_iSourceLine + 1) + "  Unexpected character [" + ch + "]  Line:" + (m_iSourceLine + 1) + " Column:" + m_iSourceChar + " [" + m_listSourceLines.get(m_iSourceLine) + "]");
    }
    private void AddToken(TokenType type) {
        AddToken(type, ch);
    }
    private void AddToken(TokenType type, Object lexeme) {
        m_listTokens.add(new Token(type, lexeme, m_iSourceLine, m_iSourceChar));
        setlexState(LexState.None);
    }
    private boolean IsHexDigit(char c) {
        if (Character.isDigit(c)) {
            return true;
        }
        if ('a' <= c && c <= 'f') {
            return true;
        }
        if ('A' <= c && c <= 'F') {
            return true;
        }
        return false;
    }
}