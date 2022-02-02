import React from "react";
import { CalendarDate } from "../CalendarDate";
import { Button, Checkbox, Popconfirm } from "antd";
import { IconRemove } from "../icons/Icons";
import { useDispatch, useSelector } from "react-redux";
import { SPACE_XS } from "../../styles/spacing";
import {
  addToConcatenateSelected,
  removeFromConcatenateSelected,
} from "../samples/sampleFilesSlice";

/**
 * React component to display paired end file details
 *
 * @param file The file to display the header for
 * @param fileObjectId The sequencingobject or genomeassembly identifier
 * @param type The type of file object (sequencingobject or genomeassembly)
 * @function remove files from sample function
 * @returns {JSX.Element}
 * @constructor
 */
export function SequenceFileHeader({
  file,
  fileObjectId,
  type,
  removeSampleFiles = () => {},
  displayCheckbox,
}) {
  const { modifiable } = useSelector((state) => state.sampleReducer);
  const dispatch = useDispatch();

  const updateSelected = (e, file) => {
    if (e.target.checked) {
      dispatch(addToConcatenateSelected({ seqObject: file }));
    } else {
      dispatch(removeFromConcatenateSelected({ seqObject: file }));
    }
  };

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "space-between",
        width: `100%`,
      }}
    >
      <div>
        {modifiable && displayCheckbox ? (
          <Checkbox
            style={{ marginRight: SPACE_XS }}
            onChange={(e) => updateSelected(e, file)}
          />
        ) : null}
        <CalendarDate date={file.createdDate} />
      </div>
      {modifiable ? (
        <Popconfirm
          placement="left"
          title={
            type === "assembly"
              ? i18n("SampleFiles.deleteGenomeAssembly")
              : i18n("SampleFiles.deleteSequencingObject")
          }
          okText={i18n("SampleFiles.okText")}
          cancelText={i18n("SampleFiles.cancelText")}
          onConfirm={() =>
            removeSampleFiles({
              fileObjectId,
              type,
            })
          }
        >
          <Button shape="circle" icon={<IconRemove />} />
        </Popconfirm>
      ) : null}
    </div>
  );
}
