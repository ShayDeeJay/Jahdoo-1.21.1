package org.jahdoo.common.entities.decoy;

import net.minecraft.client.model.geom.ModelPart;

public class DecoyModel <T extends Decoy> extends AbstractDecoyModel<T> {
    public DecoyModel(ModelPart root) {
        super(root);
    }
}
