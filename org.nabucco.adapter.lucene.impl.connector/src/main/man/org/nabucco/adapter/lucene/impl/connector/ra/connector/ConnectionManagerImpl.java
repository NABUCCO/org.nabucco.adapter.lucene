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

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;

/**
 * ConnectionManagerImpl
 * 
 * The default ConnectionManager implementation for the non-managed scenario. This provides a hook
 * for a resource adapter to pass a connection request to an application server.
 * 
 * @author Frank Ratschinski, PRODYNA AG
 */
public class ConnectionManagerImpl implements ConnectionManager, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public Object allocateConnection(ManagedConnectionFactory factory, ConnectionRequestInfo cxRequestInfo)
            throws ResourceException {
        ManagedConnection mc = factory.createManagedConnection(null, cxRequestInfo);
        return mc.getConnection(null, cxRequestInfo);
    }

}
