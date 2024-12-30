package org.jahdoo.client.entity_renderer.decoy;

import net.minecraft.client.model.geom.ModelPart;
import org.jahdoo.entities.living.Decoy;

public class DecoyModel <T extends Decoy> extends AbstractDecoyModel<T> {
    public DecoyModel(ModelPart pRoot) {
        super(pRoot);
    }
}
