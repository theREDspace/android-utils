import groovy.json.JsonSlurper;

class PluginInitializer {

    static PluginInitializer instance;

    private PluginInitializer() {}

    private Set<String> plugins = new LinkedHashSet<>()

    private static def obj;

    static PluginInitializer getInstance() {
        if (instance == null) {
            obj = loadPluginsOrderFile()

            // Verify no element of pre is in post
            obj.pre.each { item -> if (obj.post.contains(item))
                throw IllegalStateException("Post and Pre cannot both contain " + item)
            }

            instance = new PluginInitializer()
        }
        return instance
    }

    void flush() {
        plugins.clear()
    }

    void addPlugin(String pluginName) {
        plugins.add(pluginName)
    }

    void initializePreScriptPlugins(def script) {
        obj.pre.each { item ->
            applyPlugin(item, script)
        }
    }

    void initializePostScriptPlugins(def script) {
        obj.post.each { item ->
            applyPlugin(item, script)
        }
    }

    private void applyPlugin(String item, def script) {
        expandedItem = item.replace('$rootDir', "${script.rootDir}")
        if (plugins.contains(expandedItem)) {
            if (item.endsWith('.gradle'))
                script.apply from: expandedItem
            else
                script.apply plugin: expandedItem
        }
    }

    private static def loadPluginsOrderFile() {
        return new JsonSlurper().parseText(new File("pluginInitializerOrder.json").text)
    }
}