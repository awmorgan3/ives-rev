// This prevents multiple builds from being queued on a single branch
def buildNumber = env.BUILD_NUMBER as int
if (buildNumber > 1) milestone(buildNumber - 1)
milestone(buildNumber)

pipeline {
    agent { label "MEM_BD422"}
    
    environment {
        projectName     =   "IVES-BWAS"
        GROUP_ID        =   "gov.irs.ives.bwas"
        mvnSettings     =   "/opt/app/CICD/.m2/ola-settings.xml"
        NEXUS_IQ_ID     =   "ola.ives.ola.bwas.ives.build"
        currentStage    =   "START"
        mvnEnforcerSkip =   "true"
        
        // OpenShift vars
        OC_TOOL_PATH         = "/opt/app/CICD/tools/oc"
        BUILD_DIR            = "${env.WORKSPACE}/.k8sbuild"
        OPENSHIFT_APP        = "ives-bwas"
        KUBECONFIG           = "${env.DEVTEST_KUBECONFIG_PATH}"
        DATACENTER           = "mem"
        REGISTRY_AUTH_FILE   = "${env.WORKSPACE}/config.json"
        
        // Java 17
        JAVA_HOME       = "/usr/lib/jvm/java-17-openjdk"
        PATH            = "${env.JAVA_HOME}/bin:${PATH}"
        
        // Image Build
        IMAGE_GROUP     =   "bola"
        DOCKER_IMAGE    =   "wa-ives-bwas"
        DOCKER_TAG      =   "${BUILD_NUMBER}-${env.GIT_HASH}"
        DOCKER_REGISTRY =   "your-registry"
        
        // AppScan vars
        M2_REPO                 =   "/opt/app/CICD/.m2/repository"
        scanHome                =   "${WORKSPACE}"
        artifactHome            =   "${WORKSPACE}/Artifacts"
        consoleLog              =   "${artifactHome}/ConsoleOut.log"
        ounceToken              =   "/home/buildsrdstestsvc/.ounce/ouncecli.token"
        appscanScriptLocation   =   "${scanHome}/appscan.script"
        scanPafFile             =   "wa-ives-bwas"
        cweReportFile           =   "$projectName-CweReport.pdf"
        
        // Manifest files
        IEP_MANIFEST = 'IEPCloudmanifest.txt'
        CSSR_MANIFEST = 'CSSRmanifest.txt'
    }
    
    options {
        disableConcurrentBuilds()
        timeout(activity: true, time: 40, unit: 'DAYS')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        skipDefaultCheckout(true)
    }

    triggers { githubPush() }

    tools { maven 'Maven-3.9.2' }
    
    stages {
        stage('Clean') {
            steps {
                deleteDir()
                script { currentStage = "Clean" }
            }
        }
        
        stage('Fetch') {
            steps {
                script { currentStage = "Fetch" }
                waCheckoutScm()
                script {
                    env.GIT_HASH    =   sh (script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    env.GIT_FULL_HASH = sh (script: "git rev-parse HEAD", returnStdout: true).trim()
                    env.GIT_EMAIL   =   sh (script: "git show -s --format=%ae", returnStdout: true).trim()
                    env.GIT_AUTHOR  =   sh (script: "git show -s --format=%an", returnStdout: true).trim()
                    env.GIT_URL     =   sh (script: "git config remote.origin.url", returnStdout: true).trim().replaceAll("git@github.enterprise.irs.gov:", "https://github.enterprise.irs.gov/").replaceAll(/\.git/,"")
                    env.GIT_REPO_NAME = GIT_URL.tokenize('/')[-1]
                    env.POM_VERSION =   waMavenFunctions.getPomVersion()
                }
                writeFile file:"${env.WORKSPACE}/app-config/application-default.properties", text:"${env.PROP_FILE_TEXT}"
                writeFile file:'wa-oc-cli.sh', text:libraryResource("wa-oc-cli.sh")
                sh "chmod +x ./wa-oc-cli.sh"
                sh "mkdir -p ${BUILD_DIR} && cp -r ${env.WORKSPACE}/Dockerfile ${env.WORKSPACE}/.dockerignore ${env.WORKSPACE}/app-config/. ${BUILD_DIR}"
                sh "$OC_TOOL_PATH --insecure-skip-tls-verify=true --kubeconfig=${KUBECONFIG} --context=mem-sbx version"
            }
        }
        
        stage('Maven Build') {
            stages {
                stage("Version for Release") {
                    when { branch "release/*" }
                    steps {
                        script { currentStage = "Version for Release" }
                        echo "Existing POM Version: ${env.POM_VERSION}"
                        script { env.RELEASE_VERSION = env.POM_VERSION.replaceAll("-SNAPSHOT", "").trim() + ".${BUILD_NUMBER}" }
                        echo "Detected release branch, updating POM version: ${RELEASE_VERSION}"
                        script { mvnEnforcerSkip = "false" }
                        sh "mvn --version"
                        sh "mvn -B -U -s $mvnSettings versions:set -DartifactId=* -DgroupId=* -DoldVersion=* -DnewVersion=${RELEASE_VERSION}"
                    }
                }
                stage("Maven Package") {
                    steps {
                        script { currentStage = "Maven Package" }
                        echo "Git Hash: ${env.GIT_HASH}"
                        sh "mvn -B -U -s $mvnSettings package -Denforcer.skip=$mvnEnforcerSkip"
                    }
                }
            }
        }
        
        stage("Publish: JUnit, JavaDoc, NexusIQ, SonarQube") {
            parallel {
                stage('Publish JUnit Reports') {
                    steps {
                        script { currentStage = "Publish JUnit Reports" }
                        junit '**/target/surefire-reports/*.xml, **/target/failsafe-reports/*.xml'
                    }
                }
                stage('Publish JavaDoc Reports') {
                    steps {
                        script { currentStage = "Publish JavaDoc Reports" }
                        sh "mvn -s $mvnSettings javadoc:aggregate-jar -q"
                        javadoc javadocDir: 'target/reports/apidocs', keepAll: false
                    }
                }
                stage('NexusIQ Scans') {
                    stages {
                        stage('NexusIQ Build Scan') {
                            when { not { anyOf { branch 'develop'; branch 'release/*'; } } }
                            steps {
                                script { currentStage = "NexusIQ Build Scan" }
                                waNexusIq(NEXUS_IQ_ID, 'build', 'NEXUS_IQ_ID', [[scanPattern: '**/*.jar']])
                            }
                        }
                        stage('NexusIQ Stage-Release Scan') {
                            when { branch 'develop' }
                            steps {
                                script { currentStage = "NexusIQ Stage-Release Scan" }
                                waNexusIq(NEXUS_IQ_ID, 'stage-release', 'NEXUS_IQ_ID', [[scanPattern: '**/target/*.jar']])
                            }
                        }
                        stage('NexusIQ Release Scan') {
                            when { branch 'release/*' }
                            steps {
                                script { currentStage = "NexusIQ Release Scan" }
                                waNexusIq(NEXUS_IQ_ID, 'release', 'NEXUS_IQ_ID', [[scanPattern: '**/target/*.jar']])
                            }
                        }
                    }
                }
                stage('SonarQube Scan') {
                    environment {
                        currentStage      = "SonarQube Scan"
                        QUALITYGATES       = "${env.BRANCH_NAME =~ '^release/*' ? 'true' : 'false'}"
                    }
                    when {
                        anyOf {
                            branch "develop";
                            branch "release/*";
                        }
                    }
                    steps {
                        echo "Run Sonarqube"
                        txpSonarScan(QUALITYGATES)
                    }
                }
            }
        }
        
        stage("Container Build") {
            environment {
                currentStage      = "Container Build"
                ENVIRONMENT       = "${env.BRANCH_NAME =~ '^release/*' ? 'sat' : 'sbx'}"
                OPENSHIFT_PROJECT = "cs-webapps-${ENVIRONMENT}"
                CONTEXT           = "${DATACENTER}-${ENVIRONMENT}"
            }
            when {
                anyOf {
                    branch "develop";
                    branch "release/*";
                    branch "test/*";
                    branch "develop-jdk17*";
                }
            }
            steps {
                echo "OPENSHIFT_PROJECT: ${OPENSHIFT_PROJECT}"
                echo "Container Tag: ${env.CONTAINER_TAG}"
                sh "find ./**/target/ -type f -name '*-exec.jar' -exec cp {} ${BUILD_DIR} \\; && ls -lah --group-directories-first ${BUILD_DIR}"
                waContainerBuild(
                    podmanSudo: "false",
                    tlsVerify: "false",
                    buildDir: BUILD_DIR,
                    imageTag: CONTAINER_TAG,
                    ocApp: OPENSHIFT_APP,
                    ocProject: OPENSHIFT_PROJECT,
                    imageGroup: IMAGE_GROUP
                )
            }
        }
        
        stage('Deploy to IEP') {
            when {
                anyOf {
                    branch "develop";
                    branch "release/*";
                }
            }
            steps {
                script { currentStage = "Deploy to IEP" }
                script {
                    def manifest = readFile(IEP_MANIFEST).trim().split('\n')
                    def appName = manifest[0]
                    def version = manifest[1]
                    def env = manifest[2]
                    
                    sh """
                        sed -i 's|\${DOCKER_REGISTRY}|\${DOCKER_REGISTRY}|g' k8s/iep-deployment.yaml
                        sed -i 's|\${DOCKER_TAG}|\${DOCKER_TAG}|g' k8s/iep-deployment.yaml
                        kubectl apply -f k8s/iep-deployment.yaml
                    """
                }
            }
        }
        
        stage('Deploy to CSSR') {
            when {
                anyOf {
                    branch "develop";
                    branch "release/*";
                }
            }
            steps {
                script { currentStage = "Deploy to CSSR" }
                script {
                    def manifest = readFile(CSSR_MANIFEST).trim().split('\n')
                    def appName = manifest[0]
                    def version = manifest[1]
                    def env = manifest[2]
                    def priority = manifest[4]
                    def replicas = manifest[5]
                    def memory = manifest[6]
                    def cpu = manifest[7]
                    
                    sh """
                        sed -i 's|\${DOCKER_REGISTRY}|\${DOCKER_REGISTRY}|g' k8s/cssr-deployment.yaml
                        sed -i 's|\${DOCKER_TAG}|\${DOCKER_TAG}|g' k8s/cssr-deployment.yaml
                        sed -i 's/replicas: 3/replicas: ${replicas}/g' k8s/cssr-deployment.yaml
                        sed -i 's/memory: "2048Mi"/memory: "${memory}Mi"/g' k8s/cssr-deployment.yaml
                        sed -i 's/cpu: "2000m"/cpu: "${cpu}m"/g' k8s/cssr-deployment.yaml
                        kubectl apply -f k8s/cssr-deployment.yaml
                    """
                }
            }
        }
        
        stage("Tag Release") {
            when { branch "release/*" }
            steps {
                script { currentStage = "Stage to Tag a Release" }
                script {
                    waTagReleases(RELEASE_VERSION, projectName)
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        failure {
            echo "BUILD FAILED @ ${currentStage}"
            waSendFailureEmail(currentStage)
        }
    }
} 