import React from "react";
import type { RangePickerProps } from "antd/es/date-picker";
import {
  Card,
  Col,
  DatePicker,
  Form,
  Input,
  Layout,
  PageHeader,
  Row,
  Table,
} from "antd";
import moment from "moment";

function CreateNcbiExport(): JSX.Element {
  const initialValues = {
    release_date: moment(new Date()),
  };

  const disabledDate: RangePickerProps["disabledDate"] = (current) => {
    // Can not select days before today for release
    return current && current < moment().startOf("day");
  };

  return (
    <Layout.Content>
      <Row gutter={[16, 16]}>
        <Col
          xxl={{ span: 16, offset: 4 }}
          xl={{ span: 20, offset: 2 }}
          sm={{ span: 22, offset: 1 }}
        >
          <PageHeader title={i18n("project.export.title")}>
            <Form layout="vertical" initialValues={initialValues}>
              <Card title={"Export Details"}>
                <Row gutter={[16, 16]}>
                  <Col xxl={12}>
                    <Form.Item
                      label={i18n("project.export.bioproject.title")}
                      help={i18n("project.export.bioproject.description")}
                    >
                      <Input />
                    </Form.Item>
                  </Col>
                  <Col xxl={12}>
                    <Form.Item
                      label={i18n("project.export.organization.title")}
                      help={i18n("project.export.organization.description")}
                    >
                      <Input />
                    </Form.Item>
                  </Col>
                  <Col xxl={12}>
                    <Form.Item
                      label={i18n("project.export.namespace.title")}
                      help={i18n("project.export.namespace.description")}
                    >
                      <Input />
                    </Form.Item>
                  </Col>
                  <Col xxl={12}>
                    <Form.Item
                      label={i18n("project.export.release_date.title")}
                      help={i18n("project.export.release_date.description")}
                      name="release_date"
                    >
                      <DatePicker
                        style={{ width: "100%" }}
                        disabledDate={disabledDate}
                      />
                    </Form.Item>
                  </Col>
                </Row>
              </Card>
              <Row>
                <Col>
                  <Table />
                </Col>
              </Row>
            </Form>
          </PageHeader>
        </Col>
      </Row>
    </Layout.Content>
  );
}

export default CreateNcbiExport;
