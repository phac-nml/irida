import React from "react";
import {
  Button,
  Checkbox,
  notification,
  Popconfirm,
  Space,
  Tag,
  Tooltip,
} from "antd";
import { SequenceFileTypeRenderer } from "./SequenceFileTypeRenderer";

import {
  downloadGenomeAssemblyFile,
  downloadSequencingObjectFile,
  fetchUpdatedSequencingObjects,
  useRemoveSampleFilesMutation,
  useUpdateDefaultSampleSequencingObjectMutation,
} from "../../../apis/samples/samples";

import {
  removeFileObjectFromSample,
  updatedSequencingObjects,
  fetchUpdatedSeqObjectsDelay,
  addToConcatenateSelected,
  removeFromConcatenateSelected,
} from "../sampleFilesSlice";
import { useDispatch, useSelector } from "react-redux";
import {
  IconCheckCircle,
  IconClock,
  IconSyncSpin,
  IconRemove,
} from "../../icons/Icons";
import { useInterval } from "../../../hooks";
import { setDefaultSequencingObject } from "../sampleSlice";
import { SequenceObjectListItem } from "../../sequence-files/SequenceObjectListItem";
import { GenomeAssemblyListItem } from "../../sequence-files/GenomeAssemblyListItem";
import { primaryColour } from "../../../utilities/theme-utilities";
import { SPACE_XS } from "../../../styles/spacing";

