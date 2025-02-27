package org.jahdoo.common.entities.decoy;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Mob;

public class AbstractDecoyModel <T extends Mob> extends HumanoidModel<T> {

    protected AbstractDecoyModel(ModelPart root) {
        super(root);
    }

}