import { notification, Space, Table, Typography } from "antd";
import React from "react";
import { useParams } from "react-router-dom";
import { useGetProjectDetailsQuery } from "../../../../apis/projects/project";
import {
  downloadProjectReferenceFile,
  getProjectReferenceFiles,
  removeProjectReferenceFile,
  uploadProjectReferenceFiles,
} from "../../../../apis/references/reference-files";
import { InfoAlert } from "../../../../components/alerts";
import {
  DownloadTableItemButton,
  RemoveTableItemButton,
} from "../../../../components/Buttons";
import { DragUpload } from "../../../../components/files/DragUpload";
import { ContentLoading } from "../../../../components/loader";

import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import { getPaginationOptions } from "../../../../utilities/antdesign-table-utilities";

const { Title } = Typography;

/**
 * React component for render the reference files page for the project.
 * @returns {*}
 * @constructor
 */
export default function ReferenceFiles() {
  const { projectId } = useParams();
  const { data: project = {}, isLoading } = useGetProjectDetailsQuery(
    projectId
  );
  const [projectReferenceFiles, setProjectReferenceFiles] = React.useState([]);
  const [, setProgress] = React.useState(0);
  const [total, setTotal] = React.useState(0);

  const paginationOptions = React.useMemo(() => getPaginationOptions(total), [
    total,
  ]);

  React.useEffect(updateReferenceFileTable, [projectId]);

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

  const uploadHintMessage = {
    true: i18n("ReferenceFile.singleOrMultiple"),
    false: "Supports single file upload",
  };

  // Columns for the reference file table
  const referenceFileTableColumns = [
    {
      title: i18n("ReferenceFile.name"),
      dataIndex: "name",
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
                  file.name,
                  project.label
                ),
              });
            }}
            tooltipText={i18n("ReferenceFile.downloadTooltip")}
            disableDownloadButton={
              file.size === i18n("server.projects.reference-file.not-found")
            }
          />,
          // Only display remove button for reference files if user can manage project
          project.canManage ? (
            <RemoveTableItemButton
              key={`remove-btn-${file.id}`}
              onRemove={() => removeProjectReferenceFile(projectId, file.id)}
              onRemoveSuccess={() => {
                updateReferenceFileTable();
              }}
              tooltipText={i18n("ReferenceFile.removeTooltip")}
              confirmText={i18n(
                "ReferenceFile.confirmText",
                file.name,
                project.label
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
      .then((files) => {
        setProjectReferenceFiles(files);
        setTotal(files.length);
      })
      .catch((message) => {
        notification.error({ message });
      });
  }

  /* Custom request for drag upload to get progress of uploads
   * as well as display notifications for reference file upload
   * success/error
   */
  const uploadFiles = async (options) => {
    const { onSuccess, onError, file, onProgress } = options;

    const formData = new FormData();
    const config = {
      headers: { "content-type": "multipart/form-data" },
      onUploadProgress: (event) => {
        const percent = Math.floor((event.loaded / event.total) * 100);
        setProgress(percent);
        if (percent === 100) {
          setTimeout(() => setProgress(0), 1000);
        }
        onProgress({ percent: Math.floor(event.loaded / event.total) * 100 });
      },
    };
    formData.append("file", file);

    uploadProjectReferenceFiles(projectId, formData, config)
      .then(() => {
        onSuccess("Ok");
        notification.success({
          message: `${i18n(
            "ReferenceFile.uploadFileSuccess",
            file.name,
            project.label
          )}`,
        });
        updateReferenceFileTable();
        document
          .querySelectorAll(".ant-upload-list-item-done")
          .forEach((a) => (a.style.display = "none"));
      })
      .catch((error) => {
        onError("Error");
        notification.error({
          message: i18n("ReferenceFile.uploadFileError", file.name),
          description: error
        });
        document
          .querySelectorAll(".ant-upload-list-item-error")
          .forEach((a) => (a.style.display = "none"));
      });
  };

  // Options for the Ant Design upload component
  const referenceFileUploadOptions = {
    multiple: true,
    accept: ".fasta",
    showUploadList: { showRemoveIcon: false, showPreviewIcon: false },
    progress: { strokeWidth: 5 },
    customRequest: uploadFiles,
  };

  return (
    <>
      <Title level={2}>{i18n("ReferenceFile.title")}</Title>
      <Space direction="vertical" style={{ width: `100%` }}>
        {project.canManage ? (
          <DragUpload
            options={referenceFileUploadOptions}
            uploadText={i18n("ReferenceFile.clickorDrag")}
            uploadHint={uploadHintMessage[referenceFileUploadOptions.multiple]}
          />
        ) : null}

        {isLoading ? (
          <ContentLoading />
        ) : projectReferenceFiles.length ? (
          <Table
            columns={referenceFileTableColumns}
            dataSource={projectReferenceFiles}
            rowKey={(file) => file.id}
            className="t-files-table"
            pagination={paginationOptions}
          />
        ) : (
          <InfoAlert
            message={alertMessage[project.canManage].text}
            className={alertMessage[project.canManage].alertClass}
          />
        )}
      </Space>
    </>
  );
}
