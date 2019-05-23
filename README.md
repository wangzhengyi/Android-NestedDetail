## 一、从一个牛逼的DEMO看什么是嵌套滚动##

我们先来看一下DEMO的效果，直观的感受一下什么是嵌套滚动：



解释上图涉及到哪些嵌套滑动操作之前，我先贴一下嵌套布局结构：

```xml
<com.wzy.nesteddetail.view.NestedWebViewRecyclerViewGroup>

    <com.wzy.nesteddetail.view.NestedScrollWebView/>

    <TextView />

    <android.support.v7.widget.RecyclerView />

</com.wzy.nesteddetail.view.NestedWebViewRecyclerViewGroup>
```

其中：

 1. NestedWebViewRecyclerViewGroup为最外层滑动容器
 2. com.wzy.nesteddetail.view.NestedScrollWebView为顶部的可嵌套滑动的WebView
 3. TextView为中部不可滑动的TextView
 4. android.support.v7.widget.RecyclerView为底部可滑动的RecyclerView

解释一下上图的gif中涉及了哪些嵌套滑动操作:

 1. 滑动顶部WebView时，首先滑动WebView的内容，WebView的内容滑动到底后再滑动外层容器；外层容器滑动到底后，再将滑动传递给RecyclerView.
 2. 滑动底部RecyclerView时，首先滑动RecyclerView的内容，RecyclerView的内容滑动到顶后再滑动外层容器；外层容器滑动到底后，再将滑动传递给WebView.
 3. 滑动本身不可滑动的View时，滑动时间被外层滑动容器拦截。滑动容器根据滑动方向和是否滑动到阈值，再将相应的滑动传递给WebView或者RecyclerView.
 4. Flying操作同理。

再不知道NestedScrolling机制之前，我相信大部分人想实现上面的滑动效果都是比较头大的，感谢Google提供了牛逼的嵌套滑动机制，学习完这篇文章，我相信大部分人都会觉得这种滑动效果还是比较简单的。

## 二、NestedScrolling接口简介 ##

在了解这种嵌套滑动效果的具体实现之前，先学习一下Google提供的NestedScrolling机制。Android在support.v4包里提供了两个嵌套滑动的类:

 - NestedScrollingParent
 - NestedScrollingChild

再解释源码定义之前，我先用通粗的语言解释一下Google提供这两个类的作用：

 1. Google从逻辑上区分了滑动的两个角色：NestedScrollingParent简称ns parent，NestedScrollingChild简称ns child。对应了滑动布局中的外层滑动容器和内部滑动容器。
 2. ns child在收到DOWN事件时，找到离自己最近的ns parent，与它进行绑定并关闭它的事件拦截机制。
 3. ns child会在接下来的MOVE事件中判定出用户触发了滑动手势，并把事件拦截下来给自己消费。
 4. 消费MOVE事件流时，对于每一个MOVE事件增加的滑动距离：
    4.1. ns child并不是直接自己消费，而是先将它交给ns parent，让ns parent可以在ns child滑动前进行消费。
    4.2. 如果ns parent没有消费或者滑动没消费完，ns child再消费剩下的滑动。
    4.3. 如果ns child消费后滑动还是有剩余，会再把剩下的滑动交给ns parent消费。
    4.4. 最后ns parent消费滑动后还有剩余，ns child可以做最终处理。
 5. flying操作同scroll操作消费原理。

NestedScrollingParent和NestedScrollingChild的源码定义也是为了配合滑动实现定义出来的：

```java
// NestedScrollingChild
void setNestedScrollingEnabled(boolean enabled);    // 设置是否开启嵌套滑动
boolean isNestedScrollingEnabled();                 // 获得设置开启了嵌套滑动
boolean startNestedScroll(@ScrollAxis int axes);    // 沿给定的轴线开始嵌套滚动
void stopNestedScroll();                            // 停止当前嵌套滚动
boolean hasNestedScrollingParent();                 // 如果有ns parent，返回true
boolean dispatchNestedPreScroll(int dx
    , int dy
    , @Nullable int[] consumed
    , @Nullable int[] offsetInWindow);              // 消费滑动时间前，先让ns parent消费
boolean dispatchNestedScroll(int dxConsumed
    , int dyConsumed
    , int dxUnconsumed
    , int dyUnconsumed
    , @Nullable int[] offsetInWindow);              // ns parent消费ns child剩余滚动后是否还有剩余。return true代表还有剩余
boolean dispatchNestedPreFling(float velocityX
    , float velocityY);                            // 消费fly速度前，先让ns parent消费
boolean dispatchNestedFling(float velocityX
    , float velocityY
    , boolean consumed);                           // ns parent消费ns child消费后的速度之后是否还有剩余。return true代表还有剩余
```
```java
// NestedScrollingParent
boolean onStartNestedScroll(@NonNull View var1
    , @NonNull View var2
    , int var3);                                  // 决定是否接收子View的滚动事件
void onNestedScrollAccepted(@NonNull View var1
    , @NonNull View var2
    , int var3);                                 // 响应子View的滚动
void onStopNestedScroll(@NonNull View var1);     // 滚动结束的回调
void onNestedPreScroll(@NonNull View var1
    , int var2
    , int var3
    , @NonNull int[] var4);                      // ns child滚动前回调
void onNestedScroll(@NonNull View var1
    , int var2
    , int var3
    , int var4
    , int var5);                                // ns child滚动后回调
boolean onNestedPreFling(@NonNull View var1
    , float var2
    , float var3);                             // ns child flying前回调
boolean onNestedFling(@NonNull View var1
    , float var2
    , float var3
    , boolean var4);                           // ns child flying后回调
int getNestedScrollAxes();                     // 返回当前布局嵌套滚动的坐标轴
```
Google为了让大家方便的实现这两个接口，提供了NestedScrollingParentHelper和NestedScrollingChildHelper这两个帮助类。所以一般实现这两个接口的写法是：

