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

import java.io.Serializable;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionSpec;
import javax.resource.cci.RecordFactory;
import javax.resource.cci.ResourceAdapterMetaData;
import javax.resource.spi.ConnectionManager;

import org.nabucco.adapter.lucene.impl.connector.api.LuceneConnection;
import org.nabucco.adapter.lucene.impl.connector.api.LuceneConnectionFactory;
import org.nabucco.adapter.lucene.impl.connector.spec.ConnectionSpecImpl;

/**
 * JiraConnectionFactoryImpl
 * 
 * @author Frank Ratschinski, PRODYNA AG
 */
public class LuceneConnectionFactoryImpl implements LuceneConnectionFactory, Serializable, Referenceable {

    private static final long serialVersionUID = 1L;

    private ManagedConnectionFactoryImpl mcf;

    private ConnectionManager cm;

    private Reference reference;

    /**
     * Creates a new {@link LuceneConnectionFactoryImpl} instance.
     * 
     * @param mcf
     *            the managed connection factory
     * @param cm
     *            the connection manager
     */
    public LuceneConnectionFactoryImpl(ManagedConnectionFactoryImpl mcf, ConnectionManager cm) {
        this.mcf = mcf;

        if (cm == null) {
            // non-managed scenario with own ConnectionManagerImpl
            this.cm = new ConnectionManagerImpl();
        } else {
            // managed scenario with ConnectionManagerImpl from AS
            this.cm = cm;
        }
    }

    @Override
    public void setReference(Reference ref) {
        this.reference = ref;
    }

    @Override
    public Reference getReference() throws NamingException {
        return this.reference;
    }

    @Override
    public Connection getConnection() throws ResourceException {

        LuceneConnection facade = null;
        ConnectionSpecImpl conSpec = new ConnectionSpecImpl();
        conSpec.setIndexPath(mcf.getIndexPath());
        conSpec.setLuceneVersion(mcf.getLuceneVersion());
        conSpec.setIndex(mcf.getIndex());

        facade = (LuceneConnection) getConnection(conSpec);
        return facade;
    }

    @Override
    public Connection getConnection(ConnectionSpec properties) throws ResourceException {

        LuceneConnection facade = null;
        ConnectionSpecImpl conSpec = (ConnectionSpecImpl) properties;
        ConnectionRequestInfoImpl info = new ConnectionRequestInfoImpl();

        info.setIndexPath(conSpec.getIndexPath());
        info.setLuceneVersion(conSpec.getLuceneVersion());
        info.setIndex(conSpec.getIndex());

        facade = (LuceneConnection) cm.allocateConnection(mcf, info);
        return facade;
    }

    @Override
    public ResourceAdapterMetaData getMetaData() throws ResourceException {
        throw new NotSupportedException("getMetaData not support");
    }

    @Override
    public RecordFactory getRecordFactory() throws ResourceException {
        throw new NotSupportedException("getMetaData not support");
    }

}
