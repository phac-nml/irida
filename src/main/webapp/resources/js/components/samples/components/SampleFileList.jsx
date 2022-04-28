import React from "react";
import { notification, Space, Tag, Tooltip } from "antd";
import { SequenceFileTypeRenderer } from "./SequenceFileTypeRenderer";
import { SingleEndFileRenderer } from "../../sequence-files/SingleEndFileRenderer";
import { PairedFileRenderer } from "../../sequence-files/PairedFileRenderer";
import {
  downloadGenomeAssemblyFile,
  downloadSequencingObjectFile,
  fetchUpdatedSequencingObjects,
  useRemoveSampleFilesMutation,
  useUpdateDefaultSampleGenomeAssemblyMutation,
  useUpdateDefaultSampleSequencingObjectMutation,
} from "../../../apis/samples/samples";

import {
  removeFileObjectFromSample,
  updatedSequencingObjects,
  fetchUpdatedSeqObjectsDelay,
} from "../sampleFilesSlice";
import { useDispatch, useSelector } from "react-redux";
import {
  IconCheckCircle,
  IconClock,
  IconSyncSpin,
  IconRemove,
} from "../../icons/Icons";
import { useInterval } from "../../../hooks";
import { setDefaultSequencingObject, setDefaultGenomeAssembly } from "../sampleSlice";

