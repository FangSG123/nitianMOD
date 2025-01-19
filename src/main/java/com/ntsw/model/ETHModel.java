package com.ntsw.model;// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ntsw.entity.ETHEntity;
import com.ntsw.entity.NaiLongEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class ETHModel extends EntityModel<ETHEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "1"), "main");
	private final ModelPart body3;
	private final ModelPart body2;
	private final ModelPart body1;
	private final ModelPart head1;
	private final ModelPart bb_main;

	public ETHModel(ModelPart root) {
		this.body3 = root.getChild("body3");
		this.body2 = root.getChild("body2");
		this.body1 = root.getChild("body1");
		this.head1 = root.getChild("head1");
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body3 = partdefinition.addOrReplaceChild("body3", CubeListBuilder.create(), PartPose.offset(-2.0F, 16.9F, -0.5F));

		PartDefinition body2 = partdefinition.addOrReplaceChild("body2", CubeListBuilder.create(), PartPose.offset(-2.0F, 6.9F, -0.5F));

		PartDefinition body2_r1 = body2.addOrReplaceChild("body2_r1", CubeListBuilder.create().texOffs(24, 22).addBox(-11.0F, -2.0F, 0.0F, 11.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 4.1F, 1.5F, 0.0F, -2.3998F, 0.0F));

		PartDefinition body2_r2 = body2.addOrReplaceChild("body2_r2", CubeListBuilder.create().texOffs(24, 22).addBox(-11.0F, -2.0F, 0.0F, 11.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.1F, 1.5F, 0.0F, -0.829F, 0.0F));

		PartDefinition body2_r3 = body2.addOrReplaceChild("body2_r3", CubeListBuilder.create().texOffs(0, 13).addBox(-2.0F, -25.1F, -0.5F, 3.0F, 18.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 24.1F, 8.5F, 0.3054F, 0.0F, 0.0F));

		PartDefinition body1 = partdefinition.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(7, 16).addBox(-7.0F, 1.9F, -0.5F, 14.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head1 = partdefinition.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -6.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition cube_r1 = bb_main.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 46).addBox(-11.0F, -2.0F, -1.0F, 17.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -28.0F, 4.0F, -0.9417F, 0.0628F, 0.019F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		body2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		body1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		head1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		poseStack.scale(2F, 2F, 2F);  // 将模型缩小一半
	}

	@Override
	public void setupAnim(ETHEntity p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {

	}
}