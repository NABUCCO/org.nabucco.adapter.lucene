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
package org.nabucco.adapter.lucene.facade.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.nabucco.framework.base.facade.datatype.Name;
import org.nabucco.framework.base.facade.datatype.property.NabuccoProperty;
import org.nabucco.framework.base.facade.datatype.property.NabuccoPropertyContainer;
import org.nabucco.framework.base.facade.datatype.property.NabuccoPropertyDescriptor;
import org.nabucco.framework.base.facade.datatype.property.PropertyAssociationType;
import org.nabucco.framework.base.facade.datatype.property.PropertyCache;
import org.nabucco.framework.base.facade.datatype.property.PropertyDescriptorSupport;
import org.nabucco.framework.base.facade.datatype.search.fulltext.FulltextDocument;
import org.nabucco.framework.base.facade.message.ServiceMessage;
import org.nabucco.framework.base.facade.message.ServiceMessageSupport;

/**
 * LuceneIndexRq<p/>A message containing the data for indexing<p/>
 *
 * @version 1.0
 * @author Nicolas Moser, PRODYNA AG, 2011-02-21
 */
public class LuceneIndexRq extends ServiceMessageSupport implements ServiceMessage {

    private static final long serialVersionUID = 1L;

    private static final String[] PROPERTY_CONSTRAINTS = { "l0,255;u0,n;m1,1;", "m1,1;" };

    public static final String INDEXNAME = "indexName";

    public static final String DOCUMENT = "document";

    /** Name of the index. */
    private Name indexName;

    /** The document for indexing. */
    private FulltextDocument document;

    /** Constructs a new LuceneIndexRq instance. */
    public LuceneIndexRq() {
        super();
        this.initDefaults();
    }

    /** InitDefaults. */
    private void initDefaults() {
    }

    /**
     * CreatePropertyContainer.
     *
     * @return the NabuccoPropertyContainer.
     */
    protected static NabuccoPropertyContainer createPropertyContainer() {
        Map<String, NabuccoPropertyDescriptor> propertyMap = new HashMap<String, NabuccoPropertyDescriptor>();
        propertyMap.put(INDEXNAME,
                PropertyDescriptorSupport.createBasetype(INDEXNAME, Name.class, 0, PROPERTY_CONSTRAINTS[0], false));
        propertyMap.put(DOCUMENT, PropertyDescriptorSupport.createDatatype(DOCUMENT, FulltextDocument.class, 1,
                PROPERTY_CONSTRAINTS[1], false, PropertyAssociationType.COMPONENT));
        return new NabuccoPropertyContainer(propertyMap);
    }

    /** Init. */
    public void init() {
        this.initDefaults();
    }

    @Override
    public Set<NabuccoProperty> getProperties() {
        Set<NabuccoProperty> properties = super.getProperties();
        properties.add(super.createProperty(LuceneIndexRq.getPropertyDescriptor(INDEXNAME), this.indexName));
        properties.add(super.createProperty(LuceneIndexRq.getPropertyDescriptor(DOCUMENT), this.getDocument()));
        return properties;
    }

    @Override
    public boolean setProperty(NabuccoProperty property) {
        if (super.setProperty(property)) {
            return true;
        }
        if ((property.getName().equals(INDEXNAME) && (property.getType() == Name.class))) {
            this.setIndexName(((Name) property.getInstance()));
            return true;
        } else if ((property.getName().equals(DOCUMENT) && (property.getType() == FulltextDocument.class))) {
            this.setDocument(((FulltextDocument) property.getInstance()));
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if ((this == obj)) {
            return true;
        }
        if ((obj == null)) {
            return false;
        }
        if ((this.getClass() != obj.getClass())) {
            return false;
        }
        if ((!super.equals(obj))) {
            return false;
        }
        final LuceneIndexRq other = ((LuceneIndexRq) obj);
        if ((this.indexName == null)) {
            if ((other.indexName != null))
                return false;
        } else if ((!this.indexName.equals(other.indexName)))
            return false;
        if ((this.document == null)) {
            if ((other.document != null))
                return false;
        } else if ((!this.document.equals(other.document)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = ((PRIME * result) + ((this.indexName == null) ? 0 : this.indexName.hashCode()));
        result = ((PRIME * result) + ((this.document == null) ? 0 : this.document.hashCode()));
        return result;
    }

    @Override
    public ServiceMessage cloneObject() {
        return this;
    }

    /**
     * Name of the index.
     *
     * @return the Name.
     */
    public Name getIndexName() {
        return this.indexName;
    }

    /**
     * Name of the index.
     *
     * @param indexName the Name.
     */
    public void setIndexName(Name indexName) {
        this.indexName = indexName;
    }

    /**
     * The document for indexing.
     *
     * @return the FulltextDocument.
     */
    public FulltextDocument getDocument() {
        return this.document;
    }

    /**
     * The document for indexing.
     *
     * @param document the FulltextDocument.
     */
    public void setDocument(FulltextDocument document) {
        this.document = document;
    }

    /**
     * Getter for the PropertyDescriptor.
     *
     * @param propertyName the String.
     * @return the NabuccoPropertyDescriptor.
     */
    public static NabuccoPropertyDescriptor getPropertyDescriptor(String propertyName) {
        return PropertyCache.getInstance().retrieve(LuceneIndexRq.class).getProperty(propertyName);
    }

    /**
     * Getter for the PropertyDescriptorList.
     *
     * @return the List<NabuccoPropertyDescriptor>.
     */
    public static List<NabuccoPropertyDescriptor> getPropertyDescriptorList() {
        return PropertyCache.getInstance().retrieve(LuceneIndexRq.class).getAllProperties();
    }
}
