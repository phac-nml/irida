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
 * @param displayConcatenationCheckbox Whether to display checkbox or not
 * @returns {JSX.Element}
 * @constructor
 */
export function SequenceFileHeader({
  file,
  fileObjectId,
  type,
  removeSampleFiles = () => {},
  displayConcatenationCheckbox,
}) {
  const { modifiable } = useSelector((state) => state.sampleReducer);
  const { concatenateSelected } = useSelector(
    (state) => state.sampleFilesReducer
  );
  const dispatch = useDispatch();

  const updateSelected = (e) => {
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
        {modifiable && displayConcatenationCheckbox ? (
          <Checkbox
            style={{ marginRight: SPACE_XS }}
            onChange={updateSelected}
            checked={
              concatenateSelected.filter((e) => e.identifier === fileObjectId)
                .length > 0
            }
            className="t-concatenation-checkbox"
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
          <Button
            shape="circle"
            icon={<IconRemove />}
            className="t-remove-file-btn"
          />
        </Popconfirm>
      ) : null}
    </div>
  );
}
