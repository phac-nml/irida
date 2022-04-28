import React from "react";
import { Button, Tag } from "antd";
import { useSelector } from "react-redux";

/**
 * React component to display button to set as default or tag if already set as default
 *
 * @param file The file to display the header for
 * @param fileObjectId The sequencingobject or genomeassembly identifier
 * @param type The type of file object (sequencingobject or genomeassembly)
 * @param {function} set default sequencing object for sample
 * @param autoDefaultFirstPair the first pair in the list of pairs (if displaying paired end files)
 * @returns {JSX.Element}
 * @constructor
 */
export function DefaultFileObject({
  file,
  fileObjectId,
  type,
  updateDefaultSequencingObject = null,
  autoDefaultFirstPair = null,
  updateDefaultGenomeAssembly = null,
  autoDefaultFirstAssembly = null,
}) {
  const { sample } = useSelector((state) => state.sampleReducer);

  const renderSetAsDefaultButton = () => {
    let className = "t-default-seq-obj-tag";
    let fileObjectKey = `set-default-${fileObjectId}`;
    let UPDATE_FUNC = updateDefaultSequencingObject;
    if(type === "assembly") {
      className = "t-set-default-genome-assembly-button";
      fileObjectKey = `set-default-genome-assembly-${fileObjectId}`;
      UPDATE_FUNC = updateDefaultGenomeAssembly
    }

    return (
      <Button
        size="small"
        key={fileObjectKey}
        onClick={() => UPDATE_FUNC(file)}
        type="link"
        className={className}
      >
        {i18n("SequenceFileHeaderOwner.setAsDefault")}
      </Button>
    );
  }

  const renderDefaultTag = () => {
    let className = "t-default-seq-obj-tag";

    if(type === "assembly") {
      className = "t-default-genome-assembly-tag";
    }

    return(
      <Tag color="#108ee9" className={className}>
        {i18n("SequenceFileHeaderOwner.default")}
      </Tag>
    );
  }
  return (
    <>
      {file.forwardSequenceFile && file.reverseSequenceFile ? (
        sample.defaultSequencingObject?.identifier === fileObjectId ||
        (autoDefaultFirstPair !== null &&
          autoDefaultFirstPair.fileInfo.identifier === fileObjectId) ? (
          renderDefaultTag()
        ) : updateDefaultSequencingObject !== null ? (
          renderSetAsDefaultButton()
        ) : null
      ) : type === "assembly" ?
        sample.defaultGenomeAssembly?.identifier === fileObjectId ||
        (autoDefaultFirstAssembly !== null &&
          autoDefaultFirstAssembly.fileInfo.identifier === fileObjectId) ? (
          renderDefaultTag()
        ) : updateDefaultGenomeAssembly !== null ? (
          renderSetAsDefaultButton()
        ) : null : null
      }
    </>
  );
}
