package xyz.openmodloader.registry;

import javax.annotation.Nonnull;

public abstract class Entry {
	
	public final String modid;
	
	public Entry(@Nonnull String modid) {
		this.modid = modid;
	}
	
	protected abstract void register();
	
	protected abstract void unregister();
}
