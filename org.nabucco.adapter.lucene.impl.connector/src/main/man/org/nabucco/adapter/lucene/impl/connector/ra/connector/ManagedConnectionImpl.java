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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

import org.nabucco.adapter.lucene.impl.connector.api.LuceneConnection;
import org.nabucco.adapter.lucene.impl.connector.ra.spi.LuceneIndexService;
import org.nabucco.adapter.lucene.impl.connector.ra.spi.LuceneIndexServiceImpl;
import org.nabucco.adapter.lucene.impl.connector.ra.spi.LuceneSearchService;
import org.nabucco.adapter.lucene.impl.connector.ra.spi.LuceneSearchServiceImpl;
import org.nabucco.framework.base.facade.datatype.geo.GeoLocation;
import org.nabucco.framework.base.facade.datatype.logger.NabuccoLogger;
import org.nabucco.framework.base.facade.datatype.logger.NabuccoLoggingFactory;
import org.nabucco.framework.base.facade.datatype.search.fulltext.FulltextDocument;
import org.nabucco.framework.base.facade.datatype.search.query.FulltextQuery;

/**
 * ManagedConnectionImpl
 * 
 * @author Frank Ratschinski, PRODYNA AG
 */
public class ManagedConnectionImpl implements ManagedConnection {

    private static NabuccoLogger logger = NabuccoLoggingFactory.getInstance().getLogger(ManagedConnectionImpl.class);

    private PrintWriter logWriter;

    private LuceneIndexService indexService;

    private LuceneSearchService searchService;

    private LuceneConnectionImpl associatedHandle;

    private ConnectionRequestInfoImpl info;

    private List<ConnectionEventListener> listener;

    /**
     * Creates a new {@link ManagedConnectionImpl} instance.
     * 
     * @param mcf
     *            the managed connection factory
     * @param subject
     *            the security subject
     * @param cxRequestInfo
     *            the connection request info
     * 
     * @throws ResourceException
     *             when the connection cannot be established
     */
    ManagedConnectionImpl(ManagedConnectionFactoryImpl mcf, Subject subject, ConnectionRequestInfo cxRequestInfo)
            throws ResourceException {
        this.listener = new ArrayList<ConnectionEventListener>();

        try {
            this.info = (ConnectionRequestInfoImpl) cxRequestInfo;

            this.initConnection();
        } catch (Exception e) {
            throw new ResourceException(e);
        }
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        this.listener.add(listener);
    }

    @Override
    public void associateConnection(Object connection) throws ResourceException {
        disassociateConnection();

        if (!(connection instanceof LuceneConnectionImpl)) {
            throw new ResourceException("Connection is not a LuceneConnectionImpl");
        }
        LuceneConnectionImpl connectionHandle = (LuceneConnectionImpl) connection;
        connectionHandle.setManagedConnection(this);
        this.associatedHandle = connectionHandle;
    }

    private void disassociateConnection() {

        if (this.associatedHandle != null) {
            this.associatedHandle.setManagedConnection(null);
            this.associatedHandle = null;
        }
    }

    /**
     * This method resets its client specific state and prepares the connection to be put back in to
     * a connection pool. The cleanup method should not cause resource adapter to close the physical
     * pipe and reclaim system resources associated with the physical connection.
     */
    @Override
    public void cleanup() throws ResourceException {
        disassociateConnection();
    }

