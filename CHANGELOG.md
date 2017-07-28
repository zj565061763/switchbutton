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
