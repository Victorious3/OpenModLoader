package xyz.openmodloader.registry;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

/**
 * Base class for all registries, keeps track of all mod's registrations. Also
 * handles lazy loading and related sorting.
 */
public abstract class Registry<T extends Entry> {

    private final Map<String, T> entries = new HashMap<>();
    private final Map<String, Set<T>> byMod = new HashMap<>();

    public void register(@Nonnull String modid, @Nonnull T entry) {
        entries.put(modid, entry);
        byMod.getOrDefault(modid, new HashSet<>()).add(entry);
    }

    public void unregister(@Nonnull T entry) {
        entries.remove(entry);
        byMod.get(entry.modid).remove(entry);
        entry.unregister();
    }

    public void unregisterAll(@Nonnull String modid) {
        Set<T> modEntries = byMod.remove(modid);
        if (modEntries == null)
            return;
        modEntries.forEach(entries::remove);
    }

    // Static part

    private static Set<RegistryBuilder> registries = new HashSet<>();
    private static List<Registry<?>> sortedRegistries = Collections.emptyList();

    public static class RegistryBuilder implements Comparable<RegistryBuilder> {
        final Registry<?> registry;
        final Set<Registry<?>> before = new HashSet<>();
        final Set<Registry<?>> after = new HashSet<>();

        RegistryBuilder(Registry<?> registry) {
            this.registry = registry;
        }

        public RegistryBuilder after(@Nonnull Registry<?> registry) {
            if (before.contains(registry))
                throw new RegistryError("Can't run before and after another registry at the same time.");
            after.add(registry);
            return this;
        }

        public RegistryBuilder before(@Nonnull Registry<?> registry) {
            if (before.contains(registry))
                throw new RegistryError("Can't run before and after another registry at the same time.");
            before.add(registry);
            return this;
        }

        @Override
        public int compareTo(RegistryBuilder o) {
            if (before.contains(o))
                return 1;
            else if (after.contains(o))
                return -1;
            return 0;
        }
    }

    protected static class RegistryError extends RuntimeException {
        private static final long serialVersionUID = 8951058577928033873L;

        public RegistryError(String desc) {
            super(desc);
        }
    }

    public static RegistryBuilder create(@Nonnull Registry<?> registry) {
        if (registries == null)
            throw new RegistryError("Can't create registries after pre-init, use an API and add it to the manifest.");

        RegistryBuilder rb = new RegistryBuilder(registry);
        registries.add(rb);
        return rb;
    }

    // Internal use only
    public static void registerAll() {
        if (sortedRegistries == null)
            throw new RegistryError("Already registered everything, stop playing around with internal methods, baka.");
        for (Registry<?> r : sortedRegistries) {
            r.entries.values().forEach(Entry::register);
        }
        sortedRegistries = null;
    }

    public static void lock() {
        if (registries == null)
            throw new RegistryError("Already locked, you don't know what you are doing. Take a break and try again.");

        sortedRegistries = Collections.unmodifiableList(registries
                .stream()
                .sorted()
                .map(e -> e.registry)
                .collect(Collectors.toList()));

        registries = null;
    }
}
