## Description

Because UDP it's an unreliable and unsecured transport we need some ways to secure our data.
These will be using RSA encryption and AES, because RSA supports a restricted packet size and our packets with sound are large.

1. The client sent to the server's initial UDP package that contains the username. It means that the client wants to connect to the server. If the client does not get a response from the server this package will be retried after some time.

2. The server sends to the client a UDP package, this package contains the public RSA key.

3. The client sends to the server a UDP package, this package contains the client's public key.

4. The server sends to the client AES key, that was encrypted by RSA

5. The client sends to the server AES key, that was encrypted by RSA

6. The client and server can send each other encrypted (AES) UDP packages. If any deviation cases, the server can move the socket to the blacklist and ignore his packages, also a record of it will be presented in the log.

7. Disconnection - client and server can make a disconnect through a special UDP package. Also, disconnection will be automatic if the client or server does not send any data while the latest 30 seconds. After disconnecting, the client and server must retry steps from 1 to 3. While disconnecting client's public key will be revoked. The server public key will be revoked after reboot or after some TTL.
