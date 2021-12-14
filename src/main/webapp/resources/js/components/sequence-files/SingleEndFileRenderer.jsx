import React from "react";
import { Avatar, Button, List, notification, Popconfirm, Space } from "antd";
import { SequenceFileHeader } from "./SequenceFileHeader";
import { setBaseUrl } from "../../utilities/url-utilities";
import { IconDownloadFile, IconFile, IconRemove } from "../icons/Icons";
import { SPACE_XS } from "../../styles/spacing";
import { removeSampleFiles } from "../samples/sampleFilesSlice";
import { useDispatch } from "react-redux";
import { useRemoveSampleFilesMutation } from "../../apis/samples/samples";

/**
 * React component to display single end file details
 *
 * @param {array} files
 * @param sampleId
 * @param fastqcResults
 * @returns {JSX.Element}
 * @constructor
 */
export function SingleEndFileRenderer({
  files,
  sampleId,
  fastqcResults = true,
}) {
  const dispatch = useDispatch();
  const [removeSampleFilesFromSample] = useRemoveSampleFilesMutation();

  const removeSingleFileFromSample = ({ fileObjectId, type }) => {
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
    <List
      bordered
      dataSource={files}
      renderItem={(file) => [
        <List.Item>
          <SequenceFileHeader file={file.fileInfo} />
        </List.Item>,
        <List.Item key={`file-${file.id}`} style={{ width: `100%` }}>
          <List.Item.Meta
            avatar={<Avatar size={`small`} icon={<IconFile />} />}
            title={
              <div style={{ display: "flex", justifyContent: "space-between" }}>
                {fastqcResults ? (
                  <a
                    href={
                      file.fileInfo.sequenceFile
                        ? setBaseUrl(
                            `samples/${sampleId}/sequenceFiles/${file.fileInfo.identifier}/file/${file.fileInfo.sequenceFile.identifier}`
                          )
                        : setBaseUrl(
                            `samples/${sampleId}/sequenceFiles/${file.fileInfo.identifier}/file/${file.fileInfo.file.identifier}`
                          )
                    }
                    target="_blank"
                  >
                    {file.fileInfo.label}
                  </a>
                ) : (
                  <span>{file.fileInfo.label}</span>
                )}

                <Space direction="horizontal" size="small">
                  <span style={{ marginRight: SPACE_XS }}>
                    {file.firstFileSize}
                  </span>
                  <Button
                    style={{ marginRight: SPACE_XS }}
                    shape="circle"
                    icon={<IconDownloadFile />}
                  />
                  <Popconfirm
                    placement="left"
                    title={
                      file.fileType === "assembly"
                        ? i18n("SampleFiles.deleteGenomeAssembly")
                        : i18n("SampleFiles.deleteSequencingObject")
                    }
                    okText={i18n("SampleFiles.okText")}
                    cancelText={i18n("SampleFiles.cancelText")}
                    onConfirm={() =>
                      removeSingleFileFromSample({
                        fileObjectId: file.fileInfo.identifier,
                        type: file.fileType,
                      })
                    }
                  >
                    <Button shape="circle" icon={<IconRemove />} />
                  </Popconfirm>
                </Space>
              </div>
            }
          />
        </List.Item>,
      ]}
    />
  );
}
