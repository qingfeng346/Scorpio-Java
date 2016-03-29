package Scorpio.CodeDom;

//数组类型 [xxx,xxx,xxx]
public class CodeArray extends CodeObject {
    public java.util.ArrayList<CodeObject> _Elements = new java.util.ArrayList<CodeObject>();
    public CodeObject[] Elements;
    public final void Init() {
        Elements = _Elements.toArray(new CodeObject[]{});
    }
}