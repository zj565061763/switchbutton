## 1.0.13
* 修复OnViewPositionChangedCallback位置变化回调可能被多次调用的
* 修复当拖动View被自定义为Button的时候由于Button内部拦截ACTION_DOWN事件导致拖动失效
* 新增sbIsDebug的xml属性可以从xml中设置是否调试模式

## 1.0.12
* 重构捕获view的逻辑判断

## 1.0.11
* 增加日志输出，方便调试

## 1.0.10
* 内部改回ViewDragHelper实现

## 1.0.9
* 重构触摸帮助类

## 1.0.8
* 修改点击控件切换选中状态的逻辑，重构触摸帮助类减少冗余

## 1.0.7
* 修改拖动逻辑判断，拖动起来更顺畅，不会出现速度过快，并且快到边界的时候有时候会卡住一下

## 1.0.6
* onDetachedFromWindow()的时候如果Scroller还未结束，强制结束

## 1.0.5
* 重构触摸帮助类，改变触发拖动的条件，增加debug模式下日志输出
* 修复计算拖动角度的时候可能出现的NaN情况

## 1.0.4
* 修复计算动画时长的时候如果最大可移动的距离传0的时候造成的计算异常

## 1.0.3
* 去掉引用ViewDragHelper，改为Scroller实现
* 去掉setNeedTrackingEdge(boolean needTrackingEdge)方法，默认支持边缘拖动

## 1.0.2
* 修复由于动态计算手柄view的默认margin可能造成的第一次显示的时候闪烁一下的问题，改为在dimens中增加默认margin配置，默认为1dp
