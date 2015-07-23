## 相信你也会对作者其它的项目感兴趣 ##
  1. 文本分类？特征选择？libsvm？不用再为这些头痛了，[TMSVM](https://code.google.com/p/tmsvm/)为你搞定一切文本分类的东西！
  1. 想从自由文本中找出一些有意义的词语吗？企业名称？商标名称？那就试试[EntNER](https://code.google.com/p/entner/)-基于统计与HMM的命名识别工具



## 系统简介 ##
解析搜索引擎结果页面提取URL并利用行密度方法抽取网页正文。将搜索引擎搜索参数与结果Tag封装在XML，实现动态配置搜索引擎


## 提取搜索引擎结果页面中的URL ##
算法步骤：
  1. 读取由用户设定的配置文件中搜索引擎的参数，构造所要搜索的URL。通过这些URL将用户输入的关键字传递给各搜索引擎。
  1. 解析搜索引擎的结果页面，通过页面中特定的Tag，将结果的url扣取出来。

## 抽取网页正文 ##
每个人手中都可能有一大堆讨论不同话题的HTML文档。但你真正感兴趣的内容可能隐藏于广告、布局表格或格式标记以及无数链接当中。甚至更糟的是，你希望那些来自菜单、页眉和页脚的文本能够被过滤掉。如果你不想为每种类型的HTML文件分别编写复杂的抽取程序的话，这里有一个解决方案：
首先来看两张图

![http://ww3.sinaimg.cn/mw600/4be783dajw1drok5qhdysj.jpg](http://ww3.sinaimg.cn/mw600/4be783dajw1drok5qhdysj.jpg)

从上面的原始输出你可以发现有些文本需要大量的HTML来编码，特别是标题、侧边栏、页眉和页脚。虽然HTML字节数的峰值多次出现，但大部分仍然低于平均值；我们也可以看到在大部分低HTML字节数的字段中，文本输出却相当高。通过计算文本与HTML字节数的比率（即密度）可以让我们更容易明白它们之间的关系：

![http://ww1.sinaimg.cn/mw600/4be783dajw1drok5t4t5sj.jpg](http://ww1.sinaimg.cn/mw600/4be783dajw1drok5t4t5sj.jpg)


密度值图更加清晰地表达了正文的密度更高，这是我们的工作的事实依据

因此算法的主要步骤如下：
  1. 解析HTML代码并记下处理的字节数。
  1. 以行或段的形式保存解析输出的文本。
  1. 统计每一行文本相应的HTML代码的字节数
  1. 通过计算文本相对于字节数的比率来获取文本密度
  1. 最后用通过阈值或者是神经网络来决定这一行是不是正文的一部分

## 系统模块 ##

![http://ww3.sinaimg.cn/mw600/4be783dajw1drok5ul3zwj.jpg](http://ww3.sinaimg.cn/mw600/4be783dajw1drok5ul3zwj.jpg)

## 使用配置 ##
### 搜索引擎配置 ###

![http://ww2.sinaimg.cn/mw600/4be783dajw1drok5v8bzkj.jpg](http://ww2.sinaimg.cn/mw600/4be783dajw1drok5v8bzkj.jpg)

以上为配置文件，这里我们以Baidu为例子，看一下看其源文件

![http://ww3.sinaimg.cn/mw600/4be783dajw1drok5w1bpsj.jpg](http://ww3.sinaimg.cn/mw600/4be783dajw1drok5w1bpsj.jpg)

  1. 第2行：

&lt;site name="baidu"&gt;

 这里的name唯一标示一个搜索引擎，由用户来输入。
  1. 第3行：标示该搜索引擎的状态：是否可用。设置成true，就代表在搜索时使用该搜索引擎，而false则为不使用该搜索引擎。
  1. 第4行：为搜索引擎的基地址
  1. 第5行为搜索关键字在搜索引擎的URL中的表示符号。
  1. 第6行为关键字的编码格式。
  1. 第7-10行为在网页中定位目标搜索结果的属性。其中过滤URL采用的过滤器为HasAttributeFilter，即找到能唯一表示URL所在的Tag中各个属性以及其value。所以在配置时，需要在filter中填入相应的Attribute的名称以及其value，如果value不确定，可以不填。
  1. 第11-13行为搜索引擎中一些可选的参数设置。如每页显示的数目。