package net.chunkful.inventoryrestore.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class ConfigurationManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationManager.class);

    private ConfigurationLoader<?> configurationLoader(Path path) {
        return HoconConfigurationLoader.builder()
                .prettyPrinting(true)
                .defaultOptions(configurationOptions ->
                        configurationOptions
                                .shouldCopyDefaults(true)
                                .implicitInitialization(false)
                                .serializers(builder ->
                                        builder.register(Locale.class, new LocaleSerializer())
                                )
                )
                .path(path)
                .build();
    }

    public <T> T load(Class<T> clazz, Path path, T defaultConfig) {
        if(!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                log.error("Failed to create directory for configuration file: {}", path, e);
            }
        }

        ConfigurationLoader<?> configurationLoader = configurationLoader(path);

        try {
            ConfigurationNode rootNode = configurationLoader.load();

            T config = rootNode.get(clazz);

            if (config == null) {
                config = defaultConfig;
            }

            rootNode.set(clazz, config);
            configurationLoader.save(rootNode);

            return config;
        } catch (ConfigurateException e) {
            log.error("Failed to load or save configuration file: {}", path, e);
        }

        return null;
    }

}
