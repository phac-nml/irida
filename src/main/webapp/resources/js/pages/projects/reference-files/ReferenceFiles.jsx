import React from "react";
import { notification, Space, Table, Typography } from "antd";
import { InfoAlert } from "../../../components/alerts";
import { setBaseUrl } from "../../../utilities/url-utilities";
import {
  downloadProjectReferenceFile,
  getProjectReferenceFiles,
  removeProjectReferenceFile,
} from "../../../apis/projects/reference-files";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { ContentLoading } from "../../../components/loader";
import {
  DownloadTableItemButton,
  RemoveTableItemButton,
} from "../../../components/Buttons";
import { DragUpload } from "../../../components/files/DragUpload";

import { getProjectInfo } from "../../../apis/projects/projects";

const { Title } = Typography;

/**
 * React component for render the reference files page for the project.
 * @returns {*}
 * @constructor
 */
export function ReferenceFiles() {
  const [projectReferenceFiles, setProjectReferenceFiles] = React.useState(0);
  const [projectInfo, setProjectInfo] = React.useState(null);
  const [loading, setLoading] = React.useState(true);

  const pathRegx = new RegExp(/projects\/(\d+)/);
  const projectId = window.location.pathname.match(pathRegx)[1];

  // On first load of page call method to get the reference files for the project
  // as well as get info about the project and permissions
  React.useEffect(() => {
    getProjectInfo(projectId)
      .then((data) => {
        setProjectInfo(data);
      })
      .catch((message) => {
        notification.error({ message });
      });
    updateReferenceFileTable();
  }, []);

  // Object to hold alert messages for if a user can manage the project or not
  const alertMessage = {
    true: {
      text: i18n("ReferenceFile.ownerUploadFileAlert"),
      alertClass: "t-rf-owner",
    },
    false: {
      text: i18n("ReferenceFile.userUploadFileAlert"),
      alertClass: "t-rf-user",
    },
  };

  // Columns for the reference file table
  const referenceFileTableColumns = [
    {
      title: i18n("ReferenceFile.name"),
      dataIndex: "label",
    },
    {
      title: i18n("ReferenceFile.size"),
      dataIndex: "size",
    },
    {
      title: i18n("ReferenceFile.added"),
      dataIndex: "createdDate",
      render(text) {
        return formatInternationalizedDateTime(text);
      },
    },
    {
      align: "right",
      render(file) {
        let actionButtons = [
          <DownloadTableItemButton
            key={`download-btn-${file.id}`}
            onDownload={() => downloadProjectReferenceFile(file.id)}
            onDownloadSuccess={() => {
              notification.success({
                message: i18n(
                  "ReferenceFile.downloadingFileSuccess",
                  file.label,
                  projectInfo.projectName
                ),
              });
            }}
            tooltipText={i18n("ReferenceFile.downloadTooltip")}
          />,
          // Only display remove button for reference files if user can manage project
          projectInfo.canManage ? (
            <RemoveTableItemButton
              key={`remove-btn-${file.id}`}
              onRemove={() => removeProjectReferenceFile(projectId, file.id)}
              onRemoveSuccess={() => {
                updateReferenceFileTable();
              }}
              tooltipText={i18n("ReferenceFile.removeTooltip")}
              confirmText={i18n(
                "ReferenceFile.confirmText",
                file.label,
                projectInfo.projectName
              )}
            />
          ) : null,
        ];
        // Return download and remove buttons spaced
        return <Space size="small">{actionButtons}</Space>;
      },
    },
  ];

  // Get the reference files for the project
  function updateReferenceFileTable() {
    getProjectReferenceFiles(projectId)
      .then(({ files }) => {
        setProjectReferenceFiles(files);
        setLoading(false);
      })
      .catch((message) => {
        notification.error({ message });
      });
  }

  // Options for the Ant Design upload component
  const referenceFileUploadOptions = {
    multiple: true,
    showUploadList: false,
    action: setBaseUrl(`ajax/reference-files/project/${projectId}`),
    onChange(info) {
      const { status } = info.file;
      if (status === "done") {
        notification.success({
          message: `${i18n(
            "ReferenceFile.uploadFileSuccess",
            info.file.name,
            projectInfo.projectName
          )}`,
        });
        updateReferenceFileTable();
      } else if (status === "error") {
        notification.error({ message: info.file.response.error });
      }
    },
  };

  /*
   * Returns the upload section if a user is allowed to manage the project.
   * Supports drag and drop as well as click to upload
   */
  function displayUploadButton() {
    if (projectInfo && projectInfo.canManage)
      return <DragUpload {...referenceFileUploadOptions} />;
  }

  // Displays the reference files table or an alert if no reference files found for project
  function displayReferenceFiles() {
    if (projectReferenceFiles.length > 0) {
      return (
        <Table
          columns={referenceFileTableColumns}
          dataSource={projectReferenceFiles}
          rowKey={(file) => file.id}
          className="t-files-table"
        />
      );
    }

    /*
     * Only return alert if there are no project reference files.
     * Depending on if user can manage project or not a different
     * alert message will be returned
     */
    let message = alertMessage[projectInfo.canManage].text;
    let alertClass = alertMessage[projectInfo.canManage].alertClass;
    return <InfoAlert message={message} className={alertClass} />;
  }

  return (
    <>
      <Title level={2}>{i18n("ReferenceFile.title")}</Title>
      <Space direction="vertical" style={{ width: `100%` }}>
        {displayUploadButton()}

        {loading ? <ContentLoading /> : displayReferenceFiles()}
      </Space>
    </>
  );
}
