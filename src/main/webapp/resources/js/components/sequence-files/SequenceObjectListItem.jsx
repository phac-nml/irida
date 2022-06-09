import React from "react";
import { Avatar, List, Space, Tag } from "antd";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import {
  FileOutlined,
  SwapOutlined,
  SwapRightOutlined,
} from "@ant-design/icons";
import { blue6, grey3 } from "../../styles/colors";

/**
 * Component to be used anywhere sequencing objects need to be listed
 * @param sequenceObject The sequencing object to list
 * @param actions Actions for paired end forward files, single end files, and fast5 files
 * @param pairedReverseActions Actions for the paired end reverse file
 * @param displayConcatenationCheckbox Whether to display the concatenation checkbox for the sequencing object
 * @param displayFileProcessingStatus Whether to display file processing/coverage
 * @returns {JSX.Element}
 * @constructor
 */
export function SequenceObjectListItem({
  sequenceObject,
  actions = [],
  pairedReverseActions = [],
  displayConcatenationCheckbox = null,
  displayFileProcessingStatus = false,
}) {
  const obj = sequenceObject.fileInfo
    ? sequenceObject.fileInfo
    : sequenceObject.file
    ? sequenceObject.file
    : sequenceObject;

  /*
   If there are qc entries then another list item is added to the sequencing object which causes the
   avatar to shift vertically. This constant restores the location of the avatar to be beside the files.
   Same applies if concatenation checkboxes are present.
   */
  const ELEMENT_ALIGN_MARGIN_TOP = sequenceObject.qcEntries?.length ? -50 : 0;

  const { files, sequenceFile, file } = obj;

  const qcEntryTranslations = {
    COVERAGE: i18n("SequenceObjectListItem.qcEntry.COVERAGE"),
    PROCESSING: i18n("SequenceObjectListItem.qcEntry.PROCESSING"),
  };

  /*
   Function to display file processing status
   */
  const getQcEntries = (entry) => {
    return (
      <Tag
        key={`file-${obj.identifier}-qc-entry-status`}
        color={entry.status === "POSITIVE" ? "green" : "red"}
      >
        {qcEntryTranslations[entry.type]} {entry.message ? entry.message : ""}
      </Tag>
    );
  };

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        width: `100%`,
        border: `solid 1px ${grey3}`,
        padding: 5,
      }}
    >
      {displayConcatenationCheckbox !== null && (
        <div style={{ marginTop: ELEMENT_ALIGN_MARGIN_TOP }}>
          {displayConcatenationCheckbox}
        </div>
      )}
      <Avatar
        style={{
          backgroundColor: blue6,
          verticalAlign: "middle",
          marginRight: 10,
          marginTop: ELEMENT_ALIGN_MARGIN_TOP,
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
      <List style={{ width: `100%` }} itemLayout="horizontal" component="div">
        {files?.length ? (
          <div>
            <List.Item
              actions={actions}
              className="t-file-details"
              key={`seqobj-pair-forward-${files[0].identifier}`}
            >
              <List.Item.Meta
                title={
                  <div
                    style={{
                      display: "flex",
                      justifyContent: "space-between",
                    }}
                  >
                    <span className="t-file-label">{files[0].label}</span>
                  </div>
                }
                description={formatInternationalizedDateTime(
                  files?.length
                    ? files[0].createdDate
                    : sequenceFile
                    ? sequenceFile.createdDate
                    : file.createdDate
                )}
              />
            </List.Item>
            <List.Item
              actions={pairedReverseActions}
              className="t-file-details"
              key={`seqobj-pair-reverse-${files[1].identifier}`}
            >
              <List.Item.Meta
                title={
                  <div
                    style={{
                      display: "flex",
                      justifyContent: "space-between",
                    }}
                  >
                    <span className="t-file-label">{files[1].label}</span>
                  </div>
                }
                description={formatInternationalizedDateTime(
                  files[1].createdDate
                )}
              />
            </List.Item>
          </div>
        ) : (
          <List.Item
            actions={actions}
            className="t-file-details"
            key={`seqobj-file-${
              sequenceFile ? sequenceFile.identifier : file.identifier
            }`}
          >
            <List.Item.Meta
              title={
                <div
                  style={{ display: "flex", justifyContent: "space-between" }}
                >
                  <span className="t-file-label">
                    {sequenceFile ? sequenceFile.label : file.label}
                  </span>
                </div>
              }
              description={formatInternationalizedDateTime(
                sequenceFile ? sequenceFile.createdDate : file.createdDate
              )}
            />
          </List.Item>
        )}

        {displayFileProcessingStatus && sequenceObject.qcEntries?.length ? (
          <List.Item key={`qc-entry-${obj.identifier}`}>
            <List.Item.Meta
              title={sequenceObject.qcEntries.map((entry) => {
                return getQcEntries(entry);
              })}
            />
          </List.Item>
        ) : null}
      </List>
    </div>
  );
}
