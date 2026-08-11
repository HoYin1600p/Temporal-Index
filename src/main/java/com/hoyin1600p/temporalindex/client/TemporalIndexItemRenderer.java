package com.hoyin1600p.temporalindex.client;

import com.hoyin1600p.temporalindex.TemporalIndex;
import com.hoyin1600p.temporalindex.storage.TemporalIndexStorage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class TemporalIndexItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static TemporalIndexItemRenderer instance;
    private static final ResourceLocation CLOSED_BOOK_TEXTURE = new ResourceLocation(
            TemporalIndex.MOD_ID,
            "textures/item/temporal_index_closed.png"
    );
    private static final float COVER_SPRITE_SCALE = 0.30F;
    private static final float COVER_SPRITE_X = 0.54F;
    private static final float COVER_SPRITE_Y = 0.675F;
    private static final float COVER_SPRITE_ROTATION = 19.0F;
    private static final float BOOK_FACE_Z = 0.03125F;
    private static final float COVER_SPRITE_Z = 0.075F;

    public TemporalIndexItemRenderer() {
        super(
                Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels()
        );
    }

    public static TemporalIndexItemRenderer getInstance() {
        if (instance == null) {
            instance = new TemporalIndexItemRenderer();
        }
        return instance;
    }

    public void renderInFrame(
            ItemStack book,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight
    ) {
        poseStack.pushPose();
        // RenderItemInFrameEvent supplies a pose centered on and oriented with
        // the front of the frame. Keep this path entirely planar so the cover
        // and its dynamic emblem cannot separate under FIXED transforms.
        poseStack.scale(0.5F, 0.5F, 0.5F);

        VertexConsumer bookConsumer = buffers.getBuffer(RenderType.entityCutoutNoCull(CLOSED_BOOK_TEXTURE));
        PoseStack.Pose bookPose = poseStack.last();
        renderQuad(
                bookConsumer,
                bookPose,
                -0.5F,
                -0.5F,
                0.5F,
                0.5F,
                0.01F,
                0.0F,
                0.0F,
                1.0F,
                1.0F,
                packedLight,
                OverlayTexture.NO_OVERLAY
        );

        ItemStack selected = TemporalIndexStorage.getSelectedDisplayStack(book);
        if (!selected.isEmpty()) {
            BakedModel selectedModel = Minecraft.getInstance().getItemRenderer().getModel(selected, null, null, 0);
            TextureAtlasSprite sprite = selectedModel.getParticleIcon();

            poseStack.pushPose();
            // The item-frame pose faces local -Z toward the viewer.
            poseStack.translate(COVER_SPRITE_X - 0.5F, COVER_SPRITE_Y - 0.5F, -0.03D);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(COVER_SPRITE_ROTATION));
            float halfSize = COVER_SPRITE_SCALE * 0.5F;
            VertexConsumer spriteConsumer = buffers.getBuffer(
                    RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS)
            );
            renderQuad(
                    spriteConsumer,
                    poseStack.last(),
                    -halfSize,
                    -halfSize,
                    halfSize,
                    halfSize,
                    0.0F,
                    sprite.getU0(),
                    sprite.getV0(),
                    sprite.getU1(),
                    sprite.getV1(),
                    packedLight,
                    OverlayTexture.NO_OVERLAY
            );
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    @Override
    public void renderByItem(
            ItemStack book,
            ItemTransforms.TransformType transformType,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        renderClosedBook(poseStack, buffers, packedLight, packedOverlay);

        ItemStack selected = TemporalIndexStorage.getSelectedDisplayStack(book);
        if (selected.isEmpty()) {
            return;
        }

        BakedModel selectedModel = Minecraft.getInstance().getItemRenderer().getModel(selected, null, null, 0);
        TextureAtlasSprite sprite = selectedModel.getParticleIcon();
        renderCoverSprite(
                sprite,
                poseStack,
                buffers,
                packedLight,
                packedOverlay,
                COVER_SPRITE_Z
        );
        renderCoverSprite(
                sprite,
                poseStack,
                buffers,
                packedLight,
                packedOverlay,
                -COVER_SPRITE_Z
        );
    }

    private static void renderCoverSprite(
            TextureAtlasSprite sprite,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay,
            float z
    ) {
        poseStack.pushPose();
        poseStack.translate(COVER_SPRITE_X, COVER_SPRITE_Y, z);
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(COVER_SPRITE_ROTATION));
        float halfSize = COVER_SPRITE_SCALE * 0.5F;
        VertexConsumer consumer = buffers.getBuffer(
                RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS)
        );
        renderQuad(
                consumer,
                poseStack.last(),
                -halfSize,
                -halfSize,
                halfSize,
                halfSize,
                0.0F,
                sprite.getU0(),
                sprite.getV0(),
                sprite.getU1(),
                sprite.getV1(),
                packedLight,
                packedOverlay
        );
        poseStack.popPose();
    }

    private static void renderClosedBook(
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(CLOSED_BOOK_TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        renderClosedBookFace(consumer, pose, BOOK_FACE_Z, 1.0F, packedLight, packedOverlay);
    }

    private static void renderClosedBookFace(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float z,
            float normalZ,
            int packedLight,
            int packedOverlay
    ) {
        renderQuad(
                consumer,
                pose,
                0.0F,
                0.0F,
                1.0F,
                1.0F,
                z,
                0.0F,
                0.0F,
                1.0F,
                1.0F,
                packedLight,
                packedOverlay,
                normalZ
        );
    }

    private static void renderQuad(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float minX,
            float minY,
            float maxX,
            float maxY,
            float z,
            float minU,
            float minV,
            float maxU,
            float maxV,
            int packedLight,
            int packedOverlay
    ) {
        renderQuad(
                consumer,
                pose,
                minX,
                minY,
                maxX,
                maxY,
                z,
                minU,
                minV,
                maxU,
                maxV,
                packedLight,
                packedOverlay,
                1.0F
        );
    }

    private static void renderQuad(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float minX,
            float minY,
            float maxX,
            float maxY,
            float z,
            float minU,
            float minV,
            float maxU,
            float maxV,
            int packedLight,
            int packedOverlay,
            float normalZ
    ) {
        vertex(consumer, pose, minX, maxY, z, minU, minV, normalZ, packedLight, packedOverlay);
        vertex(consumer, pose, maxX, maxY, z, maxU, minV, normalZ, packedLight, packedOverlay);
        vertex(consumer, pose, maxX, minY, z, maxU, maxV, normalZ, packedLight, packedOverlay);
        vertex(consumer, pose, minX, minY, z, minU, maxV, normalZ, packedLight, packedOverlay);
    }

    private static void vertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x,
            float y,
            float z,
            float u,
            float v,
            float normalZ,
            int packedLight,
            int packedOverlay
    ) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(packedOverlay)
                .uv2(packedLight)
                .normal(pose.normal(), 0.0F, 0.0F, normalZ)
                .endVertex();
    }
}
