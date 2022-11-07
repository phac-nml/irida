import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Button, Empty, Radio, Select, Table, Typography } from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import {
  IconArrowLeft,
  IconArrowRight,
} from "../../../../components/icons/Icons";
import {
  MetadataHeaderItem,
  setSampleNameColumn,
  updateHeaders,
} from "../redux/importReducer";
import { NavigateFunction } from "react-router/dist/lib/hooks";
import {
  ImportDispatch,
  ImportState,
  useImportDispatch,
  useImportSelector,
} from "../redux/store";
import { getMetadataRestrictions } from "../../../../apis/metadata/field";

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
  const [loading, setLoading] = React.useState<boolean>(false);
  const [restrictions, setRestrictions] = React.useState([]);
  const { headers, sampleNameColumn } = useImportSelector(
    (state: ImportState) => state.importReducer
  );
  const [updatedSampleNameColumn, setUpdatedSampleNameColumn] =
    React.useState<string>(sampleNameColumn);
  const updatedHeaders: MetadataHeaderItem[] = [...headers];
  const dispatch: ImportDispatch = useImportDispatch();

  React.useEffect(() => {
    getMetadataRestrictions().then((data) => {
      setRestrictions(data);
    });
  }, []);

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

  const onRestrictionChange = (item: MetadataHeaderItem, value: string) => {
    const index = updatedHeaders.findIndex(
      (header) => header.rowKey === item.rowKey
    );
    if (index !== -1) {
      const updatedHeadersItem = { ...updatedHeaders[index] };
      updatedHeadersItem.restriction = value;
      updatedHeaders[index] = updatedHeadersItem;
    }
  };

  const columns = [
    {
      title: i18n("SampleMetadataImportMapHeaders.table.header"),
      dataIndex: "name",
    },
    {
      title: i18n("SampleMetadataImportMapHeaders.table.restriction"),
      dataIndex: "restriction",
      render(id: number, item: MetadataHeaderItem) {
        return (
          <Radio.Group
            options={restrictions}
            defaultValue={item.restriction}
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
    <SampleMetadataImportWizard current={1}>
      <Text>{i18n("SampleMetadataImportMapHeaders.description")}</Text>
      <Select
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
      <Table
        className="t-metadata-uploader-headers-table"
        rowKey={(row) => row.rowKey}
        columns={columns}
        dataSource={
          updatedSampleNameColumn
            ? updatedHeaders.filter(
                (updatedHeader) =>
                  updatedHeader.name !== updatedSampleNameColumn
              )
            : undefined
        }
        pagination={false}
        scroll={{ y: 600 }}
        locale={{
          emptyText: (
            <Empty
              image={Empty.PRESENTED_IMAGE_SIMPLE}
              description={i18n("SampleMetadataImportMapHeaders.table.empty")}
            />
          ),
        }}
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
