#!/usr/bin/python
import argparse
import json
import mysql.connector
import os

def print_sequencing_files(host, user, password, database):
    db = mysql.connector.connect(
        host=host,
        user=user,
        password=password,
        database=database
    )
    cursor = db.cursor()
    # TODO: Should we double check this file doesn't exist in the actually table in case it was manually restored?
    # TODO: Should we be querying analysis_output_file, reference_file, and uploaded_assembly tables too?
    cursor.execute("SELECT DISTINCT file_path FROM sequence_file_AUD WHERE revtype=2")
    result = cursor.fetchall()
    cursor.close()
    db.close()
    return result

def main():
    parser = argparse.ArgumentParser(description="This program lists the sequence files that have been previously deleted in IRIDA.")
    parser.add_argument('--purge', help="Deletes the sequence files from the filesystem.", action="store_true")
    parser.add_argument('--host', default='localhost', help="The database host name.", required=False)
    parser.add_argument('--database', default='irida_test', help="The database name.", required=False)
    parser.add_argument('--user', default='test', help="The database user name.", required=False)
    parser.add_argument('--password', default='test', help="The database password.", required=False)

    args = parser.parse_args()
    rows = print_sequencing_files(args.host, args.user, args.password, args.database)

    if rows:
        for row in rows:
            file_path = row[0]
            if args.purge:
                try:
                    os.remove(row[0])
                    # TODO: Remove empty folders
                except OSError as e:
                    print("Error: %s - %s" % (e.filename, e.strerror))
            else:
                print(file_path)
    else:
        print("There are no deleted sequence files.")

if __name__ == '__main__':
    main()