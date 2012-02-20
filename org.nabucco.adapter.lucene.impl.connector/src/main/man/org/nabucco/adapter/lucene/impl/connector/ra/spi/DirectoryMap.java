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
package org.nabucco.adapter.lucene.impl.connector.ra.spi;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

/**
 * DirectoryMap
 * 
 * @author Frank Ratschinksi, PRODYNA AG
 */
public class DirectoryMap {

    private static DirectoryMap instance;

    private Map<String, Directory> map;

    /**
     * Creates a new {@link DirectoryMap} instance.
     */
    private DirectoryMap() {
        this.map = new HashMap<String, Directory>();
    }

    /**
     * Getter for the singleton instance.
     * 
     * @return the {@link DirectoryMap} instance.
     */
    public static DirectoryMap getInstance() {
        if (instance == null) {
            instance = new DirectoryMap();
        }
        return instance;
    }

    /**
     * Getter for the directory at the given path.
     * 
     * @param indexPath
     *            the index path
     * @param index
     *            the index
     * 
     * @return the directory
     * 
     * @throws IOException
     *             when the directory does not exist
     */
    public Directory getDirectory(String indexPath, String index) throws IOException {
        String key = indexPath + "/" + index;
        Directory dir = null;
        if (!this.map.containsKey(key)) {
            dir = new NIOFSDirectory(new File(key));
            this.map.put(key, dir);
        } else {
            dir = this.map.get(key);
        }
        return dir;
    }

    /**
     * Shutdown all directories.
     * 
     * @throws IOException
     *             when a directory cannot be closed
     */
    public void shutDown() throws IOException {
        for (String key : this.map.keySet()) {
            Directory dir = this.map.get(key);
            dir.close();
        }
    }
}
