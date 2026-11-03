# 创维酷开 9R66_A7 定制版

本分支基于 [Gathin23/cleanyourtv 的 v3.3.0 正式版](https://github.com/Gathin23/cleanyourtv/releases/tag/v3.3.0)，用于 Android 6.0 / API 23 的创维酷开电视。保留原项目许可证及署名。

- `main`：原项目源码。本次创建仓库时已同步到当时的上游主分支，可能包含正式版之后的提交。
- `skyworth-api23`：电视定制版，按正式版标签升级。请在此分支维护定制。

## 保留的功能

- Android 6 兼容：最低 API 23、Java 核心库 desugaring、高版本 Android API 的版本判断、兼容字体与文件读取。
- 酷开私有 Home intent、Home 辅助服务、仅针对原厂 Home 窗口的回跳。
- 遥控器可操作的“应用显示／隐藏”管理、普通应用入口和固定的 U 盘／文件管理入口。
- 沿用电视中已保存的应用显示、排列、列数和主题配置；升级不清除数据。
- 单行排列改用 v3.3.0 已有的 `single_row` 实现，继续读取原来的同名配置，减少重复维护。

API 23 没有可靠的 `BOOT_COUNT` 和 `disableSelf()`：依赖它们的可选“每次系统启动打开指定应用”和待机暂退辅助服务功能在本机保持停用。开机进入桌面与 Home 按键接管不依赖这些开关。

## 构建与检查

使用 JDK 17、Android SDK platform 34 / build-tools 34.0.0。在 Windows PowerShell 中：

```powershell
$env:JAVA_HOME = '你的 JDK17 路径'
$env:ANDROID_HOME = '你的 Android SDK 路径'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
# Windows 工作目录较长时，用一个确实存在的短目录存放 JDK 套接字。
$env:JAVA_TOOL_OPTIONS = '-Djdk.net.unixdomain.tmpdir=C:/tv-javatmp'
New-Item -ItemType Directory -Force -Path 'C:\tv-javatmp' | Out-Null
.\launcher\gradlew.bat -p launcher testReleaseUnitTest lintRelease assembleRelease --console=plain --no-daemon
```

APK 位于 `launcher/app/build/outputs/apk/release/app-release.apk`。本版 `versionName=3.3`、`versionCode=12`、`minSdk=23`。

覆盖安装必须沿用原有 APK 签名密钥；本机沿用最初构建使用的密钥。私钥、ADB 授权密钥、电视配置备份和截图均不上传 GitHub。其他电脑自行构建会使用它自己的调试密钥，不能直接覆盖这台电视已装的版本。

## 以后更新

1. 备份定制分支及可用 APK，获取官方新正式版标签。
2. 在定制分支合并该标签，解决冲突；以本文件的定制清单核对，保留原项目的新功能。
3. 跑完整单元测试、API 23 Lint 检查和 release 构建，核对包名、版本号、最低 API 与签名。
4. 电视开机后先记录原状态、备份当前 APK 和配置，再串行覆盖安装。
5. 验证实体遥控器 Home、首尾连续长按、快速反向、U 盘入口、应用显示设置和断电开机。

2026-11-03：317 项单元测试通过，Release 构建成功，API 23 Lint 为 0 错误；已覆盖安装到本机电视并确认启动、当前五个入口顺序、U 盘文件管理及应用显示选择保持正常。模拟方向键连续移动与首尾边界检查通过。实体遥控器 Home、长按和断电开机仍需分别确认，不能以模拟按键代替。

## 本机维护约束

- ADB 仅允许一条连接，所有电视操作串行。
- 没有 `cmd` 和 `pm set-home-activity`，不能套用新版 Android 的默认桌面设置脚本。
- 受保护包不能用 `pm disable-user`；已有隐藏通过 `pm hide` / `pm unhide` 恢复。本次升级不重新执行清理脚本，不增加系统包屏蔽。
- 不卸载、不清数据、不 root、不刷机；安装前保存当前包状态和恢复材料。
