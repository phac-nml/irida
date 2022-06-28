import React from "react";
import { Button, notification, Popconfirm, Tag, Tooltip } from "antd";
import { useDispatch, useSelector } from "react-redux";

import { SequenceFileTypeRenderer } from "./SequenceFileTypeRenderer";
import { downloadGenomeAssemblyFile } from "../../../apis/samples/samples";
import { GenomeAssemblyListItem } from "../../sequence-files/GenomeAssemblyListItem";
import { DEFAULT_ACTION_WIDTH } from "../sampleFilesSlice";
import { useUpdateDefaultSampleGenomeAssemblyMutation } from "../../../apis/samples/samples";
import { setDefaultGenomeAssembly } from "../sampleSlice";

/**
 * React component to display, remove, download genome assemblies
 * @param {function} removeSampleFiles The function to remove genome assemblies
 * @returns {JSX.Element}
 * @constructor
 */
export function GenomeAssemblyList({ removeSampleFiles = () => {} }) {
  const [updateSampleDefaultGenomeAssembly] =
    useUpdateDefaultSampleGenomeAssemblyMutation();
  const { sample, modifiable: isModifiable } = useSelector(
    (state) => state.sampleReducer
  );
  const { files } = useSelector((state) => state.sampleFilesReducer);
  const ACTION_MARGIN_RIGHT = isModifiable ? 0 : 5;
  const dispatch = useDispatch();

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
  Set default genome assembly for sample to be used for analyses
   */
  const updateDefaultGenomeAssembly = (genomeAssembly) => {
    const { fileInfo: genomeAssemblyObj } = genomeAssembly;

    updateSampleDefaultGenomeAssembly({
      sampleId: sample.identifier,
      genomeAssemblyId: genomeAssemblyObj.identifier,
    })
      .then(({ data }) => {
        dispatch(setDefaultGenomeAssembly(genomeAssemblyObj));
        notification.success({ message: data.message });
      })
      .catch((error) => {
        notification.error({ message: error });
      });
  };

  /*
   Get the actions required for a Genome Assembly
   */
  const getActionsForGenomeAssembly = (genomeAssembly, index) => {
    const { fileInfo: genomeAssemblyObj } = genomeAssembly;
    let actions = [];

    if (isModifiable) {
      if (
        (sample.defaultGenomeAssembly !== null &&
          genomeAssemblyObj.identifier ===
            sample.defaultGenomeAssembly.identifier) ||
        (sample.defaultGenomeAssembly === null && index === 0)
      ) {
        actions.push(
          <Tooltip
            title={i18n("SampleFilesList.defaultSelectedAssembly")}
            placement="top"
            key={`default-tag-tooltip-ga-${genomeAssemblyObj.identifier}`}
          >
            <Tag
              color="#108ee9"
              key={`default-tag-ga-${genomeAssemblyObj.identifier}`}
              className="t-default-genome-assembly-tag"
            >
              {i18n("SampleFilesList.default")}
            </Tag>
          </Tooltip>
        );
      } else {
        actions.push(
          <Tooltip
            title={i18n("SampleFilesList.tooltip.setAsDefaultAssembly")}
            placement="top"
            key={`set-default-tooltip-ga-${genomeAssemblyObj.identifier}`}
          >
            <Button
              size="small"
              key={`set-default-ga-${genomeAssemblyObj.identifier}`}
              onClick={() => updateDefaultGenomeAssembly(genomeAssembly)}
              type="link"
              className="t-set-default-genome-assembly-button"
              style={{ width: 100 }}
            >
              {i18n("SampleFilesList.setAsDefault")}
            </Button>
          </Tooltip>
        );
      }
    }

    actions.push(
      <span
        key={`${genomeAssemblyObj.identifier}-file-size`}
        className="t-file-size"
      >
        {genomeAssembly.firstFileSize}
      </span>,
      <Button
        type="link"
        key={`${genomeAssemblyObj.identifier}-download-btn`}
        style={{
          padding: 0,
          width: DEFAULT_ACTION_WIDTH,
          marginRight: ACTION_MARGIN_RIGHT,
        }}
        className="t-download-file-btn"
        onClick={() => {
          downloadAssemblyFile({
            sampleId: sample.identifier,
            genomeAssemblyId: genomeAssemblyObj.identifier,
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
              fileObjectId: genomeAssemblyObj.identifier,
              type: "assembly",
            });
          }}
        >
          <Button
            type="link"
            className="t-remove-file-btn"
            style={{ padding: 0, width: DEFAULT_ACTION_WIDTH }}
            key={`${genomeAssemblyObj.identifier}-remove-btn`}
          >
            {i18n("SampleFilesList.remove")}
          </Button>
        </Popconfirm>
      );
    }

    return actions;
  };

  return (
    <SequenceFileTypeRenderer title={i18n("SampleFiles.assemblies")}>
      {files.assemblies.map((assembly, index) => (
        <GenomeAssemblyListItem
          key={`assembly-${assembly.fileInfo.identifier}`}
          genomeAssembly={assembly}
          actions={getActionsForGenomeAssembly(assembly, index)}
        />
      ))}
    </SequenceFileTypeRenderer>
  );
}
