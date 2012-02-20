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

import java.util.List;

import javax.resource.ResourceException;

import org.nabucco.framework.base.facade.datatype.geo.GeoLocation;
import org.nabucco.framework.base.facade.datatype.search.fulltext.FulltextDocument;
import org.nabucco.framework.base.facade.datatype.search.query.FulltextQuery;

/**
 * SPI for Lucene Search.
 * 
 * @author Frank Ratschinski, PRODYNA AG
 * 
 */
public interface LuceneSearchService {

    /**
     * Pings the lucene index.
     * 
     * @throws ResourceException
     *             when the ping on the index fails
     */
    void ping() throws ResourceException;

    /**
     * Executes the search query on the index.
     * 
     * @param query
     *            the query to execute
     * 
     * @return the found full text documents
     * 
     * @throws ResourceException
     *             when the documents cannot be found
     */
    List<FulltextDocument> search(FulltextQuery query) throws ResourceException;

    /**
     * Executes the search query with geo information on the index.
     * 
     * @param query
     *            the query to execute
     * @param minLocation
     *            the minimum geo location
     * @param maxLocation
     *            the maximum geo location
     * 
     * @return the found full text documents
     * 
     * @throws ResourceException
     *             when the documents cannot be found
     */
    List<FulltextDocument> search(FulltextQuery query, GeoLocation minLocation, GeoLocation maxLocation)
            throws ResourceException;

    /**
     * Close the index connection.
     * 
     * @throws ResourceException
     *             when the connection cannot be closed
     */
    void close() throws ResourceException;

}
