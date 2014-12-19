## **Android PAD vehicle inspection system**
---- 
### 开发环境：
---
###### OS: Mac OS X
###### IDE: Android Studio 1.0

### 插件/开源库：
---
-[AndroidAnnotations](https://github.com/excilys/androidannotations "GitHub链接")

-[SlidingMenu](https://github.com/jfeinstein10/SlidingMenu "GitHub链接")

-[EnhancedEditText](https://github.com/DayS/EnhancedEditText "GitHub链接")

-[Google ZXing](https://github.com/zxing/zxing "GitHub链接")

-[SQLiteAssetHelper](https://github.com/jgilfelt/android-sqlite-asset-helper "GitHub链接")

-[Volley](https://github.com/mcxiaoke/android-volley "GitHub链接")

### 待完善
---
-数据库的单利模式建议采用provider；

-页面根据数据库动态生成，采用PreferenceActivity；

-钣金修复页面点击图建议采用SurfaceView 或者游戏的思想已解决七张以上叠加卡顿的限制和界面进入时的卡顿；

-照片采集建议自定义相机，引导式拍照，增强连贯性。后期也可以在照相预览界面添加半透明汽车轮廓以规范采集照片的角度；

-优化车辆信息页面滑动，减少误触EditText onTouch 事件；

