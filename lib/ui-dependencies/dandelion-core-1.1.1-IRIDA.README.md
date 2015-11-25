The dandelion-core-1.1.1-IRIDA.jar is derived from [Dandelion Core](https://github.com/dandelion/dandelion/tree/master/dandelion-core) tagged at version 1.1.1.

There are modifications to the Dandelion source code to facilitate downloading extremely large files by checking if the `ByteArrayResponseWrapper` is committed and returning if it is.  
This is the result of [Issue #93](https://github.com/dandelion/dandelion/issues/93) on the Dandelion project.

The modified code is available on [Josh Adam's Github Page](https://github.com/joshsadam/dandelion)