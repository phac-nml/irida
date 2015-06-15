#!/usr/bin/env python
import argparse, os, string, json
import xml.etree.ElementTree as ET
from bioblend import galaxy

def main(master_api_key, api_url, galaxy_admin_user, galaxy_admin_pass, galaxy_workflow_user, galaxy_workflow_pass, pipeline_xml_dir):
    gi = galaxy.GalaxyInstance(url=api_url, key=master_api_key)

    #adding new user
    admin_key = create_user(gi, galaxy_admin_user.split('@')[0], galaxy_admin_user, galaxy_admin_pass)
    workflow_key = create_user(gi, galaxy_workflow_user.split('@')[0], galaxy_workflow_user, galaxy_workflow_pass)

    print "Galaxy API key created for user. Please use API key [%s] in /etc/irida/irida.conf." % (workflow_key)

    print "Signing in as admin user."
    gi = galaxy.GalaxyInstance(url=api_url, key=admin_key)

    for (dir_name, sub_dir, file_names) in os.walk(pipeline_xml_dir):
        for xml_file in filter(lambda f: f.endswith('.xml'), file_names):
            xml_path = os.path.join(dir_name, xml_file)
            print "Going to parse " + xml_path
            xml_doc = ET.parse(xml_path)
            xml_root = xml_doc.getroot()
            for repository in xml_root.findall('.//repository'):
                name = repository.find('name').text
                owner = repository.find('owner').text
                url = repository.find('url').text
                revision = repository.find('revision').text
                tool_info = ", ".join([name, owner, url, revision])

                print "Going to install %s" % tool_info
                try:
                    gi.toolShed.install_repository_revision(url, name, owner, revision, install_tool_dependencies = True, install_repository_dependencies = True)
                except galaxy.client.ConnectionError as e:
                    error_body = json.loads(e.body)
                    if (error_body['err_code'] == 400008):
                        print "%s is already installed, skipping." % tool_info
                    else:
                        print "%s failed to install, check the galaxy logs." % tool_info
                        raise
    return

def create_user(gi, username, email, password):
    user = gi.users.create_local_user(username, email, password)
    user_key = gi.users.create_user_apikey(user['id'])

    return user_key

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Install IRIDA Galaxy tools.')
    

    parser.add_argument('--master-api-key', action="store", dest="master_api_key",required=True)
    parser.add_argument('--master-api-url', action="store", dest="api_url",required=True)
    parser.add_argument('--galaxy-admin-user', action="store", dest="galaxy_admin_user", required=True)
    parser.add_argument('--galaxy-admin-pass', action="store", dest="galaxy_admin_pass", required=True)
    parser.add_argument('--galaxy-workflow-user', action="store", dest="galaxy_workflow_user", required=True)
    parser.add_argument('--galaxy-workflow-pass', action="store", dest="galaxy_workflow_pass", required=True)
    parser.add_argument('--pipeline-xml-dir', action="store", dest="pipeline_xml_dir", required=True)
    args = parser.parse_args()
    dic = vars(args)

    main(**dic)
