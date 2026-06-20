package net.chunkful.inventoryrestore.lifecycle;

import net.chunkful.inventoryrestore.config.ConfigurationManager;
import net.chunkful.inventoryrestore.config.PluginConfiguration;
import net.chunkful.inventoryrestore.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class PluginLifecycler {

    private final Plugin owningPlugin;
    private final ConfigurationManager configurationManager;
    private final PluginBinder pluginBinder;

    private PluginBindings bindings;

    private final List<Listener> listeners;

    public PluginLifecycler(Plugin owningPlugin) {
        this.owningPlugin = owningPlugin;
        this.configurationManager = new ConfigurationManager();
        this.pluginBinder = new PluginBinder();
        this.listeners = new ArrayList<>();
    }

    private void bind() {
        PluginConfiguration pluginConfiguration = configurationManager.load(
                PluginConfiguration.class,
                owningPlugin.getDataPath().resolve("config.conf"),
                new PluginConfiguration()
        );
        bindings = pluginBinder.bind(owningPlugin, pluginConfiguration);
    }

    public void load() {
        bind();
        bindingObjects().forEach(object -> runIfLifecycle(object, Lifecycled::load));
        registerListener(new PlayerListener(bindings.recordingService()));
    }

    public void unload() {
        listeners.forEach(HandlerList::unregisterAll);
        bindingObjects().forEach(object -> runIfLifecycle(object, Lifecycled::unload));
    }

    public PluginBindings bindings() {
        return bindings;
    }

    private void registerListener(Listener listener) {
        listeners.add(listener);
        Bukkit.getPluginManager().registerEvents(listener, owningPlugin);
    }

    private void runIfLifecycle(Object object, Consumer<Lifecycled> lifecycledConsumer) {
        if (object instanceof Lifecycled lifecycled) {
            lifecycledConsumer.accept(lifecycled);
        }
    }

    private Set<Object> bindingObjects() {
        return Set.of(
                bindings.storage(),
                bindings.snapshotRecorder(),
                bindings.snapshotRestorer(),
                bindings.snapshotViewer(),
                bindings.snapshotExporter(),
                bindings.recordingService(),
                bindings.messageLoader()
        );
    }
}
