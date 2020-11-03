Please follow the instructions below on how to run the generate_json.py and update_announcement.py scripts.
The assumption is that Python3 and pip3 are already installed.

Install virtual env.
$ pip3 install virtualenv

Create a virtual python environment.
$ python3 -m venv .virtualenv

Activate the environment.
$ source .virtualenv/bin/activate

Install libraries.
$ pip3 install -r requirements.txt

Run the first script to generate a list of announcements with the updated title and message.
$ python3 generate_json.py announcement_list.json

Review the json file with the new title and message.

Run the second script to update the announcements table with the json file.
$ python3 update_announcements.py announcement_list.json

Activate the environment.
$ deactivate
