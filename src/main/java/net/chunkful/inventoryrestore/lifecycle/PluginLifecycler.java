package net.chunkful.inventoryrestore.lifecycle;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.chunkful.inventoryrestore.config.ConfigurationManager;
import net.chunkful.inventoryrestore.config.PluginConfiguration;
import net.chunkful.inventoryrestore.listener.PlayerListener;
import net.chunkful.inventoryrestore.task.ScheduledRecordingTask;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class PluginLifecycler {

    private final Plugin owningPlugin;
    private final ConfigurationManager configurationManager;
    private final PluginBinder pluginBinder;

    private PluginBindings bindings;

    private final List<Listener> listeners;
    private final List<ScheduledTask> tasks;

    public PluginLifecycler(Plugin owningPlugin) {
        this.owningPlugin = owningPlugin;
        this.configurationManager = new ConfigurationManager();
        this.pluginBinder = new PluginBinder();
        this.listeners = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }

    public void load() {
        PluginConfiguration pluginConfiguration = loadPluginConfiguration();
        bindings = pluginBinder.bind(owningPlugin, pluginConfiguration);
        bindingObjects().forEach(object -> runIfLifecycle(object, Lifecycled::load));
        registerListener(new PlayerListener(bindings.service()));
        registerTask(new ScheduledRecordingTask(pluginConfiguration, bindings.service()));
    }

    public void unload() {
        tasks.forEach(ScheduledTask::cancel);
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

    private void registerTask(Runnable runnable) {
        ScheduledTask scheduledTask = Bukkit.getServer().getAsyncScheduler().runAtFixedRate(
                owningPlugin,
                _ -> runnable.run(),
                0,
                50,
                TimeUnit.MILLISECONDS
        );
        tasks.add(scheduledTask);
    }

    private PluginConfiguration loadPluginConfiguration() {
        return configurationManager.load(
                PluginConfiguration.class,
                owningPlugin.getDataPath().resolve("config.conf"),
                new PluginConfiguration()
        );
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
                bindings.service(),
                bindings.messageLoader()
        );
    }
}
