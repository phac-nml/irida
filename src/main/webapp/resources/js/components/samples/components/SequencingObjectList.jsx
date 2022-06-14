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
import {
  CheckCircleOutlined,
  ClockCircleOutlined,
  CloseOutlined,
  SyncOutlined,
} from "@ant-design/icons";
import { useDispatch, useSelector } from "react-redux";
import { useInterval } from "../../../hooks";
import { SequenceFileTypeRenderer } from "./SequenceFileTypeRenderer";
import { SequenceObjectListItem } from "../../sequence-files/SequenceObjectListItem";
import { primaryColour } from "../../../utilities/theme-utilities";
import { SPACE_XS } from "../../../styles/spacing";

import {
  addToConcatenateSelected,
  DEFAULT_ACTION_WIDTH,
  fetchUpdatedSeqObjectsDelay,
  removeFromConcatenateSelected,
  updatedSequencingObjects,
} from "../sampleFilesSlice";
import { setDefaultSequencingObject } from "../sampleSlice";
import {
  downloadSequencingObjectFile,
  fetchUpdatedSequencingObjects,
  useUpdateDefaultSampleSequencingObjectMutation,
} from "../../../apis/samples/samples";

/**
 * React component to display, remove, download sequencing objects
 * @param {function} removeSampleFiles The function to remove sequencing objects
 * @returns {JSX.Element}
 * @constructor
 */
