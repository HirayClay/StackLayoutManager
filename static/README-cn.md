# 起源
很久之前受[android-pile-layout](https://github.com/xmuSistone/android-pile-layout)这个项目启发,觉得挺有意思的，
不过作者是用ViewGroup实现的，说在尝试用LayoutManager实现的过程没有找到适合的数学模型就放弃了，所以最近有时间自己动手
用LayoutManger实现一下，最后的效果还OK.

# 博客
这是对应的 [博客](http://blog.csdn.net/u014296305/article/details/73496017) ,希望能帮助理解实现思路，不过
博客是实现那天写的，只写了从右至左滑动叠加这一种思路，不过最原始的想法已经在里面了，至于从左到右滑动叠加，可以参考
这个[分支](https://github.com/HirayClay/StackLayoutManager/tree/orientation)的代码,其实也就是对前一种情况
做一个对称变换，不过有些小细节注意一下.
目前master分支上的代码只支持左右两种方向。orientation分支除了左右方向外还支持垂直方向（只有top方向）


# 效果
<img src="art_new.gif" width="559px" height="256px"/>

### 安装包
[download](https://github.com/HirayClay/StackLayoutManager/blob/orientation/static/app-vertical.apk)

## 使用示例
```java

Config config = new Config();
config.secondaryScale = 0.8f; //没有叠加的item的缩放比例
config.scaleRatio = 0.5f; 
config.maxStackCount = 3; //最大叠加数量
config.initialStackCount = 2;//初始状态下叠加的item数量
config.space = 70;  //item之间的间隔
recyclerview.setLayoutManager(new StackLayoutManager(config));
recyclerview.setAdapter(new StackAdapter(datas));

```
要注意的一点是，item根节点带margin可能会导致item显示不全，最好是item和内容一样大小
