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

import org.nabucco.framework.base.facade.component.connection.ConnectionException;
import org.nabucco.framework.base.facade.component.locator.AdapterLocator;
import org.nabucco.framework.base.facade.component.locator.AdapterLocatorSupport;

/**
 * Locator for LuceneAdapter.
 *
 * @author NABUCCO Generator, PRODYNA AG
 */
public class LuceneAdapterLocator extends AdapterLocatorSupport<LuceneAdapter> implements AdapterLocator<LuceneAdapter> {

    private static LuceneAdapterLocator instance;

    /**
     * Constructs a new LuceneAdapterLocator instance.
     *
     * @param adapter the Class<LuceneAdapter>.
     * @param jndiName the String.
     */
    private LuceneAdapterLocator(String jndiName, Class<LuceneAdapter> adapter) {
        super(jndiName, adapter);
    }

    @Override
    public LuceneAdapter getAdapter() throws ConnectionException {
        LuceneAdapter adapter = super.getAdapter();
        if ((adapter instanceof LuceneAdapterLocal)) {
            return new LuceneAdapterLocalProxy(((LuceneAdapterLocal) adapter));
        }
        return adapter;
    }

    /**
     * Getter for the Instance.
     *
     * @return the LuceneAdapterLocator.
     */
    public static LuceneAdapterLocator getInstance() {
        if ((instance == null)) {
            instance = new LuceneAdapterLocator(LuceneAdapter.JNDI_NAME, LuceneAdapter.class);
        }
        return instance;
    }
}
