Please follow the instructions below on how to run the purge_sequencing_files.py script.
The assumption is that Python3 and pip3 are already installed.

Install virtual env.
$ pip3 install virtualenv

Create a virtual python environment.
$ python3 -m venv .virtualenv

Activate the environment.
$ source .virtualenv/bin/activate

Install libraries.
$ pip3 install -r requirements.txt

Run the script to purge the sequence files on the filesystem.
$ python3 purge_sequence_files.py --help

Activate the environment.
$ deactivate