在某个附带源码的jar包上右键,选择属性(Properties),或者使用快捷键alt+enter
在Java Source Attachment窗口下复制External location的路径

回到search-source-attachment项目中,
在src目录下右键import,在弹出的对话框中搜索Archive File,下一步,将刚才复制的source jar路径粘贴到地址栏中,回车,
点击finish.

然后可以使用Ctrl+H搜索啦

注意:
在使用新建java项目向导完成后, 需要在search-source-attachment项目的属性中的Builders窗口里,勾选掉Java Builder
防止报错

根据
http://stackoverflow.com/questions/12088820/search-in-source-attachment-in-eclipse