**ns child**:

```java
public class NestedScrollWebView extends WebView implements NestedScrollingChild {
    private NestedScrollingChildHelper mChildHelper;

    private NestedScrollingChildHelper getNestedScrollingHelper() {
        if (mChildHelper == null) {
            mChildHelper = new NestedScrollingChildHelper(this);
        }
        return mChildHelper;
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        getNestedScrollingHelper().setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return getNestedScrollingHelper().isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return getNestedScrollingHelper().startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        getNestedScrollingHelper().stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return getNestedScrollingHelper().hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow) {
        return getNestedScrollingHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow) {
        return getNestedScrollingHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return getNestedScrollingHelper().dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return getNestedScrollingHelper().dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean startNestedScroll(int axes, int type) {
        return getNestedScrollingHelper().startNestedScroll(axes, type);
    }

    @Override
    public void stopNestedScroll(int type) {
        getNestedScrollingHelper().stopNestedScroll(type);
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        return getNestedScrollingHelper().hasNestedScrollingParent(type);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type) {
        return getNestedScrollingHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int type) {
        return getNestedScrollingHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }
}
```

**ns parent**:

```java
public class NestedWebViewRecyclerViewGroup extends ViewGroup implements NestedScrollingParent {
    private NestedScrollingParentHelper mParentHelper;

    private NestedScrollingParentHelper getNestedScrollingHelper() {
        if (mParentHelper == null) {
            mParentHelper = new NestedScrollingParentHelper(this);
        }
        return mParentHelper;
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public int getNestedScrollAxes() {
        return getNestedScrollingHelper().getNestedScrollAxes();
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        getNestedScrollingHelper().onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onStopNestedScroll(View child) {
        getNestedScrollingHelper().onStopNestedScroll(child);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        // 处理预先flying事件
        return false;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        // 处理后续flying事件
        return false;
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        // 处理后续scroll事件
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @Nullable int[] consumed) {
        // 处理预先滑动scroll事件
    }
}
```

## 三、效果实现 ##

只知道原理相信大家是不满足的，结合原理进行实操才是关键。

**实现一个支持嵌套WebView和RecyclerView滑动的外部容器**

对于外部容器，主要需要考虑上滑和下滑时外部容器的滑动处理：

1. 向上滑动时，如果WebView可以向下滑动，该事件交给WebView处理；如果WebView不可以向下滑动，并且滑动距离没有超过容器的最大滑动距离，该事件由自身处理；如果WebView不可以滑动，滑动距离超过了容器的最大滑动距离，这时将滑动时间传递给RecyclerView，让RecyclerView处理;
2. 向下滑动时，如果RecyclerView没有到顶部，该事件交给RecyclerView处理；如果RecyclerView已经到顶部，并且容器的滑动距离不为0，该事件由自身处理；如果如果RecyclerView已经到顶部，并且容器的滑动距离已经为0，则该事件交给WebView处理;

容器的最大滑动距离=所有子View的高度-容器自身的高度.

Flying和MOVE事件的处理类似，区别是：一个传递的是速度，一个传递的是距离。

