import React from "react";
import { CalendarDate } from "../CalendarDate";
import { Button, Checkbox, Popconfirm, Space, Tag, Tooltip } from "antd";
import { IconRemove } from "../icons/Icons";
import { useDispatch, useSelector } from "react-redux";
import { SPACE_XS } from "../../styles/spacing";
import {
  addToConcatenateSelected,
  removeFromConcatenateSelected,
} from "../samples/sampleFilesSlice";
import { primaryColour } from "../../utilities/theme-utilities";
import styled from "styled-components";

const HoverItem = styled.div`
  button.ant-btn-link {
    opacity: 0;
    transition: opacity 0.35s ease-in-out;
  }
  &:hover button.ant-btn-link {
    opacity: 1;
  }
`;
/**
 * React component to display sequencing object/ genome assembly header
 * for sample owner or user allowed to modify sample
 *
 * @param file The file to display the header for
 * @param fileObjectId The sequencingobject or genomeassembly identifier
 * @param type The type of file object (sequencingobject or genomeassembly)
 * @param {function} remove files from sample function
 * @param displayConcatenationCheckbox Whether to display checkbox or not
 * @param {function} set default sequencing object for sample
 * @param autoDefaultFirstPair the first pair in the list of pairs (if displaying paired end files)
 * @returns {JSX.Element}
 * @constructor
 */
export function SequenceFileHeaderOwner({
  file,
  fileObjectId,
  type,
  removeSampleFiles = () => {},
  displayConcatenationCheckbox = false,
  updateDefaultSequencingObject = null,
  autoDefaultFirstPair = null,
}) {
  const { concatenateSelected } = useSelector(
    (state) => state.sampleFilesReducer
  );
  const { sample } = useSelector((state) => state.sampleReducer);
  const dispatch = useDispatch();

  const removePopConfirmTitle =
    type === "assembly"
      ? i18n("SampleFiles.deleteGenomeAssembly")
      : i18n("SampleFiles.deleteSequencingObject");

  const updateSelected = (e) => {
    if (e.target.checked) {
      dispatch(addToConcatenateSelected({ seqObject: file }));
    } else {
      dispatch(removeFromConcatenateSelected({ seqObject: file }));
    }
  };

  return (
    <HoverItem
      style={{
        display: "flex",
        justifyContent: "space-between",
        width: `100%`,
      }}
    >
      <div>
        {displayConcatenationCheckbox ? (
          <Tooltip
            title={i18n("SampleFilesConcatenate.checkboxDescription")}
            color={primaryColour}
            placement="right"
          >
            <Checkbox
              style={{ marginRight: SPACE_XS }}
              onChange={updateSelected}
              checked={
                concatenateSelected.filter((e) => e.identifier === fileObjectId)
                  .length > 0
              }
              className="t-concatenation-checkbox"
            />
          </Tooltip>
        ) : null}
        <CalendarDate date={file.createdDate} />
      </div>
      <Space direction="horizontal" size="small">
        {file.forwardSequenceFile && file.reverseSequenceFile ? (
          sample.defaultSequencingObject?.identifier === fileObjectId ||
          (autoDefaultFirstPair !== null &&
            autoDefaultFirstPair.fileInfo.identifier === fileObjectId) ? (
            <Tag color="#108ee9" className="t-default-seq-obj-tag">
              {i18n("SequenceFileHeaderOwner.default")}
            </Tag>
          ) : updateDefaultSequencingObject !== null ? (
            <Button
              size="small"
              key={`set-default-${fileObjectId}`}
              onClick={() => updateDefaultSequencingObject(file)}
              type="link"
              className="t-set-default-seq-obj-button"
            >
              {i18n("SequenceFileHeaderOwner.setAsDefault")}
            </Button>
          ) : null
        ) : null}

        <Popconfirm
          placement="left"
          title={removePopConfirmTitle}
          okText={i18n("SampleFiles.okText")}
          cancelText={i18n("SampleFiles.cancelText")}
          onConfirm={() =>
            removeSampleFiles({
              fileObjectId,
              type,
            })
          }
          okButtonProps={{ className: "t-remove-file-confirm-btn" }}
          cancelButtonProps={{
            className: "t-remove-file-confirm-cancel-btn",
          }}
        >
          <Button
            shape="circle"
            icon={<IconRemove />}
            className="t-remove-file-btn"
          />
        </Popconfirm>
      </Space>
    </HoverItem>
  );
}
