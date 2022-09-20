import React from "react";
import { Button, Menu, notification, Popconfirm, Tag, Tooltip } from "antd";
import { useAppDispatch, useAppSelector } from "../../../hooks/useState";
import { SequenceFileTypeRenderer } from "./SequenceFileTypeRenderer";
import {
  downloadGenomeAssemblyFile,
  SampleGenomeAssembly,
  useUpdateDefaultSampleGenomeAssemblyMutation,
} from "../../../apis/samples/samples";
import { GenomeAssemblyListItem } from "../../sequence-files/GenomeAssemblyListItem";
import { DEFAULT_ACTION_WIDTH } from "../sampleFilesSlice";
import { setDefaultGenomeAssembly } from "../sampleSlice";
import { EllipsisMenu } from "../../menu/EllipsisMenu";

export interface GenomeAssemblyListProps {
  removeSampleFiles: ({
    fileObjectId,
    type,
  }: {
    fileObjectId: number;
    type: string;
  }) => void;
}

/**
 * React component to display, remove, download genome assemblies
 * @param {function} removeSampleFiles The function to remove genome assemblies
 * @returns {JSX.Element}
 * @constructor
 */
export function GenomeAssemblyList({
  removeSampleFiles = () => {
    /* function to remove sample genome assemblies */
  },
}: GenomeAssemblyListProps): JSX.Element {
  const [updateSampleDefaultGenomeAssembly] =
    useUpdateDefaultSampleGenomeAssemblyMutation();
  const { sample, modifiable: isModifiable } = useAppSelector(
    (state) => state.sampleReducer
  );
  const { files } = useAppSelector((state) => state.sampleFilesReducer);
  const ACTION_MARGIN_RIGHT = isModifiable ? 0 : 5;
  const dispatch = useAppDispatch();

  /*
   Download genome assembly files
   */
  const downloadAssemblyFile = ({
    sampleId,
    genomeAssemblyId,
  }: {
    sampleId: number;
    genomeAssemblyId: number;
  }) => {
    notification.success({
      message: i18n("SampleFiles.startingAssemblyDownload"),
    });
    downloadGenomeAssemblyFile({ sampleId, genomeAssemblyId });
  };

  /*
  Set default genome assembly for sample to be used for analyses
   */
  const updateDefaultGenomeAssembly = (
    genomeAssembly: SampleGenomeAssembly
  ) => {
    const { fileInfo: genomeAssemblyObj }: SampleGenomeAssembly =
      genomeAssembly;
    updateSampleDefaultGenomeAssembly({
      sampleId: sample.identifier,
      genomeAssemblyId: genomeAssemblyObj.identifier,
    })
      .unwrap()
      .then(({ message }: { message: string }) => {
        dispatch(setDefaultGenomeAssembly(genomeAssemblyObj));
        notification.success({ message });
      })
      .catch((error) => {
        notification.error({ message: error });
      });
  };

  /*
   Get the actions required for a Genome Assembly
   */
  const getActionsForGenomeAssembly = (
    genomeAssembly: SampleGenomeAssembly,
    index: number
  ) => {
    const { fileInfo: genomeAssemblyObj } = genomeAssembly;
    const actions: React.ReactElement[] = [];

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
              color={`var(--blue-6)`}
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
      </span>
    );

    const menu = (
      <Menu>
        <Menu.Item
          key={`menu-item-download-genome-assembly-${genomeAssemblyObj.identifier}`}
          onClick={() => {
            downloadAssemblyFile({
              sampleId: sample.identifier,
              genomeAssemblyId: genomeAssemblyObj.identifier,
            });
          }}
        >
          <Button
            type="link"
            key={`${genomeAssemblyObj.identifier}-download-btn`}
            style={{
              padding: 0,
              width: DEFAULT_ACTION_WIDTH,
              marginRight: ACTION_MARGIN_RIGHT,
            }}
            className="t-download-file-btn"
          >
            {i18n("SampleFilesList.download")}
          </Button>
        </Menu.Item>

        {isModifiable && (
          <Menu.Item
            key={`menu-item-remove-genome-assembly-${genomeAssemblyObj.identifier}`}
          >
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
                onClick={(e) => e?.stopPropagation()}
              >
                {i18n("SampleFilesList.remove")}
              </Button>
            </Popconfirm>
          </Menu.Item>
        )}
      </Menu>
    );

    actions.push(<EllipsisMenu overlay={menu} />);

    return actions;
  };

  return (
    <SequenceFileTypeRenderer title={i18n("SampleFiles.assemblies")}>
      {files.assemblies?.map(
        (assembly: SampleGenomeAssembly, index: number) => (
          <GenomeAssemblyListItem
            key={`assembly-${assembly.fileInfo.identifier}`}
            genomeAssembly={assembly}
            actions={getActionsForGenomeAssembly(assembly, index)}
          />
        )
      )}
    </SequenceFileTypeRenderer>
  );
}
