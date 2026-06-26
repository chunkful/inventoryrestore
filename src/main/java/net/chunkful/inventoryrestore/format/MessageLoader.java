package net.chunkful.inventoryrestore.format;

import net.chunkful.inventoryrestore.format.tag.CenterTag;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore;
import net.kyori.adventure.translation.GlobalTranslator;
import net.chunkful.inventoryrestore.lifecycle.Lifecycled;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

public class MessageLoader implements Lifecycled {

    private static final Logger log = LoggerFactory.getLogger(MessageLoader.class);
    private final Plugin owningPlugin;
    private final Set<Locale> availableLocales;

    public MessageLoader(Plugin owningPlugin, Set<Locale> availableLocales) {
        this.owningPlugin = owningPlugin;
        this.availableLocales = availableLocales;
    }

    private MiniMessageTranslationStore translator;

    @Override
    public void load() {
        translator = MiniMessageTranslationStore.create(Key.key(owningPlugin, "messages"), miniMessage());
        translator.defaultLocale(Locale.US);
        for (Locale availableLocale : availableLocales) {
            try {
                registerLocale(translator, availableLocale);
            } catch (MalformedURLException exception) {
                log.error("Failed to load locale {}", availableLocale, exception);
            }
        }
        GlobalTranslator.translator().addSource(translator);
    }

    @Override
    public void unload() {
        GlobalTranslator.translator().removeSource(translator);
    }

    private void registerLocale(final MiniMessageTranslationStore translationStore,
                                final Locale locale) throws MalformedURLException {
        final ResourceBundle bundle = ResourceBundle.getBundle("messages.messages", locale,
                messageClassLoader(owningPlugin));
        translationStore.registerAll(locale, bundle, false);
    }


    private URLClassLoader messageClassLoader(final Plugin plugin) throws MalformedURLException {
        return new URLClassLoader(new URL[]{
                plugin.getDataFolder().toURI().toURL(),
                plugin.getClass().getProtectionDomain().getCodeSource().getLocation(),
        });
    }

    private MiniMessage miniMessage() {
        return MiniMessage.builder()
                .editTags(builder -> builder.resolver(CenterTag.RESOLVER))
                .build();
    }
}
