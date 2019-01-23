import React from "react";
import { Col, Row } from "antd";
import { fetchIridaAnalysisWorkflows } from "../../apis/pipelines/pipelines";
import Pipeline from "../pipeline/Pipeline";
import { SPACING } from "../../styles";
import "./styles.css";

export class Pipelines extends React.Component {
  static propTypes = {};
  state = {
    pipelines: [],
    loaded: false
  };

  componentDidMount() {
    fetchIridaAnalysisWorkflows().then(data => {
      this.setState({ pipelines: data.pipelineList, loaded: true });
    });
  }

  render() {
    return (
      <Row gutter={16}>
        {this.state.pipelines.map(pipeline => (
          <Col
            md={24}
            lg={12}
            xl={8}
            key={pipeline.id}
            style={{ marginBottom: SPACING.DEFAULT }}
          >
            <Pipeline
              id={pipeline.id}
              name={pipeline.name}
              description={pipeline.description}
              styleName={pipeline.styleName}
            />
          </Col>
        ))}
      </Row>
    );
  }
}
