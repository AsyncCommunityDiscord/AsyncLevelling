package fr.redstom.asynclevelling.utils.jda;

import fr.redstom.asynclevelling.utils.GravenColors;

import net.dv8tion.jda.api.EmbedBuilder;

public class EmbedUtils {

    public static EmbedBuilder error(String description) {
        return new EmbedBuilder()
                .setTitle("❌ Erreur !")
                .setDescription(description)
                .setColor(GravenColors.RED);
    }
}
