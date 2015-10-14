package edu.isi.esextension.geosearch.plugin;
import edu.isi.esextension.geosearch.script.GeoSearchScriptFactory;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.script.ScriptModule;
/**
 * This class is instantiated when Elasticsearch loads the plugin for the
 * first time. If you change the name of this plugin, make sure to update
 * src/main/resources/es-plugin.properties file that points to this class.
 */
public class GeoSearchPlugin extends AbstractPlugin {

	public String name() {
        return "geo-search-plugin";
    }
    public String description() {
        return "Geo Search Plugin for ranking geoshapes based on proximity to given point";
    }

    public void onModule(ScriptModule module) {
        module.registerScript(GeoSearchScriptFactory.SCRIPT_NAME, GeoSearchScriptFactory.class);
    }
}
