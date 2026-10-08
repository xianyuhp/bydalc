package com.bydalc;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * 客户端配置：唱片的作用范围与果冻效果的各项参数都能在这里调整。
 * 生成的文件是 {@code config/bydalc-client.toml}，改完保存后按 F3+T 重载即可生效。
 * 这些都是纯视觉参数，只影响本机画面，不会与服务端校验冲突。
 */
public final class JellyConfig {
    public static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.DoubleValue RANGE;
    private static final ForgeConfigSpec.DoubleValue SPIN_DEGREES_PER_TICK;
    private static final ForgeConfigSpec.DoubleValue WOBBLE_RADIANS_PER_TICK;
    private static final ForgeConfigSpec.DoubleValue STRETCH;
    private static final ForgeConfigSpec.DoubleValue SQUASH;
    private static final ForgeConfigSpec.DoubleValue BOUNCE;

    /** 生效值，供渲染与范围判定直接读取；配置加载/重载时刷新，未加载时保持默认值。 */
    public static volatile double range = 10.0D;
    public static volatile double rangeSqr = 100.0D;
    public static volatile double spinDegreesPerTick = 15.0D;
    public static volatile double wobbleRadiansPerTick = 0.45D;
    public static volatile double stretch = 0.35D;
    public static volatile double squash = 0.22D;
    public static volatile double bounce = 0.15D;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("effect");
        RANGE = builder
                .comment("作用半径（格）。以唱片机为中心，该范围内的所有生物都会触发果冻效果。")
                .defineInRange("range", 10.0D, 0.0D, 128.0D);
        SPIN_DEGREES_PER_TICK = builder
                .comment("每 tick 绕 Y 轴旋转的角度。15 表示约 1.2 秒转一圈；填 0 则不旋转。")
                .defineInRange("spinDegreesPerTick", 15.0D, 0.0D, 360.0D);
        WOBBLE_RADIANS_PER_TICK = builder
                .comment("伸缩的角速度。0.45 约为每 0.7 秒完成一次拉伸加压缩，数值越大抖得越快。")
                .defineInRange("wobbleRadiansPerTick", 0.45D, 0.0D, 3.0D);
        STRETCH = builder
                .comment("垂直方向最大拉伸幅度。0.35 表示最高被拉到 135% 高；填 0 则不伸缩。")
                .defineInRange("stretch", 0.35D, 0.0D, 1.0D);
        SQUASH = builder
                .comment("水平方向最大收缩幅度。0.22 表示最细时被压到 78% 宽。")
                .defineInRange("squash", 0.22D, 0.0D, 1.0D);
        BOUNCE = builder
                .comment("拉伸时向上弹起的最大高度（格）。仅为视觉偏移，不会真正移动实体。")
                .defineInRange("bounce", 0.15D, 0.0D, 2.0D);
        builder.pop();
        SPEC = builder.build();
    }

    /** 配置加载或重载时，把文件里的值同步到上面供渲染使用的静态字段。 */
    public static void onConfigChanged(ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }
        range = RANGE.get();
        rangeSqr = range * range;
        spinDegreesPerTick = SPIN_DEGREES_PER_TICK.get();
        wobbleRadiansPerTick = WOBBLE_RADIANS_PER_TICK.get();
        stretch = STRETCH.get();
        squash = SQUASH.get();
        bounce = BOUNCE.get();
    }

    private JellyConfig() {}
}
