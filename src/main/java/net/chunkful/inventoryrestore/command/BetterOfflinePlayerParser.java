package net.chunkful.inventoryrestore.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.bukkit.BukkitCaptionKeys;
import org.incendo.cloud.bukkit.BukkitCommandContextKeys;
import org.incendo.cloud.bukkit.parser.OfflinePlayerParser;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class BetterOfflinePlayerParser<C> implements ArgumentParser<C, OfflinePlayer>, BlockingSuggestionProvider.Strings<C> {

    public static <C> @NonNull ParserDescriptor<C, OfflinePlayer> offlinePlayerParser() {
        return ParserDescriptor.of(new BetterOfflinePlayerParser<>(), OfflinePlayer.class);
    }

    public static <C> CommandComponent.@NonNull Builder<C, OfflinePlayer> offlinePlayerComponent() {
        return CommandComponent.<C, OfflinePlayer>builder().parser(offlinePlayerParser());
    }

    @Override
    public @NonNull ArgumentParseResult<OfflinePlayer> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        final String input = commandInput.readString();

        OfflinePlayer player;
        try {
            UUID uuid = UUID.fromString(input);
            try {
                player = Bukkit.getOfflinePlayer(uuid);
            } catch (final Exception e) {
                return ArgumentParseResult.failure(new BetterOfflinePlayerParseException(input, commandContext));
            }
        } catch (final IllegalArgumentException e) {
            if (input.length() > 16) {
                return ArgumentParseResult.failure(new BetterOfflinePlayerParseException(input, commandContext));
            }
            try {
                player = Bukkit.getOfflinePlayer(input);
            } catch (final Exception e2) {
                return ArgumentParseResult.failure(new BetterOfflinePlayerParseException(input, commandContext));
            }
        }

        return ArgumentParseResult.success(player);
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        final CommandSender bukkit = commandContext.get(BukkitCommandContextKeys.BUKKIT_COMMAND_SENDER);
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> !(bukkit instanceof Player && !((Player) bukkit).canSee(player)))
                .flatMap(player -> Stream.of(player.getName(), player.getUniqueId().toString()))
                .collect(Collectors.toList());
    }

    public static final class BetterOfflinePlayerParseException extends ParserException {

        private final String input;

        public BetterOfflinePlayerParseException(
                final @NonNull String input,
                final @NonNull CommandContext<?> context
        ) {
            super(
                    OfflinePlayerParser.class,
                    context,
                    BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_OFFLINEPLAYER,
                    CaptionVariable.of("input", input)
            );
            this.input = input;
        }

        /**
         * Get the supplied input
         *
         * @return String value
         */
        public @NonNull String input() {
            return this.input;
        }
    }
}
