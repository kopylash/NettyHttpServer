NettyHttpServer
==========
Simple Http Server based on Netty framework

Netty Framework version 4.0.26.Final has been used (stable from maven repo)

-request http://127.0.0.1:8080/hello displays "Hello World" in 10 seconds.
-request http://127.0.0.1:8080/redirect?url=targetUrl redirects to http://targetUrl.
-request http://127.0.0.1:8080/status displays info about connections, requests, etc...

Implementation details.
During developing I used Netty.io site, their user guide, Habrahabr.com, JavaDocs of Netty framework.
There are comments in code that explain the nuances of implementation, I hope it will be enough.


