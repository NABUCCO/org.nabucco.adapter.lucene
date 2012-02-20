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
package org.nabucco.adapter.lucene.facade.service.index;

import org.nabucco.adapter.lucene.facade.exception.LuceneException;
import org.nabucco.adapter.lucene.facade.message.LuceneGeoIndexRq;
import org.nabucco.adapter.lucene.facade.message.LuceneIndexRq;
import org.nabucco.framework.base.facade.message.EmptyServiceMessage;
import org.nabucco.framework.base.facade.message.ServiceRequest;
import org.nabucco.framework.base.facade.message.ServiceResponse;
import org.nabucco.framework.base.facade.service.Service;

/**
 * IndexLucene<p/>Service for indexing the fulltext search<p/>
 *
 * @version 1.0
 * @author Nicolas Moser, PRODYNA AG, 2011-05-05
 */
public interface IndexLucene extends Service {

    /**
     * Add the document to the given index.
     *
     * @param rq the ServiceRequest<LuceneIndexRq>.
     * @return the ServiceResponse<EmptyServiceMessage>.
     * @throws LuceneException
     */
    ServiceResponse<EmptyServiceMessage> index(ServiceRequest<LuceneIndexRq> rq) throws LuceneException;

    /**
     * Add the document with geo information to the given index.
     *
     * @param rq the ServiceRequest<LuceneGeoIndexRq>.
     * @return the ServiceResponse<EmptyServiceMessage>.
     * @throws LuceneException
     */
    ServiceResponse<EmptyServiceMessage> indexGeo(ServiceRequest<LuceneGeoIndexRq> rq) throws LuceneException;
}
