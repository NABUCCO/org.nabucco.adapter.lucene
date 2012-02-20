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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.SessionContext;
import org.nabucco.adapter.lucene.facade.exception.LuceneException;
import org.nabucco.adapter.lucene.facade.message.LuceneGeoSearchRq;
import org.nabucco.adapter.lucene.facade.message.LuceneSearchRq;
import org.nabucco.adapter.lucene.facade.message.LuceneSearchRs;
import org.nabucco.adapter.lucene.facade.service.search.SearchLucene;
import org.nabucco.framework.base.facade.message.ServiceRequest;
import org.nabucco.framework.base.facade.message.ServiceResponse;
import org.nabucco.framework.base.facade.service.injection.InjectionException;
import org.nabucco.framework.base.facade.service.injection.InjectionProvider;
import org.nabucco.framework.base.impl.service.ServiceSupport;
import org.nabucco.framework.base.impl.service.resource.ResourceManager;
import org.nabucco.framework.base.impl.service.resource.ResourceManagerFactory;

/**
 * SearchLuceneImpl<p/>Service for indexing the fulltext search<p/>
 *
 * @version 1.0
 * @author Nicolas Moser, PRODYNA AG, 2011-05-05
 */
public class SearchLuceneImpl extends ServiceSupport implements SearchLucene {

    private static final long serialVersionUID = 1L;

    private static final String ID = "SearchLucene";

    private static Map<String, String[]> ASPECTS;

    private SearchServiceHandler searchServiceHandler;

    private SearchGeoServiceHandler searchGeoServiceHandler;

    private SessionContext sessionContext;

    /** Constructs a new SearchLuceneImpl instance. */
    public SearchLuceneImpl() {
        super();
    }

    @Override
    public void postConstruct() {
        super.postConstruct();
        InjectionProvider injector = InjectionProvider.getInstance(ID);
        ResourceManager resourceManager = ResourceManagerFactory.getInstance().createResourceManager(
                this.sessionContext, super.getLogger());
        this.searchServiceHandler = injector.inject(SearchServiceHandler.getId());
        if ((this.searchServiceHandler != null)) {
            this.searchServiceHandler.setResourceManager(resourceManager);
            this.searchServiceHandler.setLogger(super.getLogger());
        }
        this.searchGeoServiceHandler = injector.inject(SearchGeoServiceHandler.getId());
        if ((this.searchGeoServiceHandler != null)) {
            this.searchGeoServiceHandler.setResourceManager(resourceManager);
            this.searchGeoServiceHandler.setLogger(super.getLogger());
        }
    }

    @Override
    public void preDestroy() {
        super.preDestroy();
    }

    @Override
    public String[] getAspects(String operationName) {
        if ((ASPECTS == null)) {
            ASPECTS = new HashMap<String, String[]>();
            ASPECTS.put("search", NO_ASPECTS);
            ASPECTS.put("searchGeo", NO_ASPECTS);
        }
        String[] aspects = ASPECTS.get(operationName);
        if ((aspects == null)) {
            return ServiceSupport.NO_ASPECTS;
        }
        return Arrays.copyOf(aspects, aspects.length);
    }

    @Override
    public ServiceResponse<LuceneSearchRs> search(ServiceRequest<LuceneSearchRq> rq) throws LuceneException {
        if ((this.searchServiceHandler == null)) {
            super.getLogger().error("No service implementation configured for search().");
            throw new InjectionException("No service implementation configured for search().");
        }
        ServiceResponse<LuceneSearchRs> rs;
        this.searchServiceHandler.init();
        rs = this.searchServiceHandler.invoke(rq);
        this.searchServiceHandler.finish();
        return rs;
    }

    @Override
    public ServiceResponse<LuceneSearchRs> searchGeo(ServiceRequest<LuceneGeoSearchRq> rq) throws LuceneException {
        if ((this.searchGeoServiceHandler == null)) {
            super.getLogger().error("No service implementation configured for searchGeo().");
            throw new InjectionException("No service implementation configured for searchGeo().");
        }
        ServiceResponse<LuceneSearchRs> rs;
        this.searchGeoServiceHandler.init();
        rs = this.searchGeoServiceHandler.invoke(rq);
        this.searchGeoServiceHandler.finish();
        return rs;
    }
}
