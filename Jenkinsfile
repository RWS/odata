#!groovy

@Library('delivery') _

commonBuild {
    dependencyCheck = false

    mavenPhases = [
            default: 'install',
    ]

    mavenProfiles = [default: 'local-build']
}
