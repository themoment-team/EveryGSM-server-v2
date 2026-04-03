package team.themoment.everygsm.server.v2.thirdparty.feign.discord.dto;

import java.util.List;

public record DiscordWebhookPayload(List<Embed> embeds) {

    public record Embed(String title, String description, int color) {
    }

    public static DiscordWebhookPayload serverError(String title, String description) {
        return new DiscordWebhookPayload(List.of(new Embed(title, description, 0xFF4C4C)));
    }
}
