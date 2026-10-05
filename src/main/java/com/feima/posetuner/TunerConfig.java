package com.feima.posetuner;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/**
 * 单姿态配置。
 *
 * <p>配置文件 {@code config/posetuner.toml} 里只有一节 {@code [pose]}。
 *
 * <p>单位：
 * <ul>
 *   <li>旋转：度</li>
 *   <li>root 位移：格</li>
 *   <li>其它部位位移：模型像素</li>
 * </ul>
 *
 * <p>位移是相对部件初始位置的偏移，写 0 = 不偏移。
 *
 * <p><b>生效方式</b>：mixin 每帧直接读 {@link Fields} 里的
 * {@code DoubleValue.get()}。Configured 改值会写同一个内存
 * {@code ConfigValue}，因此改完下一帧立刻生效，不需要任何 reload。
 */
public final class TunerConfig {

    private TunerConfig() {}

    public static final class Fields {
        public final ForgeConfigSpec.DoubleValue rx, ry, rz, px, py, pz;

        Fields(ForgeConfigSpec.DoubleValue rx, ForgeConfigSpec.DoubleValue ry,
               ForgeConfigSpec.DoubleValue rz, ForgeConfigSpec.DoubleValue px,
               ForgeConfigSpec.DoubleValue py, ForgeConfigSpec.DoubleValue pz) {
            this.rx = rx; this.ry = ry; this.rz = rz;
            this.px = px; this.py = py; this.pz = pz;
        }

        public boolean isZero() {
            return rx.get() == 0.0 && ry.get() == 0.0 && rz.get() == 0.0
                    && px.get() == 0.0 && py.get() == 0.0 && pz.get() == 0.0;
        }
    }

    public static final ForgeConfigSpec SPEC;

    public static final Fields root;
    public static final Fields head;
    public static final Fields body;
    public static final Fields rightArm;
    public static final Fields leftArm;
    public static final Fields rightLeg;
    public static final Fields leftLeg;

    static {
        Pair<Fields[], ForgeConfigSpec> pair =
                new ForgeConfigSpec.Builder().configure(TunerConfig::build);
        Fields[] arr = pair.getLeft();
        SPEC     = pair.getRight();
        root     = arr[0];
        head     = arr[1];
        body     = arr[2];
        rightArm = arr[3];
        leftArm  = arr[4];
        rightLeg = arr[5];
        leftLeg  = arr[6];
    }

    private static Fields[] build(ForgeConfigSpec.Builder b) {
        b.comment(
                "PoseTuner 配置。",
                "按 P 开关强制姿态预览。",
                "旋转单位：度。root 位移单位：格。其它部位位移单位：模型像素。",
                "位移是相对部件初始位置的偏移；写 0 = 不偏移。",
                "所有默认值都是 0，模型保持站立姿态。改哪个值，就动哪个部位。",
                "推荐在 Configured 界面里改，改完立刻生效。"
        ).push("pose");

        Fields root     = part(b, "root");
        Fields head     = part(b, "head");
        Fields body     = part(b, "body");
        Fields rightArm = part(b, "rightArm");
        Fields leftArm  = part(b, "leftArm");
        Fields rightLeg = part(b, "rightLeg");
        Fields leftLeg  = part(b, "leftLeg");

        b.pop();
        return new Fields[]{root, head, body, rightArm, leftArm, rightLeg, leftLeg};
    }

    private static Fields part(ForgeConfigSpec.Builder b, String name) {
        b.push(name);
        Fields f = new Fields(
                rot(b, "X 旋转"),
                rot(b, "Y 旋转"),
                rot(b, "Z 旋转"),
                pos(b, "X 位移"),
                pos(b, "Y 位移"),
                pos(b, "Z 位移")
        );
        b.pop();
        return f;
    }

    private static ForgeConfigSpec.DoubleValue rot(ForgeConfigSpec.Builder b, String c) {
        return b.comment(c).defineInRange("rot" + c.charAt(0), 0.0D, -360.0D, 360.0D);
    }

    private static ForgeConfigSpec.DoubleValue pos(ForgeConfigSpec.Builder b, String c) {
        return b.comment(c).defineInRange("pos" + c.charAt(0), 0.0D, -64.0D, 64.0D);
    }
}