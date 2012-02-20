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
package org.nabucco.adapter.lucene.impl.connector.api;

import java.util.List;

import javax.resource.ResourceException;

import org.nabucco.framework.base.facade.datatype.geo.GeoLocation;
import org.nabucco.framework.base.facade.datatype.search.fulltext.FulltextDocument;
import org.nabucco.framework.base.facade.datatype.search.query.FulltextQuery;
import org.nabucco.framework.base.impl.service.resource.ResourceConnection;

/**
 * LuceneConnection
 * 
 * @author Frank Ratschinski, PRODYNA AG
 */
public interface LuceneConnection extends ResourceConnection {

    /**
     * Ping the Lucene index.
     * 
     * @throws ResourceException
     *             when the ping fails
     */
    void ping() throws ResourceException;

    /**
     * Adds a full text document to the index.
     * 
     * @param document
     *            the document to index
     * 
     * @throws ResourceException
     *             when the indexing fails
     */
    void indexDocument(FulltextDocument document) throws ResourceException;

    /**
     * Adds a full text document with geo information to the index.
     * 
     * @param document
     *            the document to index
     * @param location
     *            the geo location
     * 
     * @throws ResourceException
     *             when the indexing fails
     */
    void indexDocument(FulltextDocument document, GeoLocation location) throws ResourceException;

    /**
     * Search for a list of full text documents.
     * 
     * @param query
     *            the search query to execute
     * 
     * @return the list of found full text documents
     * 
     * @throws ResourceException
     *             when the search fails
     */
    List<FulltextDocument> search(FulltextQuery query) throws ResourceException;

    /**
     * Search for a list of full text documents in a geo range.
     * 
     * @param query
     *            the search query to execute
     * @param minLocation
     *            the minimum geo location
     * @param maxLocation
     *            the maximum geo location
     * 
     * @return the list of found full text documents
     * 
     * @throws ResourceException
     *             when the search fails
     */
    List<FulltextDocument> search(FulltextQuery query, GeoLocation minLocation, GeoLocation maxLocation)
            throws ResourceException;

}
