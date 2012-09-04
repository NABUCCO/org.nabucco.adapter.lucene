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

import java.io.IOException;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.nabucco.adapter.lucene.impl.connector.ra.spi.config.SearchConfigMapping;
import org.nabucco.adapter.lucene.impl.connector.ra.spi.util.LuceneAccentConverter;
import org.nabucco.framework.base.facade.datatype.BasetypeType;
import org.nabucco.framework.base.facade.datatype.Flag;
import org.nabucco.framework.base.facade.datatype.extension.ExtensionMap;
import org.nabucco.framework.base.facade.datatype.extension.ExtensionPointType;
import org.nabucco.framework.base.facade.datatype.extension.ExtensionResolver;
import org.nabucco.framework.base.facade.datatype.extension.schema.search.SearchFieldExtension;
import org.nabucco.framework.base.facade.datatype.extension.schema.search.SearchIndexExtension;
import org.nabucco.framework.base.facade.datatype.geo.GeoLocation;
import org.nabucco.framework.base.facade.datatype.logger.NabuccoLogger;
import org.nabucco.framework.base.facade.datatype.logger.NabuccoLoggingFactory;
import org.nabucco.framework.base.facade.datatype.search.fulltext.FulltextDocument;
import org.nabucco.framework.base.facade.datatype.search.fulltext.FulltextField;

/**
 * Implementation of Lucence index service.
 * 
 * @author Frank Ratschinski, PRODNYA AG
 * @author Nicolas Moser, PRODNYA AG
 */
public class LuceneIndexServiceImpl implements LuceneIndexService {

    private static NabuccoLogger logger = NabuccoLoggingFactory.getInstance().getLogger(LuceneIndexServiceImpl.class);

    private Directory directory;

    private IndexWriter writer;

    private SearchConfigMapping mapping;

    /**
     * Creates a new {@link LuceneIndexServiceImpl} instance.
     * 
     * @param index
     *            the index
     * @param indexPath
     *            the index path
     * @param luceneVersion
     *            the lucene version
     */
    public LuceneIndexServiceImpl(String index, String indexPath, String luceneVersion) {

        try {
            ExtensionResolver er = new ExtensionResolver();
            ExtensionMap searchIndexConfigMap = er
                    .resolveExtensions(ExtensionPointType.ORG_NABUCCO_FRAMEWORK_SEARCH_INDEX);
            this.mapping = new SearchConfigMapping(searchIndexConfigMap);

            this.directory = DirectoryMap.getInstance().getDirectory(indexPath, index);

        } catch (Exception e) {
            logger.error(e, "Cannot create IndexWriter");
        }
    }

    @Override
    public void indexDocument(FulltextDocument document, String index) throws IOException {
        this.indexDocument(document, null, index);
    }

    @Override
    public void indexDocument(FulltextDocument document, GeoLocation location, String index) throws IOException {

        try {
            this.writer = new IndexWriter(this.directory, new SimpleAnalyzer(), MaxFieldLength.UNLIMITED);
            this.deletePreviousIndexedDocument(document, index, this.writer);

            // Skip if the document is marked as deleted!
            Flag deleted = document.getDeleted();
            if (deleted != null && deleted.getValue() != null && deleted.getValue()) {
                return;
            }

            Document luceneDocument = new Document();

            this.addGeoLocation(location, luceneDocument);

            for (FulltextField field : document.getFieldList()) {

                if (field.getFieldName() == null || field.getFieldName().getValue() == null) {
                    logger.warning("Cannot index field with name [null].");
                    continue;
                }

                if (field.getFieldValue() == null || field.getFieldValue().getValue() == null) {
                    logger.warning("Cannot index field with value [null].");
                    continue;
                }

                String fieldName = field.getFieldName().getValue();
                BasetypeType fieldType = field.getFieldType();
                String fieldValue = field.getFieldValue().getValue();

                SearchFieldExtension fieldConfig = this.mapping.getFieldExtension(index, fieldName);

                if (fieldConfig == null) {
                    this.indexAsDefault(index, luceneDocument, fieldName, fieldValue);
                } else {

                    Boolean isSearchField = fieldConfig.getSearchField().getValue().getValue();

                    this.index(luceneDocument, fieldType, fieldName, fieldValue);

                    if (isSearchField) {
                        String searchName = fieldName + LuceneConstants.SEARCH_FIELD_SUFFIX;
                        String searchValue = LuceneAccentConverter.removeAccents(fieldValue).toLowerCase();
                        this.index(luceneDocument, BasetypeType.STRING, searchName, searchValue);
                    }
                }

            }

            this.writer.addDocument(luceneDocument);

        } finally {
            this.writer.optimize();
            this.writer.commit();
            this.writer.close();
        }
    }

