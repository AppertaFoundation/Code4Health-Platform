#!/bin/sh

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

echo "Location of run script is '$SOURCE'"
RDIR="$( dirname "$SOURCE" )"
DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
if [ "$DIR" != "$RDIR" ]; then
  echo "DIR '$RDIR' resolves to '$DIR'"
fi

cd $DIR
printf '%s\n' "Running ${project.artifactId} from folder: ${DIR}" # to print to stdout
# Always run using 'exec' - so bash will kill the jvm when it receives a kill signal - see http://veithen.github.io/2014/11/16/sigterm-propagation.html
exec java -Xms256m -Xmx1000m -Dlog4j.configuration=file:${DIR}/config/log4j.properties -jar ${DIR}/${project.artifactId}.${package.type} -Dspring.profiles.active=prod,swagger --spring.profiles.active=prod,swagger