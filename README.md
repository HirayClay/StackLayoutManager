[中文](static/README-cn.md)
（因为目前代码组织得非常烂，仅仅是个玩具，如果有Item 增删操作的话最好不要用进项目，因为predictive动画并不支持。当初只是模仿着写，对RV的一整套东西并没有理解很透彻）

# Why
A long long time ago ,i was inspired by this project [android-pile-layout](https://github.com/xmuSistone/android-pile-layout) ,the author cannot find the appropriate math model with [LayoutManager](https://github.com/HirayClay/StackLayoutManager/blob/orientation/app/src/main/java/com/hirayclay/StackLayoutManager.java) .Now i have some spare time and try to do the UI with layoutManager,barely ok with the result.</br>

# Blog
this is the relevant [blog](http://blog.csdn.net/u014296305/article/details/73496017) ,i hope it helps to understanding it</br>

# Display
### horizontal
<img src="static/hrreverse.gif" width="561px" height="282px"/>

### vertical(only top)
<img src="static/VerticallSLM.gif" width="388px" height="632px"/>

### Demo Apk
[download](static/app-vertical.apk)(vertical support)

### Attention
If you don't care about item ADD REMOVE operation, this is what you want,
because predictive animation is not supported yet,otherwise take care!

# Usage
```java

Config config = new Config();
config.secondaryScale = 0.8f;
config.scaleRatio = 0.5f;
config.maxStackCount = 3;
config.initialStackCount = 2;
config.space = 70;
config.parallex = 1.5f;//parallex factor
config.align= Align.RIGHT
recyclerview.setLayoutManager(new StackLayoutManager(config));
recyclerview.setAdapter(new StackAdapter(datas));

```

