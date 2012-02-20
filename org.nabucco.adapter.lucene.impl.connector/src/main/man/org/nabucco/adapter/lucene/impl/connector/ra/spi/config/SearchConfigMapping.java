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
package org.nabucco.adapter.lucene.impl.connector.ra.spi.config;

import java.util.HashMap;
import java.util.Map;

import org.nabucco.framework.base.facade.datatype.extension.ExtensionMap;
import org.nabucco.framework.base.facade.datatype.extension.schema.search.SearchFieldExtension;
import org.nabucco.framework.base.facade.datatype.extension.schema.search.SearchIndexExtension;
import org.nabucco.framework.base.facade.datatype.logger.NabuccoLogger;
import org.nabucco.framework.base.facade.datatype.logger.NabuccoLoggingFactory;

/**
 * SearchConfigMapping
 * 
 * @author Frank Ratschinski, PRODYNA AG
 */
public class SearchConfigMapping {

    private static final NabuccoLogger logger = NabuccoLoggingFactory.getInstance()
            .getLogger(SearchConfigMapping.class);

    private Map<String, Map<String, SearchFieldExtension>> mapping = new HashMap<String, Map<String, SearchFieldExtension>>();

    private Map<String, SearchIndexExtension> indexMapping = new HashMap<String, SearchIndexExtension>();

    /**
     * Creates a new {@link SearchConfigMapping} instance.
     * 
     * @param map
     *            the configured extension map
     */
    public SearchConfigMapping(ExtensionMap map) {

        String[] idList = map.getExtensionNames();

        for (String id : idList) {

            SearchIndexExtension extension = (SearchIndexExtension) map.getExtension(id);

            Map<String, SearchFieldExtension> indexMap = new HashMap<String, SearchFieldExtension>();
            this.indexMapping.put(id, extension);

            for (SearchFieldExtension fieldExtension : extension.getFieldList()) {
                indexMap.put(fieldExtension.getName().getValue().getValue(), fieldExtension);
            }

            this.mapping.put(extension.getIdentifier().getValue(), indexMap);
        }
    }

    /**
     * Getter for the index extension.
     * 
     * @param index
     *            the index
     * 
     * @return the search index extension
     */
    public SearchIndexExtension getIndexExtension(String index) {
        return this.indexMapping.get(index);
    }

    /**
     * Getter for the field extension.
     * 
     * @param index
     *            the index
     * @param field
     *            the field
     * 
     * @return the search field extension
     */
    public SearchFieldExtension getFieldExtension(String index, String field) {
        Map<String, SearchFieldExtension> map = this.mapping.get(index);
        if (map == null) {
            logger.error("Cannot get extension mapping for index [" + index + "].");
            return null;
        }
        SearchFieldExtension ext = map.get(field);
        if (ext == null) {
            logger.error("Cannot get extension mapping for index [" + index + "] field [" + field + "].");
            return null;
        }
        return ext;
    }
}
