export REPO_PATH=file:../.jwildfire-lib/.m2/repository/
mvn deploy:deploy-file -Durl=$REPO_PATH -Dfile=../target/jwildfire-lib-7.31-SNAPSHOT-shaded.jar -DgroupId=jwildfire.org -DartifactId=jwildfire-lib -Dpackaging=jar -Dversion=7.40.0-SNAPSHOT
