/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package bval.v20.customprovider;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.validation.Validation;
import javax.validation.ValidationProviderResolver;
import javax.validation.ValidatorFactory;
import javax.validation.spi.ValidationProvider;

import org.junit.Test;

import componenttest.app.FATServlet;

@SuppressWarnings("serial")
@WebServlet("/CustomProviderTestServlet")
public class CustomProviderTestServlet extends FATServlet {

//    @Resource(lookup = "TestValidatorFactory")
//    ValidatorFactory resourceValidatorFactory;
//
//    @Inject
//    ValidatorFactory injectValFact;
//
//    @Test
//    public void testInject() {
//        assertNotNull(injectValFact);
//    }

    @Inject
    ValidatorFactory vf;

    @Test
    public void testInjectVF() throws Exception {
        ValidationProviderResolver resolver = new ValidationProviderResolver() {

            @Override
            public List<ValidationProvider<?>> getValidationProviders() {
                List<ValidationProvider<?>> list = new ArrayList<ValidationProvider<?>>();
                list.add(new MyCustomBvalProvider());
                return list;
            }
        };

        //assertNotNull(injectValFact);
        ValidatorFactory vf = Validation.byDefaultProvider()
                        .providerResolver(resolver)
                        .configure()
                        .buildValidatorFactory();

        System.out.println("@AGG got VF: " + vf);
        assertNotNull(vf);
        assertTrue("Expected instanceof MyCustomValidatorFactory but was: " + vf,
                   vf instanceof MyCustomValidatorFactory);
    }

//    /**
//     * Verify that the module ValidatorFactory may be injected and looked up at:
//     * java:comp/env/TestValidatorFactory
//     */
//    @Test
//    public void testDefaultInjectionAndLookupValidatorFactory() throws Exception {
//        assertNotNull("Injection of ValidatorFactory never occurred.", injectValFact);
//
//        assertNotNull(InitialContext.doLookup("java:comp/env/TestValidatorFactory"));
//    }

}
