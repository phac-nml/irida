import React, { useEffect, useState } from "react";
import { render } from "react-dom";
import { RemoteApiStatus } from "../admin/components/remote-connections/RemoteApiStatus";
import {
  createSynchronizedProject,
  getListOfRemoteApis,
  getProjectsForAPI,
} from "../../apis/remote-api/remote-api";
import { Button, Card, Checkbox, Col, Form, Input, Row, Select } from "antd";
import { SPACE_LG } from "../../styles/spacing";
import { HelpPopover } from "../../components/popovers";

function NewRemoteProjectForm() {
  const [apis, setApis] = useState([]);
  const [selectedApi, setSelectedApi] = useState();
  const [projects, setProjects] = useState([]);
  const [connected, setConnected] = useState();
  const [manual, setManual] = useState(false);
  const [form] = Form.useForm();

  useEffect(() => {
    getListOfRemoteApis().then((list) => {
      setApis(list);
    });
  }, [form]);

  const updateApiStatus = (value) => {
    if (value !== "default") {
      setSelectedApi(apis[value]);
    } else {
      setSelectedApi(undefined);
    }
  };

  const getApiProjects = () => {
    if (selectedApi.id) {
      getProjectsForAPI({ id: selectedApi.id })
        .then(setProjects)
        .then(() => setConnected(true));
    }
  };

  const createRemote = () => {
    const url = form.getFieldValue("project");
    const frequency = form.getFieldValue("frequency");
    createSynchronizedProject({
      url,
      frequency,
    }).then((response) => console.log(response));
  };

  return (
    <Row style={{ marginTop: SPACE_LG }}>
      <Col md={{ span: 12, offset: 6 }}>
        <Card title={i18n("NewProjectSync.title")}>
          <Form
            form={form}
            layout="vertical"
            initialValues={{
              frequency: 7,
            }}
          >
            <Form.Item name="api" label={i18n("NewProjectSync.api")}>
              <Select
                showSearch
                onChange={updateApiStatus}
                placeholder={i18n("NewProjectSync.api.placeholder")}
              >
                {apis.map((api, index) => (
                  <Select.Option key={`api-${api.id}`} value={index}>
                    {api.name}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
            {selectedApi ? (
              <>
                <Form.Item label={i18n("NewProjectSync.api-status")}>
                  <RemoteApiStatus
                    api={selectedApi}
                    onConnect={getApiProjects}
                  />
                </Form.Item>
                {connected ? (
                  <>
                    <Form.Item
                      label={i18n("NewProjectSync.project")}
                      name="project"
                    >
                      <Select
                        onChange={(value) =>
                          form.setFieldsValue({ projectUrl: value })
                        }
                        showSearch
                        options={projects}
                        placeholder={i18n("NewProjectSync.project.placeholder")}
                      />
                    </Form.Item>
                    <Form.Item
                      required
                      label={
                        <span>
                          REMOTE PROJECT URL
                          <Checkbox
                            onChange={(e) => setManual(e.target.checked)}
                          >
                            Set url manually
                            <HelpPopover
                              content={
                                <div>{i18n("NewProjectSync.url.help")}</div>
                              }
                            />
                          </Checkbox>
                        </span>
                      }
                      name="projectUrl"
                    >
                      <Input disabled={!manual} />
                    </Form.Item>
                    <Form.Item
                      label={i18n("NewProjectSync.frequency")}
                      name="frequency"
                    >
                      <Select>
                        <Select.Option value={1}>
                          {i18n("NewProjectSync.frequency.1")}
                        </Select.Option>
                        <Select.Option value={7}>
                          {i18n("NewProjectSync.frequency.7")}
                        </Select.Option>
                        <Select.Option value={30}>
                          {i18n("NewProjectSync.frequency.30")}
                        </Select.Option>
                        <Select.Option value={60}>
                          {i18n("NewProjectSync.frequency.60")}
                        </Select.Option>
                        <Select.Option value={90}>
                          {i18n("NewProjectSync.frequency.90")}
                        </Select.Option>
                      </Select>
                    </Form.Item>
                  </>
                ) : null}
                <div style={{ display: "flex", flexDirection: "row-reverse" }}>
                  <Button type="primary" onClick={createRemote}>
                    {i18n("NewProjectSync.submit")}
                  </Button>
                </div>
              </>
            ) : null}
          </Form>
        </Card>
      </Col>
    </Row>
  );
}

render(<NewRemoteProjectForm />, document.querySelector("#root"));
