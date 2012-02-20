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
package org.nabucco.adapter.lucene.facade.service.search;

import org.nabucco.adapter.lucene.facade.exception.LuceneException;
import org.nabucco.adapter.lucene.facade.message.LuceneGeoSearchRq;
import org.nabucco.adapter.lucene.facade.message.LuceneSearchRq;
import org.nabucco.adapter.lucene.facade.message.LuceneSearchRs;
import org.nabucco.framework.base.facade.message.ServiceRequest;
import org.nabucco.framework.base.facade.message.ServiceResponse;
import org.nabucco.framework.base.facade.service.Service;

/**
 * SearchLucene<p/>Service for indexing the fulltext search<p/>
 *
 * @version 1.0
 * @author Nicolas Moser, PRODYNA AG, 2011-05-05
 */
public interface SearchLucene extends Service {

    /**
     * Search the index for the given document.
     *
     * @param rq the ServiceRequest<LuceneSearchRq>.
     * @return the ServiceResponse<LuceneSearchRs>.
     * @throws LuceneException
     */
    ServiceResponse<LuceneSearchRs> search(ServiceRequest<LuceneSearchRq> rq) throws LuceneException;

    /**
     * Search the index for the given document with geo information.
     *
     * @param rq the ServiceRequest<LuceneGeoSearchRq>.
     * @return the ServiceResponse<LuceneSearchRs>.
     * @throws LuceneException
     */
    ServiceResponse<LuceneSearchRs> searchGeo(ServiceRequest<LuceneGeoSearchRq> rq) throws LuceneException;
}
