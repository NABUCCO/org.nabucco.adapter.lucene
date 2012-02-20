/*
 * Copyright 2012 PRODYNA AG
 *
 * Licensed under the Eclipse Public License (EPL), Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/eclipse-1.0.php or
 * http://www.nabucco.org/License.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nabucco.adapter.lucene.impl.connector.ra.spi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.nabucco.adapter.lucene.impl.connector.ra.spi.util.LuceneAccentConverter;
import org.nabucco.framework.base.facade.datatype.BasetypeType;
import org.nabucco.framework.base.facade.datatype.Name;
import org.nabucco.framework.base.facade.datatype.geo.GeoLocation;
import org.nabucco.framework.base.facade.datatype.logger.NabuccoLogger;
import org.nabucco.framework.base.facade.datatype.logger.NabuccoLoggingFactory;
import org.nabucco.framework.base.facade.datatype.search.fulltext.FieldValue;
import org.nabucco.framework.base.facade.datatype.search.fulltext.FulltextDocument;
import org.nabucco.framework.base.facade.datatype.search.fulltext.FulltextField;
import org.nabucco.framework.base.facade.datatype.search.query.FulltextQuery;
import org.nabucco.framework.base.facade.datatype.search.query.FulltextQueryField;
import org.nabucco.framework.base.facade.datatype.search.query.FulltextQueryRangeField;
import org.nabucco.framework.base.facade.datatype.search.query.FulltextQueryValueField;

/**
 * Implementation of Lucene search service.
 * 
 * @author Frank Ratschinski, PRODYNA AG
 */
public class LuceneSearchServiceImpl implements LuceneSearchService {

    /**
     * Comment for <code>LONGITUDE</code>
     */
    private static final String LONGITUDE = "longitude";

    /**
     * Comment for <code>LATITUDE</code>
     */
    private static final String LATITUDE = "latitude";

    private static final NabuccoLogger logger = NabuccoLoggingFactory.getInstance().getLogger(
            LuceneSearchServiceImpl.class);

    private Directory directory;

    private File indexFile;

    private boolean noIndexExists = true;

    /**
     * Creates a new {@link LuceneSearchServiceImpl} instance.
     * 
     * @param index
     *            the index
     * @param indexPath
     *            the index path
     * 
     * @throws ResourceException
     *             when the search service cannot be instantiated
     */
    public LuceneSearchServiceImpl(String index, String indexPath) throws ResourceException {
        try {
            this.directory = DirectoryMap.getInstance().getDirectory(indexPath, index);
            this.indexFile = new File(indexPath + "/" + index);
        } catch (IOException e) {
            throw new ResourceException("Error instantiating LuceneSearchServiceImpl.", e);
        }
    }

    @Override
    public List<FulltextDocument> search(FulltextQuery fulltextQuery) throws ResourceException {
        return this.search(fulltextQuery, null, null);
    }

    @Override
    public List<FulltextDocument> search(FulltextQuery fulltextQuery, GeoLocation minLocation, GeoLocation maxLocation)
            throws ResourceException {

        List<FulltextDocument> result = new ArrayList<FulltextDocument>();

        if (fulltextQuery.getFieldList().size() == 0) {
            if (minLocation == null || maxLocation == null) {
                logger.warning("No Lucene Search Criteria defined, abort Search.");
                return result;
            }
        }

        if (this.noIndexExists) {
            if (!this.indexFile.exists()) {
                logger.warning("Cannot search in index path [", this.indexFile.getAbsolutePath(), "] does not exists.");
                return result;
            }

            this.noIndexExists = false;
        }

        Searcher searcher = null;

        try {
            searcher = new IndexSearcher(this.directory);

            int maxResults = LuceneConstants.DEFAULT_MAX_RESULT;
            if (fulltextQuery.getMaxResult() != null && fulltextQuery.getMaxResult().getValue() != null) {
                maxResults = fulltextQuery.getMaxResult().getValue();
            }

            Query query = this.createQuery(fulltextQuery.getFieldList(), minLocation, maxLocation);

            TopDocs searchResult = searcher.search(query, maxResults);

            if (logger.isDebugEnabled()) {
                logger.debug("Found [", String.valueOf(searchResult.totalHits), "] results of maxResults [",
                        String.valueOf(maxResults), "].");
            }

            for (ScoreDoc doc : searchResult.scoreDocs) {
                int id = doc.doc;
                Document resultDocument = searcher.doc(id);
                this.fillResult(result, resultDocument);
            }

            return result;

        } catch (IOException e) {
            throw new ResourceException("Error executing Lucene index search.", e);
        } finally {
            try {
                if (searcher != null) {
                    searcher.close();
                }
            } catch (Exception e) {
                logger.error(e, "Error closing Lucene Searcher.");
            }
        }
    }

