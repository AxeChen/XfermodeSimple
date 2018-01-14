#### 1、Xfermode是什么
在Android自定义控件或者对图片等进行处理时需要做一些图像混合的操作时，会用到xfermode。利用xfermode可以做出许多有趣的UI效果时。比如做不同形状的头像，刮刮卡。
在做这些效果之前需要先了解xfermode的使用。


#### 2、PorterDuffXfermode
xfermode有三个子类：AvoidXfermode, PixelXorXfermode和PorterDuffXfermode。其中AvoidXfermode, PixelXorXfermode已经过时不推荐使用。那么PorterDuffXfermode则是需要了解的东西。


##### 2.1、理解Xfermode的基本概念
一下的一张图和一段伪代码可以理解PorterDuffXfermode的基本概念。

![](https://user-gold-cdn.xitu.io/2018/1/14/160f54d184ac2f29?w=915&h=493&f=png&s=14340)

Xfermode理解起来并不是很难，根据上面的图可以理解为，两个不同的像素点。通过Xfermode的不同的混合模式混合之后展示出来的新的像素点效果。（注意这里是针对每一个像素的混合效果。而且这两个像素点需要是在画布上的同一位置，可以理解为重叠）

伪代码可以这样表示：
```
// 初始化PorterDuffXfermode
private PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

// 在ondraw中使用PorterDuffXfermode
 protected void onDraw(Canvas canvas) {

        // DstBitmap SRCBitmap 为两个不同的bitmap
        canvas.drawBitmap(DstBitmap,0,0,mPaint);
        // PorterDuffXfermode和paint联用
        mPaint.setXfermode(xfermode);
        canvas.drawBitmap(SRCBitmap,0,0,mPaint);
        
        // 将xfermode制空
        mPaint.setXfermode(null);
    }
```
以上的代码也比较简单理解：
先draw一个bitmap,然后设置paint的xfermode，然后在画第二个bitmap。这样他们重叠的部分就会出现不通过的UI效果了。

##### 2.2、Xfermode的多种混合模式
PorterDuffXfermode的构造函数：
```
public PorterDuffXfermode(PorterDuff.Mode mode) {}
```
参数传入了PorterDuff.Mode mode，以下列出PorterDuff的所以的Mode。
```
public enum Mode {
    /** [0, 0] */
    CLEAR       (0),
    /** [Sa, Sc] */
    SRC         (1),
    /** [Da, Dc] */
    DST         (2),
    /** [Sa + (1 - Sa)*Da, Rc = Sc + (1 - Sa)*Dc] */
    SRC_OVER    (3),
    /** [Sa + (1 - Sa)*Da, Rc = Dc + (1 - Da)*Sc] */
    DST_OVER    (4),
    /** [Sa * Da, Sc * Da] */
    SRC_IN      (5),
    /** [Sa * Da, Sa * Dc] */
    DST_IN      (6),
    /** [Sa * (1 - Da), Sc * (1 - Da)] */
    SRC_OUT     (7),
    /** [Da * (1 - Sa), Dc * (1 - Sa)] */
    DST_OUT     (8),
    /** [Da, Sc * Da + (1 - Sa) * Dc] */
    SRC_ATOP    (9),
    /** [Sa, Sa * Dc + Sc * (1 - Da)] */
    DST_ATOP    (10),
    /** [Sa + Da - 2 * Sa * Da, Sc * (1 - Da) + (1 - Sa) * Dc] */
    XOR         (11),
    /** [Sa + Da - Sa*Da, Sc*(1 - Da) + Dc*(1 - Sa) + min(Sc, Dc)] */
    DARKEN      (12),
    /** [Sa + Da - Sa*Da, Sc*(1 - Da) + Dc*(1 - Sa) + max(Sc, Dc)] */
    LIGHTEN     (13),
    /** [Sa * Da, Sc * Dc] */
    MULTIPLY    (14),
    /** [Sa + Da - Sa * Da, Sc + Dc - Sc * Dc] */
    SCREEN      (15),
    /** Saturate(S + D) */
    ADD         (16),
    OVERLAY     (17);
}

```
注释中表明了这些模式的算法。  
在了解这些算法之前需要先了解像素颜色通道。

一个像素的颜色是由四个分量组成即：**ARGB**   
A为透明度通道。RGB为颜色通道。
```
A : 像素点的透明度通道 值为0-1f
R : 像素点红色通道 值为0-250f
G ：像素点绿色通道 值为0-250f
B ：像素点蓝色通道 值为0-250f
```
如果某个像素点的通道值越大则改像素点所占颜色比例越多。比如A（透明通道）A值越小就越透明。A为0就完全透明，A为1f就是完全不透明。当然这个概念在xfermode中只要了解就行了。xfermode不会去改变这些值。

那么注释中的sa、sc、da、 dc可以如下理解

```
S为源图 D为目标图
Sa：全称为Source alpha，表示源图的Alpha通道；
Sc：全称为Source color，表示源图的颜色；
Da：全称为Destination alpha，表示目标图的Alpha通道；
Dc：全称为Destination color，表示目标图的颜色.
```

表示一个源图片的像素点： ```[Sa , Sc]```  
表示一个目标图片的像素点： ```[Da , Dc]```  
然后根据不同的模式的算法就可以生成混合后的像素点了。当然混合后的像素点也是用```[A , B]```这种格式表示。


##### 2.3、这些模式的算法
不同的模式有不同的算法。算不同最后的结果也不同。  

![image](https://user-gold-cdn.xitu.io/2018/1/14/160f2a5f099d59c5?w=312&h=391&f=jpeg&s=128612)

官方的贴图非常形象的展示出各种混合模式使用后展示的效果。

接下来挑出一个常用的例子```SRC_IN```来解释下这些算法的基本应用。   
**圆形头像**实现的方式可能有很多。比如用bitmapshader等等。使用xfermode同样能实现。

看下SRC_IN注释的算法方式。
```
SRC_IN      (5),
/** [Sa * Da, Sa * Dc] */
```
SRC_IN的算法是这样的：  

（a）、**Sa * Da**：源图（S）像素透明度和目标图片（D）像素的透明的决定混合后像素的透明度  
（b）、**Sa * Dc**：源图（S）像素透明度和目标图片（D）像素的颜色决定混合后像素的颜色    

那么混合的图解：

![SRC_IN图解](https://user-gold-cdn.xitu.io/2018/1/14/160f36b5f33802fa?w=878&h=473&f=png&s=101442)

从（a）（b）可以看出，源图片只采用了透明度的变化。混合后图像像素的透明度和颜色都和源图的像素的透明度的有关。如果源图的像素是透明的，那么混合后的像素为透明。反之不透明。所以源图为：

![源图片（S）](https://user-gold-cdn.xitu.io/2018/1/14/160f331be501b7a1?w=201&h=200&f=png&s=2671)

  
从（b）可以看出，决定混合后图像素颜色是由目标图片（D）决定的。所以目标图片是：  

![目标图片（D）](https://user-gold-cdn.xitu.io/2018/1/14/160f3312e63df070?w=200&h=200&f=jpeg&s=9706)

这里主要是理解算法：[Sa * Da, Sa * Dc]  
最后效果：
![](https://user-gold-cdn.xitu.io/2018/1/14/160f371e73ce51f0?w=297&h=313&f=png&s=51751)

示例代码：
```
public class CustomHeadView extends View {

    private Bitmap DBitmap;

    private Bitmap SBitmap;

    private Paint mPaint;

    private PorterDuffXfermode xfermode;

    public CustomHeadView(Context context) {
        this(context, null);
    }

    public CustomHeadView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomHeadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint = new Paint();
        // 这个图片是正常的头像
        DBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.head_d,null);
        // 这个图片是中间一个圆，四个角透明的图片
        SBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.head_s,null);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);

        canvas.drawBitmap(SBitmap,0,0,mPaint);
        mPaint.setXfermode(xfermode);
        canvas.drawBitmap(DBitmap,0,0,mPaint);

        mPaint.setXfermode(null);
        canvas.restoreToCount(layerId);
    }
}

```

上述例子已经非常清晰的说明了xfermode的算法：

**源图（S）和目标图（D）像素的透明度和颜色，通过特定的算法来算出混合后新图的透明度和颜色。（注意这里是对每个像素进行操作）**

这里只是举了一个例子，如果需要了解更多的xfermode的效果可以看下这个博客：
[https://www.jianshu.com/p/d11892bbe055](https://www.jianshu.com/p/d11892bbe055
)
#### 3、一些常用PorterDuffXfermode的例子


##### 3.1、各种形状的图形
使用xfermode来完成圆形头像只是其中之一。如果有特殊要求，想弄成其他的形状都是可以的。
如果我上面写的圆形图片的例子能够理解，那么其他的各种形状的例子使用的方法是一样的。

![各种形状的图形](https://user-gold-cdn.xitu.io/2018/1/14/160f393b3d121096?w=360&h=577&f=png&s=135933)

##### 3.2、刮刮卡效果
实际上实现一个效果并不是说只能采用一种叠加模式。用不同的模式也能做到相同的效果。
这里展示的刮刮卡效果，采用**DST_OUT**模式。
还有一个撕掉美女的衣服和刮刮卡类似，但是用的是**SRC_OUT**模式。  

这里的源图和目标图有点不同，源图或者目标图是手指滑动的路径。毕竟划过的路径变成透明了。


![刮刮卡效果](https://user-gold-cdn.xitu.io/2018/1/14/160f538dde89deb7?w=400&h=710&f=gif&s=4913855)

这里撕掉美女衣服太过底图太“火爆”，动图就不截取了。

##### 3.3、xfermode不仅仅以上的效果。其他的就不一一介绍。
```
主要是我没写出其他的效果来~
```

#### 4、这个不知道起个什么标题好，就记录下我学习xfermode遇到的问题把。
1、 使用xfermode可能会纠结源图和目标图的问题。    
```
 这个确实是这样的。具体哪个做源图和目标图要根据具体实现的效果和不同混合模式的算法去确定。 
```
2、使用xfermode是针对图片的像素的。  
```
 前面提到多次，操作的是像素。
```
3、要调试多次才能达到效果。
```
这个问题我遇到多次，觉得xfermode有毒。   
```
4、这篇文章只是基础，而且写的还有点烂。 
```
 好的文章应该是下一篇！
```

[源码地址：https://github.com/AxeChen/XfermodeSimple](https://github.com/AxeChen/XfermodeSimple)




