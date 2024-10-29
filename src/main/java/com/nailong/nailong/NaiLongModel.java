package com.nailong.nailong;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class NaiLongModel extends EntityModel<NaiLongEntity> {

    // 定义模型层的位置，用于注册和引用
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Main.MODID, "nailong_entity"), "main");
    private final ModelPart bone;
    private final ModelPart bone3;
    private final ModelPart front_left_leg;
    private final ModelPart front_left_shin;
    private final ModelPart front_left_foot;
    private final ModelPart bone2;
    private final ModelPart front_right_leg;
    private final ModelPart front_right_shin;
    private final ModelPart front_right_foot;
    private final ModelPart back_right_foot;
    private final ModelPart back_right_shin;
    private final ModelPart back_left_shin;
    private final ModelPart back_left_foot;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart bone4;

    private final ModelPart root;

    public NaiLongModel(ModelPart root) {
        super();
        this.root = root;
        this.bone = root.getChild("bone");
        this.bone3 = this.bone.getChild("bone3");
        this.front_left_leg = this.bone3.getChild("front_left_leg");
        this.front_left_shin = this.bone3.getChild("front_left_shin");
        this.front_left_foot = this.bone3.getChild("front_left_foot");
        this.bone2 = this.bone.getChild("bone2");
        this.front_right_leg = this.bone2.getChild("front_right_leg");
        this.front_right_shin = this.bone2.getChild("front_right_shin");
        this.front_right_foot = this.bone2.getChild("front_right_foot");
        this.back_right_foot = this.bone.getChild("back_right_foot");
        this.back_right_shin = this.bone.getChild("back_right_shin");
        this.back_left_shin = this.bone.getChild("back_left_shin");
        this.back_left_foot = this.bone.getChild("back_left_foot");
        this.body = this.bone.getChild("body");
        this.head = this.bone.getChild("head");
        this.bone4 = root.getChild("bone4");
    }

    @Override
    public void setupAnim(NaiLongEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // 在这里设置实体的动画逻辑
    }

    // 定义模型的结构
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition bone3 = bone.addOrReplaceChild("bone3", CubeListBuilder.create(), PartPose.offset(11.0F, 11.0F, 92.0F));

        PartDefinition front_left_leg = bone3.addOrReplaceChild("front_left_leg", CubeListBuilder.create().texOffs(86, 168).addBox(-5.0F, -62.0F, -49.0F, 8.0F, 24.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(16.0F, -27.0F, -45.0F));

        PartDefinition front_left_shin = bone3.addOrReplaceChild("front_left_shin", CubeListBuilder.create().texOffs(226, 138).addBox(-4.0F, -59.0F, -48.0F, 6.0F, 24.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(16.0F, -6.0F, -45.0F));

        PartDefinition front_left_foot = bone3.addOrReplaceChild("front_left_foot", CubeListBuilder.create().texOffs(144, 104).addBox(-4.0F, -58.0F, -57.0F, 8.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(15.0F, 17.0F, -45.0F));

        PartDefinition bone2 = bone.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offset(-4.0F, 23.0F, 47.0F));

        PartDefinition front_right_leg = bone2.addOrReplaceChild("front_right_leg", CubeListBuilder.create().texOffs(100, 2).addBox(-5.0F, -62.0F, -49.0F, 8.0F, 24.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-23.0F, -44.0F, 0.0F));

        PartDefinition front_right_shin = bone2.addOrReplaceChild("front_right_shin", CubeListBuilder.create().texOffs(214, 36).addBox(-4.0F, -59.0F, -48.0F, 6.0F, 24.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-23.0F, -23.0F, 0.0F));

        PartDefinition front_right_foot = bone2.addOrReplaceChild("front_right_foot", CubeListBuilder.create().texOffs(132, 2).addBox(-5.0F, -58.0F, -57.0F, 8.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(-23.0F, 0.0F, 0.0F));

        PartDefinition back_right_foot = bone.addOrReplaceChild("back_right_foot", CubeListBuilder.create().texOffs(112, 0).addBox(-10.0F, -58.0F, -65.0F, 18.0F, 6.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(-16.0F, 52.0F, 44.0F));

        PartDefinition back_right_shin = bone.addOrReplaceChild("back_right_shin", CubeListBuilder.create().texOffs(196, 0).addBox(-7.0F, -60.0F, -45.0F, 12.0F, 32.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-16.0F, 22.0F, 36.0F));

        PartDefinition back_left_shin = bone.addOrReplaceChild("back_left_shin", CubeListBuilder.create().texOffs(196, 0).addBox(-7.0F, -60.0F, -45.0F, 12.0F, 32.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(16.0F, 22.0F, 36.0F));

        PartDefinition back_left_foot = bone.addOrReplaceChild("back_left_foot", CubeListBuilder.create().texOffs(112, 0).addBox(-10.0F, -58.0F, -65.0F, 18.0F, 6.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(16.0F, 52.0F, 44.0F));

        PartDefinition body = bone.addOrReplaceChild("body", CubeListBuilder.create().texOffs(72, 70).addBox(-25.0F, -138.0F, -61.0F, 48.0F, 67.0F, 39.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 43.0F, 39.0F));

        PartDefinition head = bone.addOrReplaceChild("head", CubeListBuilder.create().texOffs(112, 30).addBox(-9.0F, -104.0F, -11.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));

        PartDefinition bone4 = partdefinition.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(37, 68).addBox(-7.0F, -89.8473F, -30.4763F, 15.0F, 30.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 53.0F, 71.0F, 0.3054F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.scale(0.3F, 0.3F, 0.3F);  // 将模型缩小一半
        poseStack.translate(0,3f,0);
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
