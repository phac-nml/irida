Building Packer Images
======================

You can find more on building packer images in our documentation package, but here's a quick heads up.

You **cannot** build qemu and VirtualBox images in parallel, both qemu and VirtualBox want to use the same virtualization features of the processor and qemu clobbers VirtualBox. So you must run:

    packer build -parallel=false template.json

If you want to run *only* one or the other, you can run something like:

    packer build -only=qemu template.json

Or, for VirtualBox:

    packer build -only=virtualbox-iso template.json
