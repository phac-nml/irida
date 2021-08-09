import { Button, Form, Select, Space, Spin } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { useGetCartQuery } from "../../../../apis/cart/cart";
import { useGetProjectsForUserQuery } from "../../../../apis/projects/projects";
import { nextStep, setProject } from "./shareSlice";

/**
 * React component to render a select field for selection of the project
 * to share samples with.
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareProject() {
  const project = useSelector((state) => state.share.project);
  const { data: samples = [] } = useGetCartQuery();
  const [projectSamplesInCart, setProjectSamplesInCart] = React.useState([]);

  const [query, setQuery] = React.useState("");
  const dispatch = useDispatch();

  const { data: projects = [], isFetching } = useGetProjectsForUserQuery(query);

  const setValue = (newValue) => {
    const project = projects.find((project) => project.identifier === newValue);
    dispatch(setProject(project));
  };

  React.useEffect(() => {
    if (project) {
      // Check to see if the project is in the cart
      setProjectSamplesInCart(
        samples.filter(
          (s) => Number(s.project.id) === Number(project.identifier)
        )
      );
    }
  }, [project, samples]);

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      <Form.Item
        label={i18n("ShareProject.label")}
        hasFeedback={projectSamplesInCart.length}
        validateStatus={projectSamplesInCart.length ? "warning" : "success"}
        help={
          projectSamplesInCart.length > 0 &&
          `Samples from this project are in the cart and will not be re-copied`
        }
      >
        <Select
          autoFocus
          size="large"
          value={project?.identifier}
          onChange={setValue}
          options={projects.map((project) => ({
            label: project.name,
            value: project.identifier,
          }))}
          showSearch
          onSearch={setQuery}
          style={{ width: `100%` }}
          filterOption={false}
          notFoundContent={isFetching ? <Spin size="small" /> : null}
        />
      </Form.Item>

      <div style={{ display: "flex", flexDirection: "row-reverse" }}>
        <Button disabled={!project} onClick={() => dispatch(nextStep())}>
          {i18n("ShareProject.next")}
        </Button>
      </div>
    </Space>
  );
}
