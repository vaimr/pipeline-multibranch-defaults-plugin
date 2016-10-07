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

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.TopLevelItem;
import jenkins.branch.BranchProjectFactory;
import jenkins.model.TransientActionFactory;
import org.jenkinsci.plugins.workflow.cps.Snippetizer;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

import java.util.Collection;
import java.util.Collections;

/**
 * Representation of a set of workflows keyed off of source branches.
 */
@SuppressWarnings({"unchecked", "rawtypes"}) // core’s fault
public class WorkflowMultiBranchDefProject extends WorkflowMultiBranchProject {

    public WorkflowMultiBranchDefProject(ItemGroup parent, String name) {
        super(parent, name);
    }

    protected BranchProjectFactory<WorkflowJob, WorkflowRun> newProjectFactory() {
        return new WorkflowBranchDefProjectFactory();
    }

    @Extension
    public static class DescriptorImpl extends WorkflowMultiBranchProject.DescriptorImpl {

        @Override
        public String getDisplayName() {
            return DefMessages.WorkflowMultiBranchDefProject_DisplayName();
        }

        public String getDescription() {
            return DefMessages.WorkflowMultiBranchDefProject_Description();
        }

        public String getIconFilePathPattern() {
            return "plugin/workflow-multibranch-def/images/:size/pipelinemultibranchdefproject.png";
        }

        @Override
        public TopLevelItem newInstance(ItemGroup parent, String name) {
            return new WorkflowMultiBranchDefProject(parent, name);
        }
    }

    @Extension
    public static class PerFolderAdder extends TransientActionFactory<WorkflowMultiBranchDefProject> {

        @Override
        public Class<WorkflowMultiBranchDefProject> type() {
            return WorkflowMultiBranchDefProject.class;
        }

        @Override
        public Collection<? extends Action> createFor(WorkflowMultiBranchDefProject target) {
            if (target.hasPermission(Item.EXTENDED_READ)) {
                return Collections.singleton(new Snippetizer.LocalAction());
            } else {
                return Collections.emptySet();
            }
        }

    }

}
