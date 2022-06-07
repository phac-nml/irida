import React from "react";
import { Avatar, List, Space, Tag } from "antd";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import {
  FileOutlined,
  SwapOutlined,
  SwapRightOutlined,
} from "@ant-design/icons";
import { blue6 } from "../../styles/colors";

/**
 * Component to be used anywhere sequencing objects need to be listed
 * @param sequenceObject
 * @param pairedReverseActions
 * @param displayConcatenationCheckbox
 * @param concatenationCheckbox
 * @param displayFileProcessingStatus
 * @returns {JSX.Element}
 * @constructor
 */

export function SequenceObjectListItem({
  sequenceObject,
  actions = [],
  pairedReverseActions = [],
  displayConcatenationCheckbox = false,
  concatenationCheckbox = null,
  displayFileProcessingStatus = false,
}) {
  const obj = sequenceObject.fileInfo
    ? sequenceObject.fileInfo
    : sequenceObject.file
    ? sequenceObject.file
    : sequenceObject;

  const { files, sequenceFile, file } = obj;

  const qcEntryTranslations = {
    COVERAGE: i18n("SampleFilesList.qcEntry.COVERAGE"),
    PROCESSING: i18n("SampleFilesList.qcEntry.PROCESSING"),
  };

  /*
   Function to display file processing status
   */
  const getQcEntries = (entry) => {
    return (
      <Tag
        key={`file-${file.id}-qc-entry-status`}
        color={entry.status === "POSITIVE" ? "green" : "red"}
      >
        {qcEntryTranslations[entry.type]} {entry.message ? entry.message : ""}
      </Tag>
    );
  };

  return (
    <List.Item>
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          width: `100%`,
          border: "solid 1px #EEE",
          padding: 5,
        }}
      >
        {displayConcatenationCheckbox && concatenationCheckbox}
        <Avatar
          style={{
            backgroundColor: blue6,
            verticalAlign: "middle",
            marginRight: 10,
          }}
          icon={
            files?.length && files.length > 1 ? (
              <SwapOutlined />
            ) : sequenceFile ? (
              <SwapRightOutlined />
            ) : (
              <FileOutlined />
            )
          }
        />
        <List style={{ width: `100%` }} itemLayout="horizontal">
          <List.Item actions={actions}>
            <List.Item.Meta
              title={
                <div
                  style={{ display: "flex", justifyContent: "space-between" }}
                >
                  <span>
                    {files?.length
                      ? files[0].label
                      : sequenceFile
                      ? sequenceFile.label
                      : file.label}
                  </span>
                </div>
              }
              description={
                <Space
                  direction="vertical"
                  size="small"
                  style={{ width: "100%" }}
                >
                  {formatInternationalizedDateTime(
                    files?.length
                      ? files[0].createdDate
                      : sequenceFile
                      ? sequenceFile.createdDate
                      : file.createdDate
                  )}

                  {displayFileProcessingStatus &&
                  sequenceObject.qcEntries !== null
                    ? sequenceObject.qcEntries.map((entry) => {
                        return getQcEntries(entry);
                      })
                    : null}
                </Space>
              }
            />
          </List.Item>
          {files?.length && (
            <List.Item actions={pairedReverseActions}>
              <List.Item.Meta
                title={
                  <div
                    style={{ display: "flex", justifyContent: "space-between" }}
                  >
                    <span>{files[1].label}</span>
                  </div>
                }
                description={
                  <Space
                    direction="vertical"
                    size="small"
                    style={{ width: "100%" }}
                  >
                    {formatInternationalizedDateTime(files[1].createdDate)}
                    {displayFileProcessingStatus &&
                    sequenceObject.qcEntries !== null
                      ? sequenceObject.qcEntries.map((entry) => {
                          return getQcEntries(entry);
                        })
                      : null}
                  </Space>
                }
              />
            </List.Item>
          )}
        </List>
      </div>
    </List.Item>
  );
}
