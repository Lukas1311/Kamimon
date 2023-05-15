package de.uniks.stpmon.k;

import dagger.Binds;
import dagger.Module;
import de.uniks.stpmon.k.service.FriendCache;
import de.uniks.stpmon.k.service.IFriendCache;

import javax.inject.Singleton;

@Module
public abstract class BoundModule {
	@Binds
	@Singleton
	public abstract IFriendCache friendCache(FriendCache cache);
}
