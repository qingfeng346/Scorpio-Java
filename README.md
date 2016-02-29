# Scorpio-Java #
* author : while
* QQ群 : 245199668 [加群](http://shang.qq.com/wpa/qunwpa?idkey=8ef904955c52f7b3764403ab81602b9c08b856f040d284f7e2c1d05ed3428de8)
* Sco脚本的c#实现 : https://github.com/qingfeng346/Scorpio-CSharp
* 网络协议,Excel表数据转换工具 : https://github.com/qingfeng346/ScorpioConversion
* 国内用户如果网比较慢可以在此链接下载 : http://git.oschina.net/qingfeng346/Scorpio-Java

## 此脚本为java平台一个轻量级脚本,纯java实现 兼容所有Java平台以及Java语言的引擎 语法类似 javascript
## 具体脚本示例请前往c#版查看 https://github.com/qingfeng346/Scorpio-CSharp

## 源码目录说明:
* **Scorpio** 脚本引擎项目,平常使用只需导入或引用此目录即可
* **ScorpioExec** 跟lua.exe一样,命令行调用Scorpio脚本
* 源码项目使用ant编译,如需编译jar文件请自行编译build.xml

## Scorpio脚本Hello World函数:
```java
package Scorpio;
public class HelloWorld {
	public static class Test { 
		private int a = 100;
		public Test(int a) {
			this.a = a;
		}
		public void Func() {
			System.out.println("Func " + a);
		}
		public static void StaticFunc() {
			System.out.println("StaticFunc");
		}
	}
	public static void main(String[] args) {
		Script script = new Script();		//new一个Script对象
		script.LoadLibrary();								//加载所有Scorpio的库，源码在Library目录下
		script.SetObject("CTest", script.CreateObject(new Test(300)));	//SetObject可以设置一个c#对象到脚本里
		//LoadString 解析一段字符串,LoadString传入的参数就是热更新的文本文件内容
		try {
			script.LoadString("test", "print(\"hello world\")");
			String str = "MyTest = import_type(\"Scorpio.HelloWorld$Test\")		//import_type 要写入类的全路径 要加上命名空间 否则找不到此类,然后赋值给 MyTest 对象\n" +
					"MyTest.StaticFunc()			//调用类的静态函数\n" + 
					"var t = MyTest(200)			//new 一个Test对象, 括号里面是构造函数的参数\n" + 
					"t.Func()					//调用类的内部函数\n" +
					"CTest.Func()				//调用类的内部函数 CTest是通过 script.SetObject 函数设置\n";
			//Scorpio脚本调用c#函数
			script.LoadString("test", str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

```