import {
  Alert,
  Button,
  Checkbox,
  Empty,
  Form,
  Input,
  Space,
  Table,
  Typography,
} from "antd";
import React from "react";
import { useGetCartSamplesQuery } from "../../../apis/cart/cart";
import { IconExperiment } from "../../../components/icons/Icons";
import {
  SampleDetailViewer
} from "../../../components/samples/SampleDetailViewer";
import { blue6 } from "../../../styles/colors";

/**
 * Component to render samples that are in the cart (if any).
 * User can select which samples to add to the new project.
 * @param {Object} form - Ant Design form API
 * @returns {JSX.Element}
 * @constructor
 */
export function CreateProjectSamples({ form }) {
  const { data: samples = {}, isLoading } = useGetCartSamplesQuery();
  const [organismFilter, setOrganismFilter] = React.useState([]);
  const [selected, setSelected] = React.useState([]);
  const [lock, setLock] = React.useState(false);

  React.useEffect(() => {
    const exists = {};
    /*
    For all unlocked samples we need to get the unique values of the organism names.
    Then format them into a manner that can be consumed by the dropdown.
     */
    samples.unlocked?.forEach((sample) => {
      if (!exists[sample.organism]) {
        exists[sample.organism] = {
          text: sample.organism,
          value: sample.organism,
        };
      }
    });
    setOrganismFilter(Object.values(exists));
  }, [samples]);

  return (
    <Space direction="vertical">
      {samples.locked?.length > 0 && (
        <Alert
          type="warning"
          showIcon
          message={i18n("CreateProjectSamples.locked")}
        />
      )}
      {samples.unlocked?.length ? (
        <>
          <Table
            className="t-samples"
            loading={isLoading}
            rowSelection={{
              type: "checkbox",
              selectedRowKeys: selected,
              onChange: (selectedRowKeys, selectedRows) => {
                setSelected(selectedRowKeys);
                form.setFieldsValue({
                  samples: selectedRows.map((s) => Number(s.identifier)),
                });
              },
            }}
            scroll={{ y: 600 }}
            pagination={false}
            dataSource={samples.unlocked}
            rowKey={(sample) => `sample-${sample.identifier}`}
            columns={[
              {
                title: i18n("CreateProjectSamples.sampleName"),
                dataIndex: "label",
                render: (text, sample) => (
                  <SampleDetailViewer sampleId={sample.identifier}>
                    <Button size="small">{sample.label}</Button>
                  </SampleDetailViewer>
                ),
                onFilter: (value, record) =>
                  record.label.toLowerCase().indexOf(value.toLowerCase()) >= 0,
              },
              {
                title: i18n("CreateProjectSamples.organism"),
                dataIndex: "organism",
                filters: organismFilter,
                onFilter: (value, record) => record.organism === value,
              },
            ]}
          />
          <Form.Item name="samples" hidden>
            <Input />
          </Form.Item>
          <Form.Item name="lock" valuePropName="checked">
            <Checkbox
              disabled={selected.length === 0}
              checked={lock}
              onChange={(e) => setLock(e.target.checked)}
            >
              {i18n("CreateProjectSamples.lock")}
            </Checkbox>
          </Form.Item>
        </>
      ) : (
        <Empty
          image={<IconExperiment />}
          imageStyle={{
            fontSize: 60,
            color: blue6,
          }}
          description={
            <div className="t-no-samples">
              <Typography.Paragraph>
                {i18n("CreateProjectSamples.empty-description")}
              </Typography.Paragraph>
              <Typography.Text type="secondary">
                {i18n("CreateProjectSamples.empty-message")}
              </Typography.Text>
            </div>
          }
        />
      )}
    </Space>
  );
}
