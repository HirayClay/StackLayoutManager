# Why
A long long time ago ,i was inspired by this project [android-pile-layout](https://github.com/xmuSistone/android-pile-layout) ,the author cannot find the appropriate math model with [LayoutManager](https://github.com/HirayClay/StackLayoutManager/blob/master/app/src/main/java/com/hirayclay/StackLayoutManager.java) .Now i have some spare time and try to do the UI with layoutManager,barely ok with the result.</br>

# Blog
this is the relevant [blog](http://blog.csdn.net/u014296305/article/details/73496017) ,i hope it helps to understanding it</br>

# Display
<img src="static/art_new.gif"/>
<img src="static/stackmanager3.gif" width="0px" height="0px"/></br>

### Demo Apk
[download](static/app.apk)

# Usage
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

