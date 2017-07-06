# SwitchButton
Android开关按钮

## 默认效果
![](http://thumbsnap.com/i/KBISOucv.gif?0705)

## 覆盖全局默认效果
支持覆盖的默认配置：<br>
* colors <br>
可以在项目中定义colors覆盖库中的默认配置<br>
![](http://thumbsnap.com/i/VJIMDfDU.png?0706)<br>

* dimens <br>
可以在项目中定义dimens覆盖库中的默认配置<br>
![](http://thumbsnap.com/i/RoMc4bVA.png?0706)<br>

* drawable <br>
可以在项目中定义drawable覆盖库中的默认配置，覆盖drawable后以上的colors和dimens设置失效<br>
![](http://thumbsnap.com/i/vErZPQhN.png?0706)<br>

## xml属性
* 支持的xml属性配置：<br>
![](http://thumbsnap.com/i/4DrNGJt9.png?0706)<br>

## 自定义效果
![](http://thumbsnap.com/i/YS9spIQs.gif?0706)<br>

1. xml中布局：<br>
![](http://thumbsnap.com/i/GEdAFteT.png?0706)<br>
2. java文件中：<br>
![](http://thumbsnap.com/s/9xlaALzm.png?0706)<br>

## 完全自定义效果
![](http://thumbsnap.com/i/4jo7RqHa.gif?0706)<br>
xml中布局：<br>
![](http://thumbsnap.com/i/8Z9dbQ1f.png?0706)<br>
指定view的id为库中的默认id即可完全自定义view的展示效果，定义任何你想要的效果，可以指定其中一个id或者全部id<br>
<br>
库中支持的id如下：
* 正常view的id：`lib_sb_view_normal`<br>
* 选中view的id：`lib_sb_view_checked`<br>
* 手柄view的id：`lib_sb_view_thumb`<br>
