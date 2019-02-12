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

package com.ibm.ws.feature.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ibm.ws.feature.tasks.FeatureBuilder;

import aQute.bnd.header.Attrs;

public class FeatureInfo {

    public static enum Kind {
        NOSHIP,
        BETA,
        GA
    }

    public static enum Edition {
        FULL,
        UNSUPPORTED,
        ZOS,
        ND,
        BASE,
        CORE
    }

    private String[] lockedAutoFeatures;
    private String[] lockedDependentFeatures;
    private String[] lockedActivatingAutoFeature;

    private Set<String> autoFeatures = new LinkedHashSet<String>();
    private Set<String> dependentFeatures = new LinkedHashSet<String>();
    private Set<String> activatingAutoFeature = new LinkedHashSet<String>();
    private final Set<String> providedMavenCoords = new HashSet<>();

    private Edition edition;
    private Kind kind;

    private boolean isInit = false;
    private File feature;
    private String name;

    public FeatureInfo(File feature) {
        this.feature = feature;
    }

    public String[] getAutoFeatures() {
        if (!isInit)
            populateInfo();

        return this.lockedAutoFeatures;
    }

    public String getName() {
        if (!isInit)
            populateInfo();

        return this.name;
    }

    //Activating autofeature just means "I'm an autofeature, and i *might* activate this other feature
    //So it's like a "Sometimes" dependency, but is potentially useful for figuring out a superset of
    //potential provisioned features.
    protected void addActivatingAutoFeature(String featureName) {
        if (!isInit)
            populateInfo();

        if (activatingAutoFeaturesLocked)
            return;

        this.activatingAutoFeature.add(featureName);
    }

    public String[] getActivatingAutoFeatures() {
        if (activatingAutoFeaturesLocked)
            return this.lockedActivatingAutoFeature;
        else
            return null;

    }

    private boolean activatingAutoFeaturesLocked = false;

    protected synchronized void lockActivatingAutoFeatures() {
        this.lockedActivatingAutoFeature = this.activatingAutoFeature.toArray(new String[this.activatingAutoFeature.size()]);
        activatingAutoFeaturesLocked = true;
        activatingAutoFeature = null;
    }

    public String[] getDependentFeatures() {
        if (!isInit)
            populateInfo();

        return this.lockedDependentFeatures;
    }

    public Edition getEdition() {
        if (!isInit)
            populateInfo();

        return this.edition;

    }

    public Kind getKind() {
        if (!isInit)
            populateInfo();

        return this.kind;
    }

    public Set<String> getProvidedMavenCoords() {
        if (!isInit)
            populateInfo();
        return providedMavenCoords;
    }

    private synchronized void populateInfo() {
        if (isInit)
            return;

        FeatureBuilder builder = new FeatureBuilder();

        try {
            builder.setProperties(this.feature);

            String edition = builder.getProperty("edition");
            String kind = builder.getProperty("kind");
            this.name = builder.getProperty("symbolicName");

            this.edition = Edition.valueOf(edition.toUpperCase());
            this.kind = Kind.valueOf(kind.toUpperCase());

            for (String autoFeature : builder.getAutoFeatures()) {
                this.autoFeatures.add(autoFeature);
            }
            this.lockedAutoFeatures = this.autoFeatures.toArray(new String[this.autoFeatures.size()]);

            for (Map.Entry<String, Attrs> feature : builder.getFeatures()) {
                String item = feature.getKey().toString();
                this.dependentFeatures.add(item);
            }

            this.lockedDependentFeatures = this.dependentFeatures.toArray(new String[this.dependentFeatures.size()]);

            // Fill the mavenCoordinates
            for (Entry<String, Attrs> bundle : builder.getBundles()) {
                String mavenCoords = bundle.getValue().get("mavenCoordinates");
                if (mavenCoords != null)
                    providedMavenCoords.add(mavenCoords);
            }

            this.autoFeatures = null;
            this.dependentFeatures = null;

            builder.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            builder = null;
        }

        isInit = true;
    }

}
