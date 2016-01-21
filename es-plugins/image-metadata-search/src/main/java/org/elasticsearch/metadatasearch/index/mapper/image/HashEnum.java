package org.elasticsearch.metadatasearch.index.mapper.image;
public enum HashEnum {
	LSH;
    public static HashEnum getByName(String name) {
        return valueOf(name.toUpperCase());
    }
}
