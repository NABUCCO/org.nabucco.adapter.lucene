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

import org.nabucco.adapter.lucene.facade.adapter.LuceneAdapter;
import org.nabucco.adapter.lucene.facade.service.index.IndexLucene;
import org.nabucco.adapter.lucene.facade.service.search.SearchLucene;
import org.nabucco.framework.base.facade.exception.service.ServiceException;
import org.nabucco.framework.base.facade.message.ping.PingRequest;
import org.nabucco.framework.base.facade.message.ping.PingResponse;

/**
 * LuceneAdapterLocalProxy<p/>Adapter for accesing the Lucene indexing/search resource.<p/>
 *
 * @version 1.0
 * @author Nicolas Moser, PRODYNA AG, 2011-05-10
 */
public class LuceneAdapterLocalProxy implements LuceneAdapter {

    private static final long serialVersionUID = 1L;

    private final LuceneAdapterLocal delegate;

    /**
     * Constructs a new LuceneAdapterLocalProxy instance.
     *
     * @param delegate the LuceneAdapterLocal.
     */
    public LuceneAdapterLocalProxy(LuceneAdapterLocal delegate) {
        super();
        if ((delegate == null)) {
            throw new IllegalArgumentException("Cannot create local proxy for adapter [null].");
        }
        this.delegate = delegate;
    }

    @Override
    public PingResponse ping(PingRequest request) {
        return this.delegate.ping(request);
    }

    @Override
    public String getId() {
        return this.delegate.getId();
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    @Override
    public String getJndiName() {
        return this.delegate.getJndiName();
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }

    @Override
    public IndexLucene getIndexLucene() throws ServiceException {
        return this.delegate.getIndexLuceneLocal();
    }

    @Override
    public SearchLucene getSearchLucene() throws ServiceException {
        return this.delegate.getSearchLuceneLocal();
    }
}
