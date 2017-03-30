
if [ -d  /ci/DNSCore/RegressionTestCB/src/main/java/de/uzk/hki/da ]; then
	rm -rf /ci/DNSCore/RegressionTestCB/src/main/java/de/uzk/hki/da
fi

mkdir -p /ci/DNSCore/RegressionTestCB/src/main/java/de/uzk/hki/da/
cp -R /ci/DNSCore/ContentBroker/src/test/java/de/uzk/hki/da/at /ci/DNSCore/RegressionTestCB/src/main/java/de/uzk/hki/da/
cp -R /ci/DNSCore/ContentBroker/src/test/java/de/uzk/hki/da/test /ci/DNSCore/RegressionTestCB/src/main/java/de/uzk/hki/da/


