/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.beanvalidation.v20.cdi.internal;

import javax.el.ELManager;
import javax.el.ExpressionFactory;

import org.hibernate.validator.cdi.ValidationExtension;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.ibm.ws.cdi.extension.WebSphereCDIExtension;

@Component(configurationPolicy = ConfigurationPolicy.IGNORE)
public class LibertyHibernateValidatorExtension implements WebSphereCDIExtension {

    private ValidationExtension delegate;

    @Activate
    protected void activate() {
        System.out.println("@AGG activating " + getClass().getSimpleName());

        System.out.println("@AGG can we get EL expression factory?");
        System.out.println(ExpressionFactory.newInstance());
        System.out.println(ELManager.getExpressionFactory());
    }

    private void initHibernateValidator() {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        System.out.println("@AGG TCCL is: " + tccl);
//
//        URL url = tccl.getResource("META-INF/services/javax.validation.spi.ValidationProvider");
//        System.out.println("Got meta-inf resource: " + url);
//
//        System.out.println("Attempt to load hibernate validator...");
//        try {
//            System.out.println("Got class: " + Class.forName("org.hibernate.validator.HibernateValidator"));
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace(System.out);
//        }
//
//        System.out.println("Attempt to load hibernate validator...");
//        try {
//            System.out.println("Got class: " + Class.forName("org.hibernate.validator.HibernateValidator", false, tccl));
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace(System.out);
//        }
//
//        for (ValidationProvider<?> provider : ServiceLoader.load(ValidationProvider.class, tccl)) {
//            System.out.println("With tccl, found: " + provider);
//        }

//        System.out.println("Found providers: " + loadProviders(tccl));

        if (delegate == null)
            delegate = new ValidationExtension();
    }

//    private List<ValidationProvider<?>> loadProviders(ClassLoader classloader) {
//        ServiceLoader<ValidationProvider> loader = ServiceLoader.load(ValidationProvider.class, classloader);
//        Iterator<ValidationProvider> providerIterator = loader.iterator();
//        List<ValidationProvider<?>> validationProviderList = new ArrayList();
//        while (providerIterator.hasNext()) {
//            try {
//                validationProviderList.add(providerIterator.next());
//            } catch (ServiceConfigurationError localServiceConfigurationError) {
//            }
//        }
//        return validationProviderList;
//    }
//
//    public void afterBeanDiscovery(@Observes AfterBeanDiscovery afterBeanDiscoveryEvent, BeanManager beanManager) {
//        initHibernateValidator();
//        delegate.afterBeanDiscovery(afterBeanDiscoveryEvent, beanManager);
//    }
//
//    public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery beforeBeanDiscoveryEvent, BeanManager beanManager) {
//        initHibernateValidator();
//        delegate.beforeBeanDiscovery(beforeBeanDiscoveryEvent, beanManager);
//    }
//
//    public <T> void processAnnotatedType(@Observes @WithAnnotations({ Constraint.class, Valid.class,
//                                                                      ValidateOnExecution.class }) ProcessAnnotatedType<T> processAnnotatedTypeEvent) {
//        initHibernateValidator();
//        delegate.processAnnotatedType(processAnnotatedTypeEvent);
//    }
//
//    public void processBean(@Observes ProcessBean<?> processBeanEvent) {
//        initHibernateValidator();
//        delegate.processBean(processBeanEvent);
//    }

}