核心代码如下:
```java
@Override
public void onNestedPreScroll(@NonNull View target, int dx, int dy, @Nullable int[] consumed) {
    boolean isWebViewBottom = !canWebViewScrollDown();
    boolean isCenter = isParentCenter();
    if (dy > 0 && isWebViewBottom && getScrollY() < getInnerScrollHeight()) {
        //为了WebView滑动到底部，继续向下滑动父控件
        scrollBy(0, dy);
        if (consumed != null) {
            consumed[1] = dy;
        }
    } else if (dy < 0 && isCenter) {
        //为了RecyclerView滑动到顶部时，继续向上滑动父控件
        scrollBy(0, dy);
        if (consumed != null) {
            consumed[1] = dy;
        }
    }
    if (isCenter && !isWebViewBottom) {
        //异常情况的处理
        scrollToWebViewBottom();
    }
}


@Override
public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
    if (target instanceof NestedScrollWebView) {
        //WebView滑到底部时，继续向下滑动父控件和RV
        currentScrollType = SCROLL_WEB_PARENT;
        parentFling(velocityY);
    } else if (target instanceof RecyclerView && velocityY < 0 && getScrollY() >= getInnerScrollHeight()) {
        //RV滑动到顶部时，继续向上滑动父控件和WebView，这里用于计算到达父控件的顶部时RV的速度
        currentScrollType = SCROLL_RV_PARENT;
        parentFling((int) velocityY);
    } else if (target instanceof RecyclerView && velocityY > 0) {
        isRvFlingDown = true;
    }

    return false;
}

@Override
public void computeScroll() {
    if (mScroller.computeScrollOffset()) {
        int currY = mScroller.getCurrY();
        switch (currentScrollType) {
            case SCROLL_WEB_PARENT://WebView向父控件滑动
                if (isRvFlingDown) {
                    //RecyclerView的区域的fling由自己完成
                    break;
                }
                scrollTo(0, currY);
                invalidate();
                checkRvTop();
                if (getScrollY() == getInnerScrollHeight() && !hasFling) {
                    //滚动到父控件底部，滚动事件交给RecyclerView
                    hasFling = true;
                    recyclerViewFling((int) mScroller.getCurrVelocity());
                }
                break;
            case SCROLL_PARENT_WEB://父控件向WebView滑动
                scrollTo(0, currY);
                invalidate();
                if (currY <= 0 && !hasFling) {
                    //滚动父控件顶部，滚动事件交给WebView
                    hasFling = true;
                    webViewFling((int) -mScroller.getCurrVelocity());
                }
                break;
            case SCROLL_RV_PARENT://RecyclerView向父控件滑动，fling事件，单纯用于计算速度
                if (getScrollY() != 0) {
                    invalidate();
                } else if (!hasFling) {
                    hasFling = true;
                    //滑动到顶部时，滚动事件交给WebView
                    webViewFling((int) -mScroller.getCurrVelocity());
                }
                break;
        }
    }
}
```

**实现一个支持嵌套滑动的WebView**

本身WebView是不支持嵌套滑动的，想要支持嵌套滑动，我们需要让WebView实现NestedScrollingChild接口，并且处理好TouchEvent方法中的事件传递。
实现NestedScrollingChild接口比较简单，上面也介绍过了，可以使用Google提供的NestedScrollingChildHelper辅助类。
处理TouchEvent的思路，需要遵循以下步骤：

 1. DOWN事件时通知父布局，自己要开始嵌套滑动了。
 2. 对于MOVE事件，先交给父布局消费。父布局判断WebView不能向下滑动了，就父布局消费；还能向下滑动，就给WebView消费。
 3. 对于Flying事件，同样先咨询父布局是否消费。父布局判断WebView不能向下滑动了，就父布局消费；还能向下滑动，就给WebView消费。

WebView最大滑动距离=WebView自身内容的高度-WebView容器的高度

思路比较简单，我们看一下对应的核心代码：
```java
@Override
public boolean onTouchEvent(MotionEvent event) {
    android.util.Log.d("wangzhengyi", "NestedWebview onTouchEvent: event=" + event.getAction());
    switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mWebViewContentHeight = 0;
            mLastY = (int) event.getRawY();
            mFirstY = mLastY;
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            initOrResetVelocityTracker();
            mIsSelfFling = false;
            mHasFling = false;
            mMaxScrollY = getWebViewContentHeight() - getHeight();
            startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            break;
        case MotionEvent.ACTION_MOVE:
            initVelocityTrackerIfNotExists();
            mVelocityTracker.addMovement(event);
            int y = (int) event.getRawY();
            int dy = y - mLastY;
            mLastY = y;
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (!dispatchNestedPreScroll(0, -dy, mScrollConsumed, null)) {
                scrollBy(0, -dy);
            }
            if (Math.abs(mFirstY - y) > TOUCHSLOP) {
                //屏蔽WebView本身的滑动，滑动事件自己处理
                event.setAction(MotionEvent.ACTION_CANCEL);
            }
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            if (isParentResetScroll() && mVelocityTracker != null) {
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int yVelocity = (int) -mVelocityTracker.getYVelocity();
                recycleVelocityTracker();
                mIsSelfFling = true;
                flingScroll(0, yVelocity);
            }
            break;
    }
    super.onTouchEvent(event);
    return true;
}

@Override
public void computeScroll() {
    if (mScroller.computeScrollOffset()) {
        final int currY = mScroller.getCurrY();
        if (!mIsSelfFling) {
            // parent flying
            scrollTo(0, currY);
            invalidate();
            return;
        }

        if (isWebViewCanScroll()) {
            scrollTo(0, currY);
            invalidate();
        }
        if (!mHasFling
                && mScroller.getStartY() < currY
                && !canScrollDown()
                && startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
                && !dispatchNestedPreFling(0, mScroller.getCurrVelocity())) {
            //滑动到底部时，将fling传递给父控件和RecyclerView
            mHasFling = true;
            dispatchNestedFling(0, mScroller.getCurrVelocity(), false);
        }
    }
}
```

## 总结 ##

NestedScrolling机制看似复杂，但其实就是实现两个接口的事情，而且Google提供了辅助类Helper来帮助我们实现接口。这种机制将滑动时间的传递封装起来，通过Helper辅助类实现ns parent和ns child之间的连接和交互。通过接口回调，也实现了ns parent和ns child的解耦。