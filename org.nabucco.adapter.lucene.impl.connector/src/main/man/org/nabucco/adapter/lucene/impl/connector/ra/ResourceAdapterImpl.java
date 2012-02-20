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
package org.nabucco.adapter.lucene.impl.connector.ra;

import java.io.IOException;
import java.io.Serializable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

import org.nabucco.adapter.lucene.impl.connector.ra.spi.DirectoryMap;
import org.nabucco.framework.base.facade.datatype.logger.NabuccoLogger;
import org.nabucco.framework.base.facade.datatype.logger.NabuccoLoggingFactory;

/**
 * ResourceAdapterImpl
 * 
 * @author Frank Ratschinski, PRODYNA AG
 */
public class ResourceAdapterImpl implements ResourceAdapter, Serializable {

    private static final long serialVersionUID = 1L;

    private static NabuccoLogger logger = NabuccoLoggingFactory.getInstance().getLogger(ResourceAdapterImpl.class);

    @SuppressWarnings("unused")
    private transient Context jndiContext = null;

    @Override
    public void endpointActivation(MessageEndpointFactory factory, ActivationSpec spec) throws ResourceException {
    }

    @Override
    public void endpointDeactivation(MessageEndpointFactory factory, ActivationSpec spec) {
    }

    @Override
    public XAResource[] getXAResources(ActivationSpec[] spec) throws ResourceException {
        throw new NotSupportedException("XATransactions are not supported");
    }

    @Override
    public void start(BootstrapContext ctx) throws ResourceAdapterInternalException {
        logger.info("Starting ResourceAdapter ...");

        try {
            this.jndiContext = new InitialContext();
        } catch (NamingException ex) {
            throw new ResourceAdapterInternalException(ex);
        }

        logger.info("ResourceAdapter started");
    }

    @Override
    public void stop() {
        logger.info("Stopping ResourceAdapter ...");

        try {
            DirectoryMap.getInstance().shutDown();
        } catch (IOException e) {
            logger.info(e, "Cannot close directory");
        }

        logger.info("ResourceAdapter stopped");
    }

}
