# KubePAPS
This repository contains all the classes and test done on the partition modulo of PAPS. This implementation test how well will perform PAPS if integrated with a real-world framework such as Kubernetes.
In this repository it's also present a starting point for the implementation of the Allocation and Implementation modules. The idea was to keep as much as possible the code of the original prototype, but the whole implementation was not possible to complete.

The finished and used classes are in the partition package.
the main class prova contains a set of test that were used to trubleshoot our prototype. 
All the classes inside the partition packge have been copied inside another project that integrated our code with the building functionalities of the frameWork openfaas. 
HOwever, all the testing main are only in this project, since the code has been cleaned before shipping it to openfaas.
