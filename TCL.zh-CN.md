# TCL／雷鸟 Android 9 维护版

本分支 `tcl-android9` 保存已部署的 CleanYourTV 3.4 TCL 维护版，`versionCode 15`。
2026-11-09 合并上游正式版 `v3.4.0`，保留此前 TCL 定制及签名/包名更新方式。
基于本仓库 `skyworth-api23` 的提交 `141861486e301d83385b7f077a3908bf787f0007`，
保留原项目许可证、署名及 Android 6 兼容代码；TCL 改动在此分支独立维护。
酷开电视继续使用 [`skyworth-api23`](https://github.com/zyluoqun/cleanyourtv/tree/skyworth-api23)。

## 已验证设备与行为

- TCL／雷鸟，设备代号 `tcl_m7642`，Android 9 / API 28。
- 固件 `TVOS-04.16.031.01.12`。仅代表这台设备的验证结果，不保证其它固件相同。
- CleanYourTV 是标准默认 HOME，原厂桌面 `com.tcl.cyberui` 保持停用。
- 不依赖 CleanYourTV 无障碍守护；部署过程没有 root、刷机、卸载或清除应用数据。

本机固件按包名限制第三方 HOME。构建时使用已有的 `launcherApplicationId` 参数指定
`com.alibaba.dingtalk.focus`，Java namespace 仍为 `com.gathin.tvhome`。
这是本维护版的兼容身份，并非钉钉应用；不能与具有相同包名的其它应用共存。

## 本分支的改动

1. **开机 HOME 入口**：增加 `BootHomeActivity` 并设为 `directBootAware`。
   用户解锁前只显示简单等待画面，不读取凭据加密存储中的桌面配置；解锁后进入 `MainActivity`。
   此前直接把 `MainActivity` 设为 HOME 会导致本机开机停在初始化界面。
2. **应用显示／隐藏**：保存选择时递增设置版本。返回桌面时立即刷新，无需再改列数或程序坞下方行数。
3. **U 盘入口**：把原厂 `com.tcl.ui_mediaCenter` 固定为“U 盘／文件管理”卡片，
   不受旧隐藏记录影响，也不在应用显示选择中重复出现；保留原有酷开文件管理器支持。
   打开后选择 U 盘，再进入 `Browse Folders` 浏览目录。未加入额外文件管理器。
4. **升级版本号**：`versionName 3.4`，`versionCode 15`，高于此前本机维护版本。
5. **上游 3.4 修复**：包括 TCL Android 9 待机后时钟刷新、输入键路由及其它上游正式版修复。

## 构建

需要 JDK 17、Android SDK platform 34 / build-tools 34.0.0，以及可用的 Android SDK 路径。
在仓库根目录的 PowerShell 中执行：

```powershell
$env:JAVA_HOME = '你的 JDK17 路径'
$env:ANDROID_HOME = '你的 Android SDK 路径'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
Set-Location launcher
.\gradlew.bat --no-daemon '-PlauncherApplicationId=com.alibaba.dingtalk.focus' testReleaseUnitTest lintRelease assembleRelease
```

APK 输出：`launcher/app/build/outputs/apk/release/app-release.apk`。
省略 `launcherApplicationId` 参数会构建默认包名，不能作为这台电视现有安装的覆盖更新。

Release 沿用此分支既有的本机 Android debug 签名配置。覆盖更新必须使用本电脑原来的签名，
仅从另一台电脑重新构建不能保证可覆盖安装。签名私钥留在本电脑，不进入 GitHub。
后续更新应增加 `versionCode`，保留包名和签名，不卸载旧应用、不清数据。

3.4 本次实装 APK 为 827,606 字节，SHA256：

```text
912ea8151badbcfea3275932b8c1c6bd6835dcfa6b49207ed34f2331de421557
```

该散列用于核对本次本机构建与实装文件，不表示不同环境重新构建必然逐字节相同。
APK 大于上游 576 KB 的 PR 尺寸门槛；这是个人 fork 的设备维护分支，没有修改上游 CI 或尺寸门槛。

## 更新与恢复

本机固件拒绝标准 `pm install`，原厂安装器也报告安装异常。
此前通过电视自带“多屏互动”的安装确认流程完成了覆盖安装，并核对实装 APK 散列。
仓库保存源码和构建说明，原始备份、APK、安装会话记录及恢复脚本继续只保存在本电脑。

更新前备份当前 APK、应用配置、默认 HOME 和包启用状态。
新版本启动并确认默认 HOME 可用之前，不停用任何当前使用的桌面。
本机正确 HOME 组件为：

```text
com.alibaba.dingtalk.focus/com.gathin.tvhome.BootHomeActivity
```

若需恢复这台设备的原厂桌面，使用本地恢复脚本；相应的单条 ADB shell 命令为：

```text
pm default-state --user 0 com.tcl.cyberui
cmd package set-home-activity com.tcl.cyberui/.MainActivity
am start -n com.tcl.cyberui/.MainActivity
```

恢复原厂 HOME 不会恢复下面的精简应用。恢复它们应按本地备份逐项还原原启用状态，
不要把整批应用统一设为 `enabled`，也不要恢复无关组件。

## 本机精简记录

以下是用户选择停用的 18 个应用，使用 `pm disable-user --user 0` 逐项操作。
这是本机偏好记录，不是其它电视通用的自动停用清单；JiqiMao 和小T助手停用后不再保留语音功能。

| 应用 | 包名 |
| --- | --- |
| TCL智慧家居 | `com.tcl.convergehome` |
| T惠购 | `com.tcl.tshop` |
| 家庭相机 | `com.tcl.camera` |
| 智能相册 | `com.tcl.tvsmartalbum` |
| 有线频道 | `com.tcl.tliveplay` |
| 机顶盒设置 | `com.kookong.tvplus` |
| 游戏中心 | `com.tcl.gamecenter` |
| 玩机技巧 | `com.tcl.playskill` |
| 用户中心 | `com.tcl.usercenter` |
| 艺生活 | `com.tcl.appreciate.art` |
| 视频通话 | `com.tcl.videocall` |
| 雷咚咚少儿 | `com.tcl.ffeducation` |
| JiqiMao | `com.tcl.rewalleve` |
| 全景浏览器 | `com.tcl.browser` |
| 天气预报 | `com.tcl.common.weather` |
| 小T助手 | `com.tcl.walleve` |
| 应用商店 | `com.tcl.appmarket2` |
| 搜索 | `com.tcl.wholenetsearch` |

此前停用的 `com.tcl.bootadservice`、`com.tcl.c2dm.client`、`com.tcl.bi`、`com.tcl.logkit` 保持原状。
基础电视／HDMI 相关包 `com.tcl.tv`、媒体中心、系统设置、输入法和蓝牙保持启用。
“有线频道”对应独立应用 `com.tcl.tliveplay`，不等同于基础电视包 `com.tcl.tv`。

## 验证记录与后续维护

2026-11-09 升级 3.4：

- 325 项 JVM 测试通过、Lint 0 错误；签名与此前实装版一致。
- 备份更新前 APK、外部配置与系统状态后覆盖安装，未卸载、清除数据或改动停用清单。
- 保留 `BootHomeActivity`、固定 U 盘卡片及应用显示保存刷新修复。
- 更新前桌面及显示选择已读取；更新后隐藏/恢复 SmartTube 即时生效，最后恢复原显示状态。
- 实机卡片数 `7 → 6 → 7`，默认 HOME、升级前停用清单和实装 APK 散列均已核对；U 盘目录可打开，时钟跨分钟更新正常。
- 文档链接检查 10 项通过。完整 Python 工具测试在本机默认 GBK 环境遇到子进程解码错误后中止，未宣称全仓工具测试通过。
- 本轮未重启或切换待机；冷启动及待机恢复测试仍不以模拟 Home 代替。

2026-11-04：

- 319 项 JVM 单元测试全部通过，Release 构建成功，Lint 为 0 错误。
- 新增两项 TCL U 盘可见性回归测试：修复前失败，修复后通过。
- 旧版实机复现 SmartTube 取消显示后仍留在桌面；新版保存后卡片数即时 `8 → 7 → 8`，
  全程未修改列数或程序坞下方行数，最后恢复 SmartTube 显示。
- 桌面 U 盘卡片打开原厂媒体中心，识别已插入 U 盘并进入文件目录。
- 实装 APK 散列与构建产物一致，默认 HOME 仍指向 `BootHomeActivity`，方向键及返回桌面检查通过。
- `versionCode 13` 的 DirectBoot 修复已在此前实际开机后验证进入 CleanYourTV，用户确认实体 Home 返回正常。
  `versionCode 14` 保留同一开机入口，本轮没有再次重启；本轮 18 项停用后的冷启动及长期待机稳定性尚未重新验证。

后续请在本分支维护 TCL 改动，先对照上游正式版合并必要修复，再构建、备份和串行覆盖更新。
保留原厂桌面恢复途径；不要把本分支的 TCL HOME 及媒体中心行为合并回酷开分支。
