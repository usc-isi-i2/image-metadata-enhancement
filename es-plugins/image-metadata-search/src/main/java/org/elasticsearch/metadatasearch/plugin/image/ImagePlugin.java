package org.elasticsearch.metadatasearch.plugin.image;

import org.elasticsearch.indices.IndicesModule;
import org.elasticsearch.metadatasearch.index.mapper.image.ImageMapper;
import org.elasticsearch.metadatasearch.index.query.image.ImageQueryParser;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.threadpool.ThreadPool;


public class ImagePlugin extends Plugin {

    @Override
    public String name() {
        return ImageMapper.CONTENT_TYPE;
    }

    @Override
    public String description() {
        return "Elasticsearch Image Plugin";
    }

    public void onModule(IndicesModule indicesModule) {
        indicesModule.registerMapper(ImageMapper.CONTENT_TYPE, new ImageMapper.TypeParser(new ThreadPool(ImageMapper.CONTENT_TYPE)));
        indicesModule.registerQueryParser(ImageQueryParser.class);
    }
    
    
}
