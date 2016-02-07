package org.elasticsearch.metadatasearch.index.query.image;

import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.metadatasearch.lire.feature.ImageLireFeature;

import java.io.IOException;

/**
 * Calculate score for each image
 * score = (1 / distance) * boost
 */
public abstract class AbstractImageScorer extends Scorer {
    private final String luceneFieldName;
    private final ImageLireFeature lireFeature;
    private final IndexReader reader;
    private final float boost;
    private BinaryDocValues binaryDocValues;

    protected AbstractImageScorer(Weight weight, String luceneFieldName, ImageLireFeature lireFeature, IndexReader reader,
                                  float boost) {
        super(weight);
        this.luceneFieldName = luceneFieldName;
        this.lireFeature = lireFeature;
        this.reader = reader;
        this.boost = boost;
    }

    @Override
    public float score() throws IOException {
        assert docID() != NO_MORE_DOCS;

        if (binaryDocValues == null) {
            LeafReader atomicReader = (LeafReader) reader;
            binaryDocValues = atomicReader.getBinaryDocValues(luceneFieldName);
        }

        try {
            BytesRef bytesRef = new BytesRef();
            bytesRef =   binaryDocValues.get(docID());
            ImageLireFeature docFeature = lireFeature.getClass().newInstance();
            docFeature.setByteArrayRepresentation(bytesRef.bytes);
            float score =  lireFeature.getSimilarity(docFeature);

            return score * boost;
        } catch (Exception e) {
            throw new ElasticsearchException("Failed to calculate score", e);
        }
    }

    @Override
    public int freq() {
        return 1;
    }
}
