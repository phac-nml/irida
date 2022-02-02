import React from "react";
import { CalendarDate } from "../CalendarDate";
import { Button, Popconfirm } from "antd";
import { IconRemove } from "../icons/Icons";
import { useSelector } from "react-redux";
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
}) {
  const { modifiable } = useSelector((state) => state.sampleReducer);

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "space-between",
        width: `100%`,
      }}
    >
      <CalendarDate date={file.createdDate} />
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
