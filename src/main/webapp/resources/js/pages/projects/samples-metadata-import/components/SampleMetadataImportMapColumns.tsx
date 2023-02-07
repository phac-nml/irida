import React, { useMemo } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  Button,
  Empty,
  Radio,
  Select,
  Space,
  Table,
  Tag,
  Typography,
} from "antd";
import {
  IconArrowLeft,
  IconArrowRight,
} from "../../../../components/icons/Icons";
import {
  MetadataHeaderItem,
  setSampleNameColumn,
  updateHeaders,
  updateStep,
} from "../redux/importReducer";
import { NavigateFunction } from "react-router/dist/lib/hooks";
import {
  ImportDispatch,
  ImportState,
  useImportDispatch,
  useImportSelector,
} from "../redux/store";
import { getMetadataRestrictions } from "../../../../apis/metadata/field";
import {
  getColourForRestriction,
  getRestrictionLabel,
  Restriction,
  RestrictionListItem,
} from "../../../../utilities/restriction-utilities";

/**
 * React component that displays Step #2 of the Sample Metadata Uploader.
 * This page is where the user selects the sample name column.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportMapColumns(): JSX.Element {
  const { projectId } = useParams<{ projectId: string }>();
  const navigate: NavigateFunction = useNavigate();
  const [loading, setLoading] = React.useState<boolean>(false);
  const [restrictions, setRestrictions] = React.useState<RestrictionListItem[]>(
    []
  );
  const { headers, sampleNameColumn } = useImportSelector(
    (state: ImportState) => state.importReducer
  );
  const [updatedSampleNameColumn, setUpdatedSampleNameColumn] =
    React.useState<string>(sampleNameColumn);
  const dispatch: ImportDispatch = useImportDispatch();

  const updatedHeaders = useMemo(() => [...headers], [headers]);

  React.useEffect(() => {
    dispatch(updateStep(1, "process"));
    getMetadataRestrictions().then((data) => {
      setRestrictions(data);
    });
  }, [dispatch]);

  const onSubmit = async () => {
    if (projectId && updatedSampleNameColumn) {
      setLoading(true);
      await dispatch(
        setSampleNameColumn({ projectId, updatedSampleNameColumn })
      );
      await dispatch(updateHeaders(updatedHeaders));
      navigate(`/${projectId}/sample-metadata/upload/review`);
    }
  };

  const onSampleNameColumnChange = (value: string) => {
    setUpdatedSampleNameColumn(value);
  };

  const onRestrictionChange = (
    item: MetadataHeaderItem,
    value: Restriction
  ) => {
    const index = updatedHeaders.findIndex(
      (header) => header.rowKey === item.rowKey
    );
    if (index !== -1) {
      const updatedHeadersItem = { ...updatedHeaders[index] };
      updatedHeadersItem.targetRestriction = value;
      updatedHeaders[index] = updatedHeadersItem;
    }
  };

  const dataSource = useMemo(
    () =>
      updatedSampleNameColumn
        ? updatedHeaders.filter(
            (updatedHeader) => updatedHeader.name !== updatedSampleNameColumn
          )
        : undefined,
    [updatedSampleNameColumn, updatedHeaders]
  );

  const columns = [
    {
      title: i18n("SampleMetadataImportMapColumns.table.header"),
      dataIndex: "name",
    },
    {
      title: i18n("SampleMetadataImportMapColumns.table.existingRestriction"),
      dataIndex: "existingRestriction",
      render(id: number, item: MetadataHeaderItem) {
        if (item.existingRestriction) {
          return (
            <Tag color={getColourForRestriction(item.existingRestriction)}>
              {getRestrictionLabel(restrictions, item.existingRestriction)}
            </Tag>
          );
        } else {
          return undefined;
        }
      },
    },
    {
      title: i18n("SampleMetadataImportMapColumns.table.targetRestriction"),
      dataIndex: "restriction",
      render(id: number, item: MetadataHeaderItem) {
        return (
          <Radio.Group
            options={restrictions}
            defaultValue={item.targetRestriction}
            disabled={item.name === updatedSampleNameColumn}
            onChange={({ target: { value } }) =>
              onRestrictionChange({ ...item }, value)
            }
            optionType="button"
          />
        );
      },
    },
  ];

  return (
    <Space size="large" direction="vertical" style={{ width: "100%" }}>
      <Space size="small" direction="vertical" style={{ width: "100%" }}>
        <Typography.Text strong>
          {i18n("SampleMetadataImportMapColumns.form.sampleNameColumn")}
        </Typography.Text>
        <Select
          autoFocus
          style={{ width: 300 }}
          value={updatedSampleNameColumn}
          onChange={onSampleNameColumnChange}
          className="t-metadata-uploader-sample-name-column-select"
        >
          {headers.map((header) => (
            <Select.Option key={header.name} value={header.name}>
              {header.name}
            </Select.Option>
          ))}
        </Select>
      </Space>
      <Space size="small" direction="vertical" style={{ width: "100%" }}>
        <Typography.Text strong>
          {i18n("SampleMetadataImportMapColumns.form.metadataColumns")}
        </Typography.Text>
        <Table
          className="t-metadata-uploader-columns-table"
          rowKey={(row) => row.rowKey}
          columns={columns}
          dataSource={dataSource}
          pagination={false}
          scroll={{ y: 600 }}
          locale={{
            emptyText: (
              <Empty
                image={Empty.PRESENTED_IMAGE_SIMPLE}
                description={i18n("SampleMetadataImportMapColumns.table.empty")}
              />
            ),
          }}
        />
      </Space>
      <div style={{ display: "flex" }}>
        <Button
          className="t-metadata-uploader-file-button"
          icon={<IconArrowLeft />}
          onClick={() => navigate(-1)}
        >
          {i18n("SampleMetadataImportMapColumns.button.back")}
        </Button>
        <Button
          className="t-metadata-uploader-preview-button"
          onClick={onSubmit}
          style={{ marginLeft: "auto" }}
          loading={loading}
        >
          {i18n("SampleMetadataImportMapColumns.button.next")}
          <IconArrowRight />
        </Button>
      </div>
    </Space>
  );
}
