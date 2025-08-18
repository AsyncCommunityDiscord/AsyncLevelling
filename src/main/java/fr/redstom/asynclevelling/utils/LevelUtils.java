package fr.redstom.asynclevelling.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LevelUtils {

    @Value("${xp.min_per_message}")
    private int minPerMessage = 2;

    @Value("${xp.max_per_message}")
    private int maxPerMessage = 5;

    @Value("${xp.min_message_length}")
    private int minMessageLength = 10;

    @Value("${xp.max_message_length}")
    private int maxMessageLength = 100;

    public long xpForNextLevelAt(long level) {
        double exp = 5 * Math.pow(level, 2) + (50 * level) + 100;
        double exp2 = (exp * 20) / level;

        return Math.min(Math.round(exp), Math.round(exp2));
    }

    public long flattenMessageLengthIntoGain(double messageLength) {
        if (messageLength < minMessageLength) {
            return minPerMessage;
        }
        if (messageLength > maxMessageLength) {
            return maxPerMessage;
        }

        return Math.round(
                minPerMessage
                        + ((messageLength - minMessageLength)
                                * (maxPerMessage - minPerMessage)
                                / (maxMessageLength - minMessageLength)));
    }

    public String formatExperience(long xp, long level) {
        long totalXp = xpForNextLevelAt(level);

        return NumberUtils.formatNumber(xp) + "/" + NumberUtils.formatNumber(totalXp);
    }
}
