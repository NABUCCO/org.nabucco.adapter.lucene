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
package org.nabucco.adapter.lucene.facade.adapter;

import org.nabucco.adapter.lucene.facade.service.index.IndexLucene;
import org.nabucco.adapter.lucene.facade.service.search.SearchLucene;
import org.nabucco.framework.base.facade.exception.service.ServiceException;

/**
 * LuceneAdapterLocal<p/>Adapter for accesing the Lucene indexing/search resource.<p/>
 *
 * @version 1.0
 * @author Nicolas Moser, PRODYNA AG, 2011-05-10
 */
public interface LuceneAdapterLocal extends LuceneAdapter {

    /**
     * Getter for the IndexLuceneLocal.
     *
     * @return the IndexLucene.
     * @throws ServiceException
     */
    IndexLucene getIndexLuceneLocal() throws ServiceException;

    /**
     * Getter for the SearchLuceneLocal.
     *
     * @return the SearchLucene.
     * @throws ServiceException
     */
    SearchLucene getSearchLuceneLocal() throws ServiceException;
}
