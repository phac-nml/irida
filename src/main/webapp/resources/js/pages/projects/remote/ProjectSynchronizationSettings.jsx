import React, { useEffect, useState } from "react";
import { Button, notification, Select } from "antd";
import { BasicList } from "../../../components/lists";
import { RemoteApiStatus } from "../../admin/components/remote-connections/RemoteApiStatus";
import {
  updateRemoteProjectSyncSettings,
  getRemoteProjectSyncSettings }
from "../../../apis/projects/projects";
import { formatDate } from "../../../utilities/date-utilities";

/**
 * React component for render the remote project sync settings.
 * @returns {*}
 * @constructor
 */
export function ProjectSynchronizationSettings() {
  const projectId = window.project.id;
  const syncNowEnabledStates = ["SYNCHRONIZED", "ERROR"];
  const [remoteProjectData, setRemoteProjectData] = useState(null);
  const [disableSyncNow, setDisableSyncNow] = useState(false);

  /*
  When this component is rendered, query the api for the specific settings
  for this remote project.
   */
  useEffect(() => {
    getRemoteProjectSyncSettings(projectId).then(res => {
      setRemoteProjectData(res.remoteProjectInfo);
    }).catch((message) => {
      notification.error({ message: message });
    });
  }, []);

  const syncSettings = remoteProjectData === null ? [] : [
      {
        title: i18n("ProjectRemoteSettings.lastSync"),
        desc: (<div>
          <span>{formatDate({ date: remoteProjectData.lastUpdate })}</span>
          <br />
          <Button type="primary"
                  onClick={() => updateSyncSettings({forceSync: true})}
                  disabled={(disableSyncNow ? true : false) ||
                            (syncNowEnabledStates.includes(remoteProjectData.remoteStatus.syncStatus) ? false : true)}
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
        title: i18n("ProjectRemoteSettings.syncFrequency"),
        desc: (
          <Select
            defaultValue={remoteProjectData.projectSyncFrequency}
            style={{ width: "100%" }}
            onChange={(e) => updateSyncSettings({ frequency: e })}>
              {renderFrequencies()}
          </Select>
        )
      },
      {
        title: i18n("ProjectRemoteSettings.syncUser"),
        desc: (<div>
          <span>{remoteProjectData.syncUser.label}</span>
          <br />
          { window.TL._USER.identifier !== remoteProjectData.syncUser.identifier ?
              <Button
                onClick={() => updateSyncSettings({changeUser: true})}>
                {i18n("ProjectRemoteSettings.becomeSyncUser")}
              </Button>
            : null
          }
        </div>)
      }
    ];

  // Returns a list (sync frequencies) of select options
  function renderFrequencies() {
    const frequencyList = [];
    if(remoteProjectData !== null) {
      for (let frequency of remoteProjectData.projectSyncFrequencies) {
        frequencyList.push(
          <Select.Option key={frequency} value={frequency}>
            {i18n(`ProjectRemoteSettings.frequency.${frequency}`)}
          </Select.Option>
        );
      }
    }
    return frequencyList;
  }

  // Used to update sync user, sync frequency, and force to sync now
  function updateSyncSettings(data) {
    updateRemoteProjectSyncSettings(projectId, data).then(res => {
      notification.success({ message: res.result });
      if(data.forceSync === true) {
        setDisableSyncNow(true);
      }
    }).catch((message) => {
      notification.error({ message: message });
    });
  }

  return (
    <>
      <h1>{i18n("ProjectRemoteSettings.heading")}</h1>
      <BasicList dataSource={syncSettings} />
    </>
  );
}
