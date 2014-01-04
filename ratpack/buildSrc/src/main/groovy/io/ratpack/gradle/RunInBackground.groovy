package io.ratpack.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class RunInBackground extends DefaultTask {

    String command
    List<String> args = []
    File directory
    String readyText
    int timeout = 10


    @TaskAction
    void start() {
        def process = new ProcessBuilder(command, *args).directory(directory).start()
        project.ext.applicationUnderTest = process

        def latch = new CountDownLatch(1)
        Thread.start {
            try {
                process.errorStream.eachLine { String line ->
                    if (latch.count) {
                        if (line.contains(readyText)) {
                            latch.countDown()
                        }
                    }
                }
            } catch (IOException ignore) {}
        }

        if (!latch.await(timeout, TimeUnit.SECONDS)) {
            throw new RuntimeException("Timeout waiting for application to start")
        }
    }
}
