---
layout: default
---

Galaxy Environment Setup
========================

Many of the tools in Galaxy are written in languages like Perl or Python and require specific modules to be installed.  In addition, there are a few software packages external to Galaxy that will need to be available when executing tools.  This guide describes how to setup this software in such a way that it is independent of system-wide software and could easily be shared across a cluster.

Executable Software
-------------------

Some tools require external software such as [SAMTools][] or [MUMMer][] to be installed.  To setup the proper environment for Galaxy to find this software please install this software and add entries in the `$GALAXY_ENV` file to setup the `PATH` and other environment variables properly.  If you are running in a clustered environment please make sure these dependency software are shared across your cluster.

In particular [SAMTools 0.1.18][] must be installed and made available for Galaxy.

PerlBrew
--------

Software like [PerlBrew][] can be used to keep a separate instance of Perl and Perl modules specific to Galaxy.  To install [PerlBrew][] please do the following while logged in as the `$GALAXY_USER`.

```bash
export PERLBREW_ROOT=~/perl5/perlbrew # directory to install PerlBrew (this is the default)

curl -L http://install.perlbrew.pl | bash # installs PerlBrew under $PERLBREW_ROOT
source $PERLBREW_ROOT/etc/bashrc # Load up PerlBrew environment in this session
```
Once PerlBrew is installed you can download and build a particular version of Perl by running:

```bash
perlbrew install perl-5.16.0
```

Once a particular version of Perl is installed, you can setup a local space for installing modules with.

```bash
perlbrew lib create galaxy
```

This will create space to install modules specific for Galaxy.  Please see [Customize PerlBrew][] documentation for more information.

Once the particular Perl is installed you can switch to using this version of Perl with.

```bash
perlbrew use perl-5.16.0@galaxy

perlbrew list
perl --version
```

To make these changes permanent, please add the following lines to `$GALAXY_ENV`.

```bash
source $PERLBREW_ROOT/etc/bashrc
perlbrew use perl-5.16.0@galaxy
```

Please also install [App::cpanminus][], which provides an easy way to install Perl modules.  This can be done with.

```bash
perlbrew install-cpanm
```

Now you are ready to install any dependency Perl modules for Galaxy.  All modules will be installed under `$PERLBREW_HOME/libs/perl-5.16.0@galaxy`.

[Customize PerlBrew]: http://perlbrew.pl/Install-a-sitecustomize.pl-file-with-perlbrew.html
[PerlBrew]: http://perlbrew.pl/
[App::cpanminus]: http://search.cpan.org/~miyagawa/App-cpanminus-1.7027/lib/App/cpanminus.pm
[SAMTools]: http://www.htslib.org/
[SAMTools 0.1.18]: http://downloads.sourceforge.net/project/samtools/samtools/0.1.18/samtools-0.1.18.tar.bz2
[MUMMer]: http://mummer.sourceforge.net/
