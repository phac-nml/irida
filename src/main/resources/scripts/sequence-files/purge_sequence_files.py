#!/usr/bin/python
import argparse
import mysql.connector
import os

def list_sequence_files(host, user, password, database):
    db = mysql.connector.connect(
        host=host,
        user=user,
        password=password,
        database=database
    )
    cursor = db.cursor()
    # TODO: Should we double check this file doesn't exist in the actual table in case it was manually restored?
    cursor.execute("SELECT DISTINCT file_path FROM sequence_file_AUD WHERE id in (SELECT DISTINCT id FROM sequence_file_AUD WHERE revtype=2)")
    result = cursor.fetchall()
    cursor.close()
    db.close()
    return result

def main():
    parser = argparse.ArgumentParser(description="This program lists the sequence files that have been previously deleted in IRIDA.")
    parser.add_argument('--purge', help="Deletes the sequence files from the filesystem.", action="store_true")
    parser.add_argument('--baseDirectory', help="The sequence file base directory.", required=True)
    parser.add_argument('--host', default='localhost', help="The database host name.", required=False)
    parser.add_argument('--database', default='irida_test', help="The database name.", required=False)
    parser.add_argument('--user', default='test', help="The database user name.", required=False)
    parser.add_argument('--password', default='test', help="The database password.", required=False)

    args = parser.parse_args()
    rows = list_sequence_files(args.host, args.user, args.password, args.database)

    if rows:
        for row in rows:
            file_path = args.baseDirectory + row[0]
            if args.purge:
                try:
                    os.remove(file_path)
                    directory_name = os.path.dirname(file_path)
                    os.removedirs(directory_name)
                except OSError as e:
                    print("Error: %s - %s" % (e.filename, e.strerror))
            else:
                print(file_path)
    else:
        print("There are no deleted sequence files.")

if __name__ == '__main__':
    main()