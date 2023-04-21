#!/usr/bin/python
import argparse
import datetime
import mysql.connector
import os

def remove(path, purge):
    if purge:
        try:
            if os.path.exists(path):
                if os.path.isdir(path):
                    os.rmdir(path)
                elif os.path.isfile(path):
                    os.remove(path)
                print("Deleted ", path)
        except OSError as e:
            print(e)
    else:
        print(path)

def list_sequence_files(startDate, endDate, host, user, password, database):
    db = mysql.connector.connect(
        host=host,
        user=user,
        password=password,
        database=database
    )
    cursor = db.cursor()
    # TODO: Should we double check this file doesn't exist in the actual table in case it was manually restored?
    if(startDate and endDate):
      cursor.execute("SELECT DISTINCT file_path FROM sequence_file_AUD WHERE revtype=2 AND modified_date BETWEEN %s AND %s", (startDate, endDate))
    elif(startDate):
      cursor.execute("SELECT DISTINCT file_path FROM sequence_file_AUD WHERE revtype=2 AND modified_date >= %s", (startDate,))
    elif(endDate):
      cursor.execute("SELECT DISTINCT file_path FROM sequence_file_AUD WHERE revtype=2 AND modified_date <= %s", (endDate,))
    else:
      cursor.execute("SELECT DISTINCT file_path FROM sequence_file_AUD WHERE revtype=2")
    result = cursor.fetchall()
    cursor.close()
    db.close()
    return result

def main():
    parser = argparse.ArgumentParser(description="This program lists the sequence files and folders that have been previously deleted in IRIDA.")
    parser.add_argument('--purge', help="Deletes the sequence files and folders from the filesystem.", action="store_true")
    parser.add_argument('--startDate', type=datetime.date.fromisoformat, help="The start date in format YYYY-MM-DD (inclusive).", required=False)
    parser.add_argument('--endDate', type=datetime.date.fromisoformat, help="The end date in format YYYY-MM-DD (inclusive).", required=False)
    parser.add_argument('--baseDirectory', default='/tmp/irida/sequence-files', help="The sequence file base directory.", required=False)
    parser.add_argument('--host', default='localhost', help="The database host name.", required=False)
    parser.add_argument('--database', default='irida_test', help="The database name.", required=False)
    parser.add_argument('--user', default='test', help="The database user name.", required=False)
    parser.add_argument('--password', default='test', help="The database password.", required=False)

    args = parser.parse_args()
    rows = list_sequence_files(args.startDate, args.endDate, args.host, args.user, args.password, args.database)
    if rows:
        for row in rows:
            sequence_file_directory = os.path.dirname(os.path.dirname(os.path.join(args.baseDirectory, row[0])))
            for root, dirs, files in os.walk(sequence_file_directory, topdown=False):
                for name in files:
                    file = os.path.join(root, name)
                    remove(file, args.purge)
                for name in dirs:
                    directory = os.path.join(root, name)
                    remove(directory, args.purge)
            remove(sequence_file_directory, args.purge)
    print("All done.")

if __name__ == '__main__':
    main()