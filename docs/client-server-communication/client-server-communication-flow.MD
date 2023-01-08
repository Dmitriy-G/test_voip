## Description

While the data transferring process all UDP packages will be encrypted by RSA. All clients and servers must have a private and public key.

### The package flow
1. Client sends the package to the server. This package is encrypted by a public server key.
2. Server decrypting this package through own private key.
3. Server walking by all active clients and send this package, previously encrypt this package through the public key of the specific client.
4. The client receives a package from the server, and decrypting it through its own private key.
