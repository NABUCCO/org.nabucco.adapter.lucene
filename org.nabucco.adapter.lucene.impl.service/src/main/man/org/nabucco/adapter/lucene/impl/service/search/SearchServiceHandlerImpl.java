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
package org.nabucco.adapter.lucene.impl.service.search;

import java.util.List;

import javax.resource.ResourceException;

import org.nabucco.adapter.lucene.facade.exception.LuceneException;
import org.nabucco.adapter.lucene.facade.message.LuceneSearchRq;
import org.nabucco.adapter.lucene.facade.message.LuceneSearchRs;
import org.nabucco.adapter.lucene.impl.connector.api.LuceneConnection;
import org.nabucco.framework.base.facade.datatype.search.fulltext.FulltextDocument;

/**
 * SearchServiceHandlerImpl
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class SearchServiceHandlerImpl extends SearchServiceHandler {

    private static final long serialVersionUID = 1L;

    @Override
    protected LuceneSearchRs search(LuceneSearchRq rq) throws LuceneException {

        LuceneSearchRs rs = new LuceneSearchRs();

        String index = rq.getQuery().getIndexName().getValue();
        LuceneConnection connection = null;

        try {
            connection = super.getResourceManager().getConnection(index);
            List<FulltextDocument> documents = connection.search(rq.getQuery());

            rs.getDocumentList().addAll(documents);

        } catch (ResourceException re) {
            super.getLogger().error(re, "ResourceException during searching on Lucene Index '", index, "'.");
            throw new LuceneException("ResourceException during indexing to Lucene Index '" + index + "'.", re);
        } catch (Exception re) {
            super.getLogger().error(re, "Error searching fulltext document on index '", index, "'.");
            throw new LuceneException("Error searching fulltext document on index '" + index + "'.", re);

        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    super.getLogger().error("Cannot close Lucene Connection.");
                }
            }
        }

        return rs;
    }

}
