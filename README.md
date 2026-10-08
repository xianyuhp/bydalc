# 通透了

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-3C8527)
![NeoForge](https://img.shields.io/badge/NeoForge-21.1.77-E8843C)
![License](https://img.shields.io/badge/License-GPL--3.0-blue)
![Version](https://img.shields.io/badge/Version-1.0.0-lightgrey)

一张会让人变成果冻的唱片。

把它塞进唱片机，半径 10 格内的**所有生物**都会原地上下拉伸、像果冻一样 Q 弹，并且持续 360° 旋转 —— 包括玩家本人。整个效果是纯客户端渲染，不会挪动实体、也不会动你的视角。

- 模组名：通透了
- 模组 ID：`bydalc`
- 作者：Xyu_Fox
- 许可：GPL-3.0

---

## 效果说明

当唱片机正在播放本模组的唱片时，以唱片机为中心 **10 格**范围内的生物会获得：

| 表现 | 具体行为 |
| --- | --- |
| 果冻伸缩 | 垂直方向在 65% ~ 135% 之间周期性拉伸/压扁，水平方向反向轻微收缩 |
| 原地弹跳 | 拉伸时向上弹起最多 0.15 格，落脚点不变 |
| 持续旋转 | 绕自身视觉中心每 1.2 秒转满一圈，不停歇 |

其它要点：

- **视角完全不受影响**，相机、准星、操作都正常。第一人称下你看不到自己身体在转（其他玩家看你是在转的）。
- 每只生物的伸缩相位按实体 ID 错开，不会像机械方阵一样整齐划一。
- 效果只在**真正播放这张唱片**时生效；唱片被取出、放完或唱片机被破坏后立即停止。
- 服务端只负责广播「哪台唱片机在放」，不做任何实体改动，所以**不会影响实体坐标、存档和战斗判定**。


## 安装

1. 安装 **NeoForge 21.1.77**（对应 Minecraft **1.21.1**）
2. 把 `bydalc-1.0.0.jar` 放进 `.minecraft/mods/`
3. 启动游戏

单人和多人服务器都需要安装（服务端负责同步唱片机状态）。多人环境下其他玩家也能看到果冻效果。

## 模组兼容性

判定入口没有走 NeoForge 高层的生物渲染事件，而是直接挂在**所有实体渲染的总入口**上，因此：

- 任何模组添加的生物都能生效，不要求它的渲染器继承 `LivingEntityRenderer`（很多模组 BOSS 正是自己写的渲染器）
- 多部件生物（多段结构的龙、多头怪等）的部件是在主体渲染流程内绘制的，会自动跟随主体一起扭动
- 判定标准是 `LivingEntity`，也就是所有模组生物的公共基类
- 距离按**碰撞箱最近点**计算，体型巨大的模组 BOSS 站在唱片机旁边也能被正确纳入范围
- 旋转轴心取「碰撞箱高度」与「渲染剔除箱高度」的较大值，模型远大于碰撞箱的模组生物是绕模型视觉中心转，而不是绕脚底甩

不受影响的实体：掉落物、箭、船、矿车等非生物实体。

## 从源码构建

需要 **JDK 21**。

```bash
./gradlew build
```

产物位于 `build/libs/bydalc-1.0.0.jar`。

常用开发命令：

```bash
./gradlew runClient   # 启动开发客户端
./gradlew runServer   # 启动开发服务端
```

> `gradle/wrapper/gradle-wrapper.properties` 里的分发地址指向了腾讯云镜像（`mirrors.cloud.tencent.com`）。如果你的网络能直连 `services.gradle.org`，可以自行改回官方地址。

## 项目结构

```
src/main/java/com/bydalc/
├── BydalcMod.java                          模组入口，注册与事件挂载
├── JellyClientState.java                   客户端保存的「正在播放的唱片机」与 10 格范围判定
├── client/
│   └── JellyVisuals.java                   果冻变换的数学部分
├── event/
│   └── JellyJukeboxTracker.java            服务端跟踪正在播放的唱片机并同步给客户端
├── mixin/
│   └── EntityRenderDispatcherMixin.java    把变换包在实体渲染调用两侧
├── network/
│   └── JellyNetwork.java                   自定义网络包（唱片机坐标列表）
└── registry/
    ├── ModItems.java                       唱片物品 + 唱片曲目注册键
    └── ModSounds.java                      音效注册

src/main/resources/
├── META-INF/neoforge.mods.toml
├── bydalc.mixins.json
├── assets/bydalc/                        语言、模型、贴图、音效
└── data/bydalc/                          唱片曲目与合成配方
```

## 调整手感

没有配置文件，参数都在 [JellyVisuals.java](src/main/java/com/bydalc/client/JellyVisuals.java) 顶部：

```java
private static final float  SPIN_DEGREES_PER_TICK     = 15.0F; // 旋转速度，15 = 1.2 秒一圈
private static final double WOBBLE_RADIANS_PER_TICK   = 0.45D; // 伸缩频率，约 0.7 秒一个来回
private static final float  STRETCH                   = 0.35F; // 垂直拉伸幅度
private static final float  SQUASH                    = 0.22F; // 水平收缩幅度
private static final float  BOUNCE                    = 0.15F; // 弹跳高度（格）
```

作用半径在 [JellyClientState.java](src/main/java/com/bydalc/JellyClientState.java)：

```java
private static final double RANGE_SQR = 10.0D * 10.0D; // 半径 10 格
```

## 许可证

本项目采用 **GNU General Public License v3.0**，完整协议见 [LICENSE](LICENSE)。

你可以自由使用、修改和分发，但基于本项目衍生的作品同样需要以 GPL-3.0 开源。
