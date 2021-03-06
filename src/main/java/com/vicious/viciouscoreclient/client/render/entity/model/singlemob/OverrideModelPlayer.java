package com.vicious.viciouscoreclient.client.render.entity.model.singlemob;

import com.vicious.viciouscoreclient.client.configuration.EntityModelOverrideCFG;
import com.vicious.viciouscoreclient.client.render.entity.model.IOverrideModel;
import com.vicious.viciouscore.common.util.reflect.Reflection;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHandSide;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Overridden version of ModelPlayer which changes how the game modifies the models on client rendering.
 * This models type gets injected into the mainModel field when RenderOverrideHandler#overrideModelSpider is called.
 */
public class OverrideModelPlayer extends ModelPlayer implements IOverrideModel {
    public Queue<Runnable> transforms = new LinkedList<>();
    public List<EnumHandSide> ignoreHandSides = new ArrayList<>();
    private Map<String, Field> partMap = new HashMap<>();

    private boolean doRemove = false;
    public OverrideModelPlayer(ModelPlayer og) {
        super(1F, (boolean) Reflection.accessField(og,"smallArms"));
        clone(og,partMap);
    }

    public void overrideRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        //Runs rotations after MC has done its own bullshit.
        while(!transforms.isEmpty()){
            transforms.remove().run();
        }
    }

    /**
     * Handles arm rendering. If you don't want the item to move with the arm, you can add the hand side to ignoreHandSides.
     */
    @Override
    public void postRenderArm(float scale, EnumHandSide side) {
        if(ignoreHandSides.contains(side)){
            if(doRemove) ignoreHandSides.remove(side);
            doRemove = !doRemove;
            return;
        }
        super.postRenderArm(scale, side);
    }
    public void applicate(EntityModelOverrideCFG<?> configurations) {
        applicate(partMap, configurations);
    }

    @Override
    public void queueTransformer(Runnable in) {
        transforms.offer(in);
    }

    @Override
    public void ignoreHandSide(EnumHandSide in) {
        ignoreHandSides.add(in);
    }


    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.overrideRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        GlStateManager.pushMatrix();

        if (this.isChild)
        {
            float f = 2.0F;
            GlStateManager.scale(0.75F, 0.75F, 0.75F);
            GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
            this.bipedHead.render(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
            this.bipedBody.render(scale);
            this.bipedRightArm.render(scale);
            this.bipedLeftArm.render(scale);
            this.bipedRightLeg.render(scale);
            this.bipedLeftLeg.render(scale);
            this.bipedHeadwear.render(scale);
        }
        else
        {
            if (entityIn.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            this.bipedHead.render(scale);
            this.bipedBody.render(scale);
            this.bipedRightArm.render(scale);
            this.bipedLeftArm.render(scale);
            this.bipedRightLeg.render(scale);
            this.bipedLeftLeg.render(scale);
            this.bipedHeadwear.render(scale);
        }
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        if (this.isChild)
        {
            float f = 2.0F;
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
            this.bipedLeftLegwear.render(scale);
            this.bipedRightLegwear.render(scale);
            this.bipedLeftArmwear.render(scale);
            this.bipedRightArmwear.render(scale);
            this.bipedBodyWear.render(scale);
        }
        else
        {
            if (entityIn.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            this.bipedLeftLegwear.render(scale);
            this.bipedRightLegwear.render(scale);
            this.bipedLeftArmwear.render(scale);
            this.bipedRightArmwear.render(scale);
            this.bipedBodyWear.render(scale);
        }

        GlStateManager.popMatrix();
        resetTransformations(boxList);

    }
}
