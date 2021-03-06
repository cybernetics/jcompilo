#!/bin/bash
set -e

version=${version-14.0.2}
build=${build-139.659.2}
filename=ideaIC-${version}.tar.gz
url=http://download.jetbrains.com/idea/${filename}
files=(jps-server idea annotations extensions openapi util)
working=build/artifacts/package.idea
idea=${working}/${filename}

if [ "$1" = "clean" ]; then
	rm -rf ${working}
fi

mkdir -p ${working}

if [ ! -f ${idea} ]; then
    wget -O ${idea} ${url}
fi

extract=`printf "idea-IC-${build}/lib/%s.jar " "${files[@]}"`
tar xzv -C ${working} -f ${idea} --strip-components=2 ${extract}

for name in "${files[@]}"; do
    file=${working}/${name}
    pack=${file}-${build}.pack.gz
    echo Packing ${name} ...
    pack200 ${pack} ${file}.jar
    echo Checksums...
    md5sum ${pack} | cut -f 1 -d' ' > ${pack}.md5
    sha1sum ${pack} | cut -f 1 -d' ' > ${pack}.sha1
    echo Uploading...
    s3cmd -P --add-header=Cache-Control:"public, max-age=3600" put ${pack} ${pack}.md5 ${pack}.sha1 s3://repo.bodar.com/com/intellij/${name}/${build}/
done

