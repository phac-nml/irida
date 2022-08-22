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
import React from "react";
import { render } from "react-dom";
import { Provider, useSelector } from "react-redux";
import { useGetPotentialProjectsToShareToQuery } from "../../../apis/projects/projects";
import {
  useShareSamplesWithProjectMutation,
  useValidateSamplesMutation,
} from "../../../apis/projects/samples";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { ShareMetadata } from "./ShareMetadata";
import { ShareNoSamples } from "./ShareNoSamples";
import { ShareProject } from "./ShareProject";
import { ShareSamples } from "./ShareSamples";
import { ShareSuccess } from "./ShareSuccess";
import ShareLarge from "./ShareLarge";
import store from "./store";

/**
 * Base component for sharing samples between projects.
 *
 * @returns {JSX.Element}
 * @constructor
 */
function ShareApp() {
  const [step, setStep] = React.useState(0);
  const [prevDisabled, setPrevDisabled] = React.useState(true);
  const [nextDisabled, setNextDisabled] = React.useState(true);
  const [shareLarge, setShareLarge] = React.useState(false);
  const [error, setError] = React.useState(undefined);
  const [finished, setFinished] = React.useState(false);
  const [existingIds, setExistingIds] = React.useState([]);
  const [existingNames, setExistingNames] = React.useState([]);
  const [validateSamples] = useValidateSamplesMutation();

  /*
  Create redirect href to project samples page.
  */
  const [redirect] = React.useState(
    () => window.location.href.match(/(.*)\/share/)[1]
  );

  const {
    samples = [],
    currentProject,
    locked,
    targetProject,
    remove,
    metadataRestrictions,
  } = useSelector((state) => state.shareReducer);

  const [shareSamplesWithProject] = useShareSamplesWithProjectMutation();

  const { data: projects, isLoading: projectsLoading } =
    useGetPotentialProjectsToShareToQuery(currentProject, {
      skip: !currentProject,
    });

  const filtered = samples.filter(
    (sample) =>
      !existingIds.includes(sample.id) && !existingNames.includes(sample.name)
  );

  /*
  Samples in target project which have the same ids as the ones being shared from the source project
   */
  const targetProjectSampleIdsDuplicate = samples.filter((sample) =>
    existingIds.includes(sample.id)
  );

  /*
  Samples in target project which have the same names as the ones being shared from the source project
   */
  const targetProjectSampleNamesDuplicate = samples.filter(
    (sample) =>
      existingNames.includes(sample.name) && !existingIds.includes(sample.id)
  );

  const steps = [
    {
      title: i18n("ShareLayout.project"),
      component: <ShareProject projects={projects} />,
    },
    {
      title: i18n("ShareLayout.samples"),
      component: (
        <ShareSamples
          samples={filtered}
          targetProjectSampleIdsDuplicate={targetProjectSampleIdsDuplicate}
          targetProjectSampleNamesDuplicate={targetProjectSampleNamesDuplicate}
        />
      ),
    },
    { title: i18n("ShareLayout.restrictions"), component: <ShareMetadata /> },
  ];

  React.useEffect(() => {
    if (targetProject?.identifier) {
      validateSamples({
        projectId: targetProject?.identifier,
        body: {
          samples: samples.map((sample) => ({
            name: sample.name,
          })),
        },
      }).then((response) => {
        let filtered = response.data.samples.filter(
          (sample) => sample.ids.length !== 0
        );
        setExistingIds(
          filtered
            .map((sample) => {
              return sample.ids;
            })
            .flat()
        );
        setExistingNames(
          filtered.map((sample) => {
            return sample.name;
          })
        );
      });
    }
  }, [targetProject?.identifier]);

  React.useEffect(() => {
    if (step === 0) {
      setPrevDisabled(true);
      setNextDisabled(
        targetProject === undefined && typeof error === "undefined"
      );
      return;
    } else if (step === 1) {
      setPrevDisabled(false);
      setNextDisabled(filtered.length === 0 || samples.length === 0);
      return;
    }
    setPrevDisabled(false);
    setNextDisabled(step === steps.length - 1);
  }, [error, targetProject, step, steps.length, samples.length]);

  /*
  1. No Samples - this would be if the user came to this page from anything
  other than the share samples link.
   */
  const NO_SAMPLES = typeof samples === "undefined" || samples.length === 0;

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
    // Just do a bulk share if only 500.
    if (samples.length < 500) {
      try {
        shareSamplesWithProject({
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
        // Remove the share from session storage
        window.sessionStorage.removeItem("share");
      } catch (e) {
        setError(e);
      }
    } else {
      setShareLarge(true);
    }
  };

  const onShareLargeComplete = () => {
    setShareLarge(false);
    setFinished(true);
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
                    ) : shareLarge ? (
                      <ShareLarge
                        samples={samples}
                        current={currentProject}
                        target={targetProject.identifier}
                        locked={locked}
                        remove={remove}
                        restrictions={metadataRestrictions}
                        onComplete={onShareLargeComplete}
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
