# v2.0 Released Oct 1, 2018

### Warnings

Pipeline multibranch defaults plugin v1.1 and older created a new Job type named
**Multibranch Pipeline with defaults**.  This is considered a design flaw.
Convert all jobs using this old type to a normal `Multibranch Pipeline` job
type.

**Multibranch Pipeline with defaults** will be removed in a later release.

### New features

- Sandbox support has been added for better flexibility in security.  See [PR
  #3][#3].
- `Jenkinsfile` ID is customizable from the config file provider plugin which
  means different multibranch pipelines may use a different default
  `Jenkinsfile`.  See [PR #3][#3], [JENKINS-41108][JENKINS-41108].
- Job DSL syntax is fixed and now includes new sandbox and customizable
  `Jenkinsfile`.  See [PR #3][#3], [JENKINS-43285][JENKINS-43285].

[#3]: https://github.com/jenkinsci/pipeline-multibranch-defaults-plugin/pull/3
[JENKINS-41108]: https://issues.jenkins-ci.org/browse/JENKINS-41108
[JENKINS-43285]: https://issues.jenkins-ci.org/browse/JENKINS-43285

# v1.1 Released Jan 5, 2017

- Update code for config-file-provider changes. See [PR #1][#1].

[#1]: https://github.com/jenkinsci/pipeline-multibranch-defaults-plugin/pull/1

# v1.0 Released Nov 18, 2016

Initial release; no notes available.
