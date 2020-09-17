import React, { useEffect, useState } from "react";
import { Button, Form, notification , Typography} from "antd";
import { BasicList } from "../../../components/lists";
import { RemoteApiStatus } from "../../admin/components/remote-connections/RemoteApiStatus";
import {
  updateRemoteProjectSyncSettings,
  getRemoteProjectSyncSettings }
from "../../../apis/projects/remote-projects";
import { formatDate } from "../../../utilities/date-utilities";
import { SyncFrequencySelect } from "../../../components/remote-api/SyncFrequencySelect";
import { HelpPopover } from "../../../components/popovers";

const { Title } = Typography;

/**
 * React component for render the remote project sync settings.
 * @returns {*}
 * @constructor
 */
export function ProjectSynchronizationSettings() {
  const projectId = window.location.pathname.match(/projects\/(\d+)/)[1];
  const syncNowEnabledStates = ["SYNCHRONIZED", "ERROR"];
  const [remoteProjectData, setRemoteProjectData] = useState(null);
  const [disableSyncNow, setDisableSyncNow] = useState(false);

  /*
  When this component is rendered, query the api for the specific settings
  for this remote project.
   */
  useEffect(() => {
    getRemoteProjectSyncSettings(projectId).then(remoteProjectSettings => {
      setRemoteProjectData(remoteProjectSettings);
    }).catch(({message}) => {
      notification.error({ message });
    });
  }, []);

  const syncSettings = remoteProjectData === null ? [] : [
      {
        title: i18n("ProjectRemoteSettings.lastSync"),
        desc: (<div>
          <div>{formatDate({ date: remoteProjectData.lastUpdate })}</div>
          <Button type="primary"
                  onClick={() => updateSyncSettings({ forceSync: true })}
                  disabled={(disableSyncNow ? true : false) ||
                            (syncNowEnabledStates.includes(remoteProjectData.remoteStatus.syncStatus) ? false : true)}
                  className="t-sync-now-btn"
          >
            {i18n("ProjectRemoteSettings.syncNow")}
          </Button>
        </div>)
      },
      {
        title: i18n("ProjectRemoteSettings.remoteConnection"),
        desc: (<span>{remoteProjectData.remoteAPI.label}<RemoteApiStatus
          key="status"
          api={{ id: remoteProjectData.remoteAPI.identifier }}
        /></span>)
      },
      {
        title: <span>
                <span>{i18n("ProjectRemoteSettings.syncFrequency")}</span>
                <HelpPopover
                  content={<div>{i18n("SyncFrequencySelect.frequency.help")}</div>}
                />
               </span>,
        desc: (
          <Form initialValues={{
            frequency: remoteProjectData.projectSyncFrequencies.indexOf(remoteProjectData.projectSyncFrequency),
          }}>
            <SyncFrequencySelect
              onChange={(e) => updateSyncSettings({ projectSyncFrequency: remoteProjectData.projectSyncFrequencies[e] })}
              labelRequired={false}
            />
          </Form>
        )
      },
      {
        title: i18n("ProjectRemoteSettings.syncUser"),
        desc: (<div>
          <div>{remoteProjectData.syncUser.label}</div>
          { window.TL._USER.identifier !== remoteProjectData.syncUser.identifier ?
              <Button
                onClick={() => updateSyncSettings({ changeUser: true })}
                className="t-become-sync-user-btn"
              >
                {i18n("ProjectRemoteSettings.becomeSyncUser")}
              </Button>
            : null
          }
        </div>)
      }
    ];

  // Used to update sync user, sync frequency, and force to sync now
  function updateSyncSettings({forceSync, changeUser, projectSyncFrequency}) {
    updateRemoteProjectSyncSettings(projectId, {forceSync, changeUser, projectSyncFrequency}).then(({ responseMessage }) => {
      notification.success({ message: responseMessage });
      if(forceSync) {
        setDisableSyncNow(true);
      }
    }).catch(({message}) => {
      notification.error({message});
    });
  }

  return (
    <>
      <Title level={2} className="t-main-heading">
        {i18n("ProjectRemoteSettings.heading")}
      </Title>
      <BasicList dataSource={syncSettings} />
    </>
  );
}
