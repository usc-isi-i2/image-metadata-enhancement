package org.elasticsearch.metadatasearch.index.mapper.image;

import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.Base64;
import org.elasticsearch.common.collect.ImmutableOpenMap;

import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import net.semanticmetadata.lire.utils.SerializationUtils;
import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.mapper.*;
import org.elasticsearch.metadatasearch.index.hashes.LSH;
import org.elasticsearch.metadatasearch.lire.feature.ImageLireFeature;
import org.elasticsearch.metadatasearch.plugin.utils.Utils;
import org.elasticsearch.threadpool.ThreadPool;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static org.elasticsearch.index.mapper.MapperBuilders.binaryField;
import static org.elasticsearch.index.mapper.MapperBuilders.stringField;

public class ImageMapper extends FieldMapper {

    private static ESLogger logger = ESLoggerFactory.getLogger(ImageMapper.class.getName());
    public static final int MAX_IMAGE_DIMENSION = 1024;
    public static final String CONTENT_TYPE = "image";
    public static final String HASH = "hash";
    public static final String FEATURE = "feature";
    public static final String METADATA = "metadata";
    

    static {
        try 
        {
            LSH.readHashFunctions();
        } 
        catch (Exception e) {
        	 logger.error("Failed to initialize hash function", e);
		}
    }

    static final class ImageFieldType extends MappedFieldType {
        public ImageFieldType() {}
        protected ImageFieldType(ImageMapper.ImageFieldType ref) {
            super(ref);
        }
        public ImageMapper.ImageFieldType clone() {
            return new ImageMapper.ImageFieldType(this);
        }
        @Override
        public String typeName() {
            return CONTENT_TYPE;
        }
        public String value(Object value) {
            return value == null?null:value.toString();
        }
    }

    
    public static class Defaults {
        public static final ContentPath.Type PATH_TYPE = ContentPath.Type.FULL;
        public static final ImageFieldType FIELD_TYPE = new ImageFieldType();
        static {
            FIELD_TYPE.freeze();
        }
    }

    public static class Builder extends FieldMapper.Builder<Builder, ImageMapper> {
    	
    	private ContentPath.Type pathType = Defaults.PATH_TYPE;
        private ThreadPool threadPool;
        private Map<FeatureEnum, Map<String, Object>> features = Maps.newHashMap();
        private Map<String, Mapper.Builder> metadataBuilders = Maps.newHashMap();

        public Builder(String name, ThreadPool threadPool) {
        	super(name, new ImageFieldType());
            this.threadPool = threadPool;
            this.builder = this;
        }

        public Builder addFeature(FeatureEnum featureEnum, Map<String, Object> featureMap) {
            this.features.put(featureEnum, featureMap);
            return this;
        }

        public Builder addMetadata(String metadata, Mapper.Builder metadataBuilder) {
            this.metadataBuilders.put(metadata, metadataBuilder);
            return this;
        }

        @Override
        public ImageMapper build(BuilderContext context) {
            Map<String, FieldMapper> featureMappers = Maps.newHashMap();
            Map<String, FieldMapper> hashMappers = Maps.newHashMap();
            Map<String, FieldMapper> metadataMappers = Maps.newHashMap();

            context.path().add(name);
            // add feature and hash mappers
            for (FeatureEnum featureEnum : features.keySet()) {
                Map<String, Object> featureMap = features.get(featureEnum);
                String featureName = featureEnum.name();

                // add feature mapper
                featureMappers.put(featureName,(FieldMapper) binaryField(featureName).store(true).includeInAll(false).index(false).build(context));

                // add hash mapper if hash is required
                if (featureMap.containsKey(HASH)){
                    List<String> hashes = (List<String>) featureMap.get(HASH);
                    for (String h : hashes) {
                        String hashFieldName = featureName + "." + HASH + "." + h;
                        hashMappers.put(hashFieldName, (FieldMapper) stringField(hashFieldName).store(true).includeInAll(false).index(true).build(context));
                    }
                }
            }

            // add metadata mappers
            context.path().add(METADATA);
            for (Map.Entry<String, Mapper.Builder> entry : metadataBuilders.entrySet()){
                String metadataName = entry.getKey();
                Mapper.Builder metadataBuilder = entry.getValue();
                metadataMappers.put(metadataName,(FieldMapper) metadataBuilder.build(context));
            }
            context.path().remove();  // remove METADATA
            context.path().remove();  // remove name
            
            
            MappedFieldType defaultFieldType = Defaults.FIELD_TYPE.clone();
            if(this.fieldType.indexOptions() != IndexOptions.NONE && !this.fieldType.tokenized()) {
                defaultFieldType.setOmitNorms(true);
                defaultFieldType.setIndexOptions(IndexOptions.DOCS);
                if(!this.omitNormsSet && this.fieldType.boost() == 1.0F) {
                    this.fieldType.setOmitNorms(true);
                }

                if(!this.indexOptionsSet) {
                    this.fieldType.setIndexOptions(IndexOptions.DOCS);
                }
            }

            defaultFieldType.freeze();
            this.setupFieldType(context);

            return new ImageMapper(name,  threadPool, context.indexSettings(), features, featureMappers, hashMappers, metadataMappers,fieldType, defaultFieldType, multiFieldsBuilder.build(this, context), copyTo);
        }
    }

