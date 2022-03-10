package me.bluetree242.libertybansexpansion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import space.arim.libertybans.api.LibertyBans;
import space.arim.libertybans.api.PlayerVictim;
import space.arim.libertybans.api.PunishmentType;
import space.arim.libertybans.api.punish.Punishment;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
public class PlayerCache {
    private final LibertyBans libertyBans;
    private final UUID uuid;

    @Getter
    private boolean muted = false;
    @Getter
    private boolean banned = false;

    protected void load() throws ExecutionException, InterruptedException {
        List<Punishment> punishments = libertyBans.getSelector()
                .selectionBuilder()
                .selectActiveOnly(true)
                .victim(PlayerVictim.of(uuid)).build().getAllSpecificPunishments().toCompletableFuture().get();
        if (punishments.isEmpty()) {
            muted = false;
            banned = false;
        } else {
            for (Punishment punishment : punishments) {
                if (punishment.getType() == PunishmentType.BAN) banned = true;
                if (punishment.getType() == PunishmentType.MUTE) muted = true;
            }
        }
    }
}
