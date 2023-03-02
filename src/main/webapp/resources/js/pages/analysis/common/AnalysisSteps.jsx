/*
 * This file displays the steps of the analysis
 * (Queued, Preparing, Submitting, Running,
 * Completing, Completed)
 */

/*
 * The following import statements makes available all the elements
 * required by the component
 */
import React, { useContext } from "react";
import { Col, Row, Steps } from "antd";
import { AnalysisContext, stateMap } from "../../../contexts/AnalysisContext";

import { Running, Success } from "../../../components/icons";

import { getHumanizedDuration } from "../../../utilities/date-utilities";

const { Step } = Steps;

export function AnalysisSteps() {
  const { analysisContext } = useContext(AnalysisContext);

  const analysisDuration = getHumanizedDuration({
    date: analysisContext.duration,
  });
  const { isError: analysisError, previousState, analysisState } = analysisContext;

  return (
    <Row>
      <Col span={24}>
        <Steps
          current={
            analysisError ? stateMap[previousState] : stateMap[analysisState]
          }
          status={analysisError ? "error" : "finish"}
        >
          <Step
            title={i18n("AnalysisSteps.new")}
            icon={analysisState === "NEW" ? <Running /> : null}
            description={
              analysisState === "NEW" ||
              ((previousState === null || previousState === "NEW") &&
                analysisError)
                ? analysisDuration
                : null
            }
          />
          <Step
            title={i18n("AnalysisSteps.preparing")}
            icon={
              analysisState === "PREPARING" || analysisState === "PREPARED" ? (
                <Running />
              ) : null
            }
            description={
              analysisState === "PREPARED" ||
              analysisState === "PREPARING" ||
              ((previousState === "PREPARING" ||
                previousState === "PREPARED") &&
                analysisError)
                ? analysisDuration
                : null
            }
          />
          <Step
            title={i18n("AnalysisSteps.submitting")}
            icon={analysisState === "SUBMITTING" ? <Running /> : null}
            description={
              analysisState === "SUBMITTING" ||
              (previousState === "SUBMITTING" && analysisError)
                ? analysisDuration
                : null
            }
          />
          <Step
            title={i18n("AnalysisSteps.running")}
            icon={analysisState === "RUNNING" ? <Running /> : null}
            description={
              analysisState === "RUNNING" ||
              (previousState === "RUNNING" && analysisError)
                ? analysisDuration
                : null
            }
          />
          <Step
            title={i18n("AnalysisSteps.completing")}
            icon={
              analysisState === "COMPLETING" ||
              analysisState === "FINISHED_RUNNING" ||
              analysisState === "POST_PROCESSING" ||
              analysisState === "TRANSFERRING" ? (
                <Running />
              ) : null
            }
            description={
              analysisState === "COMPLETING" ||
              analysisState === "FINISHED_RUNNING" ||
              analysisState === "POST_PROCESSING" ||
              analysisState === "TRANSFERRING" ||
              ((previousState === "COMPLETING" ||
                previousState === "FINISHED_RUNNING" ||
                previousState === "POST_PROCESSING" ||
                previousState === "TRANSFERRING") &&
                analysisError)
                ? analysisDuration
                : null
            }
          />
          <Step
            title={i18n("AnalysisSteps.completed")}
            icon={analysisContext.isCompleted ? <Success /> : null}
          />
        </Steps>
      </Col>
    </Row>
  );
}