    public static class TypeParser implements Mapper.TypeParser {
        private ThreadPool threadPool;

        public TypeParser(ThreadPool threadPool) {
            this.threadPool = threadPool;
        }

        @SuppressWarnings({"unchecked"})
        @Override
        public Mapper.Builder parse(String name, Map<String, Object> node, ParserContext parserContext) throws MapperParsingException {
            ImageMapper.Builder builder = new ImageMapper.Builder(name, threadPool);
            Map<String, Object> features = Maps.newHashMap();
            Map<String, Object> metadatas = Maps.newHashMap();

            
            for(Iterator<Map.Entry<String, Object>> iterator = node.entrySet().iterator(); iterator.hasNext();)
            {
            	Map.Entry<String, Object> entry = iterator.next();
            	 String fieldName = entry.getKey();
                 Object fieldNode = entry.getValue();
                 if (FEATURE.equals(fieldName)) {
                     features = (Map<String, Object>) fieldNode;
                 } else if (METADATA.equals(fieldName)) {
                     metadatas = (Map<String, Object>) fieldNode;
                 }
                 iterator.remove();
           }


            if (features == null || features.isEmpty()) {
                throw new ElasticsearchException("Feature not found");
            }

            // process features
            for (Map.Entry<String, Object> entry : features.entrySet()) {
                String feature = entry.getKey();
                Map<String, Object> featureMap = (Map<String, Object>) entry.getValue();

                // process hash for each feature
                if (featureMap.containsKey(HASH)) {
                    Object hashVal = featureMap.get(HASH);
                    List<String> hashes = Lists.newArrayList();
                    if (hashVal instanceof List) {
                        for (String h : (List<String>)hashVal) {
                            hashes.add(HashEnum.valueOf(h).name());
                        }
                    } else if (hashVal instanceof String) {
                        hashes.add(HashEnum.valueOf((String) hashVal).name());
                    } else {
                        throw new ElasticsearchException("Malformed hash value");
                    }
                    featureMap.put(HASH, hashes);
                }

                FeatureEnum featureEnum = FeatureEnum.getByName(feature);
                builder.addFeature(featureEnum, featureMap);
            }

            // process metadata
            for (Map.Entry<String, Object> entry : metadatas.entrySet()) {
                String metadataName = entry.getKey();
                Map<String, Object> metadataMap = (Map<String, Object>) entry.getValue();
                String fieldType = (String) metadataMap.get("type");
                builder.addMetadata(metadataName, parserContext.typeParser(fieldType).parse(metadataName, metadataMap, parserContext));
            }

            return builder;
        }
    }

    private final String name;

    private final ThreadPool threadPool;

    private final Settings settings;

    private volatile ImmutableOpenMap<FeatureEnum, Map<String, Object>> features = ImmutableOpenMap.of();

    private volatile ImmutableOpenMap<String, FieldMapper> featureMappers = ImmutableOpenMap.of();

    private volatile ImmutableOpenMap<String, FieldMapper> hashMappers = ImmutableOpenMap.of();

    private volatile ImmutableOpenMap<String, FieldMapper> metadataMappers = ImmutableOpenMap.of();

    
    public ImageMapper(String name, ThreadPool threadPool, Settings settings, Map<FeatureEnum, Map<String, Object>> features, Map<String, FieldMapper> featureMappers,
            Map<String, FieldMapper> hashMappers, Map<String, FieldMapper> metadataMappers,MappedFieldType type, MappedFieldType defaultFieldType,MultiFields multiFields, CopyTo copyTo) {
    	super(name, type, defaultFieldType, settings, multiFields, copyTo);
    	this.name = name;
        this.threadPool = threadPool;
        this.settings = settings;
        if (features != null) {
            this.features = ImmutableOpenMap.builder(this.features).putAll(features).build();
        }
        if (featureMappers != null) {
            this.featureMappers = ImmutableOpenMap.builder(this.featureMappers).putAll(featureMappers).build();
        }
        if (hashMappers != null) {
            this.hashMappers = ImmutableOpenMap.builder(this.hashMappers).putAll(hashMappers).build();
        }
        if (metadataMappers != null) {
            this.metadataMappers = ImmutableOpenMap.builder(this.metadataMappers).putAll(metadataMappers).build();
        }

    }
    
    @Override
    public String name() {
        return name;
    }

