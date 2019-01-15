import React from "react";
import PropTypes from "prop-types";
import { Card } from "antd";
import styled from "styled-components";

const PipelineCard = styled(Card)`
  .ant-card-body {
    @media (min-width: 0) and (max-width: 1200px) {
      height: 150px;
    }
    @media (min-width: 1201px) {
      height: 200px;
    }
  }
`;

const Heading = styled.h4`
  margin: 0;
  padding: 8px 15px;
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
          <div style={{
            padding: 10,
            borderTopLeftRadius: 2,
            borderTopRightRadius: 2,
          }} className={this.props.styleName}>
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
