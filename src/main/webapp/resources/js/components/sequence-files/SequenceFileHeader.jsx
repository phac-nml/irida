import React from "react";
import { CalendarDate } from "../CalendarDate";
import { useSelector } from "react-redux";

import { SequenceFileHeaderOwner } from "./SequenceFileHeaderOwner";

/**
 * React component to display sequencing object/genome assembly header
 * depending on if user is sample owner (or has ability to modify sample)
 * or not
 *
 * @param file The file to display the header for
 * @param fileObjectId The sequencingobject or genomeassembly identifier
 * @param type The type of file object (sequencingobject or genomeassembly)
 * @function remove files from sample function
 * @param displayConcatenationCheckbox Whether to display checkbox or not
 * @function set default sequencing object for sample
 * @returns {JSX.Element}
 * @constructor
 */
export function SequenceFileHeader({
  file,
  fileObjectId,
  type,
  removeSampleFiles = () => {},
  displayConcatenationCheckbox = false,
  updateDefaultSequencingObject = null,
}) {
  const { modifiable, sample } = useSelector((state) => state.sampleReducer);
  return (
    <div
      style={{
        display: "flex",
        justifyContent: "space-between",
        width: `100%`,
      }}
    >
      {modifiable ? (
        <SequenceFileHeaderOwner
          file={file}
          fileObjectId={fileObjectId}
          removeSampleFiles={removeSampleFiles}
          type={type}
          displayConcatenationCheckbox={displayConcatenationCheckbox}
          updateDefaultSequencingObject={updateDefaultSequencingObject}
        />
      ) : (
        <CalendarDate date={file.createdDate} />
      )}
    </div>
  );
}
