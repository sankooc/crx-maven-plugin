/*
 * Copyright 2012 Brian Matthews
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.btmatthews.maven.plugins.crx.test;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;

import com.btmatthews.maven.plugins.crx.CRXMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

/**
 * Unit test the {@link CRXMojo} class.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.0.0
 */
public class TestMojo extends AbstractMojoTestCase {

    /**
     * The mojo used for all unit tests.
     */
    private CRXMojo mojo;

    /**
     * The Maven project model used to configure the mojo for unit testing.
     */
    private MavenProject project;

    /**
     * The Maven project helper used to configure the mojo for unit testing
     */
    private MavenProjectHelper projectHelper;

    /**
     * Create the test fixtures including the CRX mojo and a mocked the Maven project model and Maven project
     * helper.
     *
     * @throws Exception If there was an error configuring the CRX mojo.
     */
    protected void setUp() throws Exception {
        mojo = new CRXMojo();
        project = mock(MavenProject.class);
        projectHelper = mock(MavenProjectHelper.class);
        setVariableValueToObject(mojo, "outputDirectory", new File("target"));
        setVariableValueToObject(mojo, "project", project);
        setVariableValueToObject(mojo, "projectHelper", projectHelper);
    }

    /**
     * Verify that a .crx file can be created without a pemPassword or classifier property.
     *
     * @throws Exception If there was an error executing the unit test.
     */
    public void testMojo() throws Exception {
        setVariableValueToObject(mojo, "finalName", "HelloWorld");
        setVariableValueToObject(mojo, "pemFile", new File("target/test-classes/crxtest.pem"));
        setVariableValueToObject(mojo, "crxSourceDirectory", new File("target/test-classes/HelloWorld"));
        mojo.execute();
        verify(project).setFile(any(File.class));
    }

    /**
     * Verify that a .crx file can be created with a pemPassword but without a classifier property.
     *
     * @throws Exception If there was an error executing the unit test.
     */
    public void testMojoWithPasssword() throws Exception {
        setVariableValueToObject(mojo, "finalName", "HelloWorld");
        setVariableValueToObject(mojo, "pemFile", new File("target/test-classes/crxtest1.pem"));
        setVariableValueToObject(mojo, "pemPassword", "everclear");
        setVariableValueToObject(mojo, "crxSourceDirectory", new File("target/test-classes/HelloWorld"));
        mojo.execute();
        verify(project).setFile(any(File.class));
    }

    public void testMojoWhenPEMFileDoesNotExist() throws Exception {
        try {
            setVariableValueToObject(mojo, "finalName", "HelloWorld");
            setVariableValueToObject(mojo, "pemFile", new File("target/test-classes/crxtest2.pem"));
            setVariableValueToObject(mojo, "pemPassword", "everclear");
            setVariableValueToObject(mojo, "crxSourceDirectory", new File("target/test-classes/HelloWorld"));
            mojo.execute();
            fail();
        } catch (final MojoExecutionException e) {
            assertEquals("Could not load the public/private key from the PEM file", e.getMessage());
        }
    }

    public void testMojoWhenPEMFileIsCorrupted() throws Exception {
        try {
            setVariableValueToObject(mojo, "finalName", "HelloWorld");
            setVariableValueToObject(mojo, "pemFile", new File("target/test-classes/crxtest3.pem"));
            setVariableValueToObject(mojo, "pemPassword", "everclear");
            setVariableValueToObject(mojo, "crxSourceDirectory", new File("target/test-classes/HelloWorld"));
            mojo.execute();
            fail();
        } catch (final MojoExecutionException e) {
            assertEquals("Could not load the public/private key from the PEM file", e.getMessage());
        }
    }

    /**
     * Verify that a .crx file can be created without a classifier property.
     *
     * @throws Exception If there was an error executing the unit test.
     */
    public void testMojoWithClassifier() throws Exception {
        setVariableValueToObject(mojo, "finalName", "HelloWorld");
        setVariableValueToObject(mojo, "pemFile", new File("target/test-classes/crxtest.pem"));
        setVariableValueToObject(mojo, "classifier", "debug");
        setVariableValueToObject(mojo, "crxSourceDirectory", new File("target/test-classes/HelloWorld"));
        mojo.execute();
        verify(projectHelper).attachArtifact(same(project), eq("crx"), eq("debug"), any(File.class));
    }

    /**
     * Verify that an exception is thrown if there is no manifest file in the source directory.
     */
    public void testMojoFailsWithoutManifest() throws Exception {
        try {
            setVariableValueToObject(mojo, "finalName", "HelloWorld");
            setVariableValueToObject(mojo, "pemFile", new File("target/test-classes/crxtest.pem"));
            setVariableValueToObject(mojo, "classifier", "debug");
            setVariableValueToObject(mojo, "crxSourceDirectory", new File("target/test-classes/GoodbyeWorld"));
            mojo.execute();
            fail();
        } catch (final MojoExecutionException e) {
            assertEquals("Missing manifest.json file", e.getMessage());
        }
    }
}
