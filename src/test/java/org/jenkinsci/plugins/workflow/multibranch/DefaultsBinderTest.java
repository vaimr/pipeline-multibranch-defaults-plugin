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

package org.jenkinsci.plugins.workflow.multibranch;

import jenkins.branch.BranchProperty;
import jenkins.branch.BranchSource;
import jenkins.branch.DefaultBranchPropertyStrategy;
import jenkins.plugins.git.GitSCMSource;
import jenkins.plugins.git.GitSampleRepoRule;
import org.jenkinsci.lib.configprovider.ConfigProvider;
import org.jenkinsci.lib.configprovider.model.Config;
import org.jenkinsci.plugins.configfiles.groovy.GroovyScript;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.test.steps.SemaphoreStep;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;

import static net.sf.ezmorph.test.ArrayAssertions.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DefaultsBinderTest {
    @ClassRule
    public static BuildWatcher buildWatcher = new BuildWatcher();
    @Rule
    public JenkinsRule r = new JenkinsRule();
    @Rule
    public GitSampleRepoRule sampleGitRepo = new GitSampleRepoRule();

    @Test
    public void testDefaultJenkinsFile() throws Exception {
        ConfigProvider configProvider = ConfigProvider.getByIdOrNull(GroovyScript.class.getName());
        Config config = new GroovyScript("Jenkinsfile", "Jenkinsfile", "",
            "semaphore 'wait'; node {checkout scm; echo readFile('file')}");
        configProvider.save(config);

        sampleGitRepo.init();
        sampleGitRepo.write("file", "initial content");
        sampleGitRepo.git("commit", "--all", "--message=flow");
        WorkflowMultiBranchProject mp = r.jenkins.createProject(WorkflowMultiBranchDefProject.class, "p");
        mp.getSourcesList().add(new BranchSource(new GitSCMSource(null, sampleGitRepo.toString(), "", "*", "", false),
            new DefaultBranchPropertyStrategy(new BranchProperty[0])));
        WorkflowJob p = WorkflowMultiBranchDefProjectTest.scheduleAndFindBranchProject(mp, "master");
        SemaphoreStep.waitForStart("wait/1", null);
        WorkflowRun b1 = p.getLastBuild();
        assertNotNull(b1);
        assertEquals(1, b1.getNumber());
        SemaphoreStep.success("wait/1", null);
        r.assertLogContains("initial content", r.waitForCompletion(b1));
    }

    @Test
    public void testDefaultJenkinsFileLoadFromWorkspace() throws Exception {
        ConfigProvider configProvider = ConfigProvider.getByIdOrNull(GroovyScript.class.getName());
        Config config = new GroovyScript("Jenkinsfile", "Jenkinsfile", "",
            "semaphore 'wait'; node {checkout scm; load 'Jenkinsfile'}");
        configProvider.save(config);

        sampleGitRepo.init();
        sampleGitRepo.write("Jenkinsfile", "echo readFile('file')");
        sampleGitRepo.git("add", "Jenkinsfile");
        sampleGitRepo.write("file", "initial content");
        sampleGitRepo.git("commit", "--all", "--message=flow");
        WorkflowMultiBranchProject mp = r.jenkins.createProject(WorkflowMultiBranchDefProject.class, "p");
        mp.getSourcesList().add(new BranchSource(new GitSCMSource(null, sampleGitRepo.toString(), "", "*", "", false),
            new DefaultBranchPropertyStrategy(new BranchProperty[0])));
        WorkflowJob p = WorkflowMultiBranchDefProjectTest.scheduleAndFindBranchProject(mp, "master");
        SemaphoreStep.waitForStart("wait/1", null);
        WorkflowRun b1 = p.getLastBuild();
        assertNotNull(b1);
        assertEquals(1, b1.getNumber());
        SemaphoreStep.success("wait/1", null);
        r.assertLogContains("initial content", r.waitForCompletion(b1));
    }

}
