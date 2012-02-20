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
package org.nabucco.adapter.lucene.impl.adapter;

import org.nabucco.adapter.lucene.facade.adapter.LuceneAdapterLocal;
import org.nabucco.adapter.lucene.facade.adapter.LuceneAdapterRemote;
import org.nabucco.adapter.lucene.facade.service.index.IndexLucene;
import org.nabucco.adapter.lucene.facade.service.search.SearchLucene;
import org.nabucco.framework.base.facade.component.handler.PostConstructHandler;
import org.nabucco.framework.base.facade.component.handler.PreDestroyHandler;
import org.nabucco.framework.base.facade.exception.service.ServiceException;
import org.nabucco.framework.base.facade.service.injection.InjectionProvider;
import org.nabucco.framework.base.impl.component.adapter.AdapterSupport;

/**
 * LuceneAdapterImpl<p/>Adapter for accesing the Lucene indexing/search resource.<p/>
 *
 * @version 1.0
 * @author Nicolas Moser, PRODYNA AG, 2011-05-10
 */
public class LuceneAdapterImpl extends AdapterSupport implements LuceneAdapterLocal, LuceneAdapterRemote {

    private static final long serialVersionUID = 1L;

    private static final String ID = "LuceneAdapter";

    /** Constructs a new LuceneAdapterImpl instance. */
    public LuceneAdapterImpl() {
        super();
    }

    @Override
    public void postConstruct() {
        super.postConstruct();
        InjectionProvider injector = InjectionProvider.getInstance(ID);
        PostConstructHandler handler = injector.inject(PostConstructHandler.getId());
        if ((handler == null)) {
            if (super.getLogger().isDebugEnabled()) {
                super.getLogger().debug("No post construct handler configured for \'", ID, "\'.");
            }
            return;
        }
        handler.setLocatable(this);
        handler.setLogger(super.getLogger());
        handler.invoke();
    }

    @Override
    public void preDestroy() {
        super.preDestroy();
        InjectionProvider injector = InjectionProvider.getInstance(ID);
        PreDestroyHandler handler = injector.inject(PreDestroyHandler.getId());
        if ((handler == null)) {
            if (super.getLogger().isDebugEnabled()) {
                super.getLogger().debug("No pre destroy handler configured for \'", ID, "\'.");
            }
            return;
        }
        handler.setLocatable(this);
        handler.setLogger(super.getLogger());
        handler.invoke();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return ADAPTER_NAME;
    }

    @Override
    public String getJndiName() {
        return JNDI_NAME;
    }

    @Override
    public IndexLucene getIndexLuceneLocal() throws ServiceException {
        return super.lookup(LuceneAdapterJndiNames.INDEX_LUCENE_LOCAL, IndexLucene.class);
    }

    @Override
    public IndexLucene getIndexLucene() throws ServiceException {
        return super.lookup(LuceneAdapterJndiNames.INDEX_LUCENE_REMOTE, IndexLucene.class);
    }

    @Override
    public SearchLucene getSearchLuceneLocal() throws ServiceException {
        return super.lookup(LuceneAdapterJndiNames.SEARCH_LUCENE_LOCAL, SearchLucene.class);
    }

    @Override
    public SearchLucene getSearchLucene() throws ServiceException {
        return super.lookup(LuceneAdapterJndiNames.SEARCH_LUCENE_REMOTE, SearchLucene.class);
    }
}
