package de.uzk.hki.da.convert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.contract.PublicationRight;

public abstract class PublishConversionStrategyBase implements ConversionStrategy{
	
	/** The audiences. */
	protected String[] audiences = new String [] {"PUBLIC", "INSTITUTION" };
	
	protected Object object;
	
	@Override
	public abstract List<Event> convertFile(ConversionInstruction ci) 
			throws IOException, FileNotFoundException;
	
	
	/**
	 * @author Daniel M. de Oliveira
	 * @param audience
	 * @return
	 */
	protected PublicationRight getPublicationRightForAudience(String audience){
		for (PublicationRight right:object.getRights().getPublicationRights()){
			if (right.getAudience().toString().equals(audience)) return right;
		}
		return null;
	}
}
