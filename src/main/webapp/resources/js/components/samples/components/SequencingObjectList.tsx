import React from "react";
import {
  Button,
  Checkbox,
  Menu,
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
import { useAppDispatch, useAppSelector } from "../../../hooks/useState";
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
  SampleSequencingObject,
  SequencingObject,
  useUpdateDefaultSampleSequencingObjectMutation,
} from "../../../apis/samples/samples";
import { CheckboxChangeEvent } from "antd/lib/checkbox";
import { EllipsisMenu } from "../../menu/EllipsisMenu";

const fileProcessTranslations: { [key: string]: string } = {
  UNPROCESSED: i18n("SampleFilesList.fileProcessingState.UNPROCESSED"),
  QUEUED: i18n("SampleFilesList.fileProcessingState.QUEUED"),
  PROCESSING: i18n("SampleFilesList.fileProcessingState.PROCESSING"),
  FINISHED: i18n("SampleFilesList.fileProcessingState.FINISHED"),
  ERROR: i18n("SampleFilesList.fileProcessingState.ERROR"),
};

export interface SequencingObjectListProps {
  removeSampleFiles: ({
    fileObjectId,
    type,
  }: {
    fileObjectId: number;
    type: string;
  }) => void;
}

/**
 * React component to display, remove, download sequencing objects
 * @param {function} removeSampleFiles The function to remove sequencing objects
 * @returns {JSX.Element}
 * @constructor
 */
