#!/bin/sh

GALAXY_ROOT_DIR=/path/to/galaxy-dist
GALAXY_CONFIG=$GALAXY_ROOT_DIR/config/galaxy.ini
CLEANUP_LOG=$GALAXY_ROOT_DIR/galaxy_cleanup.log
DAYS_TO_KEEP=0

cd $GALAXY_ROOT_DIR

echo -e "\nBegin cleanup at `date`" >> $CLEANUP_LOG
echo -e "Begin delete useless histories" >> $CLEANUP_LOG
python scripts/cleanup_datasets/cleanup_datasets.py $GALAXY_CONFIG -d $DAYS_TO_KEEP -1 -r >> $CLEANUP_LOG
echo -e "\nBegin purge deleted histories" >> $CLEANUP_LOG
python scripts/cleanup_datasets/cleanup_datasets.py $GALAXY_CONFIG -d $DAYS_TO_KEEP -2 -r >> $CLEANUP_LOG
echo -e "\nBegin purge deleted datasets" >> $CLEANUP_LOG
python scripts/cleanup_datasets/cleanup_datasets.py $GALAXY_CONFIG -d $DAYS_TO_KEEP -3 -r >> $CLEANUP_LOG
echo -e "\nBegin purge deleted libraries" >> $CLEANUP_LOG
python scripts/cleanup_datasets/cleanup_datasets.py $GALAXY_CONFIG -d $DAYS_TO_KEEP -4 -r >> $CLEANUP_LOG
echo -e "\nBegin purge deleted library folders" >> $CLEANUP_LOG
python scripts/cleanup_datasets/cleanup_datasets.py $GALAXY_CONFIG -d $DAYS_TO_KEEP -5 -r >> $CLEANUP_LOG
echo -e "\nEnd cleanup at `date`" >> $CLEANUP_LOG
