/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.isis.applib.services.layout;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.layout.common.Grid;

public interface GridService {

    /**
     * Whether any metadata exists for this domain class, and if so then whether it is valid or invalid.
     */
    @Programmatic
    boolean exists(Class<?> domainClass);

    /**
     * Returns (raw unnormalized) metadata, eg per the <code>.layout.xml</code> file.
     */
    @Programmatic Grid fromXml(Class<?> domainClass);

    /**
     * Obtains the (normalized) layout metadata, if any, for the (domain class of the) specified domain object.
     */
    @Programmatic Grid toGrid(Object domainObject);

    /**
     * Obtains the (normalized) layout metadata, if any, for the specified domain class.
     */
    @Programmatic Grid toGrid(Class<?> domainClass);

    String tnsAndSchemaLocation(final Grid grid);
}