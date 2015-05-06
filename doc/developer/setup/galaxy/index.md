---
layout: default
---

The workstation setup instructions describe the prerequisites that are required for running our integration test suite (that in turn installs Galaxy), but the Galaxy that's used by the tests is transient and discarded after the tests are complete.

This guide describes how to configure Galaxy for executing IRIDA workflows. Please note that this guide is suitable for setting up a Galaxy instance for running on a developer workstation, **not** for setting up a cluster or production environment.

* This comment becomes the table of contents
{:toc}

Prerequisites
-------------

For completeness sake, we'll list *all* requirements for Galaxy here. You may have installed some of the prerequisites in the [developer set up guide](..).

Galaxy and the SNVPhyl pipeline require several tools to be installed outside of Galaxy:

* MUMmer (from: <http://mummer.sourceforge.net>)
* SAMtools (0.1.x series, specifically **NOT** the 1.0 series, from: <http://samtools.sourceforge.net/>)
  * ncurses development sources (`libncurses5-dev` on Ubuntu or `ncurses-devel` on CentOS)
* BioPerl (**Specifically version 1.6.901**, please use `cpanm` to install <http://search.cpan.org/CPAN/authors/id/C/CJ/CJFIELDS/BioPerl-1.6.901.tar.gz>)
* The Perl module `Parallel::ForkManager`
* Development tools (`gcc`, etc.)
* A database server (we prefer MySQL or MariaDB)
* Mercurial DVCS
* CMake
* Python `pip`
* YAML development sources (`libyaml-dev` on Ubuntu or `libyaml-devel` on CentOS)
* Python development sources (`python-dev` on Ubuntu or `python-devel` on CentOS)
* ZLib development sources (`zlib1g-dev` on Ubuntu or `zlib-devel` on CentOS)

### Install instructions for Ubuntu

These commands were tested on an up-to-date install of Ubuntu 14.04.

You can copy/paste the command below to install the prerequisites on Ubuntu:

```bash
sudo apt-get install mariadb-{client,server} cmake mercurial libyaml-dev \\
                     python-pip python-dev zlib1g-dev cpanminus mummer \\
                     samtools build-essential
```

### Install instructions for CentOS

These commands were tested on an up-to-date install of CentOS 7.

You can copy/paste the command below to install the prerequisites on CentOS:

```bash
sudo yum install epel-release && \\
sudo yum install mariadb{,-server} cmake mercurial libyaml-devel python-pip \\
                 python-devel zlib-devel perl-App-cpanminus ncurses-devel && \\
sudo yum groupinstall "Development Tools"
```    

MUMmer and SAMtools are not available in the standard CentOS repositories.

#### Installing MUMmer

You can either download a pre-built binary from a source like PBone (<http://rpm.pbone.net/index.php3?stat=3&search=MUMmer>) or you can build it yourself.

MUMmer is fairly trivial to build, so we recommend that you install it from source:

```bash
sudo -s
mkdir -p /opt/mummer/ # or wherever you would prefer to put it
pushd /opt/mummer/
wget http://downloads.sourceforge.net/project/mummer/mummer/3.23/MUMmer3.23.tar.gz
tar xf MUMmer3.23.tar.gz
pushd MUMmer3.23/
make
sudo make install
popd
popd
echo "PATH=\$PATH:/opt/mummer/MUMmer3.23/" > /etc/profile.d/mummer.sh # or put it on your $PATH in some other way
exit
```

#### Installing SAMtools

You can either download a pre-built binary from a source lik PBone (<http://rpm.pbone.net/index.php3?stat=3&search=samtools>) or you can build it yourself.

SAMtools is fairly trivial to build, so we recommend that you install it from source:

```bash
mkdir -p /opt/samtools
pushd /opt/samtools
wget http://downloads.sourceforge.net/project/samtools/samtools/0.1.19/samtools-0.1.19.tar.bz2
tar xf samtools-0.1.19.tar.bz2
pushd samtools-0.1.19
make
popd
popd
echo "PATH=\$PATH:/opt/samtools/samtools-0.1.19/" > /etc/profile.d/samtools.sh
```

### Common install instructions

Once you've finished running the distro-specific install commands, you can use `cpanm` and `pip` to install any remaining dependencies.

```bash
sudo cpanm Parallel::ForkManager http://search.cpan.org/CPAN/authors/id/C/CJ/CJFIELDS/BioPerl-1.6.901.tar.gz
```

Verify your setup
-----------------

Assuming that you've finished the rest of the [developer setup guide](..), you should now be able to run the complete test suite.

You can run the complete test suite with `mvn`:

```bash
mvn clean verify
```

**Warning**: The complete integration test suite takes approximately 1 hour to complete execution.
