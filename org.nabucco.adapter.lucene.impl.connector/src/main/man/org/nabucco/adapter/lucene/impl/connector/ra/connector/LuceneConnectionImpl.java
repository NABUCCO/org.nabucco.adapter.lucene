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
package org.nabucco.adapter.lucene.impl.connector.ra.connector;

import java.util.List;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.ConnectionMetaData;
import javax.resource.cci.Interaction;
import javax.resource.cci.LocalTransaction;
import javax.resource.cci.ResultSetInfo;

import org.nabucco.adapter.lucene.impl.connector.api.LuceneConnection;
import org.nabucco.framework.base.facade.datatype.geo.GeoLocation;
import org.nabucco.framework.base.facade.datatype.search.fulltext.FulltextDocument;
import org.nabucco.framework.base.facade.datatype.search.query.FulltextQuery;

/**
 * LuceneConnectionImpl
 * 
 * @author Frank Ratschinski, PRODYNA AG
 */
public class LuceneConnectionImpl implements LuceneConnection {

    private ManagedConnectionImpl mc;

    /**
     * Creates a new {@link LuceneConnectionImpl} instance.
     */
    LuceneConnectionImpl() {
    }

    /**
     * Setter for the managed connection.
     * 
     * @param managedConnection
     *            the managed connection to set
     */
    public void setManagedConnection(ManagedConnectionImpl managedConnection) {
        this.mc = managedConnection;
    }

    @Override
    public void ping() throws ResourceException {
        this.mc.ping();
    }

    @Override
    public void indexDocument(FulltextDocument document) throws ResourceException {
        this.mc.indexDocument(document);
    }

    @Override
    public void indexDocument(FulltextDocument document, GeoLocation location) throws ResourceException {
        this.mc.indexDocument(document, location);
    }

    @Override
    public List<FulltextDocument> search(FulltextQuery query) throws ResourceException {
        return this.mc.search(query);
    }

    @Override
    public List<FulltextDocument> search(FulltextQuery query, GeoLocation minLocation, GeoLocation maxLocation)
            throws ResourceException {
        return this.mc.search(query, minLocation, maxLocation);
    }

    @Override
    public void close() throws ResourceException {

        if (this.mc != null) {
            mc.closeHandle(this);
        }
    }

    @Override
    public Interaction createInteraction() throws ResourceException {
        throw new NotSupportedException("createInteraction not support");
    }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        throw new NotSupportedException("getLocalTransaction not support");
    }

    @Override
    public ConnectionMetaData getMetaData() throws ResourceException {
        throw new NotSupportedException("getMetaData not support");
    }

    @Override
    public ResultSetInfo getResultSetInfo() throws ResourceException {
        throw new NotSupportedException("getResultSetInfo not support");
    }

}
