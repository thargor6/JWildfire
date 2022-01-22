export REPO_PATH=file:../../JWildfireLib/orig_repo/.m2/repository/
mvn deploy:deploy-file -Durl=$REPO_PATH -Dfile=../target/jwildfire-lib-7.40-SNAPSHOT-shaded.jar -DgroupId=org.jwildfire -DartifactId=jwildfire-lib -Dpackaging=jar -Dversion=7.40.0-SNAPSHOT
