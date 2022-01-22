export REPO_PATH=file:../.local-maven-repo/.m2/repository/
export GROUP=local
mvn deploy:deploy-file -Durl=$REPO_PATH -Dfile=./LeapJava.jar -DgroupId=$GROUP -DartifactId=LeapJava -Dpackaging=jar -Dversion=0.0.1-SNAPSHOT
mvn deploy:deploy-file -Durl=$REPO_PATH -Dfile=./jwildfire-legacy-lib.jar -DgroupId=$GROUP -DartifactId=jwildfire-legacy-lib -Dpackaging=jar -Dversion=0.0.1-SNAPSHOT
mvn deploy:deploy-file -Durl=$REPO_PATH -Dfile=./jcodec-0.2.6-SNAPSHOT.jar -DgroupId=$GROUP -DartifactId=jcodec -Dpackaging=jar -Dversion=0.0.1-SNAPSHOT
mvn deploy:deploy-file -Durl=$REPO_PATH -Dfile=./jcodec-javase-0.2.6-SNAPSHOT.jar -DgroupId=$GROUP -DartifactId=jcodec-javase -Dpackaging=jar -Dversion=0.0.1-SNAPSHOT
mvn deploy:deploy-file -Durl=$REPO_PATH -Dfile=./quickhull3d.1.4.jar -DgroupId=$GROUP -DartifactId=quickhull3d -Dpackaging=jar -Dversion=1.4-SNAPSHOT
mvn deploy:deploy-file -Durl=$REPO_PATH -Dfile=./oobj-loader.jar -DgroupId=$GROUP -DartifactId=oobj-loader -Dpackaging=jar -Dversion=0.0.1-SNAPSHOT
#mvn deploy:deploy-file -Durl=$REPO_PATH -Dfile=./janino.jar -DgroupId=$GROUP -DartifactId=janino -Dpackaging=jar -Dversion=0.0.1-SNAPSHOT
