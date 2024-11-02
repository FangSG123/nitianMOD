    package com.ntsw.model;

    import com.mojang.blaze3d.vertex.PoseStack;
    import com.mojang.blaze3d.vertex.VertexConsumer;
    import com.ntsw.Main;
    import com.ntsw.entity.NaiLongEntity;
    import net.minecraft.client.model.EntityModel;
    import net.minecraft.client.model.geom.ModelLayerLocation;
    import net.minecraft.client.model.geom.ModelPart;
    import net.minecraft.client.model.geom.builders.*;
    import net.minecraft.client.model.geom.PartPose;
    import net.minecraft.resources.ResourceLocation;

    public class NaiLongModel extends EntityModel<NaiLongEntity> {

        // 定义模型层的位置，用于注册和引用
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Main.MODID, "nailong_entity"), "main");
        private final ModelPart leg;
        private final ModelPart head;
        private final ModelPart body;
        private final ModelPart weiba;
        private final ModelPart foot;
        private final ModelPart back_right_foot;
        private final ModelPart back_right_shin;
        private final ModelPart back_left_shin;
        private final ModelPart back_left_foot;

        public NaiLongModel(ModelPart root) {
            super();
            this.leg = root.getChild("leg");
            this.head = root.getChild("head");
            this.body = root.getChild("body");
            this.weiba = root.getChild("weiba");
            this.foot = root.getChild("foot");
            this.back_right_foot = this.foot.getChild("back_right_foot");
            this.back_right_shin = this.foot.getChild("back_right_shin");
            this.back_left_shin = this.foot.getChild("back_left_shin");
            this.back_left_foot = this.foot.getChild("back_left_foot");
        }

        @Override
        public void setupAnim(NaiLongEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            // 在这里设置实体的动画逻辑
        }

        // 定义模型的结构
        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();
            PartDefinition leg = partdefinition.addOrReplaceChild("leg", CubeListBuilder.create().texOffs(30, 122).addBox(-10.0F, -70.0F, -46.0F, 14.0F, 31.0F, 24.0F, new CubeDeformation(0.0F))
                    .texOffs(13, 225).addBox(-1.0F, -20.0F, -48.0F, 3.0F, 8.0F, 5.0F, new CubeDeformation(0.0F))
                    .texOffs(13, 225).addBox(-7.0F, -20.0F, -48.0F, 3.0F, 8.0F, 5.0F, new CubeDeformation(0.0F))
                    .texOffs(13, 225).addBox(59.0F, -20.0F, -48.0F, 3.0F, 8.0F, 5.0F, new CubeDeformation(0.0F))
                    .texOffs(13, 225).addBox(53.0F, -20.0F, -48.0F, 3.0F, 8.0F, 5.0F, new CubeDeformation(0.0F))
                    .texOffs(147, 159).addBox(51.0F, -46.0F, -45.0F, 12.0F, 31.0F, 19.0F, new CubeDeformation(0.0F))
                    .texOffs(30, 122).addBox(50.0F, -70.0F, -46.0F, 14.0F, 31.0F, 24.0F, new CubeDeformation(0.0F))
                    .texOffs(147, 159).addBox(-9.0F, -46.0F, -45.0F, 12.0F, 31.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offset(-27.0F, 3.0F, 34.0F));

            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 18.0F, -24.0F));

            PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(100, 21).addBox(-16.0F, -22.0F, -34.0F, 24.0F, 24.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -91.0F, 32.0F, 0.2618F, 0.0F, 0.0F));

            PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(31, 70).addBox(-24.0F, -19.6135F, -76.7759F, 48.0F, 46.0F, 64.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 8.0F, -1.5272F, 0.0F, 0.0F));

            PartDefinition weiba = partdefinition.addOrReplaceChild("weiba", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

            PartDefinition weiba_r1 = weiba.addOrReplaceChild("weiba_r1", CubeListBuilder.create().texOffs(37, 68).addBox(-14.0F, -30.0F, -1.0F, 15.0F, 30.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, -21.0F, 25.0F, 0.2618F, 0.0F, 0.0F));

            PartDefinition foot = partdefinition.addOrReplaceChild("foot", CubeListBuilder.create().texOffs(10, 222).addBox(-23.0F, -5.0F, -22.0F, 3.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(10, 222).addBox(-11.0F, -5.0F, -22.0F, 3.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(10, 222).addBox(9.0F, -5.0F, -22.0F, 3.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(10, 222).addBox(15.0F, -5.0F, -22.0F, 3.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(10, 222).addBox(21.0F, -5.0F, -22.0F, 3.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(10, 222).addBox(-17.0F, -5.0F, -22.0F, 3.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

            PartDefinition back_right_foot = foot.addOrReplaceChild("back_right_foot", CubeListBuilder.create().texOffs(112, 0).addBox(-9.0F, -59.0F, -62.0F, 18.0F, 6.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(-16.0F, 52.0F, 44.0F));

            PartDefinition back_right_shin = foot.addOrReplaceChild("back_right_shin", CubeListBuilder.create().texOffs(196, 0).addBox(-6.0F, -61.0F, -42.0F, 12.0F, 32.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-16.0F, 22.0F, 36.0F));

            PartDefinition back_left_shin = foot.addOrReplaceChild("back_left_shin", CubeListBuilder.create().texOffs(196, 0).addBox(-6.0F, -61.0F, -42.0F, 12.0F, 32.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(16.0F, 22.0F, 36.0F));

            PartDefinition back_left_foot = foot.addOrReplaceChild("back_left_foot", CubeListBuilder.create().texOffs(112, 0).addBox(-9.0F, -59.0F, -62.0F, 18.0F, 6.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(16.0F, 52.0F, 44.0F));

            return LayerDefinition.create(meshdefinition, 256, 256);
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            weiba.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            foot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.scale(0.5F, 0.5F, 0.5F);  // 将模型缩小一半
        }
    }
