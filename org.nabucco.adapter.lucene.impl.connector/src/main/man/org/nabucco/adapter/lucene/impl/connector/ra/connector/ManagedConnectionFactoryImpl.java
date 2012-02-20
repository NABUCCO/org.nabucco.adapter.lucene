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
import java.util.Iterator;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterAssociation;
import javax.security.auth.Subject;

import org.nabucco.framework.base.facade.datatype.logger.NabuccoLogger;
import org.nabucco.framework.base.facade.datatype.logger.NabuccoLoggingFactory;

/**
 * ManagedConnectionFactoryImpl
 * 
 * @author Frank Ratschinski, PRODYNA AG
 */
public class ManagedConnectionFactoryImpl implements ManagedConnectionFactory, ResourceAdapterAssociation {

    private static final long serialVersionUID = 1L;

    private static NabuccoLogger logger = NabuccoLoggingFactory.getInstance().getLogger(
            ManagedConnectionFactoryImpl.class);

    private ResourceAdapter ra;

    private transient PrintWriter out;

    private String indexPath;

    private String luceneVersion;

    private String index;

    @Override
    public Object createConnectionFactory() throws ResourceException {
        return new LuceneConnectionFactoryImpl(this, null);
    }

    @Override
    public Object createConnectionFactory(ConnectionManager cxManager) throws ResourceException {
        LuceneConnectionFactoryImpl cf = null;
        try {
            cf = new LuceneConnectionFactoryImpl(this, cxManager);
        } catch (Exception e) {
            throw new ResourceException(e.getMessage());
        }
        return cf;
    }

    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo info)
            throws ResourceException {
        return new ManagedConnectionImpl(this, subject, info);
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return this.out;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ManagedConnection matchManagedConnections(Set connections, Subject subject, ConnectionRequestInfo info)
            throws ResourceException {

        for (Iterator<?> iter = connections.iterator(); iter.hasNext();) {
            Object obj = iter.next();

            if (obj instanceof ManagedConnectionImpl) {
                ManagedConnectionImpl managedConnection = (ManagedConnectionImpl) obj;

                if (managedConnection.matches(subject, info)) {
                    return managedConnection;
                }
            }
        }
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter writer) throws ResourceException {
        this.out = writer;
    }

    @Override
    public ResourceAdapter getResourceAdapter() {
        return ra;
    }

    @Override
    public void setResourceAdapter(ResourceAdapter resourceAdapter) throws ResourceException {
        this.ra = resourceAdapter;
    }

    /**
     * Getter for the index path.
     * 
     * @return the index path
     */
    public String getIndexPath() {
        return this.indexPath;
    }

    /**
     * Setter for the index path.
     * 
     * @param indexPath
     *            the index path to set
     */
    public void setIndexPath(String indexPath) {
        this.indexPath = indexPath;
        logger.info("Index configured: " + indexPath);
    }

    /**
     * Getter for the lucene version.
     * 
     * @return the version
     */
    public String getLuceneVersion() {
        return this.luceneVersion;
    }

    /**
     * Setter for the lucene version.
     * 
     * @param luceneVersion
     *            the lucene version to set
     */
    public void setLuceneVersion(String luceneVersion) {
        this.luceneVersion = luceneVersion;
    }

    /**
     * Getter for the index string.
     * 
     * @return the index
     */
    public String getIndex() {
        return this.index;
    }

    /**
     * Setter for the index string.
     * 
     * @param index
     *            the index to set
     */
    public void setIndex(String index) {
        this.index = index;
    }

}
