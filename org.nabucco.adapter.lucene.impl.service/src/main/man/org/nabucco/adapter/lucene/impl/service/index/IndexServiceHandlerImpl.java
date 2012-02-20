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
package org.nabucco.adapter.lucene.impl.service.index;

import javax.resource.ResourceException;

import org.nabucco.adapter.lucene.facade.exception.LuceneException;
import org.nabucco.adapter.lucene.facade.message.LuceneIndexRq;
import org.nabucco.adapter.lucene.impl.connector.api.LuceneConnection;
import org.nabucco.framework.base.facade.message.EmptyServiceMessage;

/**
 * IndexServiceHandlerImpl
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class IndexServiceHandlerImpl extends IndexServiceHandler {

    private static final long serialVersionUID = 1L;

    @Override
    protected EmptyServiceMessage index(LuceneIndexRq msg) throws LuceneException {

        String index = msg.getIndexName().getValue();
        super.getLogger().info("Indexing document in lucene adapter for index [", index, "].");

        LuceneConnection connection = null;

        try {
            connection = super.getResourceManager().getConnection(index);
            connection.indexDocument(msg.getDocument());

        } catch (ResourceException re) {
            super.getLogger().error(re, "ResourceException during indexing to Lucene Index '", index, "'.");
            throw new LuceneException("ResourceException during indexing to Lucene Index '" + index + "'.", re);
        } catch (Exception e) {
            super.getLogger().error(e, "Error indexing fulltext document on index '", index, "'.");
            throw new LuceneException("Error indexing fulltext document on index '" + index + "'.", e);

        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    super.getLogger().error("Cannot close Lucene Connection.");
                }
            }
        }

        return new EmptyServiceMessage();
    }

}