    /**
     * Destroys the physical connection to the underlying resource manager.
     */
    @Override
    public void destroy() throws ResourceException {
        logger.info("Destroying LuceneManagedConectionImpl");
        disassociateConnection();
        if (this.indexService != null) {
            this.indexService.close();
            this.indexService = null;
        }
        if (this.searchService != null) {
            this.searchService.close();
            this.searchService = null;
        }
    }

    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo info) throws ResourceException {

        // create new LuceneConnection and associate it with this
        // ManagedConnection
        LuceneConnection connection = new LuceneConnectionImpl();
        associateConnection(connection);
        return connection;
    }

    protected void closeHandle(LuceneConnection connection) {
        disassociateConnection();

        for (ConnectionEventListener listener : this.listener) {
            ConnectionEvent event = new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED);
            event.setConnectionHandle(connection);
            listener.connectionClosed(event);
        }
    }

    /*
     * Transaction-Management --------------------------------------------
     */

    /*
     * If not supported (e.g. <transaction-support>NoTransaction</transaction-support>), throw a
     * ResourceException.
     */
    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        throw new NotSupportedException("No transaction support");
    }

    /*
     * If not supported (e.g. <transaction-support>LocalTransaction</transaction-support>), throw a
     * ResourceException.
     */
    @Override
    public XAResource getXAResource() throws ResourceException {
        throw new NotSupportedException("XATransactions are not supported");
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return this.logWriter;
    }

    @Override
    public ManagedConnectionMetaData getMetaData() throws ResourceException {
        return null;
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        this.listener.remove(listener);
    }

    @Override
    public void setLogWriter(PrintWriter writer) throws ResourceException {
        this.logWriter = writer;
    }

    protected boolean matches(final Subject subject, final ConnectionRequestInfo info) {

        if (info == null || !(info instanceof ConnectionRequestInfoImpl)) {
            return false;
        }
        ConnectionRequestInfoImpl cri = (ConnectionRequestInfoImpl) info;

        if (!this.info.getIndexPath().equals(cri.getIndexPath())) {
            return false;
        }

        if (!this.info.getLuceneVersion().equals(cri.getLuceneVersion())) {
            return false;
        }

        if (!this.info.getIndex().equals(cri.getIndex())) {
            return false;
        }
        return true;
    }

    /**
     * Getter for the connection request info.
     * 
     * @return the request info
     */
    public ConnectionRequestInfoImpl getInfo() {
        return this.info;
    }

    /**
     * Initialize the connection
     * 
     * @throws ResourceException
     *             when the connection cannot be established
     */
    private void initConnection() throws ResourceException {

        logger.info("Initializing LuceneManagedConectionImpl.");
        String indexPath = this.info.getIndexPath();
        String luceneVersion = this.info.getLuceneVersion();
        String index = this.info.getIndex();

        this.indexService = new LuceneIndexServiceImpl(index, indexPath, luceneVersion);
        this.searchService = new LuceneSearchServiceImpl(index, indexPath);
    }

    /**
     * Sends a ping to the Lucene Directory.
     * 
     * @throws ResourceException
     *             when the ping fails
     */
    public void ping() throws ResourceException {

        String indexPath = this.getInfo().getIndexPath();
        String luceneVersion = this.getInfo().getLuceneVersion();

        try {
            this.searchService.ping();
        } catch (Exception e) {
            logger.error(e, "Cannot ping on indexPath [", indexPath, "] luceneVersion [", luceneVersion, "].");
            throw new ResourceException("Cannot ping on indexPath ["
                    + indexPath + "] luceneVersion [" + luceneVersion + "].", e);
        }
    }

    /**
     * Adds the full text document to the index.
     * 
     * @param document
     *            the document to add
     * 
     * @throws ResourceException
     *             when the indexing fails
     */
    public void indexDocument(FulltextDocument document) throws ResourceException {

        String indexPath = this.getInfo().getIndexPath();
        String luceneVersion = this.getInfo().getLuceneVersion();
        String index = this.getInfo().getIndex();

        try {
            logger.info("Indexing on indexPath [" + indexPath + "] luceneVersion [" + luceneVersion + "].");

            this.indexService.indexDocument(document, index);
        } catch (Exception e) {
            logger.error(e, "Cannot index document on indexPath [", indexPath, "] luceneVersion [", luceneVersion, "].");
            throw new ResourceException("Cannot index document on indexPath ["
                    + indexPath + "] luceneVersion [" + luceneVersion + "].", e);
        }

    }

    /**
     * Adds the full text document to the index.
     * 
     * @param document
     *            the document to add
     * @param location
     *            the geo location
     * 
     * @throws ResourceException
     *             when the indexing fails
     */
    public void indexDocument(FulltextDocument document, GeoLocation location) throws ResourceException {

        String indexPath = this.getInfo().getIndexPath();
        String luceneVersion = this.getInfo().getLuceneVersion();
        String index = this.getInfo().getIndex();

        try {
            logger.info("Indexing on indexPath [" + indexPath + "] luceneVersion [" + luceneVersion + "].");

            this.indexService.indexDocument(document, location, index);
        } catch (Exception e) {
            logger.error(e, "Cannot index document on indexPath [", indexPath, "] luceneVersion [", luceneVersion, "].");
            throw new ResourceException("Cannot index document on indexPath ["
                    + indexPath + "] luceneVersion [" + luceneVersion + "].", e);
        }
    }

    /**
     * Search for documents on the index.
     * 
     * @param query
     *            the search query to execute
     * 
     * @return the found documents
     * 
     * @throws ResourceException
     *             when the search fails
     */
    public List<FulltextDocument> search(FulltextQuery query) throws ResourceException {

        String indexPath = this.getInfo().getIndexPath();
        String luceneVersion = this.getInfo().getLuceneVersion();

        try {
            logger.info("Searching on indexPath [", indexPath, "] luceneVersion [", luceneVersion, "].");

            return this.searchService.search(query);

        } catch (Exception e) {
            logger.error(e, "Cannot search documents on indexPath [", indexPath, "] luceneVersion [", luceneVersion,
                    "].");
            throw new ResourceException("Cannot search documents on indexPath ["
                    + indexPath + "] luceneVersion [" + luceneVersion + "].", e);
        }
    }

    /**
     * Search for documents on the index.
     * 
     * @param query
     *            the search query to execute
     * @param minLocation
     *            the min geo location
     * @param maxLocation
     *            the max geo location
     * 
     * @return the found documents
     * 
     * @throws ResourceException
     *             when the search fails
     */
    public List<FulltextDocument> search(FulltextQuery query, GeoLocation minLocation, GeoLocation maxLocation)
            throws ResourceException {

        String indexPath = this.getInfo().getIndexPath();
        String luceneVersion = this.getInfo().getLuceneVersion();

        try {
            logger.info("Searching on indexPath [", indexPath, "] luceneVersion [", luceneVersion, "].");

            return this.searchService.search(query, minLocation, maxLocation);

        } catch (Exception e) {
            logger.error(e, "Cannot search documents on indexPath [", indexPath, "] luceneVersion [", luceneVersion,
                    "].");
            throw new ResourceException("Cannot search documents on indexPath ["
                    + indexPath + "] luceneVersion [" + luceneVersion + "].", e);
        }
    }
}
