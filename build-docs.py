import os
import sys
import shutil
import time

if len(sys.argv) == 1:
    print("IRIDA documentation deployment script.")
    print("Usage: build-docs.py TARGET_DIRECTORY")
    print("TARGET_DIRECTORY should be the 'irida-documentation/docs' directory")
    exit(0)

pom_file = 'pom.xml'
result_dir = sys.argv[1]
doc_dir = 'doc/'

# Check if we're in the irida directory
if not os.path.exists(pom_file):
    print("Cannot find pom file.  This script must be run in the base directory of the IRIDA repository.")
    exit(1)

if not os.path.isdir(doc_dir):
    print("Cannot find `/doc` directory.  This script must be run in the base directory of the IRIDA repository.")
    exit(1)

# check if the argument is the irida-documentation/docs directory
if not (result_dir.endswith('docs') or result_dir.endswith('docs/')) or not os.path.isdir(result_dir):
    print("The target directory doesn't appear to be the irida-docuemtation repository's `/docs` directory.")
    exit(1)

# first run mvn site to build the javadoc
print("========== Building documentation pages")
retval = os.system('mvn clean site')

# ensure it built correctly
if retval != 0:
    print("========== Docs construction failed.  See above for error messages")
    exit(1)


print("========== Documentation built successfully")

# Give the user 5 seconds to chicken out
print("========== WARNING: this script will now replace the existing docs directory {} with the docs built above".format(result_dir))
print("========== This process will contine in 5 seconds...")
time.sleep(5)

# Remove the original docs directory
print("========== Removing original directory {}".format(result_dir))
try:
    shutil.rmtree(result_dir)
except OSError as e:
    print ("Error: %s - %s." % (e.filename, e.strerror))
    exit(1)

# Copy the new files to the result directory
print("========== Removed original directory.  Copying new files")
try:
    shutil.copytree(doc_dir,result_dir)
except OSError as e:
    print ("Error: %s - %s." % (e.filename, e.strerror))
    exit(1)

# Print a success message
print("========== Process complete.\nYou can now go to the irida-documentation repository, create a new branch, add the changed files, and create a new PR to deploy the changes.")

