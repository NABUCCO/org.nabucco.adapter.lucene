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
package org.nabucco.adapter.lucene.impl.connector.ra.connector;

import javax.resource.spi.ConnectionRequestInfo;


/**
 * ConnectionRequestInfoImpl
 * 
 * @author Frank Ratschinski, PRODYNA AG
 */
public class ConnectionRequestInfoImpl implements ConnectionRequestInfo {

    /** The id of the index. */
    private String indexPath;

    /** The path of the index. */
    private String luceneVersion;

    /** The index string */
    private String index;

    /**
     * Creates a new {@link ConnectionRequestInfoImpl} instance.
     */
    public ConnectionRequestInfoImpl() {
    }

    /**
     * Getter for the index path.
     * 
     * @return the index path
     */
    public String getIndexPath() {
        return this.indexPath;
    }

    /**
     * Setter for the index path.
     * 
     * @param indexPath
     *            the index path to set
     */
    public void setIndexPath(String indexPath) {
        this.indexPath = indexPath;
    }

    /**
     * Getter for the lucene version.
     * 
     * @return the version
     */
    public String getLuceneVersion() {
        return this.luceneVersion;
    }

    /**
     * Setter for the lucene version.
     * 
     * @param luceneVersion
     *            the lucene version to set
     */
    public void setLuceneVersion(String luceneVersion) {
        this.luceneVersion = luceneVersion;
    }

    /**
     * Getter for the index string.
     * 
     * @return the index
     */
    public String getIndex() {
        return this.index;
    }

    /**
     * Setter for the index string.
     * 
     * @param index
     *            the index to set
     */
    public void setIndex(String index) {
        this.index = index;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.index == null) ? 0 : this.index.hashCode());
        result = prime * result + ((this.indexPath == null) ? 0 : this.indexPath.hashCode());
        result = prime * result + ((this.luceneVersion == null) ? 0 : this.luceneVersion.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConnectionRequestInfoImpl other = (ConnectionRequestInfoImpl) obj;
        if (this.index == null) {
            if (other.index != null)
                return false;
        } else if (!this.index.equals(other.index))
            return false;
        if (this.indexPath == null) {
            if (other.indexPath != null)
                return false;
        } else if (!this.indexPath.equals(other.indexPath))
            return false;
        if (this.luceneVersion == null) {
            if (other.luceneVersion != null)
                return false;
        } else if (!this.luceneVersion.equals(other.luceneVersion))
            return false;
        return true;
    }

}
