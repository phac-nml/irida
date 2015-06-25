---
layout: default
---

Building VMs with packer
========================
{:.no_toc}

This guide describes how to set up your workstation to build VM images of IRIDA with [packer](https://packer.io).

* This comment becomes the table of contents
{:toc}

General Requirements
--------------------

You're required to install a few different pieces of software on your machine before you can get started:

1. [packer](https://packer.io) (install guide: <https://packer.io/docs/installation.html>)
2. [VirtualBox](https://www.virtualbox.org) (install guide: <https://www.virtualbox.org/wiki/Linux_Downloads>)
3. [qemu](http://wiki.qemu.org/Main_Page).

Building the VM
---------------

You can build the VM once you've got the prerequisites installed. From the `packer/` directory in the root of the project folder, run:

You **cannot** build qemu and VirtualBox images in parallel, both qemu and VirtualBox want to use the same virtualization features of the processor and qemu clobbers VirtualBox. So you must run:

    packer build -parallel=false template.json

If you want to run *only* one or the other, you can run something like:

    packer build -only=qemu template.json

Or, for VirtualBox:

    packer build -only=virtualbox-iso template.json

This will (for both VirtualBox and qemu):

1. Download a CentOS 7.1 ISO,
2. Run an automated CentOS kickstart script in VirtualBox (VirtualBox should pop up on your screen),
3. Install the VirtualBox tools,
4. Run the customization scripts (importantly running `irida-web.sh` and `irida-galaxy.sh`),
5. Package the customized VirtualBox image as a VirtualBox appliance (found in `packer/output-virtualbox-iso`), and a qemu disk image (found in `packer/output-qemu`).

Using the VM
------------

You can import the `.ovf` file in `packer/output-virtualbox-iso` into VirtualBox by double clicking it, or running something like:

```bash
cd output-virtualbox-iso
xdg-open packer-virtualbox-iso*.ovf
```

### Ports

By default, the appliance is set up using NAT for networking. The appliance is configured to do port forwarding via `localhost` such that you can access IRIDA by navigating to <http://localhost:48888/irida>.

You can SSH into the virtual machine with:

```bash
ssh -p 42222 vagrant@localhost
```

The default password is `vagrant`.

You may also view the Galaxy interface at <http://localhost:49999/>. The default user accounts created in Galaxy are `admin@localhost.localdomain` with password `adminpassword` and `workflow@localhost.localdomain` with password `workflowpassword`.

You may also change the machine to use bridged networking instead of NAT so that you can connect to the machine using an external IP address. Tomcat is configured to respond on port 80, so if you use bridged networking, you can navigate to <http://{bridged-ip}/irida>.

### Log files

We've configured packer to build us an image of CentOS 7.1, which has migrated to [systemd](http://www.freedesktop.org/wiki/Software/systemd/). Traditionally, tomcat logs are found at `/var/log/tomcat/catalina.out`, but you can't find them there with systemd. Instead, you must use `journalctl` to inspect the logs of a service. To view tomcat logs, run:

```bash
sudo journalctl -u tomcat
```

To tail the logs, run:

```bash
sudo journalctl -u tomcat -f
```

The Galaxy log files are not managed or configured by systemd, rather Galaxy manages them itself. You may view Galaxy log files by running:

```bash
sudo tailf /opt/irida/galaxy/galaxy-dist/paster.log
```
