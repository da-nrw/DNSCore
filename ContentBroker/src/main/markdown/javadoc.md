### Generating JavaDoc

You can generate the JavaDoc API documentation using the JavaDoc maven plugin. Type the following commands after changing to your local git repository directory (DNSCore or SIP-Builder):

mvn javadoc:javadoc  
mvn javadoc:test-javadoc

### Uploading JavaDoc to GitHub Pages

The generated JavaDoc files (both main and test) can be published on GitHub Pages via the following command:

mvn scm-publish:publish-scm

Afterwards you can find the documentation at:

http://da-nrw.github.io/DNSCore/apidocs/ (DNSCore Main)  
http://da-nrw.github.io/DNSCore/testapidocs/ (DNSCore Test)  
http://da-nrw.github.io/SIP-Builder/apidocs (SIP-Builder Main)  
http://da-nrw.github.io/SIP-Builder/testapidocs (SIP-Builder Test)
