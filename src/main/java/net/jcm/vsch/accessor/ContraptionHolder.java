package net.jcm.vsch.accessor;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;

import java.util.List;

public interface ContraptionHolder {
	List<AbstractContraptionEntity> clearContraptions();
	void restoreContraptions(List<AbstractContraptionEntity> contraptions);
}
