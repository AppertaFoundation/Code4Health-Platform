#!/bin/sh

###
# #%L
# ${project.artifactId} Server
# %%
# Copyright (C) 2012 - 2015 Noesis Informatica
# %%
# All right reserved.
#
# Licensed under the Noesis Informatica License;
# you may not use this file except in compliance with the License.
#
# Access to the source code does not represent waiver of the terms of the
# license. This code is NOT open source. You are not permitted to copy this code!
#
# The License Agreement does not in any way grant you any rights over the
# Intellectual Property of the SOFTWARE PRODUCT. YOU acknowledge that the
# ownership of the Intellectual Property at all times rest with Noesis Informatica Ltd.
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED,
# INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
# MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
#
# Contact: info@noesisinformatica.com
# Website: http://noesisinformatica.com
# #L%
###
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