    /**
     * Create the query for the given full text query fields.
     * 
     * @param fieldList
     *            the list of search fields
     * @param location
     *            the geo location
     * @param range
     *            the range in km
     * 
     * @return the search query
     */
    private Query createQuery(List<FulltextQueryField> fieldList, GeoLocation minLocation, GeoLocation maxLocation) {

        BooleanQuery query = new BooleanQuery();

        if (fieldList.size() == 1) {

            FulltextQueryField field = fieldList.get(0);

            if (field instanceof FulltextQueryValueField) {
                this.addValueParameter(query, (FulltextQueryValueField) field);
            } else if (field instanceof FulltextQueryRangeField) {
                this.addRangeParameter(query, (FulltextQueryRangeField) field);
            }

        } else if (fieldList.size() > 1) {

            BooleanQuery booleanQuery = new BooleanQuery();

            for (int i = 0; i < fieldList.size(); i++) {

                FulltextQueryField field = fieldList.get(i);

                if (field instanceof FulltextQueryValueField) {
                    this.addValueParameter(booleanQuery, (FulltextQueryValueField) field);
                } else if (field instanceof FulltextQueryRangeField) {
                    this.addRangeParameter(query, (FulltextQueryRangeField) field);
                }
            }

            query.add(booleanQuery, Occur.MUST);
        }

        this.addGeoData(query, minLocation, maxLocation);

        return query;
    }

    /**
     * Add geo information to the search query.
     * 
     * @param query
     *            the query to add the geo data
     * @param minLocation
     *            the min extreme value
     * @param maxLocation
     *            the max extreme value
     */
    private void addGeoData(BooleanQuery query, GeoLocation minLocation, GeoLocation maxLocation) {

        if (minLocation == null || maxLocation == null) {
            logger.debug("No Geo information defined.");
            return;
        }

        if (minLocation.getLatitude() == null || minLocation.getLatitude().getValue() == null) {
            logger.warning("Minimum Latitude is not defined.");
            return;
        }

        if (minLocation.getLongitude() == null || minLocation.getLongitude().getValue() == null) {
            logger.warning("Minimum Longitude is not defined.");
            return;
        }

        if (maxLocation.getLatitude() == null || maxLocation.getLatitude().getValue() == null) {
            logger.warning("Maximum Latitude is not defined.");
            return;
        }

        if (maxLocation.getLongitude() == null || maxLocation.getLongitude().getValue() == null) {
            logger.warning("Maximum Longitude is not defined.");
            return;
        }

        Double minLatitude = minLocation.getLatitude().getValue();
        Double minLongitude = minLocation.getLongitude().getValue();
        Double maxLatitude = maxLocation.getLatitude().getValue();
        Double maxLongitude = maxLocation.getLongitude().getValue();

        query.add(NumericRangeQuery.newDoubleRange(LATITUDE, minLatitude, maxLatitude, true, true), Occur.MUST);
        query.add(NumericRangeQuery.newDoubleRange(LONGITUDE, minLongitude, maxLongitude, true, true), Occur.MUST);
    }

    /**
     * Add a single value query to the boolean query.
     * 
     * @param query
     *            the boolean query to add the value query
     * @param field
     *            the value field holding the query parameter
     */
    private void addValueParameter(BooleanQuery query, FulltextQueryValueField field) {

        String fieldName;
        if (field == null || field.getFieldName() == null || field.getFieldName().getValue() == null) {
            fieldName = "";
        } else {
            fieldName = field.getFieldName().getValue();
        }

        String fieldValue;
        if (field == null || field.getFieldValue() == null || field.getFieldValue().getValue() == null) {
            fieldValue = "";
        } else {
            fieldValue = field.getFieldValue().getValue();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Searching for text field [", fieldName, "] value [", fieldValue, "].");
        }

        if (fieldValue.endsWith(LuceneConstants.CHAR_EQUALS)) {
            query.add(this.createExactQuery(fieldName, fieldValue), Occur.MUST);
        } else {
            query.add(this.createWildcardQuery(fieldName, fieldValue), Occur.MUST);
        }
    }

