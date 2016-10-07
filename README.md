# Motivation
Concept of Pipeline plugins it requires save Jenkinsfile in root repository. Workflow Multibranch Plugin extend this idea to build all branches in repository. This is very easy for CI.

In organization may be many modules in separate repositories (over 100 I have). This is exactly modules. They may be build one Jenkinsfile. But save duplicate Jenkins file each repository makes very hard support.

This is plugin have Jenkinsfile in [global Jenkins script store](https://github.com/jenkinsci/config-file-provider-plugin). And load is for all tasks. 

The Jenkinsfile may load specified Jenkinsfile from SCM for build concrete module branch.

# Basics
This plugin extend [workflow-multibranch-plugin](https://github.com/jenkinsci/workflow-multibranch-plugin) and differs only one option in configuration

# How it works
Config files load in order:

* Jenkinsfile from root in checkout
* Jenkinsfile from [global Jenkins script store](https://github.com/jenkinsci/config-file-provider-plugin)
* Jenkinsfile from [UI](https://jenkins.io/doc/book/pipeline/overview/#writing-pipeline-scripts-in-the-jenkins-ui) - not realized

# Configuration steps
## Create job
Enter name and select job type:

![create job](https://habrastorage.org/files/c77/cb7/9a7/c77cb79a7c794f7aa25827dafafb64b0.png)

## Select build mode
In job options go to "Build Configuration" section and select "by default Jenkinsfile":

![select option](https://habrastorage.org/files/112/bed/263/112bed26372e4b239e12353dc0d73ef6.png)


If your select option "by Jenkinsfile" task will also work as a Workflow Multibranch Plugin

## Select other options
All other options fully equivalent with Workflow Multibranch Plugin

## Create and save default Jenkinsfile
Write your default Jenkinsfile ([Pipeline write tutorial](https://github.com/jenkinsci/pipeline-plugin/blob/master/TUTORIAL.md)) and go to "Managed files" in jenkins manage section:

![manage files section](https://habrastorage.org/files/5f5/431/300/5f5431300e8e431ab66ef975f41aaf76.png)


Add new config file with Groovy type and Jenkinsfile name:

![add config](https://habrastorage.org/files/9d8/143/155/9d81431553144a7bb73320a5a0856c5e.png)


Write pipeline:

![pipeline](https://habrastorage.org/files/37e/807/853/37e807853c03404bacf8362a1bfc3c50.png)

***After change global scripts may require administrative [approval](https://wiki.jenkins-ci.org/display/JENKINS/Script+Security+Plugin)***

## Usage sample
All modules builds default Jenkinsfile. This is libs and other dependencies. But have modules with specific build configurations (run hard tests, deploy to docker etc.)
Default configurations sample:
```groovy
#!groovy​
node('builder-01||builder-02||builder-03') {
    try {
        stage('Checkout') {...}

        stage('Prepare') {...}

        def hasJenkinsfile = fileExists 'Jenkinsfile'
        if (hasJenkinsfile) {
            load 'Jenkinsfile' // Loading Jenkinsfile from checkout
        } else {
            stage('Build') {...}

            stage('Test') {...}
        }
        currentBuild.result = 'SUCCESS'
    } catch (error) {
        currentBuild.result = 'FAILURE'
        throw error
    } finally {
        stage('Clean') {...}
    }
}
```

Jenkinsfile in SCM example:
```groovy
#!groovy​
stage('Build') {...}

stage('Deploy and Tests') {...}
currentBuild.result = 'SUCCESS'
```

# Versions
To be released

# Authors
* **Saponenko Denis** - *Initial work* - [vaimr](https://github.com/vaimr)

See also the list of [contributors](https://github.com/vaimr/workflow-multibranch-def-plugin/contributors) who participated in this project.

# License
```
The MIT License

Copyright (c) 2016 Saponenko Denis

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```