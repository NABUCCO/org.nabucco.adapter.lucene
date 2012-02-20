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
import org.nabucco.framework.base.facade.datatype.geo.GeoLocation;
import org.nabucco.framework.base.facade.datatype.property.NabuccoProperty;
import org.nabucco.framework.base.facade.datatype.property.NabuccoPropertyContainer;
import org.nabucco.framework.base.facade.datatype.property.NabuccoPropertyDescriptor;
import org.nabucco.framework.base.facade.datatype.property.PropertyAssociationType;
import org.nabucco.framework.base.facade.datatype.property.PropertyCache;
import org.nabucco.framework.base.facade.datatype.property.PropertyDescriptorSupport;
import org.nabucco.framework.base.facade.datatype.search.query.FulltextQuery;
import org.nabucco.framework.base.facade.message.ServiceMessage;
import org.nabucco.framework.base.facade.message.ServiceMessageSupport;

/**
 * LuceneGeoSearchRq<p/>A message containing the search information.<p/>
 *
 * @version 1.0
 * @author Nicolas Moser, PRODYNA AG, 2011-02-21
 */
public class LuceneGeoSearchRq extends ServiceMessageSupport implements ServiceMessage {

    private static final long serialVersionUID = 1L;

    private static final String[] PROPERTY_CONSTRAINTS = { "m1,1;", "m1,1;", "m1,1;" };

    public static final String QUERY = "query";

    public static final String MINLOCATION = "minLocation";

    public static final String MAXLOCATION = "maxLocation";

    /** The search query. */
    private FulltextQuery query;

    /** The minimum geo location. */
    private GeoLocation minLocation;

    /** The maximum geo location */
    private GeoLocation maxLocation;

    /** Constructs a new LuceneGeoSearchRq instance. */
    public LuceneGeoSearchRq() {
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
        propertyMap.put(QUERY, PropertyDescriptorSupport.createDatatype(QUERY, FulltextQuery.class, 0,
                PROPERTY_CONSTRAINTS[0], false, PropertyAssociationType.COMPONENT));
        propertyMap.put(MINLOCATION, PropertyDescriptorSupport.createDatatype(MINLOCATION, GeoLocation.class, 1,
                PROPERTY_CONSTRAINTS[1], false, PropertyAssociationType.COMPONENT));
        propertyMap.put(MAXLOCATION, PropertyDescriptorSupport.createDatatype(MAXLOCATION, GeoLocation.class, 2,
                PROPERTY_CONSTRAINTS[2], false, PropertyAssociationType.COMPONENT));
        return new NabuccoPropertyContainer(propertyMap);
    }

    /** Init. */
    public void init() {
        this.initDefaults();
    }

    @Override
    public Set<NabuccoProperty> getProperties() {
        Set<NabuccoProperty> properties = super.getProperties();
        properties.add(super.createProperty(LuceneGeoSearchRq.getPropertyDescriptor(QUERY), this.getQuery()));
        properties
                .add(super.createProperty(LuceneGeoSearchRq.getPropertyDescriptor(MINLOCATION), this.getMinLocation()));
        properties
                .add(super.createProperty(LuceneGeoSearchRq.getPropertyDescriptor(MAXLOCATION), this.getMaxLocation()));
        return properties;
    }

    @Override
    public boolean setProperty(NabuccoProperty property) {
        if (super.setProperty(property)) {
            return true;
        }
        if ((property.getName().equals(QUERY) && (property.getType() == FulltextQuery.class))) {
            this.setQuery(((FulltextQuery) property.getInstance()));
            return true;
        } else if ((property.getName().equals(MINLOCATION) && (property.getType() == GeoLocation.class))) {
            this.setMinLocation(((GeoLocation) property.getInstance()));
            return true;
        } else if ((property.getName().equals(MAXLOCATION) && (property.getType() == GeoLocation.class))) {
            this.setMaxLocation(((GeoLocation) property.getInstance()));
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
        final LuceneGeoSearchRq other = ((LuceneGeoSearchRq) obj);
        if ((this.query == null)) {
            if ((other.query != null))
                return false;
        } else if ((!this.query.equals(other.query)))
            return false;
        if ((this.minLocation == null)) {
            if ((other.minLocation != null))
                return false;
        } else if ((!this.minLocation.equals(other.minLocation)))
            return false;
        if ((this.maxLocation == null)) {
            if ((other.maxLocation != null))
                return false;
        } else if ((!this.maxLocation.equals(other.maxLocation)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = ((PRIME * result) + ((this.query == null) ? 0 : this.query.hashCode()));
        result = ((PRIME * result) + ((this.minLocation == null) ? 0 : this.minLocation.hashCode()));
        result = ((PRIME * result) + ((this.maxLocation == null) ? 0 : this.maxLocation.hashCode()));
        return result;
    }

    @Override
    public ServiceMessage cloneObject() {
        return this;
    }

    /**
     * The search query.
     *
     * @return the FulltextQuery.
     */
    public FulltextQuery getQuery() {
        return this.query;
    }

    /**
     * The search query.
     *
     * @param query the FulltextQuery.
     */
    public void setQuery(FulltextQuery query) {
        this.query = query;
    }

    /**
     * The minimum geo location.
     *
     * @return the GeoLocation.
     */
    public GeoLocation getMinLocation() {
        return this.minLocation;
    }

    /**
     * The minimum geo location.
     *
     * @param minLocation the GeoLocation.
     */
    public void setMinLocation(GeoLocation minLocation) {
        this.minLocation = minLocation;
    }

    /**
     * The maximum geo location
     *
     * @return the GeoLocation.
     */
    public GeoLocation getMaxLocation() {
        return this.maxLocation;
    }

    /**
     * The maximum geo location
     *
     * @param maxLocation the GeoLocation.
     */
    public void setMaxLocation(GeoLocation maxLocation) {
        this.maxLocation = maxLocation;
    }

    /**
     * Getter for the PropertyDescriptor.
     *
     * @param propertyName the String.
     * @return the NabuccoPropertyDescriptor.
     */
    public static NabuccoPropertyDescriptor getPropertyDescriptor(String propertyName) {
        return PropertyCache.getInstance().retrieve(LuceneGeoSearchRq.class).getProperty(propertyName);
    }

    /**
     * Getter for the PropertyDescriptorList.
     *
     * @return the List<NabuccoPropertyDescriptor>.
     */
    public static List<NabuccoPropertyDescriptor> getPropertyDescriptorList() {
        return PropertyCache.getInstance().retrieve(LuceneGeoSearchRq.class).getAllProperties();
    }
}
