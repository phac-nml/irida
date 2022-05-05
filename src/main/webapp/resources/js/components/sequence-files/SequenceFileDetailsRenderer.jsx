import React from "react";
import { Avatar, Button, List, Space } from "antd";
import { IconDownloadFile } from "../icons/Icons";
import { FastQC } from "../samples/components/fastqc/FastQC";
import { setFastQCModalData } from "../samples/components/fastqc/fastQCSlice";
import { useDispatch, useSelector } from "react-redux";

/**
 * React component to display paired end file details
 *
 * @param file The file to display details for
 * @param fileObjectId The sequencingobject identifier
 * @param {function} download sequence file function
 * @param {function} get file processing state function
 * @returns {JSX.Element}
 * @constructor
 */
export function SequenceFileDetailsRenderer({
  file,
  fileObjectId,
  downloadSequenceFile = () => {},
  getProcessingState = () => {},
}) {
  const dispatch = useDispatch();
  const { fastQCModalVisible, sequencingObjectId, fileId } = useSelector(
    (state) => state.fastQCReducer
  );

  return (
    <List.Item
      key={`file-${file.id}`}
      style={{ width: `100%` }}
      className="t-file-details"
    >
      <List.Item.Meta
        avatar={
          <Avatar size={`small`} style={{ marginTop: 3 }} icon={file.icon} />
        }
        title={
          <div style={{ display: "flex", justifyContent: "space-between" }}>
            {file.processingState === "FINISHED" ? (
              <div>
                <Button
                  type="link"
                  style={{ padding: 0 }}
                  onClick={() =>
                    dispatch(
                      setFastQCModalData({
                        fileLabel: file.label,
                        fileId: file.id,
                        sequencingObjectId: fileObjectId,
                        fastQCModalVisible: true,
                        processingState: file.processingState,
                      })
                    )
                  }
                >
                  <span className="t-file-label">{file.label}</span>
                </Button>
                {fastQCModalVisible &&
                sequencingObjectId === fileObjectId &&
                fileId === file.id ? (
                  <FastQC />
                ) : null}
              </div>
            ) : (
              <div>
                <span className="t-file-label">{file.label}</span>
              </div>
            )}

            <Space direction="horizontal" size="small">
              {getProcessingState(file.processingState)}
              <span className="t-file-size">{file.filesize}</span>
              <Button
                shape="circle"
                icon={<IconDownloadFile />}
                className="t-download-file-btn"
                onClick={() => {
                  downloadSequenceFile({
                    sequencingObjectId: fileObjectId,
                    sequenceFileId: file.id,
                  });
                }}
              />
            </Space>
          </div>
        }
      />
    </List.Item>
  );
}
