import React from "react";
import PropTypes from "prop-types";
import { Button, Card } from "antd";
import styled from "styled-components";
import { FONT_SIZE_DEFAULT, FONT_WEIGHT_HEAVY } from "../../styles/fonts";
import { SPACE_SM } from "../../styles/spacing";
import { BREAK_MD_MAX, BREAK_XL_MAX } from "../../styles/break-points";
import { getI18N } from "../../utilities/i18n-utilties";

const PipelineCard = styled(Card)`
  .ant-card-body {
    height: 150px;
    line-height: 1.8;
    overflow-x: auto;
    padding: ${SPACE_SM};
    font-size: ${FONT_SIZE_DEFAULT};
    @media (${BREAK_MD_MAX}) {
      height: 250px;
    }
    @media (${BREAK_XL_MAX}) {
      height: 200px;
    }
  }
`;

const Heading = styled.span`
  border-top-left-radius: 2px;
  border-top-right-radius: 2px;
  height: 65px;
  line-height: 65px;
  padding: 0 ${SPACE_SM};
  word-break: break-word;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: ${FONT_SIZE_DEFAULT};
  font-weight: ${FONT_WEIGHT_HEAVY};
`;

/**
 * Component to display a Pipeline Label, description, and the ability to select
 * the pipeline.
 */
export default class Pipeline extends React.Component {
  static propTypes = {
    /** Pipeline identifier */
    id: PropTypes.string.isRequired,
    /** Pipeline name */
    name: PropTypes.string.isRequired,
    /**  Pipeline description */
    description: PropTypes.string.isRequired,
    /** Styles specific for this pipeline.  Affects the way the pipeline name is displayed */
    styleName: PropTypes.string.isRequired,
    /**
     * Ability to select this pipeline
     */
    displaySelect: PropTypes.bool.isRequired
  };

  render() {
    return (
      <PipelineCard
        cover={
          <Heading className={this.props.styleName} title={this.props.name}>
            {this.props.name}
          </Heading>
        }
        actions={
          this.props.displaySelect
            ? [
                <Button
                  size="small"
                  className={`t-${this.props.name.replace(/\s/g, "_")}_btn`}
                  href={`${window.TL.BASE_URL}pipelines/${this.props.id}`}
                >
                  {getI18N("pipelines.cart.select")}
                </Button>
              ]
            : []
        }
      >
        {this.props.description}
      </PipelineCard>
    );
  }
}
