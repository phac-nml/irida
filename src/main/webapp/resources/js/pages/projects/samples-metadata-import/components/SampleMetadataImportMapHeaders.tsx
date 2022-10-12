import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Button, Select, Table, Typography } from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import {
  IconArrowLeft,
  IconArrowRight,
} from "../../../../components/icons/Icons";
import {
  MetadataHeaderItem,
  setSampleNameColumn,
} from "../services/importReducer";
import { NavigateFunction } from "react-router/dist/lib/hooks";
import {
  ImportDispatch,
  ImportState,
  useImportDispatch,
  useImportSelector,
} from "../store";

const { Text } = Typography;

/**
 * React component that displays Step #2 of the Sample Metadata Uploader.
 * This page is where the user selects the sample name column.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportMapHeaders(): JSX.Element {
  const { projectId } = useParams<{ projectId: string }>();
  const navigate: NavigateFunction = useNavigate();
  const [column, setColumn] = React.useState<string>();
  const [loading, setLoading] = React.useState<boolean>(false);
  const { headers, sampleNameColumn } = useImportSelector(
    (state: ImportState) => state.importReducer
  );
  const dispatch: ImportDispatch = useImportDispatch();

  React.useEffect(() => {
    if (!column) {
      setColumn(sampleNameColumn ? sampleNameColumn : headers[0]?.name);
    }
  }, [sampleNameColumn, headers, column]);

  const onSubmit = async () => {
    if (projectId && column) {
      setLoading(true);
      await dispatch(setSampleNameColumn({ projectId, column }));
      navigate(`/${projectId}/sample-metadata/upload/review`);
    }
  };

  const columns = [
    {
      title: "Headers",
      dataIndex: "name",
    },
    {
      title: "Restriction",
      dataIndex: "level",
      render(id: number, item: MetadataHeaderItem) {
        return (
          <Select defaultValue={item.level}>
            <Select.Option value={1}>Level 1</Select.Option>
            <Select.Option value={2}>Level 2</Select.Option>
            <Select.Option value={3}>Level 3</Select.Option>
            <Select.Option value={4}>Level 4</Select.Option>
          </Select>
        );
      },
    },
  ];

  const rowSelection = {
    onChange: (
      selectedRowKeys: React.Key[],
      selectedRows: MetadataHeaderItem[]
    ) => {
      setColumn(selectedRows[0].name);
    },
  };

  return (
    <SampleMetadataImportWizard current={1}>
      <Text>{i18n("SampleMetadataImportMapHeaders.description")}</Text>
      <Table
        className="t-metadata-uploader-header-table"
        rowKey={(row) => row.rowKey}
        rowSelection={{
          type: "radio",
          ...rowSelection,
        }}
        columns={columns}
        dataSource={headers}
        pagination={false}
      />
      <div style={{ display: "flex" }}>
        <Button
          className="t-metadata-uploader-file-button"
          icon={<IconArrowLeft />}
          onClick={() => navigate(-1)}
        >
          {i18n("SampleMetadataImportMapHeaders.button.back")}
        </Button>
        <Button
          className="t-metadata-uploader-preview-button"
          onClick={onSubmit}
          style={{ marginLeft: "auto" }}
          loading={loading}
        >
          {i18n("SampleMetadataImportMapHeaders.button.next")}
          <IconArrowRight />
        </Button>
      </div>
    </SampleMetadataImportWizard>
  );
}
