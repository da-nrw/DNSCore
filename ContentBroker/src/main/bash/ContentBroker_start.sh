#!/bin/bash
# The Contentbroker Start Skript


OS=`uname -s`
case "$OS" in
SunOS)
	if [ "$(echo $LOGNAME)" == "root" ]; then
		echo "This script must not be run as root" 1>&2
		exit 1
	fi	
	;;
*)
	if [ "$(id -u)" == "0" ]
	then
		echo "This script must not be run as root" 1>&2
		exit 1
	fi
	;;
esac




if [[ -f /tmp/cb.running ]] ; then
    echo "ContentBroker is already running"
    exit 1
fi

if [ "${DNSCORE_AT_IMAGEMAGICK_WRAPPERS:-false}" = "true" ]; then
    SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
    AT_BIN_DIR="$SCRIPT_DIR/at-bin"

    echo "ContentBroker: creating ImageMagick AT wrappers"

    mkdir -p "$AT_BIN_DIR" || {
        echo "ContentBroker: could not create $AT_BIN_DIR" >&2
        exit 1
    }

    cat > "$AT_BIN_DIR/convert" <<'EOF'
#!/bin/bash

FLAG_FILE="${DNSCORE_IMAGEMAGICK_DISABLED_FLAG:-/tmp/dnscore-imagemagick-disabled}"
REAL_CONVERT="${DNSCORE_REAL_CONVERT:-/usr/bin/convert}"

if [ -e "$FLAG_FILE" ]; then
    echo "ImageMagick convert intentionally disabled for acceptance test" >&2
    exit 127
fi

exec "$REAL_CONVERT" "$@"
EOF

    cat > "$AT_BIN_DIR/identify" <<'EOF'
#!/bin/bash

FLAG_FILE="${DNSCORE_IMAGEMAGICK_DISABLED_FLAG:-/tmp/dnscore-imagemagick-disabled}"
REAL_IDENTIFY="${DNSCORE_REAL_IDENTIFY:-/usr/bin/identify}"

if [ -e "$FLAG_FILE" ]; then
    echo "ImageMagick identify intentionally disabled for acceptance test" >&2
    exit 127
fi

exec "$REAL_IDENTIFY" "$@"
EOF

    chmod 755 \
        "$AT_BIN_DIR/convert" \
        "$AT_BIN_DIR/identify" || {
        echo "ContentBroker: could not make ImageMagick wrappers executable" >&2
        exit 1
    }

    export PATH="$AT_BIN_DIR:$PATH"

    echo "ContentBroker: ImageMagick AT wrappers enabled"
    echo "ContentBroker: convert=$(command -v convert)"
    echo "ContentBroker: identify=$(command -v identify)"
fi

touch /tmp/cb.running
nohup $JAVA_HOME/bin/java $JAVA_OPTS -jar ContentBroker.jar $1 1>log/stdout.log 2>log/stderr.log &

