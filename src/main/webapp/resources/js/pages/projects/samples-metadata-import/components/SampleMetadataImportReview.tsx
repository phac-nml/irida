import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  Alert,
  Button,
  Input,
  List,
  notification,
  Popover,
  Progress,
  Table,
  TableProps,
  Tag,
  Tooltip,
  Typography,
} from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import {
  IconArrowLeft,
  IconArrowRight,
  IconExclamationCircle,
} from "../../../../components/icons/Icons";
import styled from "styled-components";
import {
  MetadataValidateDetailsItem,
  saveMetadata,
  setMetadataItem,
} from "../redux/importReducer";
import { getPaginationOptions } from "../../../../utilities/antdesign-table-utilities";
import { NavigateFunction } from "react-router/dist/lib/hooks";
import {
  ImportDispatch,
  ImportState,
  useImportDispatch,
  useImportSelector,
} from "../redux/store";
import { MetadataItem } from "../../../../apis/projects/samples";
import { ColumnsType, ColumnType } from "antd/es/table";
import { TableRowSelection } from "antd/lib/table/interface";
import { ErrorAlert } from "../../../../components/alerts/ErrorAlert";

const { Paragraph, Text } = Typography;

const MetadataTable = styled(Table)`
  tr.row-error > td {
    background-color: var(--red-1);
  }
  tr.row-error:hover > td {
    background-color: var(--red-2);
  }
  tr.row-error > td.ant-table-cell-fix-left {
    background-color: var(--red-1);
  }
  tr.row-error:hover > td.ant-table-cell-fix-left {
    background-color: var(--red-2);
  }
  tr.row-error > td.ant-table-cell-fix-right {
    background-color: var(--red-1);
  }
  tr.row-error:hover > td.ant-table-cell-fix-right {
    background-color: var(--red-2);
  }
`;

