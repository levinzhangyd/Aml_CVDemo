Amlogic BDS AI Demo distribute 
Step1:配置cmake路径，本地下载opencv的android-sdk后，替换工程中的如下路径
set( OpenCV_DIR E:\\AI\\opencv\\opencv-4.10.0-android-sdk\\OpenCV-android-sdk\\sdk\\native\\jni )

step2:把模型和图片的样本资源，推送到app的安装目录下
resources:AI.zip,解压后推送到如下目录：
new Dir: /data/user/0/com.amlogic.cvdemo/files/