/**
 * React component to display, remove, download files
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleFileList() {
  const dispatch = useDispatch();
  const [removeSampleFilesFromSample] = useRemoveSampleFilesMutation();
  const [updateSampleDefaultSequencingObject] =
    useUpdateDefaultSampleSequencingObjectMutation();
  const {
    sample,
    projectId,
    modifiable: isModifiable,
  } = useSelector((state) => state.sampleReducer);
  const { files, concatenateSelected } = useSelector(
    (state) => state.sampleFilesReducer
  );

  const fileProcessTranslations = {
    UNPROCESSED: i18n("SampleFilesList.fileProcessingState.UNPROCESSED"),
    QUEUED: i18n("SampleFilesList.fileProcessingState.QUEUED"),
    PROCESSING: i18n("SampleFilesList.fileProcessingState.PROCESSING"),
    FINISHED: i18n("SampleFilesList.fileProcessingState.FINISHED"),
    ERROR: i18n("SampleFilesList.fileProcessingState.ERROR"),
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

  /*
   Get the actions required for a Paired End -> Forward sequence file, single end sequence file, and/or fast5 object
   */
  const getActionsForSequencingObject = (seqObj) => {
    let actions = [];

    const obj = seqObj.fileInfo
      ? seqObj.fileInfo
      : seqObj.file
      ? seqObj.file
      : seqObj;

    if (isModifiable && obj.files && obj.files.length === 2) {
      if (
        sample.defaultSequencingObject !== null &&
        obj.identifier === sample.defaultSequencingObject.identifier
      ) {
        actions.push(
          <Tag color="#108ee9" className="t-default-seq-obj-tag">
            {i18n("SequenceFileHeaderOwner.default")}
          </Tag>
        );
      } else {
        actions.push(
          <Tooltip
            title={
              "Set this paired end sequencing object as the default for the sample. The sequencing object will be selected by default when running a pipeline"
            }
            placement="top"
          >
            <Button
              size="small"
              key={`set-default-${obj.identifier}`}
              onClick={() => updateDefaultSequencingObject(obj)}
              type="link"
              className="t-set-default-seq-obj-button"
            >
              {i18n("SequenceFileHeaderOwner.setAsDefault")}
            </Button>
          </Tooltip>
        );
      }
    }

    actions.push(
      <Button
        type="link"
        style={{ padding: 0 }}
        className="t-download-file-btn"
        onClick={() => {
          downloadSequenceFile({
            sequencingObjectId: obj.identifier,
            sequenceFileId: obj.files[0].identifier,
          });
        }}
      >
        Download
      </Button>
    );

    if (isModifiable) {
      actions.push(
        <Popconfirm
          placement="left"
          title="Are you sure you want to remove this sequencing object?"
          okText={i18n("SampleFiles.okText")}
          cancelText={i18n("SampleFiles.cancelText")}
          okButtonProps={{ className: "t-remove-file-confirm-btn" }}
          cancelButtonProps={{
            className: "t-remove-file-confirm-cancel-btn",
          }}
          onConfirm={() => {
            removeSampleFiles({
              fileObjectId: obj.identifier,
              type: "sequencingObject",
            });
          }}
        >
          <Tooltip
            title={
              "This will remove this complete sequencing object (paired end, single end, fast5) from the sample."
            }
            placement="top"
          >
            <Button
              type="link"
              className="t-remove-file-btn"
              style={{ padding: 0 }}
            >
              Remove
            </Button>
          </Tooltip>
        </Popconfirm>
      );
    }

    actions.push(
      getProcessingStateTag(obj.processingState),
      <span className="t-file-size">{seqObj.firstFileSize}</span>
    );
    return actions;
  };

  /*
   Get the actions required for a Paired End -> Reverse sequence file
   */
  const getActionsForSequencingObjectPairedReverse = (seqObj) => {
    let actions = [];

    const obj = seqObj.fileInfo
      ? seqObj.fileInfo
      : seqObj.file
      ? seqObj.file
      : seqObj;

    actions.push(
      <Button
        type="link"
        style={{ padding: 0 }}
        className="t-download-file-btn"
        onClick={() => {
          downloadSequenceFile({
            sequencingObjectId: obj.identifier,
            sequenceFileId: obj.files[1].identifier,
          });
        }}
      >
        Download
      </Button>,
      getProcessingStateTag(obj.processingState),
      <span className="t-file-size">{seqObj.secondFileSize}</span>
    );
    return actions;
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
        Download
      </Button>
    );

    if (isModifiable) {
      actions.push(
        <Popconfirm
          placement="left"
          title="Are you sure you want to remove this genome assembly?"
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
            Remove
          </Button>
        </Popconfirm>
      );
    }

    actions.push(
      <span className="t-file-size">{genomeAssembly.firstFileSize}</span>
    );
    return actions;
  };

  /*
   Returns a checkbox with a toolbox for the passed in sequencing object
   */
  const getConcatenationCheckboxForSequencingObject = (seqObj) => {
    const obj = seqObj.fileInfo
      ? seqObj.fileInfo
      : seqObj.file
      ? seqObj.file
      : seqObj;

    return (
      <div>
        <Tooltip
          title={i18n("SampleFilesConcatenate.checkboxDescription")}
          color={primaryColour}
          placement="right"
        >
          <Checkbox
            style={{ marginRight: SPACE_XS }}
            className="t-concatenation-checkbox"
            onChange={(e) => updateSelected(e, obj)}
            checked={
              concatenateSelected.filter((e) => e.identifier === obj.identifier)
                .length > 0
            }
          />
        </Tooltip>
      </div>
    );
  };

  /*
   Update which sequencing objects are selected for concatenation
   */
  const updateSelected = (e, seqObj) => {
    if (e.target.checked) {
      dispatch(addToConcatenateSelected({ seqObject: seqObj }));
    } else {
      dispatch(removeFromConcatenateSelected({ seqObject: seqObj }));
    }
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
          {files.singles.map((sequenceObject) => (
            <SequenceObjectListItem
              key={`single-${sequenceObject.fileInfo.identifier}`}
              sequenceObject={sequenceObject}
              actions={getActionsForSequencingObject(sequenceObject)}
              displayConcatenationCheckbox={
                isModifiable && files.singles?.length >= 2
              }
              concatenationCheckbox={
                isModifiable &&
                getConcatenationCheckboxForSequencingObject(sequenceObject)
              }
            />
          ))}
        </SequenceFileTypeRenderer>
      )}
      {files.paired && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.paired")}>
          {files.paired.map((pair) => (
            <SequenceObjectListItem
              key={`pair-${pair.fileInfo.identifier}`}
              sequenceObject={pair}
              actions={getActionsForSequencingObject(pair)}
              pairedReverseActions={getActionsForSequencingObjectPairedReverse(
                pair
              )}
              displayConcatenationCheckbox={
                isModifiable && files.paired?.length >= 2
              }
              concatenationCheckbox={
                isModifiable &&
                getConcatenationCheckboxForSequencingObject(pair)
              }
            />
          ))}
        </SequenceFileTypeRenderer>
      )}
      {files.fast5 && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.fast5")}>
          {files.fast5.map((fast5Obj) => (
            <SequenceObjectListItem
              key={`fast5-${fast5Obj.fileInfo.identifier}`}
              sequenceObject={fast5Obj}
              actions={getActionsForSequencingObject(fast5Obj)}
            />
          ))}
        </SequenceFileTypeRenderer>
      )}
      {files.assemblies && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.assemblies")}>
          {files.assemblies.map((assembly) => (
            <GenomeAssemblyListItem
              key={`assembly-${assembly.fileInfo.identifier}`}
              genomeAssembly={assembly}
              actions={getActionsForGenomeAssembly(assembly)}
            />
          ))}
        </SequenceFileTypeRenderer>
      )}
    </Space>
  );
}
