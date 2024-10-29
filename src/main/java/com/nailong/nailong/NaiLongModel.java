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
        private final ModelPart bone2;
        private final ModelPart front_right_leg;
        private final ModelPart front_right_shin;
        private final ModelPart front_right_foot;
        private final ModelPart bone3;
        private final ModelPart front_right_leg2;
        private final ModelPart front_right_shin2;
        private final ModelPart front_right_foot2;
        private final ModelPart back_right_foot;
        private final ModelPart back_right_shin;
        private final ModelPart back_left_shin;
        private final ModelPart back_left_foot;
        private final ModelPart body;
        private final ModelPart head;
        private final ModelPart mirrored;
        private final ModelPart bone4;

        public NaiLongModel(ModelPart root) {
            super();
            this.bone = root.getChild("bone");
            this.bone2 = this.bone.getChild("bone2");
            this.front_right_leg = this.bone2.getChild("front_right_leg");
            this.front_right_shin = this.bone2.getChild("front_right_shin");
            this.front_right_foot = this.bone2.getChild("front_right_foot");
            this.bone3 = this.bone2.getChild("bone3");
            this.front_right_leg2 = this.bone3.getChild("front_right_leg2");
            this.front_right_shin2 = this.bone3.getChild("front_right_shin2");
            this.front_right_foot2 = this.bone3.getChild("front_right_foot2");
            this.back_right_foot = this.bone.getChild("back_right_foot");
            this.back_right_shin = this.bone.getChild("back_right_shin");
            this.back_left_shin = this.bone.getChild("back_left_shin");
            this.back_left_foot = this.bone.getChild("back_left_foot");
            this.body = this.bone.getChild("body");
            this.head = this.bone.getChild("head");
            this.mirrored = this.head.getChild("mirrored");
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

            PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(13, 225).addBox(-28.0F, 18.0F, 28.0F, 3.0F, 8.0F, 5.0F, new CubeDeformation(0.0F))
                    .texOffs(10, 222).addBox(-23.0F, 54.0F, 20.0F, 3.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(10, 222).addBox(-11.0F, 54.0F, 20.0F, 3.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(10, 222).addBox(9.0F, 54.0F, 20.0F, 3.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(10, 222).addBox(15.0F, 54.0F, 20.0F, 3.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(10, 222).addBox(21.0F, 54.0F, 20.0F, 3.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

            PartDefinition bone2 = bone.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(10, 222).addBox(-13.0F, 31.0F, -14.0F, 3.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(13, 225).addBox(-30.0F, -5.0F, -6.0F, 3.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 23.0F, 34.0F));

            PartDefinition front_right_leg = bone2.addOrReplaceChild("front_right_leg", CubeListBuilder.create().texOffs(30, 122).addBox(-10.0F, -11.0F, -4.0F, 14.0F, 31.0F, 24.0F, new CubeDeformation(0.0F))
                    .texOffs(13, 225).addBox(59.0F, 39.0F, -6.0F, 3.0F, 8.0F, 5.0F, new CubeDeformation(0.0F))
                    .texOffs(13, 225).addBox(53.0F, 39.0F, -6.0F, 3.0F, 8.0F, 5.0F, new CubeDeformation(0.0F))
                    .texOffs(147, 159).addBox(51.0F, 13.0F, -3.0F, 12.0F, 31.0F, 19.0F, new CubeDeformation(0.0F))
                    .texOffs(30, 122).addBox(50.0F, -11.0F, -4.0F, 14.0F, 31.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(-23.0F, -44.0F, 0.0F));

            PartDefinition front_right_shin = bone2.addOrReplaceChild("front_right_shin", CubeListBuilder.create().texOffs(147, 159).addBox(-9.0F, -8.0F, -3.0F, 12.0F, 31.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offset(-23.0F, -23.0F, 0.0F));

            PartDefinition front_right_foot = bone2.addOrReplaceChild("front_right_foot", CubeListBuilder.create(), PartPose.offset(-23.0F, 0.0F, 0.0F));

            PartDefinition bone3 = bone2.addOrReplaceChild("bone3", CubeListBuilder.create(), PartPose.offset(61.0F, 0.0F, 0.0F));

            PartDefinition front_right_leg2 = bone3.addOrReplaceChild("front_right_leg2", CubeListBuilder.create(), PartPose.offset(-23.0F, -44.0F, 0.0F));

            PartDefinition front_right_shin2 = bone3.addOrReplaceChild("front_right_shin2", CubeListBuilder.create(), PartPose.offset(-23.0F, -23.0F, 0.0F));

            PartDefinition front_right_foot2 = bone3.addOrReplaceChild("front_right_foot2", CubeListBuilder.create(), PartPose.offset(-23.0F, 0.0F, 0.0F));

            PartDefinition back_right_foot = bone.addOrReplaceChild("back_right_foot", CubeListBuilder.create().texOffs(112, 0).addBox(-9.0F, 0.0F, -20.0F, 18.0F, 6.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(-16.0F, 52.0F, 44.0F));

            PartDefinition back_right_shin = bone.addOrReplaceChild("back_right_shin", CubeListBuilder.create().texOffs(196, 0).addBox(-6.0F, -2.0F, 0.0F, 12.0F, 32.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-16.0F, 22.0F, 36.0F));

            PartDefinition back_left_shin = bone.addOrReplaceChild("back_left_shin", CubeListBuilder.create().texOffs(196, 0).addBox(-6.0F, -2.0F, 0.0F, 12.0F, 32.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(16.0F, 22.0F, 36.0F));

            PartDefinition back_left_foot = bone.addOrReplaceChild("back_left_foot", CubeListBuilder.create().texOffs(112, 0).addBox(-9.0F, 0.0F, -20.0F, 18.0F, 6.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(16.0F, 52.0F, 44.0F));

            PartDefinition body = bone.addOrReplaceChild("body", CubeListBuilder.create().texOffs(27, 88).addBox(-24.0F, -59.0F, -16.0F, 48.0F, 46.0F, 64.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -20.0F, 8.0F, -1.5272F, 0.0F, 0.0F));

            PartDefinition head = bone.addOrReplaceChild("head", CubeListBuilder.create().texOffs(100, 21).addBox(-14.0F, -54.0F, 51.0F, 24.0F, 24.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, -24.0F));

            PartDefinition mirrored = head.addOrReplaceChild("mirrored", CubeListBuilder.create(), PartPose.offset(0.0F, 6.0F, 24.0F));

            PartDefinition bone4 = partdefinition.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(37, 68).addBox(-6.0F, -21.0F, -5.0F, 15.0F, 30.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 53.0F, 71.0F, 0.3054F, 0.0F, 0.0F));

            return LayerDefinition.create(meshdefinition, 256, 256);
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            bone4.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.scale(0.5F, 0.5F, 0.5F);  // 将模型缩小一半
        }
    }
