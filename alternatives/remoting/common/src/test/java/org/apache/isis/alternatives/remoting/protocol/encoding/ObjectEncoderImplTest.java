/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */


package org.apache.isis.alternatives.remoting.protocol.encoding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.isis.alternatives.remoting.common.data.Data;
import org.apache.isis.alternatives.remoting.common.data.common.NullData;
import org.apache.isis.alternatives.remoting.data.DummyEncodeableObjectData;
import org.apache.isis.alternatives.remoting.data.DummyNullValue;
import org.apache.isis.alternatives.remoting.data.DummyObjectData;
import org.apache.isis.alternatives.remoting.protocol.internal.ObjectEncoderDecoderDefault;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.adapter.ResolveState;
import org.apache.isis.core.metamodel.facets.Facet;
import org.apache.isis.core.metamodel.facets.FacetHolder;
import org.apache.isis.core.metamodel.facets.object.encodeable.EncodableFacet;
import org.apache.isis.core.runtime.testspec.MovieSpecification;
import org.apache.isis.core.runtime.testspec.PersonSpecification;
import org.apache.isis.core.runtime.testsystem.ProxyJunit4TestCase;
import org.apache.isis.core.runtime.testsystem.TestProxyOid;
import org.apache.isis.core.runtime.testsystem.TestProxyVersion;
import org.apache.isis.core.testsupport.testdomain.Movie;
import org.apache.isis.core.testsupport.testdomain.Person;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;





public class ObjectEncoderImplTest extends ProxyJunit4TestCase {

    private ObjectEncoderDecoderDefault encoder;

    @Before
    public void setUp() throws Exception {
        encoder = new ObjectEncoderDecoderDefault();

        system.addSpecification(new MovieSpecification());
        system.addSpecification(new PersonSpecification());

        system.getSpecification(String.class).setupIsEncodeable();
        system.getSpecification(String.class).addFacet(new EncodableFacet() {

            public String toEncodedString(final ObjectAdapter object) {
                return object.getObject().toString();
            }

            public ObjectAdapter fromEncodedString(final String encodedData) {
                return getAdapterManager().adapterFor(encodedData);
            }

            public Class<? extends Facet> facetType() {
                return EncodableFacet.class;
            }

            public FacetHolder getFacetHolder() {
                return null;
            }

            public void setFacetHolder(final FacetHolder facetHolder) {}

            public boolean alwaysReplace() {
                return false;
            }

            public boolean isDerived() {
            	return false;
            }

            public boolean isNoop() {
                return false;
            }
            
        	public Facet getUnderlyingFacet() {
        		return null;
        	}
        	public void setUnderlyingFacet(Facet underlyingFacet) {
        		throw new UnsupportedOperationException();
        	}

        });

        /*
         * TestProxySpecification specification = new MovieSpecification(); specification.setupFields(new
         * ObjectField[] { new TestProxyField("name", system.getSpecification(String.class)), new
         * TestProxyField("person", system.getSpecification(Person.class)) });
         */
    }

    @After
    public void tearDown() throws Exception {
        system.shutdown();
    }

    @Test
    public void testRestoreCreatesNewAdapter() {
        final DummyObjectData data = new DummyObjectData(new TestProxyOid(5, true), Movie.class.getName(), true,
                new TestProxyVersion(3));
        final DummyEncodeableObjectData name = new DummyEncodeableObjectData("ET", "java.lang.String");
        final DummyNullValue reference = new DummyNullValue(Person.class.getName());
        // note - the order of the fields is by name, not the order that are defined in the specification
        data.setFieldContent(new Data[] { reference, name });

        final ObjectAdapter object = encoder.decode(data);

        assertTrue(object.getObject() instanceof Movie);

        final Movie movie = (Movie) object.getObject();
        assertEquals("ET", movie.getName());
        assertEquals(new TestProxyOid(5, true), object.getOid());
        assertEquals(new TestProxyVersion(3), object.getVersion());
        
    }

    @Test
    public void testRestoreCreatesNewAdapterInUnresolvedState() {
        final DummyObjectData data = new DummyObjectData(new TestProxyOid(5, true), Movie.class.getName(), false,
                new TestProxyVersion(3));

        final ObjectAdapter object = encoder.decode(data);

        assertTrue(object.getObject() instanceof Movie);

        assertEquals(new TestProxyOid(5, true), object.getOid());
        assertEquals(null, object.getVersion());
    }

    @Test
    public void testRestoreUpdatesExistingAdapter() {
        final Movie movie = new Movie();
        final ObjectAdapter adapter = system.createPersistentTestObject(movie);
        adapter.changeState(ResolveState.RESOLVED);

        final DummyObjectData data = new DummyObjectData(adapter.getOid(), Movie.class.getName(), true, new TestProxyVersion(3));
        final DummyEncodeableObjectData name = new DummyEncodeableObjectData("ET", "java.lang.String");
        final DummyNullValue reference = new DummyNullValue(Person.class.getName());
        data.setFieldContent(new Data[] { reference, name });

        getTransactionManager().startTransaction();
        final ObjectAdapter object = encoder.decode(data);
        getTransactionManager().endTransaction();

        assertEquals(new TestProxyVersion(3), object.getVersion());
        assertEquals("ET", movie.getName());
        assertEquals(movie, object.getObject());
    }

    @Test
    public void testRestoreIgnoredIfNoFieldData() {
        final Movie movie = new Movie();
        final ObjectAdapter adapter = system.createPersistentTestObject(movie);
        adapter.changeState(ResolveState.RESOLVED);

        final DummyObjectData data = new DummyObjectData(adapter.getOid(), Movie.class.getName(), false, new TestProxyVersion(3));

        final ObjectAdapter object = encoder.decode(data);

        assertEquals(movie, object.getObject());
        assertEquals(new TestProxyVersion(1), object.getVersion());
    }

    @Test
    public void testRestoreTransientObject() {
        final DummyObjectData movieData = new DummyObjectData(new TestProxyOid(-1), Movie.class.getName(), true, null);
        final NullData directorData = new DummyNullValue(Person.class.getName());
        final DummyEncodeableObjectData nameData = new DummyEncodeableObjectData("Star Wars", String.class.getName());
        movieData.setFieldContent(new Data[] { directorData, nameData });

        final ObjectAdapter object = encoder.decode(movieData);
        final Movie movie = (Movie) object.getObject();

        assertEquals(movie, object.getObject());
        assertEquals(new TestProxyOid(-1), object.getOid());
        assertEquals(ResolveState.TRANSIENT, object.getResolveState());
        assertEquals(null, object.getVersion());

    }
}

