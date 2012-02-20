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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.SessionContext;
import org.nabucco.adapter.lucene.facade.exception.LuceneException;
import org.nabucco.adapter.lucene.facade.message.LuceneGeoIndexRq;
import org.nabucco.adapter.lucene.facade.message.LuceneIndexRq;
import org.nabucco.adapter.lucene.facade.service.index.IndexLucene;
import org.nabucco.framework.base.facade.message.EmptyServiceMessage;
import org.nabucco.framework.base.facade.message.ServiceRequest;
import org.nabucco.framework.base.facade.message.ServiceResponse;
import org.nabucco.framework.base.facade.service.injection.InjectionException;
import org.nabucco.framework.base.facade.service.injection.InjectionProvider;
import org.nabucco.framework.base.impl.service.ServiceSupport;
import org.nabucco.framework.base.impl.service.resource.ResourceManager;
import org.nabucco.framework.base.impl.service.resource.ResourceManagerFactory;

/**
 * IndexLuceneImpl<p/>Service for indexing the fulltext search<p/>
 *
 * @version 1.0
 * @author Nicolas Moser, PRODYNA AG, 2011-05-05
 */
public class IndexLuceneImpl extends ServiceSupport implements IndexLucene {

    private static final long serialVersionUID = 1L;

    private static final String ID = "IndexLucene";

    private static Map<String, String[]> ASPECTS;

    private IndexServiceHandler indexServiceHandler;

    private IndexGeoServiceHandler indexGeoServiceHandler;

    private SessionContext sessionContext;

    /** Constructs a new IndexLuceneImpl instance. */
    public IndexLuceneImpl() {
        super();
    }

    @Override
    public void postConstruct() {
        super.postConstruct();
        InjectionProvider injector = InjectionProvider.getInstance(ID);
        ResourceManager resourceManager = ResourceManagerFactory.getInstance().createResourceManager(
                this.sessionContext, super.getLogger());
        this.indexServiceHandler = injector.inject(IndexServiceHandler.getId());
        if ((this.indexServiceHandler != null)) {
            this.indexServiceHandler.setResourceManager(resourceManager);
            this.indexServiceHandler.setLogger(super.getLogger());
        }
        this.indexGeoServiceHandler = injector.inject(IndexGeoServiceHandler.getId());
        if ((this.indexGeoServiceHandler != null)) {
            this.indexGeoServiceHandler.setResourceManager(resourceManager);
            this.indexGeoServiceHandler.setLogger(super.getLogger());
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
            ASPECTS.put("index", NO_ASPECTS);
            ASPECTS.put("indexGeo", NO_ASPECTS);
        }
        String[] aspects = ASPECTS.get(operationName);
        if ((aspects == null)) {
            return ServiceSupport.NO_ASPECTS;
        }
        return Arrays.copyOf(aspects, aspects.length);
    }

    @Override
    public ServiceResponse<EmptyServiceMessage> index(ServiceRequest<LuceneIndexRq> rq) throws LuceneException {
        if ((this.indexServiceHandler == null)) {
            super.getLogger().error("No service implementation configured for index().");
            throw new InjectionException("No service implementation configured for index().");
        }
        ServiceResponse<EmptyServiceMessage> rs;
        this.indexServiceHandler.init();
        rs = this.indexServiceHandler.invoke(rq);
        this.indexServiceHandler.finish();
        return rs;
    }

    @Override
    public ServiceResponse<EmptyServiceMessage> indexGeo(ServiceRequest<LuceneGeoIndexRq> rq) throws LuceneException {
        if ((this.indexGeoServiceHandler == null)) {
            super.getLogger().error("No service implementation configured for indexGeo().");
            throw new InjectionException("No service implementation configured for indexGeo().");
        }
        ServiceResponse<EmptyServiceMessage> rs;
        this.indexGeoServiceHandler.init();
        rs = this.indexGeoServiceHandler.invoke(rq);
        this.indexGeoServiceHandler.finish();
        return rs;
    }
}
