package me.bluetree242.libertybansexpansion;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.arim.libertybans.api.LibertyBans;
import space.arim.omnibus.Omnibus;
import space.arim.omnibus.OmnibusProvider;

import java.time.Duration;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class LibertybansExpansion extends PlaceholderExpansion {
    private LibertyBans libertyBans;
    public @NonNull LoadingCache<UUID, PlayerCache> cache = Caffeine.newBuilder()
            .maximumSize(120)
            .expireAfterWrite(Duration.ofMinutes(1))
            .refreshAfterWrite(Duration.ofSeconds(30))
            .build(key -> {
                PlayerCache cache = new PlayerCache(libertyBans, key);
                cache.load();
                return cache;
            });

    @Override
    public @NotNull String getIdentifier() {
        return "libertybans";
    }

    @Override
    public @NotNull String getAuthor() {
        return "BlueTree242";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    public String getRequiredPlugin() {
        return "LibertyBans";
    }

    public boolean register() {
        if (!canRegister()) return false;
        Omnibus omnibus = OmnibusProvider.getOmnibus();
        LibertyBans libertyBans = omnibus.getRegistry().getProvider(LibertyBans.class).orElseThrow();
        return super.register();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (player == null) return null;
        return handleRequest(player.getUniqueId(), identifier);
    }

    @Nullable
    public String onPlaceholderRequest(final Player player, @NotNull final String identifier) {
        if (player == null) return null;
        return handleRequest(player.getUniqueId(), identifier);
    }

    public String handleRequest(UUID uuid, @NotNull String identifier) {
        PlayerCache cache = this.cache.get(uuid);
        if (cache == null) return null;
        switch (identifier.toLowerCase(Locale.ROOT)) {
            case "is_banned":
                return bool(cache.isBanned());
            case "is_muted":
                return bool(cache.isMuted());
        }
        return null;
    }

    private String bool(boolean b) {
        return b ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
    }


    private static class Placeholder {
        public final UUID uuid;
        public final String[] params;

        public Placeholder(UUID uuid, String[] params) {
            this.uuid = uuid;
            this.params = params;
        }

        @Override
        public int hashCode() {
            return Objects.hash(uuid, Arrays.hashCode(params));
        }
    }
}