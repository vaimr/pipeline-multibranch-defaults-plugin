/*
 * The MIT License
 *
 * Copyright (c) 2016 Saponenko Denis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.pipeline.multibranch.defaults;

import hudson.model.Result;
import hudson.util.DescribableList;
import jenkins.branch.MultiBranchProjectFactory;
import jenkins.branch.OrganizationFolder;
import jenkins.scm.api.SCMNavigator;
import jenkins.scm.api.SCMNavigatorDescriptor;
import jenkins.scm.impl.mock.*;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.List;

import static net.sf.ezmorph.test.ArrayAssertions.assertEquals;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class PipelineMultiBranchDefaultsProjectFactoryTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Test
    public void configureAndIndexChildren() throws Exception {
        try (MockSCMController c = MockSCMController.create()) {
            OrganizationFolder folder = r.jenkins.createProject(OrganizationFolder.class, "p");
            List<MultiBranchProjectFactory> projectFactories = folder.getProjectFactories();
            projectFactories.clear();
            projectFactories.add(new PipelineMultiBranchDefaultsProjectFactory());

            c.createRepository("sample");
            MockSCMNavigator navigator = new MockSCMNavigator(c, new MockSCMDiscoverBranches(), new MockSCMDiscoverTags(), new MockSCMDiscoverChangeRequests());

            DescribableList<SCMNavigator, SCMNavigatorDescriptor> navigators = folder.getNavigators();
            navigators.add(navigator);

            assertThat("Folder has not been scanned", folder.getItem("sample"), nullValue());

            folder.scheduleBuild(0);
            r.waitUntilNoActivity();
            assertEquals(1, folder.getItems().size());

            assertThat("Folder has been scanned", folder.getItem("sample"), notNullValue());
            assertThat("We have run an index on the child item",
                folder.getItem("sample").getComputation().getResult(), is(Result.SUCCESS)
            );
        }
    }
}
