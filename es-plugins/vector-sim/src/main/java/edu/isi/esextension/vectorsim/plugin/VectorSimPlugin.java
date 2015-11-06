package edu.isi.esextension.vectorsim.plugin;

import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.script.ScriptModule;

import edu.isi.esextension.vectorsim.script.VectorSimScriptFactory;


public class VectorSimPlugin extends AbstractPlugin{
	public String name() {
        return VectorSimScriptFactory.SCRIPT_NAME;
    }
    public String description() {
        return "Vector Similarity Plugin to compare documents based on high dimensional feature vectors";
    }

    public void onModule(ScriptModule module) {
        module.registerScript(VectorSimScriptFactory.SCRIPT_NAME,VectorSimScriptFactory.class);
    }

}
