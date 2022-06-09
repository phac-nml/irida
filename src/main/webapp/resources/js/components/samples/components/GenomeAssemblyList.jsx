import React from "react";
import { Button, notification, Popconfirm } from "antd";
import { useSelector } from "react-redux";

import { SequenceFileTypeRenderer } from "./SequenceFileTypeRenderer";
import { downloadGenomeAssemblyFile } from "../../../apis/samples/samples";
import { GenomeAssemblyListItem } from "../../sequence-files/GenomeAssemblyListItem";

/**
 * React component to display, remove, download genome assemblies
 * @param {function} removeSampleFiles The function to remove genome assemblies
 * @returns {JSX.Element}
 * @constructor
 */
export function GenomeAssemblyList({ removeSampleFiles = () => {} }) {
  const { sample, modifiable: isModifiable } = useSelector(
    (state) => state.sampleReducer
  );
  const { files } = useSelector((state) => state.sampleFilesReducer);

  /*
   Download genome assembly files
   */
  const downloadAssemblyFile = ({ sampleId, genomeAssemblyId }) => {
    notification.success({
      message: i18n("SampleFiles.startingAssemblyDownload"),
    });
    downloadGenomeAssemblyFile({ sampleId, genomeAssemblyId });
  };

  /*
   Get the actions required for a Genome Assembly
   */
  const getActionsForGenomeAssembly = (genomeAssembly) => {
    let actions = [];

    actions.push(
      <Button
        type="link"
        style={{ padding: 0 }}
        className="t-download-file-btn"
        onClick={() => {
          downloadAssemblyFile({
            sampleId: sample.identifier,
            genomeAssemblyId: genomeAssembly.fileInfo.identifier,
          });
        }}
      >
        {i18n("SampleFilesList.download")}
      </Button>
    );

    if (isModifiable) {
      actions.push(
        <Popconfirm
          placement="left"
          title={i18n("SampleFilesList.removeGenomeAssembly")}
          okText={i18n("SampleFiles.okText")}
          cancelText={i18n("SampleFiles.cancelText")}
          okButtonProps={{ className: "t-remove-file-confirm-btn" }}
          cancelButtonProps={{
            className: "t-remove-file-confirm-cancel-btn",
          }}
          onConfirm={() => {
            removeSampleFiles({
              fileObjectId: genomeAssembly.fileInfo.identifier,
              type: "assembly",
            });
          }}
        >
          <Button
            type="link"
            className="t-remove-file-btn"
            style={{ padding: 0 }}
          >
            {i18n("SampleFilesList.remove")}
          </Button>
        </Popconfirm>
      );
    }

    actions.push(
      <span className="t-file-size">{genomeAssembly.firstFileSize}</span>
    );
    return actions;
  };

  return (
    <SequenceFileTypeRenderer title={i18n("SampleFiles.assemblies")}>
      {files.assemblies.map((assembly) => (
        <GenomeAssemblyListItem
          key={`assembly-${assembly.fileInfo.identifier}`}
          genomeAssembly={assembly}
          actions={getActionsForGenomeAssembly(assembly)}
        />
      ))}
    </SequenceFileTypeRenderer>
  );
}