    /**
     * Add geo information to the lucene document.
     * 
     * @param location
     *            the geo location
     * @param luceneDocument
     *            the lucene document
     */
    private void addGeoLocation(GeoLocation location, Document luceneDocument) {
        if (location == null) {
            return;
        }

        if (location.getLatitude() != null && location.getLatitude().getValue() != null) {
            NumericField field = new NumericField("latitude", Field.Store.YES, true);
            field.setDoubleValue(location.getLatitude().getValue());
            luceneDocument.add(field);
        }

        if (location.getLongitude() != null && location.getLongitude().getValue() != null) {
            NumericField field = new NumericField("longitude", Field.Store.YES, true);
            field.setDoubleValue(location.getLongitude().getValue());
            luceneDocument.add(field);
        }
    }

    /**
     * Delete previous indexed documents.
     * 
     * @param document
     *            the document
     * @param index
     *            the index
     * @param writer
     *            the index writer
     * 
     * @throws IOException
     *             when the documents cannot be deleted
     */
    private void deletePreviousIndexedDocument(FulltextDocument document, String index, IndexWriter writer)
            throws IOException {

        SearchIndexExtension extension = this.mapping.getIndexExtension(index);

        if (extension == null || extension.getKey() == null || extension.getKey().getValue() == null) {
            logger.warning("No search index extension configured.");
            return;
        }

        String keyField = extension.getKey().getValue().getValue();
        if (logger.isDebugEnabled()) {
            logger.info("Trying to delete existing index entry for key field [", keyField, "].");
        }

        for (FulltextField field : document.getFieldList()) {

            if (field.getFieldName().getValue().equals(keyField)) {
                Term term;
                if (field.getFieldValue() == null) {
                    term = new Term(keyField, "");
                } else {
                    term = new Term(keyField, field.getFieldValue().getValue());
                }
                writer.deleteDocuments(term);

                if (logger.isDebugEnabled()) {
                    logger.info("Deleting existing index entry [", field.getFieldValue().getValue(), "].");
                }
            }
        }

        writer.commit();
    }

    /**
     * Index the field with a default configuration.
     * 
     * @param index
     *            the index
     * @param doc
     *            the document to add the field
     * @param name
     *            the field name
     * @param value
     *            the field value
     */
    private void indexAsDefault(String index, Document doc, String name, String value) {
        logger.warning("No field configuration found for index [", index, "] field [", name,
                "] - using default indexing.");

        Field field = new Field(name, value.toLowerCase(), Field.Store.YES, Field.Index.NOT_ANALYZED);
        doc.add(field);
    }

    /**
     * Index the field entry as normal text.
     * 
     * @param doc
     *            the document to add the field
     * @param fieldType
     *            the field type
     * @param name
     *            the field name
     * @param value
     *            the field value
     */
    private void index(Document doc, BasetypeType fieldType, String name, String value) {
        if (logger.isDebugEnabled()) {
            logger.debug("Indexig Field [", name, "] value [", value, "].");
        }

        Fieldable field;

        try {

            switch (fieldType) {

            case INTEGER: {
                NumericField numericField = new NumericField(name, Field.Store.YES, true);
                numericField.setIntValue(Integer.parseInt(value));
                field = numericField;
                break;
            }

            case LONG: {
                NumericField numericField = new NumericField(name, Field.Store.YES, true);
                numericField.setLongValue(Long.parseLong(value));
                field = numericField;
                break;
            }

            case FLOAT: {
                NumericField numericField = new NumericField(name, Field.Store.YES, true);
                numericField.setFloatValue(Float.parseFloat(value));
                field = numericField;
                break;
            }

            case DOUBLE: {
                NumericField numericField = new NumericField(name, Field.Store.YES, true);
                numericField.setDoubleValue(Double.parseDouble(value));
                field = numericField;
                break;
            }

            default: {
                field = new Field(name, value, Field.Store.YES, Field.Index.NOT_ANALYZED);
                break;
            }

            }

        } catch (NumberFormatException nfe) {
            logger.error(nfe, "Cannot parse numeric value of field '", name, "' [", value, "].");
            throw nfe;
        }

        doc.add(field);
    }

    @Override
    public void close() {
        logger.info("Closing IndexWriter and Directory.");
        try {
            writer.commit();
            writer.close();
            writer = null;

        } catch (IOException e) {
            logger.error(e, "Cannot close writer and/or directory.");
        }

    }

}
