import React from "react";
import { useLaunch } from "./launch-context";
import { Checkbox, Col, Form, Row, Typography } from "antd";
import { SectionHeading } from "../../components/ant.design/SectionHeading";

/**
 * React component to share results with Project and Samples
 * @returns {JSX.Element}
 * @constructor
 */
export function SharePipelineResults() {
  const [{ updateSamples, projects, automatedId }] = useLaunch();

  return (
    <section>
      <SectionHeading id="launch-sharing">
        {i18n("SharePipelineResults.label")}
      </SectionHeading>
      {updateSamples ? (
        <Form.Item
          className="t-share-samples"
          name="updateSamples"
          valuePropName="checked"
        >
          <Checkbox checked>{updateSamples}</Checkbox>
        </Form.Item>
      ) : null}
      {!automatedId && (
        <>
          <Typography.Text strong>
            {i18n("ShareResultsWithProjects.label")}
          </Typography.Text>
          <Form.Item className="t-share-projects" name="projects">
            <Checkbox.Group style={{ width: `100%` }}>
              <Row>
                {projects.map((project) => (
                  <Col key={`project-${project.value}`} span={8}>
                    <Checkbox
                      style={{ lineHeight: `32px` }}
                      value={project.value}
                    >
                      {project.label}
                    </Checkbox>
                  </Col>
                ))}
              </Row>
            </Checkbox.Group>
          </Form.Item>
        </>
      )}
    </section>
  );
}