/**
 * React component to display, remove, download files
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleFileList() {
  const dispatch = useDispatch();
  const [removeSampleFilesFromSample] = useRemoveSampleFilesMutation();
  const [
    updateSampleDefaultSequencingObject,
  ] = useUpdateDefaultSampleSequencingObjectMutation();
  const [
    updateSampleDefaultGenomeAssembly,
  ] = useUpdateDefaultSampleGenomeAssemblyMutation();
  const { sample, projectId } = useSelector((state) => state.sampleReducer);
  const { files } = useSelector((state) => state.sampleFilesReducer);

  const fileProcessTranslations = {
    UNPROCESSED: i18n("SampleFilesList.fileProcessingState.UNPROCESSED"),
    QUEUED: i18n("SampleFilesList.fileProcessingState.QUEUED"),
    PROCESSING: i18n("SampleFilesList.fileProcessingState.PROCESSING"),
    FINISHED: i18n("SampleFilesList.fileProcessingState.FINISHED"),
    ERROR: i18n("SampleFilesList.fileProcessingState.ERROR"),
  };

  const qcEntryTranslations = {
    COVERAGE: i18n("SampleFilesList.qcEntry.COVERAGE"),
    PROCESSING: i18n("SampleFilesList.qcEntry.PROCESSING"),
  };
  /*
  Download sequence files (paired, single, fast5)
 */
  const downloadSequenceFile = ({ sequencingObjectId, sequenceFileId }) => {
    notification.success({
      message: i18n("SampleFiles.startingSequenceFileDownload"),
    });
    downloadSequencingObjectFile({ sequencingObjectId, sequenceFileId });
  };

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
  Remove sequencingobjects and/or genomeassembly objects from sample
   */
  const removeSampleFiles = ({ fileObjectId, type }) => {
    removeSampleFilesFromSample({
      sampleId: sample.identifier,
      fileObjectId,
      type,
    })
      .then(({ data }) => {
        notification.success({ message: data.message });
        dispatch(removeFileObjectFromSample({ fileObjectId, type }));

        if (sample.defaultsequencingObject?.identifier === fileObjectId) {
          dispatch(setDefaultSequencingObject(null));
        }
      })
      .catch((error) => {
        notification.error({ message: error });
      });
  };

  /*
  Set default sequencingobject for sample to be used for analyses
   */
  const updateDefaultSequencingObject = (sequencingObject) => {
    updateSampleDefaultSequencingObject({
      sampleId: sample.identifier,
      sequencingObjectId: sequencingObject.identifier,
    })
      .then(({ data }) => {
        dispatch(setDefaultSequencingObject(sequencingObject));
        notification.success({ message: data.message });
      })
      .catch((error) => {
        notification.error({ message: error });
      });
  };

  /*
  Set default genomeassembly for sample to be used for analyses
   */
  const updateDefaultGenomeAssembly = (genomeAssembly) => {
    updateSampleDefaultGenomeAssembly({
      sampleId: sample.identifier,
      genomeAssemblyId: genomeAssembly.identifier,
    })
      .then(({ data }) => {
        dispatch(setDefaultGenomeAssembly(genomeAssembly));
        notification.success({ message: data.message });
      })
      .catch((error) => {
        notification.error({ message: error });
      });
  };

  /*
   Get updated processing states for the sequencing objects
   which were still getting processed
   */
  useInterval(() => {
    let seqObjIdsSingles = files.singles
      ?.filter((singleEndFile) => {
        return (
          singleEndFile.fileInfo.processingState !== "FINISHED" &&
          singleEndFile.fileInfo.processingState !== "ERROR"
        );
      })
      .map((singleEndFile) => singleEndFile.fileInfo.identifier);

    let seqObjIdsPaired = files.paired
      ?.filter((pair) => {
        return (
          pair.fileInfo.processingState !== "FINISHED" &&
          pair.fileInfo.processingState !== "ERROR"
        );
      })
      .map((pair) => pair.fileInfo.identifier);

    let seqObjIdsFast5 = files.fast5
      ?.filter((fast5File) => {
        return (
          fast5File.fileInfo.processingState !== "FINISHED" &&
          fast5File.fileInfo.processingState !== "ERROR"
        );
      })
      .map((fast5File) => fast5File.fileInfo.identifier);

    let sequencingObjectIds = [];

    if (typeof seqObjIdsSingles !== "undefined") {
      sequencingObjectIds = [...sequencingObjectIds, ...seqObjIdsSingles];
    }
    if (typeof seqObjIdsPaired !== "undefined") {
      sequencingObjectIds = [...sequencingObjectIds, ...seqObjIdsPaired];
    }
    if (typeof seqObjIdsFast5 !== "undefined") {
      sequencingObjectIds = [...sequencingObjectIds, ...seqObjIdsFast5];
    }

    if (sequencingObjectIds.length) {
      fetchUpdatedSequencingObjects({
        sampleId: sample.identifier,
        projectId,
        sequencingObjectIds,
      })
        .then((data) => {
          dispatch(
            updatedSequencingObjects({
              updatedSeqObjects: data,
            })
          );
        })
        .catch((error) => {
          notification.error({ message: error });
        });
    }
  }, fetchUpdatedSeqObjectsDelay);

  /*
  Gets the processing state as a tag (icon) with a tooltip
   */
  const getProcessingStateTag = (processingState) => {
    let tagColor = "default";
    let icon = <IconClock />;

    if (processingState === "FINISHED") {
      tagColor = "success";
      icon = <IconCheckCircle />;
    } else if (processingState === "ERROR") {
      tagColor = "error";
      icon = <IconRemove />;
    } else if (processingState === "PROCESSING") {
      tagColor = "processing";
      icon = <IconSyncSpin />;
    }
    return (
      <Tooltip placement="top" title={fileProcessTranslations[processingState]}>
        <Tag color={tagColor} className="t-file-processing-status">
          {icon}
        </Tag>
      </Tooltip>
    );
  };

  return (
    <Space
      size="large"
      direction="vertical"
      style={{ maxHeight: "500px", overflowY: "auto", width: `100%` }}
      className="t-filelist-scroll"
    >
      {files.singles && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.singles")}>
          <SingleEndFileRenderer
            files={files.singles}
            sampleId={sample.identifier}
            downloadSequenceFile={downloadSequenceFile}
            removeSampleFiles={removeSampleFiles}
            getProcessingState={getProcessingStateTag}
            qcEntryTranslations={qcEntryTranslations}
            displayConcatenationCheckbox={files.singles?.length >= 2}
          />
        </SequenceFileTypeRenderer>
      )}
      {files.paired && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.paired")}>
          {files.paired.map((pair) => (
            <PairedFileRenderer
              key={`pair-${pair.identifier}`}
              pair={pair}
              sampleId={sample.identifier}
              downloadSequenceFile={downloadSequenceFile}
              removeSampleFiles={removeSampleFiles}
              getProcessingState={getProcessingStateTag}
              qcEntryTranslations={qcEntryTranslations}
              displayConcatenationCheckbox={files.paired?.length >= 2}
              updateDefaultSequencingObject={updateDefaultSequencingObject}
              autoDefaultFirstPair={
                sample.defaultSequencingObject === null ? files.paired[0] : null
              }
            />
          ))}
        </SequenceFileTypeRenderer>
      )}
      {files.fast5 && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.fast5")}>
          <SingleEndFileRenderer
            files={files.fast5}
            sampleId={sample.identifier}
            downloadSequenceFile={downloadSequenceFile}
            removeSampleFiles={removeSampleFiles}
            getProcessingState={getProcessingStateTag}
            qcEntryTranslations={qcEntryTranslations}
          />
        </SequenceFileTypeRenderer>
      )}
      {files.assemblies && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.assemblies")}>
          <SingleEndFileRenderer
            files={files.assemblies}
            fastqcResults={false}
            sampleId={sample.identifier}
            downloadAssemblyFile={downloadAssemblyFile}
            removeSampleFiles={removeSampleFiles}
            qcEntryTranslations={qcEntryTranslations}
            updateDefaultGenomeAssembly={updateDefaultGenomeAssembly}
            autoDefaultFirstAssembly={
              sample.defaultGenomeAssembly === null ? files.assemblies[0] : null
            }
          />
        </SequenceFileTypeRenderer>
      )}
    </Space>
  );
}
