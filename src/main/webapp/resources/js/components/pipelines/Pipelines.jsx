import React from "react";
import { Col, Row } from "antd";
import { fetchIridaAnalysisWorkflows } from "../../apis/pipelines/pipelines";
import Pipeline from "../pipeline/Pipeline";
import "./styles.css";
import { SPACE_MD } from "../../styles/spacing";

/**
 * Component to render all analysis pipeline for the user to select.
 */
export class Pipelines extends React.Component {
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
      <Row className="t-pipelines" gutter={16}>
        {this.state.pipelines.map(pipeline => (
          <Col
            md={24}
            lg={12}
            xl={8}
            key={pipeline.id}
            style={{ marginBottom: SPACE_MD }}
          >
            <Pipeline
              id={pipeline.id}
              name={pipeline.name}
              description={pipeline.description}
              styleName={pipeline.styleName}
              displaySelect={this.props.displaySelect}
            />
          </Col>
        ))}
      </Row>
    );
  }
}
