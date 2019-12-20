node {

    try {
        stage('Checkout'){
            checkout scm
        }
        stage('Test'){
            sh 'mvn test'
        }
        stage('Build'){
            sh 'mvn install -DskipTests=true'
        }
    }

    catch (err) {
        
        throw err
    }
    
    finally {
        sh """
        """
        }
}
