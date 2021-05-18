import { Button, Form, notification, Space, Typography } from "antd";
import React, { useEffect, useState } from "react";
import {
  getRemoteProjectSyncSettings,
  updateRemoteProjectSyncSettings,
} from "../../../../apis/projects/remote-projects";
import { BasicList } from "../../../../components/lists";
import { HelpPopover } from "../../../../components/popovers";
import { SyncFrequencySelect } from "../../../../components/remote-api/SyncFrequencySelect";
import { formatDate } from "../../../../utilities/date-utilities";
import { RemoteApiStatus } from "../../../admin/components/remote-connections/RemoteApiStatus";

const { Title } = Typography;

/**
 * React component for render the remote project sync settings.
 * @returns {*}
 * @constructor
 */
export default function ProjectSynchronizationSettings({ projectId }) {
  const syncNowEnabledStates = ["SYNCHRONIZED", "ERROR", "UNAUTHORIZED"];
  const [remoteProjectData, setRemoteProjectData] = useState(null);
  const [disableSyncNow, setDisableSyncNow] = useState(false);

  /*
  When this component is rendered, query the api for the specific settings
  for this remote project.
   */
  useEffect(() => {
    getRemoteProjectSyncSettings(projectId)
      .then((remoteProjectSettings) => {
        setRemoteProjectData(remoteProjectSettings);
      })
      .catch(({ message }) => {
        notification.error({ message });
      });
  }, [projectId]);

  const syncSettings =
    remoteProjectData === null
      ? []
      : [
          {
            title: i18n("ProjectRemoteSettings.lastSync"),
            desc: (
              <div>
                <div>{formatDate({ date: remoteProjectData.lastUpdate })}</div>
                <Space>
                  <Button
                    type="primary"
                    onClick={() => updateSyncSettings({ markSync: true })}
                    disabled={
                      disableSyncNow ||
                      !syncNowEnabledStates.includes(
                        remoteProjectData.remoteStatus.syncStatus
                      )
                    }
                    className="t-sync-now-btn"
                  >
                    {i18n("ProjectRemoteSettings.syncNow")}
                  </Button>

                  <Button
                    type="primary"
                    onClick={() => updateSyncSettings({ forceSync: true })}
                    disabled={
                      disableSyncNow ||
                      !syncNowEnabledStates.includes(
                        remoteProjectData.remoteStatus.syncStatus
                      )
                    }
                    className="t-sync-force-btn"
                  >
                    {i18n("ProjectRemoteSettings.forceSync")}
                  </Button>
                </Space>
              </div>
            ),
          },
          {
            title: i18n("ProjectRemoteSettings.remoteConnection"),
            desc: (
              <span>
                {remoteProjectData.remoteAPI.label}
                <RemoteApiStatus
                  key="status"
                  api={{ id: remoteProjectData.remoteAPI.identifier }}
                />
              </span>
            ),
          },
          {
            title: (
              <span>
                <span>{i18n("ProjectRemoteSettings.syncFrequency")}</span>
                <HelpPopover
                  content={
                    <div>{i18n("SyncFrequencySelect.frequency.help")}</div>
                  }
                />
              </span>
            ),
            desc: (
              <Form
                initialValues={{
                  frequency: remoteProjectData.projectSyncFrequencies.indexOf(
                    remoteProjectData.projectSyncFrequency
                  ),
                }}
              >
                <SyncFrequencySelect
                  onChange={(e) =>
                    updateSyncSettings({
                      projectSyncFrequency:
                        remoteProjectData.projectSyncFrequencies[e],
                    })
                  }
                  labelRequired={false}
                />
              </Form>
            ),
          },
          {
            title: i18n("ProjectRemoteSettings.syncUser"),
            desc: (
              <div>
                <div>{remoteProjectData.syncUser.label}</div>
                {window.TL._USER.identifier !==
                remoteProjectData.syncUser.identifier ? (
                  <Button
                    onClick={() => updateSyncSettings({ changeUser: true })}
                    className="t-become-sync-user-btn"
                  >
                    {i18n("ProjectRemoteSettings.becomeSyncUser")}
                  </Button>
                ) : null}
              </div>
            ),
          },
        ];

  // Used to update sync user, sync frequency, and force to sync now
  function updateSyncSettings({
    forceSync,
    markSync,
    changeUser,
    projectSyncFrequency,
  }) {
    updateRemoteProjectSyncSettings(projectId, {
      forceSync,
      markSync,
      changeUser,
      projectSyncFrequency,
    })
      .then(({ responseMessage }) => {
        notification.success({ message: responseMessage });
        if (forceSync || markSync) {
          setDisableSyncNow(true);
        }
      })
      .catch(({ message }) => {
        notification.error({ message });
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
