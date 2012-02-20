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

import org.nabucco.adapter.lucene.facade.exception.LuceneException;
import org.nabucco.adapter.lucene.facade.message.LuceneSearchRq;
import org.nabucco.adapter.lucene.facade.message.LuceneSearchRs;
import org.nabucco.framework.base.facade.exception.NabuccoException;
import org.nabucco.framework.base.facade.message.ServiceRequest;
import org.nabucco.framework.base.facade.message.ServiceResponse;
import org.nabucco.framework.base.impl.service.ServiceHandler;
import org.nabucco.framework.base.impl.service.resource.ResourceServiceHandler;
import org.nabucco.framework.base.impl.service.resource.ResourceServiceHandlerSupport;

/**
 * SearchServiceHandler<p/>Service for indexing the fulltext search<p/>
 *
 * @version 1.0
 * @author Nicolas Moser, PRODYNA AG, 2011-05-05
 */
public abstract class SearchServiceHandler extends ResourceServiceHandlerSupport implements ServiceHandler,
        ResourceServiceHandler {

    private static final long serialVersionUID = 1L;

    private static final String ID = "org.nabucco.adapter.lucene.impl.service.search.SearchServiceHandler";

    /** Constructs a new SearchServiceHandler instance. */
    public SearchServiceHandler() {
        super();
    }

    /**
     * Invokes the service handler method.
     *
     * @param rq the ServiceRequest<LuceneSearchRq>.
     * @return the ServiceResponse<LuceneSearchRs>.
     * @throws LuceneException
     */
    protected ServiceResponse<LuceneSearchRs> invoke(ServiceRequest<LuceneSearchRq> rq) throws LuceneException {
        ServiceResponse<LuceneSearchRs> rs;
        LuceneSearchRs msg;
        try {
            this.validateRequest(rq);
            this.setContext(rq.getContext());
            msg = this.search(rq.getRequestMessage());
            if ((msg == null)) {
                super.getLogger().warning("No response message defined.");
            } else {
                super.cleanServiceMessage(msg);
            }
            rs = new ServiceResponse<LuceneSearchRs>(rq.getContext());
            rs.setResponseMessage(msg);
            return rs;
        } catch (LuceneException e) {
            super.getLogger().error(e);
            throw e;
        } catch (NabuccoException e) {
            super.getLogger().error(e);
            LuceneException wrappedException = new LuceneException(e);
            throw wrappedException;
        } catch (Exception e) {
            super.getLogger().error(e);
            throw new LuceneException("Error during service invocation.", e);
        }
    }

    /**
     * Search the index for the given document.
     *
     * @param msg the LuceneSearchRq.
     * @return the LuceneSearchRs.
     * @throws LuceneException
     */
    protected abstract LuceneSearchRs search(LuceneSearchRq msg) throws LuceneException;

    /**
     * Getter for the Id.
     *
     * @return the String.
     */
    protected static String getId() {
        return ID;
    }
}
