package org.elasticsearch.metadatasearch.index.mapper.image;
import org.elasticsearch.metadatasearch.lire.feature.ImageLireFeature;
public enum FeatureEnum {
	CEDD(org.elasticsearch.metadatasearch.lire.feature.ImageCEDD.class);
    private Class<? extends ImageLireFeature> featureClass;

    FeatureEnum(Class<? extends ImageLireFeature> featureClass) {
        this.featureClass = featureClass;
    }

    public Class<? extends ImageLireFeature> getFeatureClass() {
        return featureClass;
    }

    public static FeatureEnum getByName(String name) {
        return valueOf(name.toUpperCase());
    }

}
