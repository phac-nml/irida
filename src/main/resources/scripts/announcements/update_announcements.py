#!/usr/bin/python
import argparse
import json
import mysql.connector

def update_db(json_file, host, user, password, database):
    try:
        db = mysql.connector.connect(
            host=host,
            user=user,
            password=password,
            database=database
        )
        cursor = db.cursor()
        query = """UPDATE announcement SET title = %s, message = %s WHERE id = %s;"""

        json_file = open(json_file, "r")
        json_data = json.load(json_file)
        json_file.close()

        for json_object in json_data: 
            id = json_object['id']
            title = json_object['title']
            message = json_object['message']
            data = (title, message, id)
            cursor.execute(query, data)
            db.commit()
            print("Record ID {} updated successfully!".format(id))

    except mysql.connector.Error as error:
        print("Failed to update table record: {}".format(error))

    finally:
        if (db.is_connected()):
            cursor.close()
            db.close()
            print("Database connection is closed.")

def main():
    parser = argparse.ArgumentParser()
    
    parser.add_argument( 'json_file', help="The input json file of announcement titles to be populated.")
    parser.add_argument( '--host', default='localhost', help="The database host name.", required=False)
    parser.add_argument( '--database', default='irida_test', help="The database name.", required=False)
    parser.add_argument( '--user', default='root', help="The database user name.", required=False)
    parser.add_argument( '--password', help="The database password.", required=False)

    args = parser.parse_args()

    update_db(args.json_file, args.host, args.user, args.password, args.database)

if __name__ == '__main__':
    main()