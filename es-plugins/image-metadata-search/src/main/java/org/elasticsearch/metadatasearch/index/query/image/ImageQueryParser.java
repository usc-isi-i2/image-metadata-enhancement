package org.elasticsearch.metadatasearch.index.query.image;


import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.Base64;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.query.QueryParseContext;
import org.elasticsearch.index.query.QueryParser;
import org.elasticsearch.index.query.QueryParsingException;
import org.elasticsearch.metadatasearch.index.hashes.LSH;
import org.elasticsearch.metadatasearch.index.mapper.image.FeatureEnum;
import org.elasticsearch.metadatasearch.index.mapper.image.HashEnum;
import org.elasticsearch.metadatasearch.index.mapper.image.ImageMapper;
import org.elasticsearch.metadatasearch.lire.feature.ImageLireFeature;
import org.elasticsearch.metadatasearch.plugin.utils.Utils;
import net.semanticmetadata.lire.utils.SerializationUtils;
import java.io.IOException;

public class ImageQueryParser implements QueryParser {

    public static final String NAME = "image";
    public static final int FEATURE_MATCH_TRESHOLD = 33;

    private Client client;

    @Inject
    public ImageQueryParser(Client client) {
        this.client = client;
    }

    @Override
    public String[] names() {
        return new String[] {NAME};
    }
    

    @Override
    public Query parse(QueryParseContext parseContext) throws IOException, QueryParsingException {
        XContentParser parser = parseContext.parser();

        XContentParser.Token token = parser.nextToken();
        if (token != XContentParser.Token.FIELD_NAME) {
            throw new QueryParsingException(parseContext.index(), 0, 0, "[image] query malformed, no field", null);
        }


        String fieldName = parser.currentName();
        FeatureEnum featureEnum = null;
        byte[] content = null;
        double[] featVec = null;
        HashEnum hashEnum = null;
        float boost = 1.0f;
        int limit = -1;

        String lookupIndex = parseContext.index().name();
        String lookupType = null;
        String lookupId = null;
        String lookupPath = null;
        String lookupRouting = null;


        token = parser.nextToken();
        if (token == XContentParser.Token.START_OBJECT) {
            String currentFieldName = null;
            while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
                if (token == XContentParser.Token.FIELD_NAME) {
                    currentFieldName = parser.currentName();
                } else {
                    if ("feature".equals(currentFieldName)) {
                        featureEnum = FeatureEnum.getByName(parser.text());
                    } else if ("image".equals(currentFieldName)) {
                        content =Base64.decode(parser.text());
                        featVec = SerializationUtils.toDoubleArray(content);

                    } else if ("hash".equals(currentFieldName)) {
                        hashEnum = HashEnum.getByName(parser.text());
                    } else if ("boost".equals(currentFieldName)) {
                        boost = parser.floatValue();
                    } else if ("limit".equals(currentFieldName)) {
                        limit = parser.intValue();
                    }else if ("index".equals(currentFieldName)) {
                        lookupIndex = parser.text();
                    } else if ("type".equals(currentFieldName)) {
                        lookupType = parser.text();
                    } else if ("id".equals(currentFieldName)) {
                        lookupId = parser.text();
                    } else if ("path".equals(currentFieldName)) {
                        lookupPath = parser.text();
                    } else if ("routing".equals(currentFieldName)) {
                        lookupRouting = parser.textOrNull();
                    } else {
                        throw new QueryParsingException(parseContext.index(), 0, 0, "[image] query does not support [" + currentFieldName + "]", null);
                    }
                }
            }
            parser.nextToken();
        }

        if (featureEnum == null) {
            throw new QueryParsingException(parseContext.index(), 0, 0, "No feature specified for image query", null);
        }

        String luceneFieldName = fieldName + "." + featureEnum.name();
        ImageLireFeature feature;
		try {
			feature = featureEnum.getFeatureClass().newInstance();
		} catch (InstantiationException e1) {
			throw new QueryParsingException(parseContext.index(), 0, 0, "Error extarcting Features", e1);
		} catch (IllegalAccessException e1) {
			throw new QueryParsingException(parseContext.index(), 0, 0, "Error extarcting Features", e1);
		}
        
        if (content != null) {
       	
            try {
            	feature.setHistogramFromDoubleArray(featVec);
            	} catch (Exception e) {
                throw new ElasticsearchException("Failed to parse image", e);
            }
        } 
        /*
        else if (lookupIndex != null && lookupType != null && lookupId != null && lookupPath != null) {
            String lookupFieldName = lookupPath + "." + featureEnum.name();
            GetResponse getResponse = client.get(new GetRequest(lookupIndex, lookupType, lookupId).preference("_local").routing(lookupRouting).fields(lookupFieldName).realtime(false)).actionGet();
            if (getResponse.isExists()) {
                GetField getField = getResponse.getField(lookupFieldName);
                if (getField != null) {
                    BytesReference bytesReference = (BytesReference) getField.getValue();
                    try {
                        feature = featureEnum.getFeatureClass().newInstance();
                        feature.setByteArrayRepresentation(bytesReference.array(), bytesReference.arrayOffset(), bytesReference.length());
                    } catch (Exception e) {
                        throw new ElasticsearchImageProcessException("Failed to parse image", e);
                    }
                }
            }
        }*/
        if (feature == null) {
            throw new QueryParsingException(parseContext.index(), limit, limit, "No image specified for image query", null);
        }


            int[] hash = null;
            if (hashEnum.equals(HashEnum.LSH)) {
                hash = LSH.lsh.hash(feature.getDoubleHistogram());
            }
            String hashFieldName = luceneFieldName + "." + ImageMapper.HASH + "." + hashEnum.name();

              // no max result limit, use ImageHashQuery
                BooleanQuery query = new BooleanQuery(true);
                ImageScoreCache imageScoreCache = new ImageScoreCache();

               
                for(int i=0;i<hash.length;i++)
                {
                	String x = Utils.hashTermToString(i+1, hash[i]) ;
                	query.add(new BooleanClause(new ImageHashQuery(new Term(hashFieldName, x), luceneFieldName, feature, imageScoreCache, boost), BooleanClause.Occur.SHOULD));

                }
                
                	
                query.setMinimumNumberShouldMatch(FEATURE_MATCH_TRESHOLD);
                return query;
            }

        
    
}
	