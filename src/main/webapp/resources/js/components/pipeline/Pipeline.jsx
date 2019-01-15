import React from "react";
import PropTypes from "prop-types";
import { Card } from "antd";
import styled from "styled-components";
import { colours } from "./../../styles";

const PipelineCard = styled(Card)`
  .ant-card-head {
    background-color: ${colours.PRIMARY};
    color: #ffffff;
  }
  .ant-card-body {
    @media (min-width: 0) and (max-width: 1200px) {
      height: 150px;
    }
    @media (min-width: 1201px) {
      height: 200px;
    }
  }
`;

export default class Pipeline extends React.Component {
  static propTypes = {
    id: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired,
    description: PropTypes.string
  };

  render() {
    return (
      <PipelineCard
        title={this.props.name}
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
