import {
  Alert,
  Button,
  Card,
  Col,
  PageHeader,
  Row,
  Skeleton,
  Space,
  Steps,
} from "antd";
import React, { useEffect, useState } from "react";
import { render } from "react-dom";
import { Provider, useSelector } from "react-redux";
import {
  useGetPotentialProjectsToShareToQuery
} from "../../../apis/projects/projects";
import {
  useGetSampleIdsForProjectQuery,
  useShareSamplesWithProjectMutation,
} from "../../../apis/projects/samples";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { ShareMetadata } from "./ShareMetadata";
import { ShareNoSamples } from "./ShareNoSamples";
import { ShareProject } from "./ShareProject";
import { ShareSamples } from "./ShareSamples";
import { ShareSuccess } from "./ShareSuccess";
import store from "./store";

/**
 * Base component for sharing samples between projects.
 *
 * @returns {JSX.Element}
 * @constructor
 */
function ShareApp() {
  const [step, setStep] = useState(0);
  const [prevDisabled, setPrevDisabled] = useState(true);
  const [nextDisabled, setNextDisabled] = useState(true);
  const [error, setError] = useState(undefined);
  const [finished, setFinished] = useState(false);

  /*
  Create redirect href to project samples page.
  */
  const [redirect] = useState(
    () => window.location.href.match(/(.*)\/share/)[1]
  );

  const {
    originalSamples = [],
    currentProject,
    locked,
    targetProject,
    remove,
    metadataRestrictions,
  } = useSelector((state) => state.shareReducer);

  const [shareSamplesWithProject, { isLoading, isError, error: shareError }] =
    useShareSamplesWithProjectMutation();

  const { data: existingIds = [] } = useGetSampleIdsForProjectQuery(
    targetProject?.identifier,
    {
      skip: !targetProject?.identifier,
    }
  );

  const { data: projects, isLoading: projectsLoading } =
    useGetPotentialProjectsToShareToQuery(currentProject, {
      skip: !currentProject,
    });

  const filtered = originalSamples.filter(
    (sample) => !existingIds.includes(sample.id)
  );

  const steps = [
    {
      title: i18n("ShareLayout.project"),
      component: <ShareProject projects={projects} />,
    },
    {
      title: i18n("ShareLayout.samples"),
      component: <ShareSamples samples={filtered} redirect={redirect} />,
    },
    { title: i18n("ShareLayout.restrictions"), component: <ShareMetadata /> },
  ];

  useEffect(() => {
    if (step === 0) {
      setPrevDisabled(true);
      setNextDisabled(
        targetProject === undefined && typeof error === "undefined"
      );
      return;
    } else if (step === 1) {
      setPrevDisabled(false);
      setNextDisabled(filtered.length === 0);
      return;
    }
    setPrevDisabled(false);
    setNextDisabled(step === steps.length - 1);
  }, [error, targetProject, step, steps.length]);

  /*
  1. No Samples - this would be if the user came to this page from anything
  other than the share samples link.
   */
  const NO_SAMPLES =
    typeof originalSamples === "undefined" || originalSamples.length === 0;

  if (NO_SAMPLES) {
    return <ShareNoSamples redirect={redirect} />;
  }

  /**
   * Return to previous page (project samples page)
   */
  const goToPrevious = () =>
    (window.location.href = setBaseUrl(`/projects/${currentProject}/samples`));

  const nextStep = () => setStep(step + 1);
  const previousStep = () => setStep(step - 1);

  /**
   * Server call to actually share samples with another project.
   */
  const submit = async () => {
    try {
      await shareSamplesWithProject({
        sampleIds: filtered.map((s) => s.id),
        locked,
        currentId: currentProject,
        targetId: targetProject.identifier,
        remove,
        restrictions: metadataRestrictions.map(({ restriction, id }) => ({
          restriction,
          identifier: id,
        })),
      });
      setFinished(true);
    } catch (e) {
      setError(e);
    }
  };

  return (
    <Row>
      <Col xl={{ span: 18, offset: 3 }} xs={24}>
        <Skeleton loading={projectsLoading} active>
          <Card>
            <PageHeader
              ghost={false}
              title={i18n("ShareSamples.title")}
              onBack={goToPrevious}
            >
              {projects?.length === 0 ? (
                <Alert
                  message={i18n("ShareSamples.no-project.message")}
                  description={i18n("ShareSamples.no-project.description")}
                />
              ) : (
                <Row>
                  <Col span={6}>
                    <Steps
                      direction="vertical"
                      current={step}
                      style={{ height: 400 }}
                    >
                      {steps.map((step) => (
                        <Steps.Step key={step.title} title={step.title} />
                      ))}
                    </Steps>
                  </Col>
                  <Col span={18}>
                    {finished ? (
                      <ShareSuccess
                        currentProject={currentProject}
                        samples={filtered}
                        removed={remove}
                        project={targetProject}
                      />
                    ) : (
                      <Space direction="vertical" style={{ width: `100%` }}>
                        {steps[step].component}
                        {error}
                        <div
                          style={{
                            display: "flex",
                            justifyContent: "space-between",
                          }}
                        >
                          <Button
                            className="t-share-previous"
                            disabled={prevDisabled}
                            onClick={previousStep}
                          >
                            {i18n("ShareLayout.previous")}
                          </Button>
                          {step === steps.length - 1 ? (
                            <Button
                              className="t-share-button"
                              onClick={submit}
                              type="primary"
                            >
                              {i18n("ShareButton.button")}
                            </Button>
                          ) : (
                            <Button
                              className="t-share-next"
                              disabled={nextDisabled}
                              onClick={nextStep}
                            >
                              {i18n("ShareLayout.next")}
                            </Button>
                          )}
                        </div>
                      </Space>
                    )}
                  </Col>
                </Row>
              )}
            </PageHeader>
          </Card>
        </Skeleton>
      </Col>
    </Row>
  );
}

render(
  <Provider store={store}>
    <ShareApp />
  </Provider>,
  document.querySelector("#root")
);
