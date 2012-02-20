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
package org.nabucco.adapter.lucene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.resource.ResourceException;

import org.nabucco.adapter.lucene.exception.LuceneException;
import org.nabucco.adapter.lucene.impl.service.api.LuceneConnection;
import org.nabucco.adapter.lucene.impl.service.api.LuceneConnectionFactory;
import org.nabucco.framework.base.facade.datatype.extension.ExtensionMap;
import org.nabucco.framework.base.facade.datatype.extension.ExtensionPointMap;
import org.nabucco.framework.base.facade.datatype.extension.ExtensionPointType;
import org.nabucco.framework.base.facade.datatype.extension.ExtensionResolver;
import org.nabucco.framework.base.facade.datatype.geo.GeoLocation;
import org.nabucco.framework.base.facade.datatype.logger.NabuccoLogger;
import org.nabucco.framework.base.facade.datatype.logger.NabuccoLoggingFactory;
import org.nabucco.framework.base.facade.datatype.search.fulltext.FulltextDocument;
import org.nabucco.framework.base.facade.datatype.search.query.FulltextQuery;

/**
 * Implementation of the Lucene Facade.
 * 
 * @author Frank Ratschinski, PRODYNA AG
 * @author Nicolas Moser, PRODYNA AG
 */
public class LuceneFacadeImpl implements LuceneFacade {

    private static final long serialVersionUID = 1L;

    private static NabuccoLogger logger = NabuccoLoggingFactory.getInstance().getLogger(LuceneFacadeImpl.class);

    // TODO maybe using soft references to avoid hard references to connection factories...
    private Map<String, LuceneConnectionFactory> connectionFactoryMap = new HashMap<String, LuceneConnectionFactory>();

    private ExtensionPointMap extensionPointMap = new ExtensionPointMap();

    @Resource
    private SessionContext ctx;

    /**
     * Called after adapter construction.
     */
    protected void postConstruct() {
        try {
            ExtensionResolver resolver = new ExtensionResolver();
            ExtensionMap searchConfig = resolver
                    .resolveExtensions(ExtensionPointType.ORG_NABUCCO_FRAMEWORK_SEARCH_CONFIG);
            ExtensionMap searchIndex = resolver
                    .resolveExtensions(ExtensionPointType.ORG_NABUCCO_FRAMEWORK_SEARCH_INDEX);
            extensionPointMap.addExtensionMap(searchConfig);
            extensionPointMap.addExtensionMap(searchIndex);

        } catch (Exception e) {
            logger.error(e, "Cannot configure LucenceFacde.postConstruct with NABUCCO Extensions.");
        }
    }

    @Override
    public void indexDocument(String index, FulltextDocument document) throws LuceneException {

        logger.info("Indexing document in lucene adapter for index [", index, "].");
        LuceneConnection connection = null;

        try {
            connection = this.getConnection(index);
            connection.indexDocument(document);
        } catch (ResourceException re) {
            throw new LuceneException("ResourceException occured while indexing to LuceneAdapter.", re);
        } catch (Exception e) {
            throw new LuceneException("Exception occured while getting a connection to LuceneAdapter.", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    logger.error("Cannot close Lucene Connection.");
                }
            }
        }
    }

    @Override
    public void indexDocument(String index, FulltextDocument document, GeoLocation location) throws LuceneException {

        logger.info("Indexing document in lucene adapter for index [", index, "] and geo location [",
                String.valueOf(location.getLatitude()), "/", String.valueOf(location.getLongitude()), "].");

        LuceneConnection connection = null;

        try {
            connection = this.getConnection(index);
            connection.indexDocument(document, location);
        } catch (ResourceException re) {
            throw new LuceneException("ResourceException occured while indexing to LuceneAdapter.", re);
        } catch (Exception e) {
            throw new LuceneException("Exception occured while getting a connection to LuceneAdapter.", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    logger.error("Cannot close Lucene Connection.");
                }
            }
        }
    }

    @Override
    public List<FulltextDocument> search(FulltextQuery query) throws LuceneException {
        String index = query.getIndexName().getValue();

        logger.info("Searching documents in lucene adapter for index [" + index + "].");
        LuceneConnection connection = null;

        try {
            connection = this.getConnection(index);
            return connection.search(query);
        } catch (ResourceException re) {
            throw new LuceneException("ResourceException occured while searching from LuceneAdapter.", re);
        } catch (Exception e) {
            throw new LuceneException("Exception occured while getting a connection to LuceneAdapter.", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    logger.error("Cannot close Lucene Connection.");
                }
            }
        }
    }

    @Override
    public List<FulltextDocument> search(FulltextQuery query, GeoLocation minLocation, GeoLocation maxLocation)
            throws LuceneException {

        String index = query.getIndexName().getValue();

        logger.info("Searching documents in lucene adapter for index [" + index + "] and geo location [",
                String.valueOf(minLocation.getLatitude()), "/", String.valueOf(minLocation.getLongitude()), "].");

        LuceneConnection connection = null;

        try {
            connection = this.getConnection(index);
            return connection.search(query, minLocation, maxLocation);
        } catch (ResourceException re) {
            throw new LuceneException("ResourceException occured while getting searching from LuceneAdapter.", re);
        } catch (Exception e) {
            throw new LuceneException("Exception occured while getting a connection to LuceneAdapter.", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    logger.error("Cannot close Lucene Connection.");
                }
            }
        }
    }

    /**
     * Retrieves the Lucene connection.
     * 
     * @param index
     *            the index of the connection
     * 
     * @return the connection
     * 
     * @throws LuceneException
     */
    private LuceneConnection getConnection(String index) throws LuceneException {
        try {
            this.initConnectionFactory(index);
            return (LuceneConnection) this.connectionFactoryMap.get(index).getConnection();
        } catch (ResourceException ex) {
            try {
                this.reInitConnectionFactory(index);
                logger.info("Re-initializing connection.");
                return (LuceneConnection) this.connectionFactoryMap.get(index).getConnection();
            } catch (Exception e) {
                logger.error(ex, "Could not lookup LuceneConnectionFactory.");
                throw new LuceneException(ex.getMessage());
            }
        }
    }

    /**
     * Initialize the Lucene connection factory.
     * 
     * @param index
     *            the index
     */
    private void initConnectionFactory(String index) {
        try {
            if (!this.connectionFactoryMap.containsKey(index)) {
                LuceneConnectionFactory connectionFactory = (LuceneConnectionFactory) this.ctx.lookup("java:" + index);
                this.connectionFactoryMap.put(index, connectionFactory);
            }
        } catch (Exception e) {
            logger.error(e, "Could not lookup LuceneConnectionFactory.");
        }
    }

    /**
     * Re-Initialize the Lucene connection factory, when it has already been initialized.
     * 
     * @param index
     *            the index
     */
    private void reInitConnectionFactory(String index) {
        try {
            LuceneConnectionFactory connectionFactory = (LuceneConnectionFactory) this.ctx.lookup("java:" + index);
            this.connectionFactoryMap.put(index, connectionFactory);
        } catch (Exception e) {
            logger.error(e, "Could not lookup LuceneConnectionFactory.");
        }
    }
}
