#!/bin/bash

# Based on Start-Script provided by Azureus

######## CONFIGURE ########
JAVA_PROGRAM_DIR=""				# use full path to java bin dir, ex. "/usr/java/j2sdk1.4.2/bin/"
#PROGRAM_DIR="/home/username/apps/tvbrowser"	# use full path to TV-Browser bin dir
##########i#################

MSG0="Loading TV-Browser:"
MSG1="Starting TV-Browser..."
MSG2="Java exec found in "
MSG3="OOPS, your java version is too old "
MSG4="You need to upgrade to JRE 6.0 or newer from http://java.sun.com"
MSG5="Suitable java version found "
MSG6="Configuring environment..."
MSG7="OOPS, you don't seem to have a valid JRE "
MSG8="OOPS, unable to locate java exec in "
MSG9=" hierarchy"
MSG10="Java exec not found in PATH, starting auto-search..."
MSG11="Java exec found in PATH. Verifying..."

look_for_java()
{
  JAVADIR=/usr/java
  IFS=$'\n'
  potential_java_dirs=(`ls -1 "$JAVADIR" | sort | tac`)
  IFS=
  for D in "${potential_java_dirs[@]}"; do
    if [[ -d "$JAVADIR/$D" && -x "$JAVADIR/$D/bin/java" ]]; then
      JAVA_PROGRAM_DIR="$JAVADIR/$D/bin/"
      echo $MSG2 $JAVA_PROGRAM_DIR
      if check_version ; then
        return 0
      else
        return 1
      fi
    fi
  done
  echo $MSG8 "${JAVADIR}/" $MSG9 ; echo $MSG4
  return 1
}

check_version()
{
  JAVA_HEADER=`${JAVA_PROGRAM_DIR}java -version 2>&1 | head -n 1`
  JAVA_IMPL=`echo ${JAVA_HEADER} | cut -f1 -d' '`
  if [ "$JAVA_IMPL" = "java" ] ; then
    VERSION=`echo ${JAVA_HEADER} | sed "s/java version \"\(.*\)\"/\1/"`
    if echo $VERSION | grep "^1.[0-4]" ; then
      echo $MSG3 "[${JAVA_PROGRAM_DIR}java = ${VERSION}]" ; echo $MSG4
      return 1
    else
      echo $MSG5 "[${JAVA_PROGRAM_DIR}java = ${VERSION}]" ; echo $MSG6
      return 0	      
    fi
  else
    echo $MSG7 "[${JAVA_PROGRAM_DIR}java = ${JAVA_IMPL}]" ; echo $MSG4
    return 1
  fi
}

echo $MSG1

# locate and test the java executable
if [ "$JAVA_PROGRAM_DIR" == "" ]; then
  if ! command -v java &>/dev/null; then
    echo $MSG10
    if ! look_for_java ; then
      exit 1
    fi
  else
    echo $MSG11
    if ! check_version ; then
      if ! look_for_java ; then
        exit 1
      fi
    fi
  fi
fi


# get the app dir if not already defined
if [ -z "$PROGRAM_DIR" ]; then
    if [ -L $0 ] 
    then
        SL= `file $0 | sed -e 's/.*to..\(.*\)./\1/'`
        PROGRAM_DIR=`dirname $SL`
    else
        PROGRAM_DIR=`dirname $0`
    fi
    
    PROGRAM_DIR=`cd "$PROGRAM_DIR"; pwd`
else
    if [ "$(echo ${PROGRAM_DIR}/*.jar)" = "${PROGRAM_DIR}/*.jar" ]; then
	echo "You seem to have set an invalid PROGRAM_DIR, unable to continue!"
	exit 1
    elif ! (echo ${PROGRAM_DIR}/*.jar | grep tvbrowser.jar >/dev/null 2>&1 ); then
	echo "Unable to locate tvbrowser.jar in $PROGRAM_DIR, aborting!"
	exit 1
    fi
fi

echo $MSG0

cd ${PROGRAM_DIR}

echo "${JAVA_PROGRAM_DIR}java -Xms16m -Xmx256m  -Djava.library.path=\"${PROGRAM_DIR}\" -Dpropertiesfile=linux.properties -jar tvbrowser.jar '$@'"
${JAVA_PROGRAM_DIR}java -Xms16m -Xmx128m -Djava.library.path="${PROGRAM_DIR}" -Dpropertiesfile=linux.properties -jar tvbrowser.jar "$@"

echo "TV-Browser TERMINATED."

