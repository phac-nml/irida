#!/usr/bin/python
import argparse
import json
import mysql.connector
import re

# This script will fetch all the rows from the announcement table, then parse
# the title out from the message content and return a json file with the newly
# populated title and modified message content.
#
# Step #1: Run this script to generate a json file with the modified
# announcement data.
#
# Step #2: Please review the json file to make sure the title was parsed
# correctly from the message content.
#
# Step #3: To update the announcement table with the json file run the
# update_announcement.py script next.

def create_json_file(json_file, host, user, password, database):

    json_data = []
    json_file = open(json_file,"w+")

    db = mysql.connector.connect(
        host=host,
        user=user,
        password=password,
        database=database
    )
    cursor = db.cursor()
    cursor.execute("SELECT id, created_date, created_by_id, title, message, priority FROM announcement")
    result = cursor.fetchall()
    cursor.close()
    db.close()

    for id, created_date, created_by_id, title, message, priority in result:
        
        if not title:
            lines = message.split("\n",1)

            if len(lines) > 0:
                first_line = lines[0]
                header = re.match(r"^#+\s(.+)\w(.+)$", first_line)
                
                if header:
                    title = header[1]

                    if len(lines) > 1:
                        message = lines[1]
                else:
                    if len(lines) > 1:
                        lines = lines[1].split("\n",1)

                        if len(lines) > 0:
                            second_line = lines[0]
                            underline = re.match(r"^(-|=)+$", second_line)

                            if underline:
                                title = first_line

                                if len(lines) > 1:
                                    message = lines[1]

            json_data.append({"id":id, "created_date":created_date, "created_by_id":created_by_id, "title":title, "message":message, "priority":priority})

    # convert the list to a JSON string
    json_string = json.dumps(json_data, indent=2, default=str)
    json_file.write(json_string)
    json_file.close()


def main():
    parser = argparse.ArgumentParser()

    parser.add_argument( 'json_file', help="The output json file of announcement titles to be populated.")
    parser.add_argument( '--host', default='localhost', help="The database host name.", required=False)
    parser.add_argument( '--database', default='irida_test', help="The database name.", required=False)
    parser.add_argument( '--user', default='root', help="The database user name.", required=False)
    parser.add_argument( '--password', help="The database password.", required=False)

    args = parser.parse_args()

    create_json_file(args.json_file, args.host, args.user, args.password, args.database)

if __name__ == '__main__':
    main()