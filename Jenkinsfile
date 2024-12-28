pipeline {
    agent any

    environment {
        AWS_ACCOUNT_ID = credentials('AWS_ACCOUNT_ID') // AWS Account credentials
        AWS_REGION = 'us-east-1'  // Update to your AWS region
        ECR_REPO = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
        IMAGE_NAME = 'gateway-service'
        IMAGE_TAG = "${BUILD_NUMBER}"
        EKS_CLUSTER_NAME = 'your-eks-cluster'  // Update to your cluster name
        NAMESPACE = 'application'  // Namespace for your Gateway Service
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout the source code from SCM (e.g., Git)
                checkout scm
            }
        }

        stage('Build') {
            steps {
                // Build the application using Maven
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Unit Tests') {
            steps {
                // Run unit tests
                sh './mvnw test'
            }
            post {
                always {
                    // Publish test results
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Build the Docker image
                    docker.build("${ECR_REPO}/${IMAGE_NAME}:${IMAGE_TAG}")
                }
            }
        }

        stage('Push Docker Image to ECR') {
            steps {
                script {
                    // Log in to AWS ECR and push the Docker image
                    sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPO}"
                    sh "docker push ${ECR_REPO}/${IMAGE_NAME}:${IMAGE_TAG}"
                    sh "docker tag ${ECR_REPO}/${IMAGE_NAME}:${IMAGE_TAG} ${ECR_REPO}/${IMAGE_NAME}:latest"
                    sh "docker push ${ECR_REPO}/${IMAGE_NAME}:latest"
                }
            }
        }

        stage('Deploy to EKS') {
            steps {
                script {
                    // Update kubeconfig to connect to the EKS cluster
                    sh "aws eks update-kubeconfig --region ${AWS_REGION} --name ${EKS_CLUSTER_NAME}"

                    // Create the namespace if it doesn't exist
                    sh "kubectl create namespace ${NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -"

                    // Deploy the Gateway Service
                    withKubeConfig([credentialsId: 'eks-credentials']) {
                        sh """
                            sed -i 's|IMAGE_URL_PLACEHOLDER|${ECR_REPO}/${IMAGE_NAME}:${IMAGE_TAG}|g' k8s/deployment.yaml
                            kubectl apply -f k8s/configmap.yaml -n ${NAMESPACE}
                            kubectl apply -f k8s/deployment.yaml -n ${NAMESPACE}
                            kubectl apply -f k8s/service.yaml -n ${NAMESPACE}
                        """
                        // Monitor rollout status
                        sh "kubectl rollout status deployment/${IMAGE_NAME} -n ${NAMESPACE}"
                    }
                }
            }
        }
    }

    post {
        always {
            // Clean up the workspace after the pipeline run
            cleanWs()
        }
        success {
            // Send success notification
            echo 'Pipeline completed successfully!'
        }
        failure {
            // Send failure notification
            echo 'Pipeline failed!'
        }
    }
}
