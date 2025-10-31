package your.package.name;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.user.SkillsUser;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ManaChangeExpansion extends PlaceholderExpansion {

    private final Map<UUID, Double> lastMana = new HashMap<>();

    @Override
    public @NotNull String getIdentifier() {
        return "manachange";
    }

    @Override
    public @NotNull String getAuthor() {
        return "gr1zz";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(@Nullable Player player, @NotNull String params) {
        if (!params.equalsIgnoreCase("delta")) {
            return "";
        }

        if (player == null) {
            return "";
        }

        AuraSkillsApi api = AuraSkillsApi.get();
        if (api == null) {
            return "";
        }

        SkillsUser user = api.getUser(player.getUniqueId());
        if (user == null) {
            return "";
        }

        double currentMana = user.getMana();
        UUID uuid = player.getUniqueId();
        double previousMana = lastMana.getOrDefault(uuid, currentMana);
        double delta = currentMana - previousMana;

        lastMana.put(uuid, currentMana);

        if (Math.abs(delta) < 0.01d) {
            return "";
        }

        if (isWhole(delta)) {
            return String.format("%+.0f", delta);
        }

        return String.format("%+.1f", roundToOneDecimal(delta));
    }

    private boolean isWhole(double value) {
        return Math.abs(value - Math.rint(value)) < 1e-9;
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0d) / 10.0d;
    }
}
