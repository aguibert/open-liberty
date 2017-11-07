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

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.validation.Constraint;
import javax.validation.Valid;
import javax.validation.executable.ValidateOnExecution;

import org.hibernate.validator.cdi.ValidationExtension;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.ibm.ws.cdi.extension.WebSphereCDIExtension;

@Component(configurationPolicy = ConfigurationPolicy.IGNORE)
public class LibertyHibernateValidatorExtension implements Extension, WebSphereCDIExtension {

    private ValidationExtension delegate;

    @Activate
    protected void activate() {
        System.out.println("@AGG activating " + getClass().getSimpleName());
    }

    private void initHibernateValidator() {
        System.out.println("@AGG TCCL is: " + Thread.currentThread().getContextClassLoader());
        if (delegate == null)
            delegate = new ValidationExtension();
    }

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery afterBeanDiscoveryEvent, BeanManager beanManager) {
        initHibernateValidator();
        delegate.afterBeanDiscovery(afterBeanDiscoveryEvent, beanManager);
    }

    public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery beforeBeanDiscoveryEvent, BeanManager beanManager) {
        initHibernateValidator();
        delegate.beforeBeanDiscovery(beforeBeanDiscoveryEvent, beanManager);
    }

    public <T> void processAnnotatedType(@Observes @WithAnnotations({ Constraint.class, Valid.class,
                                                                      ValidateOnExecution.class }) ProcessAnnotatedType<T> processAnnotatedTypeEvent) {
        initHibernateValidator();
        delegate.processAnnotatedType(processAnnotatedTypeEvent);
    }

    public void processBean(@Observes ProcessBean<?> processBeanEvent) {
        initHibernateValidator();
        delegate.processBean(processBeanEvent);
    }

}
