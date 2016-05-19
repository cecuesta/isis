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
package org.apache.isis.core.metamodel.facets.object.value.vsp;

import org.apache.isis.core.metamodel.adapter.mgr.AdapterManager;
import org.apache.isis.core.metamodel.deployment.DeploymentCategory;
import org.apache.isis.core.metamodel.services.ServicesInjector;
import org.apache.isis.core.metamodel.specloader.SpecificationLoader;

public class ValueSemanticsProviderContext {
    
    private final DeploymentCategory deploymentCategory;
    private final SpecificationLoader specificationLookup;
    private final AdapterManager adapterManager;
    private final ServicesInjector servicesInjector;

    public ValueSemanticsProviderContext(
            final DeploymentCategory deploymentCategory,
            final SpecificationLoader specificationLookup,
            final AdapterManager adapterManager,
            final ServicesInjector servicesInjector) {
        this.deploymentCategory = deploymentCategory;
        this.specificationLookup = specificationLookup;
        this.adapterManager = adapterManager;
        this.servicesInjector = servicesInjector;
    }

    public DeploymentCategory getDeploymentCategory() {
        return deploymentCategory;
    }
    
    public SpecificationLoader getSpecificationLoader() {
        return specificationLookup;
    }

    public AdapterManager getAdapterManager() {
        return adapterManager;
    }

    public ServicesInjector getServicesInjector() {
        return servicesInjector;
    }
}