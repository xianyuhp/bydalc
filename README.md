# 通透了

![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-3C8527)
![Forge](https://img.shields.io/badge/Forge-47.2.0-E8843C)
![License](https://img.shields.io/badge/License-GPL--3.0-blue)
![Version](https://img.shields.io/badge/Version-1.0.0-lightgrey)

> 这是 **Minecraft 1.20.1 / Forge** 分支。1.21.1 / NeoForge 版本见 [`main`](../../tree/main) 分支。

一张会让人变成果冻的唱片。

把它塞进唱片机，半径 10 格内的**所有生物**都会原地上下拉伸、像果冻一样 Q 弹，并且持续 360° 旋转 —— 包括 BOSS 和玩家本人。整个效果是纯客户端渲染，不会挪动实体、也不会动你的视角。

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
- **循环播放**：歌曲放完会自动从头重放，声音和果冻效果一直持续，直到你把唱片取出来（或唱片机被破坏）。这个机制同时也能从任何一次意外中断里自动恢复，所以服务器上不会出现「放一会儿就停了」。
- 服务端只负责广播「哪台唱片机在放」，不做任何实体改动，所以**不会影响实体坐标、存档和战斗判定**。

## 唱片

| 项目 | 值 |
| --- | --- |
| 物品 ID | `bydalc:bydj` |
| 名称 | 邦友的酒 |
| 曲目 | 活跃黑江乐 - 邦友的酒(DJ版) / 梦的结唱halo |
| 时长 | 约 191 秒 |

**获取方式**

- 创造模式：物品栏「工具与实用物品」分类
- 生存模式合成：

```
金锭  金锭  金锭
金锭  钻石  金锭
金锭  金锭  金锭
```

### 关于 `minecraft:music_discs` 标签

唱片同时被登记进原版的 `minecraft:music_discs` 物品标签（见 [music_discs.json](src/main/resources/data/minecraft/tags/items/music_discs.json)）。

1.20.1 的 `JukeboxBlockEntity.setItem()` 会先判断 `stack.is(ItemTags.MUSIC_DISCS)`，不满足就**直接返回、什么都不做**。所以模组唱片若不进这个标签，玩家右键唱片机时手上的唱片会被消耗掉，唱片机却依然是空的。除此之外该标签也用于合成配方与其它模组的「是否音乐唱片」判定。

## 安装

1. 安装 **Forge 47.2.0**（对应 Minecraft **1.20.1**）
2. 把 `bydalc-1.0.0.jar` 放进 `.minecraft/mods/`
3. 启动游戏

单人和多人服务器都需要安装（服务端负责同步唱片机状态）。多人环境下其他玩家也能看到果冻效果。

## 模组兼容性

判定入口没有走 Forge 高层的生物渲染事件，而是用 Mixin 直接挂在**所有实体渲染的总入口**上，因此：

- 任何模组添加的生物都能生效，不要求它的渲染器继承 `LivingEntityRenderer`（很多模组 BOSS 正是自己写的渲染器）
- 多部件生物（多段结构的龙、多头怪等）的部件是在主体渲染流程内绘制的，会自动跟随主体一起扭动
- 判定标准是 `LivingEntity`，也就是所有模组生物的公共基类
- 距离按**碰撞箱最近点**计算，体型巨大的模组 BOSS 站在唱片机旁边也能被正确纳入范围
- 旋转轴心取「碰撞箱高度」与「渲染剔除箱高度」的较大值，模型远大于碰撞箱的模组生物是绕模型视觉中心转，而不是绕脚底甩

不受影响的实体：掉落物、箭、船、矿车等非生物实体。

## 从源码构建

需要 **JDK 17**（Forge 1.20.1 要求；用更高版本编译出的字节码在 1.20.1 上跑不起来）。请自行安装 JDK 17 并让 Gradle 能找到它，例如设置 `JAVA_HOME`：

```bash
export JAVA_HOME=/path/to/jdk-17
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew build
```

产物位于 `build/libs/bydalc-1.0.0.jar`。

常用开发命令：

```bash
./gradlew runClient   # 启动开发客户端
./gradlew runServer   # 启动开发服务端
```

> - 本项目**没有**引入 Gradle 的 foojay 工具链自动下载插件。少了 JDK 17 时 Gradle 会直接报错，而不会去联网下载。
> - `gradle/wrapper/gradle-wrapper.properties` 里的分发地址指向腾讯云镜像（`mirrors.cloud.tencent.com`）。如果你的网络能直连 `services.gradle.org`，可以自行改回官方地址。

## 项目结构

```
src/main/java/com/bydalc/
├── BydalcMod.java                          模组入口，注册与事件挂载
├── JellyClientState.java                   客户端保存的「正在播放的唱片机」与范围判定
├── JellyConfig.java                        客户端配置项定义
├── client/
│   └── JellyVisuals.java                   果冻变换的数学部分
├── event/
│   └── JellyJukeboxTracker.java            服务端跟踪正在播放的唱片机并同步给客户端
├── mixin/
│   └── EntityRenderDispatcherMixin.java    把变换包在实体渲染调用两侧
├── network/
│   └── JellyNetwork.java                   SimpleChannel 网络包（唱片机坐标列表）
└── registry/
    ├── ModItems.java                       唱片物品（RecordItem）
    └── ModSounds.java                      音效注册

src/main/resources/
├── META-INF/mods.toml
├── bydalc.mixins.json
├── assets/bydalc/                        语言、模型、贴图、音效
└── data/
    ├── bydalc/recipes/                   合成配方
    └── minecraft/tags/items/             把唱片加入 music_discs 标签
```

## 配置

首次启动后会生成 `config/bydalc-client.toml`，直接编辑这个文件就能调整全部效果参数。改完保存，进游戏按 **F3+T** 重载资源即可生效（重启游戏也可以）。

```toml
[effect]
    # 作用半径（格）
    range = 10.0
    # 每 tick 绕 Y 轴旋转的角度，15 约等于 1.2 秒一圈，填 0 不旋转
    spinDegreesPerTick = 15.0
    # 伸缩角速度，0.45 约等于 0.7 秒完成一个来回
    wobbleRadiansPerTick = 0.45
    # 垂直最大拉伸幅度，0.35 表示最高拉到 135%
    stretch = 0.35
    # 水平最大收缩幅度，0.22 表示最细压到 78%
    squash = 0.22
    # 弹跳高度（格）
    bounce = 0.15
```

这是**客户端**配置，只影响你自己看到的画面，不需要服务端同步，也不会和别人冲突。把某个值填 0 就能单独关掉对应的效果。

## 许可证

本项目采用 **GNU General Public License v3.0**，完整协议见 [LICENSE](LICENSE)。

你可以自由使用、修改和分发，但基于本项目衍生的作品同样需要以 GPL-3.0 开源。
