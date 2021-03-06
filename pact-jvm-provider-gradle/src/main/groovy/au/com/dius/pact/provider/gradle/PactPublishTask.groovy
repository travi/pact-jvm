package au.com.dius.pact.provider.gradle

import au.com.dius.pact.provider.broker.PactBrokerClient
import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.GradleScriptException
import org.gradle.api.tasks.TaskAction

/**
 * Task to push pact files to a pact broker
 */
@SuppressWarnings('Println')
class PactPublishTask extends DefaultTask {

    @TaskAction
    void publishPacts() {
        if (!project.pact.publish) {
            throw new GradleScriptException('You must add a pact publish configuration to your build before you can ' +
                'use the pactPublish task', null)
        }

        PactPublish pactPublish = project.pact.publish
        if (pactPublish.pactDirectory == null) {
            pactPublish.pactDirectory = project.file("${project.buildDir}/pacts")
        }
        if (pactPublish.version == null) {
            pactPublish.version = project.version
        }

        def brokerClient = new PactBrokerClient(pactPublish.pactBrokerUrl)
        File pactDirectory = pactPublish.pactDirectory as File
        pactDirectory.eachFileMatch(FileType.FILES, ~/.*\.json/) { pactFile ->
            print "Publishing ${pactFile.name} ... "
            println brokerClient.uploadPactFile(pactFile, pactPublish.version)
        }
    }

}
