package com.lycanitesmobs.core.info;

import com.lycanitesmobs.ObjectManager;

public class CreatureKnowledge {
	public Beastiary beastiary;
	public String creatureName;
	public CreatureInfo creatureInfo;
	public double completion = 0;
	
	// ==================================================
    //                     Constructor
    // ==================================================
	public CreatureKnowledge(Beastiary beastiary, String creatureName, double completion) {
		this.beastiary = beastiary;
		this.creatureName = creatureName;
		this.creatureInfo = CreatureManager.getInstance().getCreature(creatureName);
		this.completion = completion;
	}
}
