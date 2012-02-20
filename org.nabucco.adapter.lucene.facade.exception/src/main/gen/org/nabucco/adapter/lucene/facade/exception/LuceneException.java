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
package org.nabucco.adapter.lucene.facade.exception;

import org.nabucco.framework.base.facade.exception.adapter.AdapterException;

/**
 * LuceneException<p/>Exception raised from the lucene adapter.<p/>
 *
 * @version 1.0
 * @author Nicolas Moser, PRODYNA AG, 2011-02-21
 */
public class LuceneException extends AdapterException {

    private static final long serialVersionUID = 1L;

    /** Constructs a new LuceneException instance. */
    public LuceneException() {
        super();
    }

    /**
     * Constructs a new LuceneException instance.
     *
     * @param message the String.
     */
    public LuceneException(String message) {
        super(message);
    }

    /**
     * Constructs a new LuceneException instance.
     *
     * @param cause the Throwable.
     */
    public LuceneException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new LuceneException instance.
     *
     * @param cause the Throwable.
     * @param message the String.
     */
    public LuceneException(String message, Throwable cause) {
        super(message, cause);
    }
}
