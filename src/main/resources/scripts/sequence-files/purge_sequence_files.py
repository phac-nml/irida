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
    cursor.execute("SELECT DISTINCT file_path FROM sequence_file_AUD WHERE revtype=2")
    result = cursor.fetchall()
    cursor.close()
    db.close()
    return result

def main():
    parser = argparse.ArgumentParser(description="This program lists the sequence files and folders that have been previously deleted in IRIDA.")
    parser.add_argument('--purge', help="Deletes the sequence files and folders from the filesystem.", action="store_true")
    parser.add_argument('--baseDirectory', help="The sequence file base directory.", required=True)
    parser.add_argument('--host', default='localhost', help="The database host name.", required=False)
    parser.add_argument('--database', default='irida_test', help="The database name.", required=False)
    parser.add_argument('--user', default='test', help="The database user name.", required=False)
    parser.add_argument('--password', default='test', help="The database password.", required=False)

    args = parser.parse_args()
    rows = list_sequence_files(args.host, args.user, args.password, args.database)

    if rows:
        for row in rows:
            sequence_file_directory = os.path.dirname(os.path.dirname(args.baseDirectory + row[0]))
            for root, dirs, files in os.walk(sequence_file_directory, topdown=False):
                for name in files:
                    file = os.path.join(root, name)
                    if args.purge:
                        os.remove(file)
                    print(file)
                for name in dirs:
                    directory = os.path.join(root, name)
                    if args.purge:
                        os.rmdir(directory)
                    print(directory)
            if args.purge:
                os.rmdir(sequence_file_directory)
            print(sequence_file_directory)

    print("All done.")

if __name__ == '__main__':
    main()