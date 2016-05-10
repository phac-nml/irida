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

Building the VM
---------------

You can build the VM once you've got the prerequisites installed. From the `packer/` directory in the root of the project folder, run:

    packer build template.json

This will:

1. Download a CentOS 7.1 ISO,
2. Run an automated CentOS kickstart script in VirtualBox (VirtualBox should pop up on your screen),
3. Install the VirtualBox tools,
4. Run the customization scripts (importantly running `irida-web.sh` and `irida-galaxy.sh`),
5. Package the customized VirtualBox image as a VirtualBox appliance (found in `packer/output-virtualbox-iso`).

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

You may also view the Galaxy interface at <http://localhost:49999/>. The default user accounts created in Galaxy are `admin@galaxy.org` with password `admin`.

You may also change the machine to use bridged networking instead of NAT so that you can connect to the machine using an external IP address. Tomcat is configured to respond on port 80, so if you use bridged networking, you can navigate to <http://{bridged-ip}/irida>.

### Log files

We've configured packer to build us an image of CentOS 7.1, which has migrated to [systemd](http://www.freedesktop.org/wiki/Software/systemd/). Traditionally, tomcat logs are found at `/var/log/tomcat/catalina.out`, but you can't find them there with systemd. Instead, you must use `journalctl` to inspect the logs of a service. To view tomcat logs, run:

```bash
sudo journalctl -u tomcat -f
```

You can view the Galaxy log files by running:

```bash
sudo docker logs -f galaxy
```