export function SequencingObjectList({ removeSampleFiles = () => {} }) {
  const [updateSampleDefaultSequencingObject] =
    useUpdateDefaultSampleSequencingObjectMutation();

  const {
    sample,
    modifiable: isModifiable,
    projectId,
  } = useSelector((state) => state.sampleReducer);
  const { files, concatenateSelected } = useSelector(
    (state) => state.sampleFilesReducer
  );
  const ACTION_MARGIN_RIGHT = isModifiable ? 0 : 5;

  const fileProcessTranslations = {
    UNPROCESSED: i18n("SampleFilesList.fileProcessingState.UNPROCESSED"),
    QUEUED: i18n("SampleFilesList.fileProcessingState.QUEUED"),
    PROCESSING: i18n("SampleFilesList.fileProcessingState.PROCESSING"),
    FINISHED: i18n("SampleFilesList.fileProcessingState.FINISHED"),
    ERROR: i18n("SampleFilesList.fileProcessingState.ERROR"),
  };

  const dispatch = useDispatch();

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
  Update which sequencing objects are selected for concatenation
  */
  const updateSelected = (e, seqObj) => {
    if (e.target.checked) {
      dispatch(addToConcatenateSelected({ seqObject: seqObj }));
    } else {
      dispatch(removeFromConcatenateSelected({ seqObject: seqObj }));
    }
  };

  /*
   Returns a checkbox with a tooltip for the passed in sequencing object
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
          key={`concatenation-checkbox-tooltip-${obj.identifier}`}
        >
          <Checkbox
            key={`concatenation-checkbox-${obj.identifier}`}
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
   Get the actions required for a Paired End -> Forward sequence file, single end sequence file, and/or fast5 object
   */
  const getActionsForSequencingObject = (seqObj, index = -1) => {
    let actions = [];

    const obj = seqObj.fileInfo
      ? seqObj.fileInfo
      : seqObj.file
      ? seqObj.file
      : seqObj;

    actions.push(
      getProcessingStateTag(obj),
      <span key={`file1-size-${obj.identifier}`} className="t-file-size">
        {seqObj.firstFileSize}
      </span>
    );

    if (isModifiable && obj.files && obj.files.length === 2) {
      if (
        (sample.defaultSequencingObject !== null &&
          obj.identifier === sample.defaultSequencingObject.identifier) ||
        (sample.defaultSequencingObject === null && index === 0)
      ) {
        actions.push(
          <Tooltip
            title={i18n("SampleFilesList.defaultSelected")}
            placement="top"
            key={`default-tag-tooltip-${obj.identifier}`}
          >
            <Tag
              color="#108ee9"
              key={`default-tag-${obj.identifier}`}
              className="t-default-seq-obj-tag"
            >
              {i18n("SampleFilesList.default")}
            </Tag>
          </Tooltip>
        );
      } else {
        actions.push(
          <Tooltip
            title={i18n("SampleFilesList.tooltip.setAsDefault")}
            placement="top"
            key={`set-default-tooltip-${obj.identifier}`}
          >
            <Button
              size="small"
              key={`set-default-${obj.identifier}`}
              onClick={() => updateDefaultSequencingObject(obj)}
              type="link"
              className="t-set-default-seq-obj-button"
              style={{ width: 100 }}
            >
              {i18n("SampleFilesList.setAsDefault")}
            </Button>
          </Tooltip>
        );
      }
    }

    actions.push(
      <Button
        type="link"
        key={`download-file1-${obj.identifier}`}
        style={{
          padding: 0,
          width: DEFAULT_ACTION_WIDTH,
          marginRight: ACTION_MARGIN_RIGHT,
        }}
        className="t-download-file-btn"
        onClick={() => {
          downloadSequenceFile({
            sequencingObjectId: obj.identifier,
            sequenceFileId: obj.files?.length
              ? obj.files[0].identifier
              : obj.sequenceFile
              ? obj.sequenceFile.identifier
              : obj.file.identifier,
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
          key={`remove-seqobj-confirm-${obj.identifier}`}
          title={i18n("SampleFilesList.removeSequencingObject")}
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
            title={i18n("SampleFilesList.tooltip.remove")}
            placement="top"
            key={`remove-seqobj-tooltip-${obj.identifier}`}
          >
            <Button
              type="link"
              key={`remove-seqobj-${obj.identifier}`}
              className="t-remove-file-btn"
              style={{ padding: 0, width: DEFAULT_ACTION_WIDTH }}
            >
              {i18n("SampleFilesList.remove")}
            </Button>
          </Tooltip>
        </Popconfirm>
      );
    }

    return actions;
  };

  /*
   Get the actions required for a Paired End -> Reverse sequence file
   */
  const getActionsForSequencingObjectPairedReverse = (seqObj) => {
    let actions = [];
    const { fileInfo: obj } = seqObj;

    actions.push(
      getProcessingStateTag(obj, "paired"),
      <span className="t-file-size" key={`file2-size-${obj.identifier}`}>
        {seqObj.secondFileSize}
      </span>,
      <Button
        type="link"
        key={`download-file2-${obj.identifier}`}
        style={{
          padding: 0,
          width: DEFAULT_ACTION_WIDTH,
          marginRight: 5,
        }}
        className="t-download-file-btn"
        onClick={() => {
          downloadSequenceFile({
            sequencingObjectId: obj.identifier,
            sequenceFileId: obj.files[1].identifier,
          });
        }}
      >
        {i18n("SampleFilesList.download")}
      </Button>
    );
    return actions;
  };

  /*
   Gets the processing state as a tag (icon) with a tooltip
  */
  const getProcessingStateTag = (obj, type = "single") => {
    let tagColor = "default";
    let icon = <ClockCircleOutlined />;

    let key =
      type === "single"
        ? `file-processing-status-file1-${obj.identifier}`
        : `file-processing-status-file2-${obj.identifier}`;

    if (obj.processingState === "FINISHED") {
      tagColor = "success";
      icon = <CheckCircleOutlined />;
    } else if (obj.processingState === "ERROR") {
      tagColor = "error";
      icon = <CloseOutlined />;
    } else if (obj.processingState === "PROCESSING") {
      tagColor = "processing";
      icon = <SyncOutlined spin />;
    }
    return (
      <Tooltip
        placement="top"
        key={key}
        title={fileProcessTranslations[obj.processingState]}
      >
        <Tag color={tagColor} key={key} className="t-file-processing-status">
          {icon}
        </Tag>
      </Tooltip>
    );
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
  Download sequence files (paired, single, fast5)
   */
  const downloadSequenceFile = ({ sequencingObjectId, sequenceFileId }) => {
    notification.success({
      message: i18n("SampleFiles.startingSequenceFileDownload"),
    });
    downloadSequencingObjectFile({ sequencingObjectId, sequenceFileId });
  };

  return (
    <Space size="large" direction="vertical" style={{ width: `100%` }}>
      {files.singles && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.singles")}>
          {files.singles.map((sequenceObject) => (
            <SequenceObjectListItem
              key={`single-${sequenceObject.fileInfo.identifier}`}
              sequenceObject={sequenceObject}
              actions={getActionsForSequencingObject(sequenceObject)}
              displayConcatenationCheckbox={
                isModifiable && files.singles?.length >= 2
                  ? getConcatenationCheckboxForSequencingObject(sequenceObject)
                  : null
              }
              displayFileProcessingStatus={true}
            />
          ))}
        </SequenceFileTypeRenderer>
      )}
      {files.paired && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.paired")}>
          {files.paired.map((pair, index) => (
            <SequenceObjectListItem
              key={`pair-${pair.fileInfo.identifier}`}
              sequenceObject={pair}
              actions={getActionsForSequencingObject(pair, index)}
              pairedReverseActions={getActionsForSequencingObjectPairedReverse(
                pair
              )}
              displayConcatenationCheckbox={
                isModifiable && files.paired?.length >= 2
                  ? getConcatenationCheckboxForSequencingObject(pair)
                  : null
              }
              displayFileProcessingStatus={true}
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
              displayFileProcessingStatus={true}
            />
          ))}
        </SequenceFileTypeRenderer>
      )}
    </Space>
  );
}
