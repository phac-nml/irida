import React from "react";
import { Avatar, Button, List, notification, Popconfirm, Space } from "antd";
import { SPACE_XS } from "../../styles/spacing";
import { IconDownloadFile, IconRemove } from "../icons/Icons";
import { removeSampleFiles } from "../samples/sampleFilesSlice";
import { useDispatch } from "react-redux";
import { useRemoveSampleFilesMutation } from "../../apis/samples/samples";

export function SequenceFileDetailsRenderer({
  file,
  isForwardFile,
  sampleId,
  fileObjectId,
}) {
  const dispatch = useDispatch();
  const [removeSampleFilesFromSample] = useRemoveSampleFilesMutation();

  const removeSequencingObjectFromSample = ({ type }) => {
    removeSampleFilesFromSample({ sampleId, fileObjectId, type })
      .then(({ data }) => {
        notification.success({ message: data.message });
        dispatch(removeSampleFiles({ fileObjectId, type }));
      })
      .catch((error) => {
        notification.error({ message: error });
      });
  };

  return (
    <List.Item key={`file-${file.id}`} style={{ width: `100%` }}>
      <List.Item.Meta
        avatar={<Avatar size={`small`} icon={file.icon} />}
        title={
          <div style={{ display: "flex", justifyContent: "space-between" }}>
            <a href={file.fastqcLink} target="_blank">
              {file.label}
            </a>
            <Space direction="horizontal" size="small">
              <span style={{ marginRight: SPACE_XS }}>{file.filesize}</span>
              <Button
                style={{ marginRight: SPACE_XS }}
                shape="circle"
                icon={<IconDownloadFile />}
              />
              {isForwardFile && (
                <Popconfirm
                  placement="left"
                  title={i18n("SampleFiles.deleteSequencingObject")}
                  okText={i18n("SampleFiles.okText")}
                  cancelText={i18n("SampleFiles.cancelText")}
                  onConfirm={() =>
                    removeSequencingObjectFromSample({ type: file.fileType })
                  }
                >
                  <Button shape="circle" icon={<IconRemove />} />
                </Popconfirm>
              )}
            </Space>
          </div>
        }
      />
    </List.Item>
  );
}