    /**
     * Add a numeric query to the boolean query.
     * 
     * @param query
     *            the boolean query to add the numeric query
     * @param field
     *            the numeric field holding the range query parameter
     */
    private void addRangeParameter(BooleanQuery query, FulltextQueryRangeField field) {

        if (field == null) {
            logger.warning("Cannot search for field [null].");
            return;
        }

        BasetypeType fieldType = field.getFieldType();

        String fieldName;
        if (field.getFieldName() == null || field.getFieldName().getValue() == null) {
            fieldName = "";
        } else {
            fieldName = field.getFieldName().getValue();
        }

        String minValue = null;
        if (field.getMinValue() != null && field.getMinValue().getValue() != null) {
            minValue = field.getMinValue().getValue();
        }

        String maxValue = null;
        if (field.getMaxValue() != null && field.getMaxValue().getValue() != null) {
            maxValue = field.getMaxValue().getValue();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Searching for field [", fieldName, "] range [", minValue, ", ", maxValue, "].");
        }

        query.add(this.createRangeQuery(fieldType, fieldName, minValue, maxValue), Occur.MUST);
    }

    /**
     * Create a wildcard search query. The result must only start with the field value.
     * 
     * @param fieldName
     *            name of the field
     * @param fieldValue
     *            value of the field
     * 
     * @return the query
     */
    private Query createWildcardQuery(String fieldName, String fieldValue) {
        BooleanQuery query = new BooleanQuery();

        String termValue = fieldValue + LuceneConstants.CHAR_WILDCARD;
        Term term = new Term(fieldName, termValue);
        query.add(new WildcardQuery(term), Occur.SHOULD);

        // Additional Search Field (Column)
        String searchTermValue = LuceneAccentConverter.removeAccents(fieldValue).toLowerCase()
                + LuceneConstants.CHAR_WILDCARD;
        Term searchTerm = new Term(fieldName + LuceneConstants.SEARCH_FIELD_SUFFIX, searchTermValue);
        query.add(new WildcardQuery(searchTerm), Occur.SHOULD);

        return query;
    }

    /**
     * Create an exact search query. The result must be exacly equal to the field value.
     * 
     * @param fieldName
     *            name of the field
     * @param fieldValue
     *            value of the field
     * 
     * @return the query
     */
    private Query createExactQuery(String fieldName, String fieldValue) {
        String searchString = fieldValue.substring(0, fieldValue.length() - 1);
        Term term = new Term(fieldName, searchString);
        return new TermQuery(term);
    }

    /**
     * Create a numeric search query. The result must be a double value.
     * 
     * @param fieldType
     *            type of the field
     * @param fieldName
     *            name of the field
     * @param minValue
     *            minimal value of the field
     * @param maxValue
     *            maximum value of the field
     * 
     * @return the query
     */
    private Query createRangeQuery(BasetypeType fieldType, String fieldName, String minValue, String maxValue) {

        switch (fieldType) {

        case INTEGER: {
            Integer min = minValue == null ? null : Integer.parseInt(minValue);
            Integer max = maxValue == null ? null : Integer.parseInt(maxValue);
            return NumericRangeQuery.newIntRange(fieldName, min, max, true, true);
        }

        case LONG: {
            Long min = minValue == null ? null : Long.parseLong(minValue);
            Long max = maxValue == null ? null : Long.parseLong(maxValue);
            return NumericRangeQuery.newLongRange(fieldName, min, max, true, true);
        }

        case FLOAT: {
            Float min = minValue == null ? null : Float.parseFloat(minValue);
            Float max = maxValue == null ? null : Float.parseFloat(maxValue);
            return NumericRangeQuery.newFloatRange(fieldName, min, max, true, true);
        }

        case DOUBLE: {
            Double min = minValue == null ? null : Double.parseDouble(minValue);
            Double max = maxValue == null ? null : Double.parseDouble(maxValue);
            return NumericRangeQuery.newDoubleRange(fieldName, min, max, true, true);
        }

        default:
            return new TermRangeQuery(fieldName, minValue, maxValue, true, true);
        }

    }

    /**
     * Fill the search result with remaining data.
     * 
     * @param result
     *            the result list
     * @param searchResult
     *            the search result
     */
    private void fillResult(List<FulltextDocument> result, Document searchResult) {
        FulltextDocument doc = new FulltextDocument();

        for (Fieldable fieldable : searchResult.getFields()) {

            Field field = (Field) fieldable;
            String value = field.stringValue();
            String name = field.name();

            if (!name.endsWith(LuceneConstants.SEARCH_FIELD_SUFFIX)) {

                FulltextField ftField = new FulltextField();
                ftField.setFieldValue(new FieldValue(value));
                ftField.setFieldName(new Name(name));

                doc.getFieldList().add(ftField);
            }
        }

        result.add(doc);
    }

    @Override
    public void ping() throws ResourceException {
        try {
            this.directory.listAll();
        } catch (Exception e) {
            throw new ResourceException("Error performing ping on Lucene index.", e);
        }
    }

    @Override
    public void close() throws ResourceException {
    }

}
