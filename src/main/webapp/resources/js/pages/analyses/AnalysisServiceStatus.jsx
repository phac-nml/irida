import React from "react";
import { Row, Col, Tooltip, Progress, Statistic } from 'antd';
import PropTypes from "prop-types";



export class AnalysisServiceStatus extends React.Component {
    render(){
        
        var queueSize = 10;

        var runningAndQueued =  this.props.queued + this.props.running;

        var totalSlots = runningAndQueued > queueSize ? runningAndQueued : queueSize;

        var totalSubmittedPct = (runningAndQueued / totalSlots) * 100;
        var runningPct = (this.props.running/totalSlots) * 100;

        var space = totalSlots - this.props.queued - this.props.running;

        var str = this.props.running + " running - " + this.props.queued + " queued - " + space + " open slots.";

        return(
            <Row>
            <Col span={4}>Analysis Engine Status:</Col>
            <Col span={24}>
            <Tooltip title={str} >
                <Progress percent={totalSubmittedPct} successPercent={runningPct} showInfo={false} />
            </Tooltip>
            </Col>
            </Row>
        );
    };
}


AnalysisServiceStatus.propTypes = {
  running: PropTypes.number.isRequired,
  queued: PropTypes.number.isRequired
};