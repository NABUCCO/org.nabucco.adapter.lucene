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

import org.nabucco.framework.base.facade.datatype.geo.GeoLocation;
import org.nabucco.framework.base.facade.datatype.search.fulltext.FulltextDocument;

/**
 * SPI for Lucene Indexing.
 * 
 * @author Frank Ratschinski, PRODYNA AG
 * 
 */
public interface LuceneIndexService {

    /**
     * Add a full text document to the index.
     * 
     * @param document
     *            the document to add
     * @param index
     *            the index to add the document
     * 
     * @throws IOException
     *             when the document cannot be added
     */
    void indexDocument(FulltextDocument document, String index) throws IOException;

    /**
     * Add a full text document with geo information to the index.
     * 
     * @param document
     *            the document to add
     * @param location
     *            the geo location
     * @param index
     *            the index to add the document
     * 
     * @throws IOException
     *             when the document cannot be added
     */
    void indexDocument(FulltextDocument document, GeoLocation location, String index) throws IOException ;

    /**
     * Closing the index writer.
     */
    void close();

}