/**
 * React component that displays Step #3 of the Sample Metadata Uploader.
 * This page is where the user reviews the metadata to be uploaded.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportReview(): JSX.Element {
  const { projectId } = useParams<{ projectId: string }>();
  const navigate: NavigateFunction = useNavigate();
  const [columns, setColumns] = React.useState<ColumnsType<MetadataItem>>([]);
  const [selected, setSelected] = React.useState<React.Key[]>([]);
  const [progress, setProgress] = React.useState<number>(0);
  const [loading, setLoading] = React.useState<boolean>(false);
  const [editingRowKey, setEditingRowKey] = React.useState<string | undefined>(
    undefined
  );
  const {
    headers,
    sampleNameColumn,
    metadata: stateMetadata,
    metadataValidateDetails: stateMetadataValidateDetails,
    metadataSaveDetails,
    percentComplete,
  } = useImportSelector((state: ImportState) => state.importReducer);
  const dispatch: ImportDispatch = useImportDispatch();
  const [metadata, setMetadata] = React.useState<MetadataItem[]>(stateMetadata);
  const [metadataValidateDetails, setMetadataValidateDetails] = React.useState<
    Record<string, MetadataValidateDetailsItem>
  >(stateMetadataValidateDetails);

  const rowSelection: TableRowSelection<MetadataItem> = {
    fixed: true,
    selectedRowKeys: selected,
    onChange: (selectedRowKeys) => {
      setSelected(selectedRowKeys);
    },
    getCheckboxProps: (record: MetadataItem) => ({
      disabled: !(
        metadataValidateDetails[record[sampleNameColumn]].isSampleNameValid ||
        metadataSaveDetails[record[sampleNameColumn]]?.saved === true
      ),
    }),
  };

  React.useEffect(() => {
    setProgress(percentComplete);
  }, [percentComplete]);

  React.useEffect(() => {
    setMetadata(stateMetadata);
  }, [stateMetadata]);

  React.useEffect(() => {
    setMetadataValidateDetails(stateMetadataValidateDetails);
  }, [stateMetadata, stateMetadataValidateDetails]);

  React.useEffect(() => {
    const sampleColumn: ColumnType<MetadataItem> = {
      title: sampleNameColumn,
      dataIndex: sampleNameColumn,
      fixed: "left",
      width: 100,
      onCell: (item) => {
        return {
          style: {
            background: metadataValidateDetails[item[sampleNameColumn]]
              ?.isSampleNameValid
              ? undefined
              : `var(--red-1)`,
          },
          onClick: () => {
            setEditingRowKey(item.rowKey);
          },
        };
      },
      render: (text, item) => {
        const valid =
          metadataValidateDetails[item[sampleNameColumn]]?.isSampleNameValid;
        if (editingRowKey === item.rowKey && !valid) {
          return (
            <Input
              defaultValue={item[sampleNameColumn]}
              onPressEnter={async (e) => {
                console.log("onPressEnter");
                console.log(e.target.value);
                const updatedItem = { ...item };
                updatedItem[sampleNameColumn] = e.target.value;
                dispatch(setMetadataItem({ metadataItem: updatedItem }));
                setEditingRowKey(undefined);
              }}
              onBlur={(e) => {
                console.log("onBLur");
                console.log(e.target.value);
                setEditingRowKey(undefined);
              }}
            />
          );
        } else {
          return <div>{text}</div>;
        }
      },
    };

    const savedColumn: ColumnType<MetadataItem> = {
      dataIndex: "saved",
      fixed: "left",
      width: 10,
      render: (text, item) => {
        if (metadataSaveDetails[item[sampleNameColumn]]?.saved === false)
          return (
            <Tooltip
              title={metadataSaveDetails[item[sampleNameColumn]]?.error}
              color={`var(--red-5)`}
            >
              <IconExclamationCircle style={{ color: `var(--red-5)` }} />
            </Tooltip>
          );
        return text;
      },
    };

    const tagColumn: ColumnType<MetadataItem> = {
      title: "",
      dataIndex: "tags",
      className: "t-metadata-uploader-new-column",
      fixed: "left",
      width: 70,
      render: (text, item) => {
        if (!metadataValidateDetails[item[sampleNameColumn]]?.foundSampleId)
          return (
            <Tag color="green">
              {i18n("SampleMetadataImportReview.table.filter.new")}
            </Tag>
          );
        return text;
      },
      filters: [
        {
          text: i18n("SampleMetadataImportReview.table.filter.new"),
          value: "new",
        },
        {
          text: i18n("SampleMetadataImportReview.table.filter.existing"),
          value: "existing",
        },
      ],
      onFilter: (value, record) =>
        value === "new"
          ? metadataValidateDetails[record[sampleNameColumn]].foundSampleId !==
            undefined
          : metadataValidateDetails[record[sampleNameColumn]].foundSampleId ===
            undefined,
    };

    const otherColumns: ColumnsType<MetadataItem> = headers
      .filter((header) => header.name !== sampleNameColumn)
      .map((header) => ({
        title: header.name,
        dataIndex: header.name,
      }));

    const updatedColumns: ColumnsType<MetadataItem> = [
      savedColumn,
      sampleColumn,
      tagColumn,
      ...otherColumns,
    ];

    setColumns(updatedColumns);
    setSelected(
      metadata
        .filter(
          (row) =>
            metadataValidateDetails[row[sampleNameColumn]].isSampleNameValid ||
            metadataSaveDetails[row[sampleNameColumn]]?.saved === true
        )
        .map((row): string => row.rowKey)
    );
  }, [
    dispatch,
    editingRowKey,
    headers,
    metadata,
    metadataSaveDetails,
    metadataValidateDetails,
    sampleNameColumn,
  ]);

  const save = async () => {
    setLoading(true);
    const selectedMetadataKeys = metadata
      .filter((metadataItem) => selected.includes(metadataItem.rowKey))
      .map((metadataItem) => metadataItem.rowKey);

    if (projectId) {
      await dispatch(saveMetadata({ projectId, selectedMetadataKeys }))
        .unwrap()
        .then(({ metadataSaveDetails }) => {
          const errorCount = Object.entries(metadataSaveDetails).filter(
            ([, metadataSaveDetailsItem]) => metadataSaveDetailsItem.error
          ).length;
          if (errorCount === 0) {
            navigate(`/${projectId}/sample-metadata/upload/complete`);
          } else {
            setLoading(false);
            notification.error({
              message: i18n(
                "SampleMetadataImportReview.notification.partialError",
                errorCount
              ),
            });
          }
        })
        .catch((payload) => {
          setLoading(false);
          notification.error({
            message: payload,
            className: "t-metadata-uploader-review-error",
          });
        });
    }
  };

  const isValid = !metadata.some(
    (row) => !metadataValidateDetails[row[sampleNameColumn]].isSampleNameValid
  );

  const lockedSampleMetadata = metadata.filter(
    (metadataItem) =>
      metadataValidateDetails[metadataItem[sampleNameColumn]].locked
  );

  return (
    <SampleMetadataImportWizard current={2}>
      <Text>{i18n("SampleMetadataImportReview.description")}</Text>
      {!isValid && (
        <ErrorAlert
          message={i18n("SampleMetadataImportReview.alert.valid.title")}
          description={
            <Paragraph>
              {i18n("SampleMetadataImportReview.alert.valid.description")}
              <ul>
                <li>{i18n("SampleMetadataImportReview.alert.valid.rule1")}</li>
                <li>{i18n("SampleMetadataImportReview.alert.valid.rule2")}</li>
                <li>{i18n("SampleMetadataImportReview.alert.valid.rule3")}</li>
              </ul>
            </Paragraph>
          }
        />
      )}
      {lockedSampleMetadata.length > 0 && (
        <Alert
          closable={true}
          message={
            <>
              <Popover
                placement="bottom"
                content={
                  <div style={{ overflowY: "auto", maxHeight: "200px" }}>
                    <List
                      size="small"
                      dataSource={lockedSampleMetadata}
                      renderItem={(metadataItem) => (
                        <List.Item>{metadataItem[sampleNameColumn]}</List.Item>
                      )}
                    />
                  </div>
                }
              >
                <Text underline>
                  {i18n(
                    "SampleMetadataImportReview.alert.locked.description.popover.content",
                    lockedSampleMetadata.length
                  )}
                </Text>
              </Popover>
              {i18n("SampleMetadataImportReview.alert.locked.description")}
            </>
          }
          type="warning"
          showIcon
        />
      )}
      <MetadataTable<(props: TableProps<MetadataItem>) => JSX.Element>
        className="t-metadata-uploader-review-table"
        rowKey={(row) => row.rowKey}
        rowClassName={(record) =>
          metadataSaveDetails[record[sampleNameColumn]]?.saved === false
            ? "row-error"
            : ""
        }
        rowSelection={rowSelection}
        columns={columns}
        dataSource={metadata.filter(
          (metadataItem) =>
            !metadataValidateDetails[metadataItem[sampleNameColumn]].locked
        )}
        pagination={getPaginationOptions(metadata.length)}
        loading={{
          indicator: <Progress type="circle" percent={progress} />,
          spinning: loading,
          tip: (
            <Text
              style={{
                display: "inline-block",
                height: 100,
                transform: "translateY(100%)",
              }}
            >
              {i18n("SampleMetadataImportReview.loading")}
            </Text>
          ),
        }}
      />
      <div style={{ display: "flex" }}>
        <Button
          className="t-metadata-uploader-column-button"
          icon={<IconArrowLeft />}
          onClick={() => navigate(-1)}
          disabled={loading}
        >
          {i18n("SampleMetadataImportReview.button.back")}
        </Button>
        <Button
          className="t-metadata-uploader-upload-button"
          style={{ marginLeft: "auto" }}
          onClick={save}
          disabled={loading}
        >
          {i18n("SampleMetadataImportReview.button.next")}
          <IconArrowRight />
        </Button>
      </div>
    </SampleMetadataImportWizard>
  );
}
