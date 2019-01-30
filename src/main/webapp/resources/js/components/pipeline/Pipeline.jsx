import React from "react";
import PropTypes from "prop-types";
import { Card } from "antd";
import styled from "styled-components";
import { BREAK_POINTS, SPACING } from "./../../styles";

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
  border-top-left-radius: 2px;
  border-top-right-radius: 2px;
  margin: 0;
  padding: ${SPACING.SMALL};
  height: 40px;
  line-height: 20px;
  word-break: break-word;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;

  @media (${BREAK_POINTS.SM}) {
    font-size: 12px !important;
  }
  @media (${BREAK_POINTS.XL}) {
    font-size: 16px !important;
  }
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
          <Heading className={this.props.styleName} title={this.props.name}>
            {this.props.name}
          </Heading>
        }
        bodyStyle={{ overflowX: "auto", padding: SPACING.SMALL }}
        actions={[
          <a href={`${window.TL.BASE_URL}pipelines/${this.props.id}`}>Select</a>
        ]}
      >
        {this.props.description}
      </PipelineCard>
    );
  }
}
