pipeline {
    agent any

    tools {
        jdk 'JDK21'
        gradle 'Gradle'
    }

    environment {
        HOST_IP = credentials('HOST-IP')
        HOST_NAME = "ubuntu"
        HOST_PASSWORD = credentials('HOST_PASSWORD')
        HARBOR_URL = credentials('harbor-url')
        SPRING_DIR = "CloBee-backend"
        JAR_BUILD_DIR = "/var/appdir/backend"
        CD_DIR = "CloBee-CD"

        GITHUB_TOKEN = credentials('GitHub_Token')
        GIT_USERNAME = "kcs19"
    }

    stages {
        stage('Prepare Workspace') {
            steps {
                sh "rm -rf ${SPRING_DIR}"
                sh "mkdir -p ${SPRING_DIR}"
            }
        }

        stage('Checkout Spring Boot') {
            steps {
                dir(SPRING_DIR) {
                    git branch: 'main',
                        url: 'https://github.com/4ourClover/CloBee-backend.git'
                }
            }
        }

        stage('Build Spring Boot App') {
            steps {
                dir(SPRING_DIR) {
                    sh "mkdir -p ${JAR_BUILD_DIR}"
                    sh "gradle clean build -x test"
                    sh "cp build/libs/*.jar ${JAR_BUILD_DIR}"
                }
            }
        }

        stage('copy Dockerfile') {
            steps {
                dir(SPRING_DIR) {
                    sh "cp Dockerfile ${JAR_BUILD_DIR}"
                }
            }
        }

        stage('Build Docker Image and Push to Harbor') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'harbor-credentials',
                                                  usernameVariable: 'HARBOR_USER',
                                                  passwordVariable: 'HARBOR_PASS')]) {
                    sh """
                        sshpass -p ${HOST_PASSWORD} ssh -o StrictHostKeyChecking=no ${HOST_NAME}@${HOST_IP} '
                        cd /home/ubuntu/appdir/backend &&
                        echo ${HARBOR_PASS} | docker login ${HARBOR_URL} -u ${HARBOR_USER} --password-stdin &&
                        docker build -t ${HARBOR_URL}/library/spring-app:${BUILD_NUMBER} . &&
                        docker push ${HARBOR_URL}/library/spring-app:${BUILD_NUMBER}
                        '
                    """
                }
            }
        }

        stage('Checkout CD Repository') {
            steps {
                // CD 레포지토리를 별도 디렉토리로 체크아웃
                dir(CD_DIR) {
                    git branch: 'main',
                        url: 'https://github.com/4ourClover/CloBee-CD.git',
                        credentialsId: 'GitHub_Token'  // 자격 증명 사용 (선택 사항)
                    sh "ls -la"  // 체크아웃 확인
                }
            }
        }

        stage('Make Changes to CD Repository') {
            steps {
                // CD 레포지토리 디렉토리에서 작업
                dir(CD_DIR) {
                    // Git 설정
                    sh """
                        ls -la ./nodes/backend/2a || echo "backend directory not found"
                        sed -i 's|image: .*spring-app:.*|image: clobee-harbor.shop/library/spring-app:${BUILD_NUMBER}|' ./nodes/backend/2a/spring-deployment-2a.yaml

                        ls -la ./nodes/backend/2c || echo "backend directory not found"
                        sed -i 's|image: .*spring-app:.*|image: clobee-harbor.shop/library/spring-app:${BUILD_NUMBER}|' ./nodes/backend/2c/spring-deployment-2c.yaml

                        # 확인 출력
                        echo "=== 2a.yaml ==="
                        cat ./nodes/backend/2a/spring-deployment-2a.yaml

                        echo "=== 2c.yaml ==="
                        cat ./nodes/backend/2c/spring-deployment-2c.yaml
                    """
                }
            }
        }

        stage('Commit and Push CD Repository') {
            steps {
                // CD 레포지토리 디렉토리에서 작업
                dir(CD_DIR) {
                    sh """
                    git config user.name "Jenkins Bot"
                    git config user.email "jenkins@clobee.io"

                    git add .

                    # 변경사항이 있는지 확인하고 커밋
                    git diff --staged --quiet || git commit -m "fix : spring.yaml version change"

                    git push https://${GIT_USERNAME}:${GITHUB_TOKEN}@github.com/4ourClover/CloBee-CD.git main
                    """
                }
            }
        }
    }

    post {
        always {
            echo "Build completed"
        }
        success {
            echo "Build succeeded!"
        }
        failure {
            echo "Build failed!"
        }
    }
}
