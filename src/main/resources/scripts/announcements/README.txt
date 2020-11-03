Please follow the instructions below on how to run the generate_json.py and update_announcement.py scripts.
The assumption is that Python and pip are already installed.

Install virtual env.
$ pip install virtualenv

Create a virtual python environment.
$ virtualenv --python C:\Path\To\Python\python.exe venv

Activate the environment.
$ .\venv\Scripts\activate

Install libraries.
$ pip install -r requirements.txt

Run the first script to generate a list of announcements with the updated title and message.
$ python generate_json.py announcement_list.json

Review the json file with the new title and message.

Run the second script to update the announcements table with the json file.
$ python update_announcements.py announcement_list.json

Activate the environment.
$ deactivate
