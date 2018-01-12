[中文](static/README-cn.md)
（因为目前代码组织得非常烂，仅仅是个玩具，最好不要用进项目，当初只是模仿着写，对RV的一整套东西并没有理解很透彻,比如动画等等一些也没支持）

# Why
A long long time ago ,i was inspired by this project [android-pile-layout](https://github.com/xmuSistone/android-pile-layout) ,the author cannot find the appropriate math model with [LayoutManager](https://github.com/HirayClay/StackLayoutManager/blob/master/app/src/main/java/com/hirayclay/StackLayoutManager.java) .Now i have some spare time and try to do the UI with layoutManager,barely ok with the result.</br>

# Blog
this is the relevant [blog](http://blog.csdn.net/u014296305/article/details/73496017) ,i hope it helps to understanding it</br>

# Display
<img src="static/art_new.gif" width="559px" height="256px"/>
<img src="static/stackmanager3.gif" width="0px" height="0px"/></br>


if you want to reverse the direction(stack item from left to right) or vertical support,like below:
<img src="static/hrreverse.gif" width="559px" height="256px"/></br>
<img src="https://github.com/HirayClay/StackLayoutManager/raw/orientation/static/VerticallSLM.gif" width="388px" height="632px"/></br>
check this [branch](https://github.com/HirayClay/StackLayoutManager/tree/orientation)


### Demo Apk
[download](static/app.apk)

# Usage（only a demo project ,take care in your production env）
```java

Config config = new Config();
config.secondaryScale = 0.8f;
config.scaleRatio = 0.5f;
config.maxStackCount = 3;
config.initialStackCount = 2;
config.space = 70;
recyclerview.setLayoutManager(new StackLayoutManager(config));
recyclerview.setAdapter(new StackAdapter(datas));

```