export function SequencingObjectList({
  removeSampleFiles = () => {
    /*Function to remove sample sequencing objects*/
  },
}: SequencingObjectListProps): JSX.Element {
  const [updateSampleDefaultSequencingObject] =
    useUpdateDefaultSampleSequencingObjectMutation();

  const {
    sample,
    modifiable: isModifiable,
    projectId,
  } = useAppSelector((state) => state.sampleReducer);
  const { files, concatenateSelected } = useAppSelector(
    (state) => state.sampleFilesReducer
  );
  const ACTION_MARGIN_RIGHT = isModifiable ? 0 : 5;

  const dispatch = useAppDispatch();

  /*
   Get updated processing states for the sequencing objects
   which were still getting processed
   */
  useInterval(() => {
    const seqObjIdsSingles = files.singles
      ?.filter((singleEndFile: SampleSequencingObject) => {
        return (
          singleEndFile.fileInfo.processingState !== "FINISHED" &&
          singleEndFile.fileInfo.processingState !== "ERROR"
        );
      })
      .map(
        (singleEndFile: SampleSequencingObject) =>
          singleEndFile.fileInfo.identifier
      );

    const seqObjIdsPaired = files.paired
      ?.filter((pair: SampleSequencingObject) => {
        return (
          (pair.fileInfo.processingState !== "FINISHED" &&
            pair.fileInfo.processingState !== "ERROR") ||
          (pair.automatedAssembly !== null &&
            pair.automatedAssembly.analysisState !== "COMPLETED" &&
            pair.automatedAssembly.analysisState !== "ERROR")
        );
      })
      .map((pair: SampleSequencingObject) => pair.fileInfo.identifier);

    const seqObjIdsFast5 = files.fast5
      ?.filter((fast5File: SampleSequencingObject) => {
        return (
          fast5File.fileInfo.processingState !== "FINISHED" &&
          fast5File.fileInfo.processingState !== "ERROR"
        );
      })
      .map(
        (fast5File: SampleSequencingObject) => fast5File.fileInfo.identifier
      );

    let sequencingObjectIds: number[] = [];

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
  const updateSelected = (
    e: CheckboxChangeEvent,
    seqObj: SequencingObject
  ): void => {
    if (e.target.checked) {
      dispatch(addToConcatenateSelected({ seqObject: seqObj }));
    } else {
      dispatch(removeFromConcatenateSelected({ seqObject: seqObj }));
    }
  };

  /*
   Returns a checkbox with a tooltip for the passed in sequencing object
   */
  const getConcatenationCheckboxForSequencingObject = (
    seqObj: SampleSequencingObject
  ) => {
    const { fileInfo: obj }: SampleSequencingObject = seqObj;

    return (
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
            concatenateSelected.filter(
              (e: SequencingObject) => e.identifier === obj.identifier
            ).length > 0
          }
        />
      </Tooltip>
    );
  };

  /*
  Check if the sequencing object should be automatically set as default
   */
  const checkSeqObjectAutoDefault = (type: string, index: number): boolean => {
    if (sample.defaultSequencingObject !== null || index !== 0) return false;
    else
      return (
        type === "pair" ||
        (files.paired === undefined && type === "single") ||
        (files.paired === undefined &&
          files.singles === undefined &&
          type === "fast5")
      );
  };

  /*
   Get the actions required for a Paired End -> Forward sequence file, single end sequence file, and/or fast5 object
   */
  const getActionsForSequencingObject = (
    seqObj: SampleSequencingObject,
    type: string,
    index = -1
  ) => {
    const actions: React.ReactElement[] = [];

    const { fileInfo: obj }: SampleSequencingObject = seqObj;

    if (isModifiable) {
      if (
        (sample.defaultSequencingObject !== null &&
          obj.identifier === sample.defaultSequencingObject.identifier) ||
        checkSeqObjectAutoDefault(type, index)
      ) {
        actions.push(
          <Tooltip
            title={i18n("SampleFilesList.defaultSelected")}
            placement="top"
            key={`default-tag-tooltip-${obj.identifier}`}
          >
            <Tag
              color={`var(--blue-6)`}
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
      getProcessingStateTag(obj),
      <span key={`file1-size-${obj.identifier}`} className="t-file-size">
        {seqObj.firstFileSize}
      </span>
    );

    let seqFileId = -1;

    if (obj.files?.length) {
      seqFileId = parseInt(obj.files[0].identifier);
    } else if (obj.sequenceFile) {
      seqFileId = parseInt(obj.sequenceFile.identifier);
    } else {
      seqFileId = parseInt(obj.file.identifier);
    }

    const menu = (
      <Menu>
        <Menu.Item
          key={`menu-item-download-file1-${obj.identifier}`}
          onClick={() =>
            downloadSequenceFile({
              sequencingObjectId: obj.identifier,
              sequenceFileId: seqFileId,
            })
          }
        >
          <Button
            type="link"
            key={`download-file1-${obj.identifier}`}
            className="t-download-file-btn"
            style={{ padding: 0, width: DEFAULT_ACTION_WIDTH }}
          >
            {i18n("SampleFilesList.download")}
          </Button>
        </Menu.Item>

        {isModifiable && (
          <Menu.Item key={`menu-item-remove-seqobj-${obj.identifier}`}>
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
                  onClick={(e) => e?.stopPropagation()}
                >
                  {i18n("SampleFilesList.remove")}
                </Button>
              </Tooltip>
            </Popconfirm>
          </Menu.Item>
        )}
      </Menu>
    );

    actions.push(<EllipsisMenu menu={menu} />);

    return actions;
  };

  /*
   Get the actions required for a Paired End -> Reverse sequence file
   */
  const getActionsForSequencingObjectPairedReverse = (
    seqObj: SampleSequencingObject
  ) => {
    const actions: React.ReactElement[] = [];
    const { fileInfo: obj }: SampleSequencingObject = seqObj;

    actions.push(
      getProcessingStateTag(obj, "paired"),
      <span className="t-file-size" key={`file2-size-${obj.identifier}`}>
        {seqObj.secondFileSize}
      </span>
    );

    const menu = (
      <Menu>
        <Menu.Item
          key={`menu-item-download-file2-${obj.identifier}`}
          onClick={() => {
            downloadSequenceFile({
              sequencingObjectId: obj.identifier,
              sequenceFileId: parseInt(obj.files[1].identifier),
            });
          }}
        >
          <Button
            type="link"
            key={`download-file2-${obj.identifier}`}
            style={{
              padding: 0,
              width: DEFAULT_ACTION_WIDTH,
              marginRight: 5,
            }}
            className="t-download-file-btn"
          >
            {i18n("SampleFilesList.download")}
          </Button>
        </Menu.Item>
      </Menu>
    );

    actions.push(<EllipsisMenu menu={menu} />);

    return actions;
  };

  /*
   Gets the processing state as a tag (icon) with a tooltip
  */
  const getProcessingStateTag = (obj: SequencingObject, type = "single") => {
    let tagColor = "default";
    let icon = <ClockCircleOutlined />;

    const key =
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
  const updateDefaultSequencingObject = (
    sequencingObject: SequencingObject
  ) => {
    updateSampleDefaultSequencingObject({
      sampleId: sample.identifier,
      sequencingObjectId: sequencingObject.identifier,
    })
      .unwrap()
      .then(({ message }: { message: string }) => {
        dispatch(setDefaultSequencingObject(sequencingObject));
        notification.success({ message });
      })
      .catch((error) => {
        notification.error({ message: error });
      });
  };

  /*
  Download sequence files (paired, single, fast5)
   */
  const downloadSequenceFile = ({
    sequencingObjectId,
    sequenceFileId,
  }: {
    sequencingObjectId: number;
    sequenceFileId: number;
  }) => {
    notification.success({
      message: i18n("SampleFiles.startingSequenceFileDownload"),
    });
    downloadSequencingObjectFile({ sequencingObjectId, sequenceFileId });
  };

  return (
    <Space size="large" direction="vertical" style={{ width: `100%` }}>
      {files.singles && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.singles")}>
          {files.singles.map(
            (sequenceObject: SampleSequencingObject, index: number) => (
              <SequenceObjectListItem
                key={`single-${sequenceObject.fileInfo.identifier}`}
                sequenceObject={sequenceObject}
                actions={getActionsForSequencingObject(
                  sequenceObject,
                  "single",
                  index
                )}
                displayConcatenationCheckbox={
                  isModifiable &&
                  files.singles !== undefined &&
                  files.singles?.length >= 2
                    ? getConcatenationCheckboxForSequencingObject(
                        sequenceObject
                      )
                    : null
                }
                pairedReverseActions={[]}
              />
            )
          )}
        </SequenceFileTypeRenderer>
      )}
      {files.paired && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.paired")}>
          {files.paired.map((pair: SampleSequencingObject, index: number) => (
            <SequenceObjectListItem
              key={`pair-${pair.fileInfo.identifier}`}
              sequenceObject={pair}
              actions={getActionsForSequencingObject(pair, "pair", index)}
              pairedReverseActions={getActionsForSequencingObjectPairedReverse(
                pair
              )}
              displayConcatenationCheckbox={
                isModifiable &&
                files.paired !== undefined &&
                files.paired.length >= 2
                  ? getConcatenationCheckboxForSequencingObject(pair)
                  : null
              }
            />
          ))}
        </SequenceFileTypeRenderer>
      )}
      {files.fast5 && (
        <SequenceFileTypeRenderer title={i18n("SampleFiles.fast5")}>
          {files.fast5.map(
            (fast5Obj: SampleSequencingObject, index: number) => (
              <SequenceObjectListItem
                key={`fast5-${fast5Obj.fileInfo.identifier}`}
                sequenceObject={fast5Obj}
                actions={getActionsForSequencingObject(
                  fast5Obj,
                  "fast5",
                  index
                )}
                displayConcatenationCheckbox={null}
                pairedReverseActions={[]}
              />
            )
          )}
        </SequenceFileTypeRenderer>
      )}
    </Space>
  );
}