    public Mapper parse(ParseContext context) throws IOException {
        byte[] content = null;
        XContentParser parser = context.parser();
        XContentParser.Token token = parser.currentToken();
        if (token == XContentParser.Token.VALUE_STRING) {
            content =Base64.decode(parser.text());
        }

        if (content == null) {
            throw new MapperParsingException("No content is provided.");
        }

        final Boolean useThreadPool = settings.getAsBoolean("index.image.use_thread_pool", true);
        final Boolean ignoreMetadataError = settings.getAsBoolean("index.image.ignore_metadata_error", true);
        final double[] featVector = SerializationUtils.toDoubleArray(content);
        final Map<FeatureEnum, ImageLireFeature> featureExtractMap = new MapMaker().makeMap();

        // have multiple features, use ThreadPool to process each feature
        /*if (useThreadPool && features.size() > 1) {
            final CountDownLatch latch = new CountDownLatch(features.size());
            Executor executor = threadPool.generic();

            for (ObjectObjectCursor<FeatureEnum, Map<String, Object>> cursor : features) {
                final FeatureEnum featureEnum = cursor.key;
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LireFeature lireFeature = featureEnum.getFeatureClass().newInstance();
                            lireFeature.extract(finalImg);
                            featureExtractMap.put(featureEnum, lireFeature);
                        } catch (Throwable e){
                            logger.error("Failed to extract feature from image", e);
                        } finally {
                            latch.countDown();
                        }
                    }
                });
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                logger.debug("Interrupted extract feature from image", e);
                Thread.currentThread().interrupt();
            }
        }
		*/

        for (ObjectObjectCursor<FeatureEnum, Map<String, Object>> cursor : features) {
            FeatureEnum featureEnum = cursor.key;
            Map<String, Object> featureMap = cursor.value;
            try 
            {
                ImageLireFeature lireFeature = featureEnum.getFeatureClass().newInstance();
                if (featureExtractMap.containsKey(featureEnum)) {   // already processed
                    lireFeature = featureExtractMap.get(featureEnum);
                } else {
                	lireFeature.setHistogramFromDoubleArray(featVector);
                }
                byte[] parsedContent = lireFeature.getByteArrayRepresentation();
                FieldMapper featureMapper = featureMappers.get(featureEnum.name());
                context = context.createExternalValueContext(parsedContent);
                featureMapper.parse(context);
                context.doc().add(new BinaryDocValuesField(name() + "." + featureEnum.name(), new BytesRef(parsedContent)));

                // add hash if required
                if (featureMap.containsKey(HASH)) {
                    List<String> hashes = (List<String>) featureMap.get(HASH);
                    for (String h : hashes) {
                        HashEnum hashEnum = HashEnum.valueOf(h);
                        int[] hashVals = null;
                        if (hashEnum.equals(HashEnum.LSH)) {
                            hashVals = LSH.lsh.hash(lireFeature.getDoubleHistogram());
                        }
                        String mapperName = featureEnum.name() + "." + HASH + "." + h;
                        FieldMapper hashMapper = (FieldMapper) hashMappers.get(mapperName);
                      
                        String hv = Utils.hashArrayToString(hashVals);
                        context = context.createExternalValueContext(hv);
                        hashMapper.parse(context);
                    }
                }
            } catch (Exception e) {
                throw new ElasticsearchException("Failed to index feature " + featureEnum.name(), e);
            }
        }

        // process metadata if required
        /*if (!metadataMappers.isEmpty()) {
            try {
                Metadata metadata = ImageMetadataReader.readMetadata(new BufferedInputStream(new BytesStreamInput(content, false)), false);
                for (Directory directory : metadata.getDirectories()) {
                    for (Tag tag : directory.getTags()) {
                        String metadataName = tag.getDirectoryName().toLowerCase().replaceAll("\\s+", "_") + "." +
                                tag.getTagName().toLowerCase().replaceAll("\\s+", "_");
                        if (metadataMappers.containsKey(metadataName)) {
                            Mapper mapper = metadataMappers.get(metadataName);
                            context.externalValue(tag.getDescription());
                            mapper.parse(context);
                        }
                    }
                }
            } catch (ImageProcessingException e) {
                logger.error("Failed to extract metadata from image", e);
                if (!ignoreMetadataError) {
                    throw new ElasticsearchImageProcessException("Failed to extract metadata from image", e);
                }
            }
        }
		*/
        
        return null;

    }

	public void close() {
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject(name);

        builder.field("type", CONTENT_TYPE);

        builder.startObject(FEATURE);
        for (ObjectObjectCursor<FeatureEnum, Map<String, Object>> cursor : features) {
            builder.field(cursor.key.name(), cursor.value);
        }
        builder.endObject();

        builder.startObject(METADATA);
        for (ObjectObjectCursor<String, FieldMapper> cursor : metadataMappers) {
            cursor.value.toXContent(builder, params);
        }
        builder.endObject();

        builder.endObject();
        return builder;
    }

    @Override
	protected void parseCreateField(ParseContext context, List<Field> fields) throws IOException {
	}

    @Override
	protected String contentType() {
		return CONTENT_TYPE;
	}
}
