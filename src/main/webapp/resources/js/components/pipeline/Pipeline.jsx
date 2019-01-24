import React from "react";
import PropTypes from "prop-types";
import { Card } from "antd";
import styled from "styled-components";
import { BREAK_POINTS } from "./../../styles";

const PipelineCard = styled(Card)`
  .ant-card-body {
    @media (${BREAK_POINTS.SM}) {
      height: 150px;
    }
    @media (${BREAK_POINTS.MD}) {
      height: 200px;
    }
    @media (${BREAK_POINTS.XL}) {
      height: 200px !important;
    }
  }
`;

const Heading = styled.h4`
  margin: 0;
  padding: 0 15px;
  overflow-x: hidden;
  word-break: break-word;
  height: 42px;
`;

export default class Pipeline extends React.Component {
  static propTypes = {
    id: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired,
    description: PropTypes.string.isRequired,
    styleName: PropTypes.string.isRequired
  };

  render() {
    return (
      <PipelineCard
        cover={
          <div
            style={{
              padding: 10,
              borderTopLeftRadius: 2,
              borderTopRightRadius: 2
            }}
            className={this.props.styleName}
          >
            <Heading>{this.props.name}</Heading>
          </div>
        }
        bodyStyle={{ overflowX: "auto" }}
        actions={[
          <a href={`${window.TL.BASE_URL}pipelines/${this.props.id}`}>Select</a>
        ]}
      >
        {this.props.description}
      </PipelineCard>
    );
  }
}
