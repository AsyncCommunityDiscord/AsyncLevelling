package fr.redstom.asynclevelling.utils;

import static fr.redstom.asynclevelling.utils.ColorPalette.*;

import fr.redstom.asynclevelling.jpa.entities.GravenMember;
import fr.redstom.asynclevelling.jpa.services.GravenMemberService;

import jakarta.annotation.Nullable;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.imageio.ImageIO;

@Service
@RequiredArgsConstructor
public class ImageGenerator {

    public static final DecimalFormat RANK_FORMATTER =
            (DecimalFormat) NumberFormat.getInstance(Locale.US);

    public static final Font OUTFIT;

    public static final Image PODIUM;
    public static final Image PODIUM_BRONZE;
    public static final Image PODIUM_SILVER;
    public static final Image PODIUM_GOLD;
    public static final Image STAR;
    public static final Image TROPHEE;

    static {
        DecimalFormatSymbols decimalFormatSymbols = RANK_FORMATTER.getDecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator(' ');
        RANK_FORMATTER.setDecimalFormatSymbols(decimalFormatSymbols);

        try {
            OUTFIT =
                    Font.createFont(
                            Font.TRUETYPE_FONT,
                            ImageGenerator.class
                                    .getClassLoader()
                                    .getResourceAsStream("fonts/outfit.ttf"));

            PODIUM =
                    ImageIO.read(
                            ImageGenerator.class
                                    .getClassLoader()
                                    .getResourceAsStream("images/podium.png"));
            PODIUM_BRONZE =
                    ImageIO.read(
                            ImageGenerator.class
                                    .getClassLoader()
                                    .getResourceAsStream("images/podium-bronze.png"));
            PODIUM_SILVER =
                    ImageIO.read(
                            ImageGenerator.class
                                    .getClassLoader()
                                    .getResourceAsStream("images/podium-silver.png"));
            PODIUM_GOLD =
                    ImageIO.read(
                            ImageGenerator.class
                                    .getClassLoader()
                                    .getResourceAsStream("images/podium-gold.png"));
            STAR =
                    ImageIO.read(
                            ImageGenerator.class
                                    .getClassLoader()
                                    .getResourceAsStream("images/star.png"));
            TROPHEE =
                    ImageIO.read(
                            ImageGenerator.class
                                    .getClassLoader()
                                    .getResourceAsStream("images/trophee.png"));
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final LevelUtils levelUtils;
    private final GravenMemberService memberService;

    private static void drawServerIcon(Graphics2D g2d, Guild guild, int x, int y, Color defaultBg)
            throws IOException {
        g2d.setClip(new Ellipse2D.Double(x, y, 100, 100));

        g2d.setColor(defaultBg);
        g2d.fillRect(x, y, 100, 100);

        if (guild.getIcon() != null) {
            InputStream serverLogo = guild.getIcon().download(512).join();
            BufferedImage serverLogoAsImage = ImageIO.read(serverLogo);

            g2d.drawImage(serverLogoAsImage, x, y, 100, 100, null);
            serverLogo.close();
        } else {
            g2d.setFont(OUTFIT.deriveFont(24f));
            FontMetrics abbreviationMetrics = g2d.getFontMetrics();
            String serverAbbreviation =
                    Arrays.stream(guild.getName().split(" ")).reduce("", (a, b) -> a + b.charAt(0));
            int serverAbbreviationWidth = abbreviationMetrics.stringWidth(serverAbbreviation);

            g2d.setColor(TEXT);
            g2d.setBackground(TEXT);
            g2d.drawString(
                    serverAbbreviation,
                    x + 50 - serverAbbreviationWidth / 2,
                    y + 50 - abbreviationMetrics.getHeight() / 2 + abbreviationMetrics.getAscent());
        }
        g2d.setClip(null);
    }

    @SneakyThrows
    public BufferedImage generateLevelImage(Member member, GravenMember gMember) {
        Color accent = member.getColor() == null ? ORANGE : member.getColor();

        InputStream avatar = member.getEffectiveAvatar().download(512).join();

        BufferedImage image = new BufferedImage(1600, 400, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(
                RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(
                RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g2d.setColor(PRIMARY_BG);
        g2d.fillRect(0, 0, 1600, 400);

        g2d.setColor(accent);
        g2d.fillRect(0, 0, 350, 350);

        BufferedImage avatarAsImage = ImageIO.read(avatar);
        g2d.drawImage(avatarAsImage, 10, 10, 330, 330, null);

        drawServerIcon(g2d, member.getGuild(), 1490, 10, SECONDARY_BG);

        g2d.setColor(SECONDARY_BG);
        g2d.setBackground(SECONDARY_BG);
        g2d.fillRect(0, 350, 1600, 50);

        double advancement =
                (double) gMember.experience()
                        / (double) levelUtils.xpForNextLevelAt(gMember.level());
        g2d.setColor(accent);
        g2d.setBackground(accent);
        g2d.fillRect(0, 350, (int) (advancement * 1600), 50);

        String text = "@" + member.getUser().getName();

        Font actualFont = OUTFIT.deriveFont(Font.BOLD, 72f);
        FontMetrics metrics = g2d.getFontMetrics(actualFont);

        int w = metrics.stringWidth(text);

        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(TEXT);
        g2d.setBackground(TEXT);
        g2d.setFont(actualFont);
        g2d.drawString(text, 400, 100);

        g2d.setColor(accent);
        g2d.drawLine(400, 125, 400 + w, 125);
        g2d.setColor(TEXT);

        Font contentFont = OUTFIT.deriveFont(Font.BOLD, 56f);
        FontMetrics contentMetrics = g2d.getFontMetrics(contentFont);
        Font titleFont = OUTFIT.deriveFont(Font.PLAIN, 36f);
        FontMetrics titleMetrics = g2d.getFontMetrics(titleFont);

        int startingWidth = 420;
        int imageSize = 96;
        int margin = 25;

        g2d.drawImage(STAR, startingWidth, 177, imageSize, imageSize, null);
        String levelContent = String.valueOf(gMember.level());
        String levelTitle = "Niveau :";
        int levelWidth =
                Math.max(
                        contentMetrics.stringWidth(levelContent),
                        titleMetrics.stringWidth(levelTitle));

        int levelTextX = startingWidth + imageSize + margin;
        g2d.setFont(contentFont);
        g2d.drawString(levelContent, levelTextX, 213 + 60);

        g2d.setFont(titleFont);
        g2d.drawString(levelTitle, levelTextX, 177 + 36);

        g2d.drawImage(
                TROPHEE,
                startingWidth + levelWidth + imageSize + margin * 3,
                177,
                imageSize,
                imageSize,
                null);
        String experienceContent =
                levelUtils.formatExperience(gMember.experience(), gMember.level());
        String experienceTitle = "Expérience :";
        int experienceWidth =
                Math.max(
                        contentMetrics.stringWidth(experienceContent),
                        titleMetrics.stringWidth(experienceTitle));

        int experienceTextX = startingWidth + levelWidth + imageSize * 2 + margin * 4;
        g2d.setFont(contentFont);
        g2d.drawString(experienceContent, experienceTextX, 213 + 60);

        g2d.setFont(titleFont);
        g2d.drawString(experienceTitle, experienceTextX, 177 + 36);

        int rank = memberService.getRank(member);
        String rankText = RANK_FORMATTER.format(rank);
        if (gMember.experience() == 0 && gMember.level() == 0) {
            rank = -1;
            rankText = "-";
        }

        g2d.drawImage(
                switch (rank) {
                    case 1 -> PODIUM_GOLD;
                    case 2 -> PODIUM_SILVER;
                    case 3 -> PODIUM_BRONZE;
                    default -> PODIUM;
                },
                startingWidth + levelWidth + experienceWidth + imageSize * 2 + margin * 6,
                177,
                imageSize,
                imageSize,
                null);

        int podiumTextX = startingWidth + levelWidth + experienceWidth + imageSize * 3 + margin * 7;
        g2d.setFont(contentFont);
        g2d.drawString(rankText, podiumTextX, 213 + 60);

        g2d.setFont(titleFont);
        g2d.drawString("Rang :", podiumTextX, 177 + 36);

        avatar.close();

        return image;
    }

    @SneakyThrows
    public BufferedImage generateLeaderboardImage(
            int page,
            @Nullable GravenMember user,
            List<GravenMember> members,
            Function<GravenMember, Member> memberMapper) {
        List<Member> dMembers = members.stream().map(memberMapper).toList();

        GravenMember originalUser = user;
        if (user != null) {
            if (members.stream().anyMatch(u -> user.user().id() == u.user().id())) {
                originalUser = null;
            }
        }

        int imageHeight =
                155
                        + /*members.size()*/ 10 * 125
                        + (/*members.size() - 1*/ 9) * 10
                        + (originalUser != null ? 175 : 0);

        BufferedImage image = new BufferedImage(1145, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(
                RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(
                RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g2d.setColor(SECONDARY_BG);
        g2d.fillRect(0, 0, 1145, imageHeight);

        dMembers.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresent(
                        first -> {
                            try {
                                drawServerIcon(
                                        g2d,
                                        first.getGuild(),
                                        10 + 25 / 2,
                                        10 + 25 / 2,
                                        PRIMARY_BG);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });

        g2d.setColor(PRIMARY_BG);
        g2d.fillRect(135, 10, 1000, 125);

        g2d.setFont(OUTFIT.deriveFont(40f));
        g2d.setColor(TEXT);
        g2d.setBackground(TEXT);

        drawCenteredText(g2d, "Rang", 207.5f, 72.5f);
        drawVCenteredText(g2d, "Nom d'utilisateur", 317.5f, 72.5f);
        drawCenteredText(g2d, "Niv.", 1073, 72.5f);

        g2d.setFont(OUTFIT.deriveFont(44f));
        for (int i = 0; i < members.size(); i++) {
            Member discordMember = dMembers.get(i);

            if (discordMember == null) {
                GravenMember gravenMember = members.get(i);

                drawMemberPosition(
                        g2d,
                        10 + (i + 1) * 135,
                        Color.BLACK,
                        null,
                        (page - 1) * 10 + i + 1,
                        gravenMember.level(),
                        "Utilisateur introuvable");
                continue;
            }

            GravenMember gravenMember = members.get(i);

            Image avatar = ImageIO.read(discordMember.getEffectiveAvatar().download().join());

            drawMemberPosition(
                    g2d,
                    10 + (i + 1) * 135,
                    discordMember.getColor(),
                    avatar,
                    (page - 1) * 10 + i + 1,
                    gravenMember.level(),
                    discordMember.getUser().getName());
        }
        for (int i = 0; i < 10 - members.size(); i++) {
            drawMemberPosition(
                    g2d,
                    members.size() * 135 + 10 + (i + 1) * 135,
                    Color.BLACK,
                    null,
                    -1,
                    -1,
                    null);
        }

        if (originalUser != null) {
            Member discordMember = memberMapper.apply(originalUser);

            Image avatar = ImageIO.read(discordMember.getEffectiveAvatar().download().join());

            drawMemberPosition(
                    g2d,
                    imageHeight - 135,
                    discordMember.getColor(),
                    avatar,
                    memberService.getRank(discordMember),
                    originalUser.level(),
                    discordMember.getUser().getName());
        }

        g2d.setColor(SECONDARY_BG);
        for (Integer x : List.of(135, 270, 1000)) {
            g2d.fillRect(x, 0, 10, imageHeight);
        }

        if (originalUser != null) {
            g2d.setColor(PRIMARY_BG);
            g2d.setBackground(PRIMARY_BG);
            g2d.fillRect(10, imageHeight - 165, 1125, 10);
        }

        return image;
    }

    private void drawMemberPosition(
            Graphics2D g2d, int y, Color color, Image avatar, int rank, long level, String name) {
        g2d.setColor(PRIMARY_BG);
        g2d.fillRect(10, y, 1125, 125);

        g2d.setColor(color == null ? ORANGE : color == Color.BLACK ? null : color);
        g2d.fillRect(10, y, 125, 125);

        g2d.drawImage(avatar, 15, y + 5, 115, 115, null);

        g2d.setColor(TEXT);
        switch (rank) {
            case 1, 2, 3 ->
                    g2d.drawImage(
                            switch (rank) {
                                case 1 -> PODIUM_GOLD;
                                case 2 -> PODIUM_SILVER;
                                case 3 -> PODIUM_BRONZE;
                                default ->
                                        throw new IllegalStateException(
                                                "Unexpected value: " + rank);
                            },
                            170,
                            y + 25,
                            75,
                            75,
                            null);
            case -1 -> drawCenteredText(g2d, "-", 207.5f, y + 62.5f);
            default ->
                    withComputedFontSize(
                            g2d,
                            NumberUtils.formatNumber(rank),
                            100,
                            (text) -> {
                                drawCenteredText(g2d, text, 207.5f, y + 62.5f);
                            });
        }

        drawVCenteredText(g2d, name != null ? "@" + name : "-", 317.5f, y + 62.5f);

        if (level < 0) {
            drawCenteredText(g2d, "-", 1073, y + 62.5f);
        } else {
            withComputedFontSize(
                    g2d,
                    NumberUtils.formatNumber(level),
                    100,
                    (text) -> {
                        drawCenteredText(g2d, text, 1073, y + 62.5f);
                    });
        }
    }

    private void drawCenteredText(Graphics2D g2d, String text, float x, float y) {
        FontMetrics metrics = g2d.getFontMetrics();
        g2d.drawString(
                text,
                x - metrics.stringWidth(text) / 2f,
                y - metrics.getHeight() / 2f + metrics.getAscent());
    }

    private void drawVCenteredText(Graphics2D g2d, String text, float x, float y) {
        FontMetrics metrics = g2d.getFontMetrics();
        g2d.drawString(text, x, y - metrics.getHeight() / 2f + metrics.getAscent());
    }

    private void withComputedFontSize(
            Graphics2D g2d, String text, int targetMaxWidth, Consumer<String> action) {
        Font original = g2d.getFont();
        Font font = original;

        FontMetrics metrics = g2d.getFontMetrics(font);

        while (metrics.stringWidth(text) > targetMaxWidth) {
            font = font.deriveFont(font.getSize() - 1f);
            metrics = g2d.getFontMetrics(font);

            if (font.getSize() == 1) break;
        }

        g2d.setFont(font);
        action.accept(text);
        g2d.setFont(original);
    }
}
