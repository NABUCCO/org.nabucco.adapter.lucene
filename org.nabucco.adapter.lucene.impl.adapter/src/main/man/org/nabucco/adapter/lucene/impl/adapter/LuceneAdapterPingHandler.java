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

import org.nabucco.adapter.lucene.impl.connector.api.LuceneConnection;
import org.nabucco.framework.base.facade.datatype.NabuccoSystem;
import org.nabucco.framework.base.facade.message.ping.PingRequest;
import org.nabucco.framework.base.facade.message.ping.PingResponse;
import org.nabucco.framework.base.facade.message.ping.PingStatus;
import org.nabucco.framework.base.impl.service.resource.ResourcePingHandler;

/**
 * LuceneAdapterPingHandler
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class LuceneAdapterPingHandler extends ResourcePingHandler {

    private static final long serialVersionUID = 1L;

    @Override
    public PingResponse ping(PingRequest request) {

        long before = NabuccoSystem.getCurrentTimeMillis();
        PingStatus status = this.pingLucene(request.getJndiName());

        long after = NabuccoSystem.getCurrentTimeMillis();
        long duration = after - before;

        return new PingResponse(status, before, duration);
    }

    /**
     * Ping the adapter.
     * 
     * @param request
     *            the ping request
     */
    private PingStatus pingLucene(String index) {

        LuceneConnection connection = null;

        try {
            connection = super.getResourceManager().getConnection(index);
            connection.ping();

            return PingStatus.AVAILABLE;

        } catch (Exception e) {
            super.getLogger().error(e, "Error pinging Lucene Index '", index, "'.");
            return PingStatus.ERROR;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    super.getLogger().error("Cannot close Lucene Connection.");
                }
            }
        }
    }
}
