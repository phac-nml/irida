import React from "react";
import { LaunchPageHeader } from "./LaunchPageHeader";
import { LaunchDetails } from "./LaunchDetails";
import { Button, Form } from "antd";
import { IconLaunchPipeline } from "../../components/icons/Icons";
import { useLaunchDispatch, useLaunchState } from "./launch-context";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { ParameterWithOptions } from "./ParameterWithOptions";

/**
 * React component to layout the content of the pipeline launch.
 * It will act as the top level logic controller.
 */
export function LaunchContent() {
  const { type, parameterWithOptions } = useLaunchState();
  const { dispatchLaunch } = useLaunchDispatch();
  console.log(parameterWithOptions);
  const [form] = Form.useForm();

  const onFinish = () => {
    // Add any required extra validation here.
    form.validateFields().then((values) => dispatchLaunch(values));
    // Add any required UI updates here.
  };

  /**
   * Helper function to determine if the option should be rendered as a
   * checkbox because it is either true or false.
   * @param {array} options
   * @returns {boolean}
   */
  const isTruthy = (options) => {
    if (options.length > 2) return false;
    return options[0].value === "true" || options[1].value === "true";
  };

  const initialValues = {
    name: `${type.replace(" ", "_")}__${formatInternationalizedDateTime(
      Date.now(),
      {
        year: "numeric",
        month: "numeric",
        day: "numeric",
      }
    ).replaceAll("/", "-")}`,
  };

  // Add any parametersWithOption default values
  parameterWithOptions.forEach((parameter) => {
    if (isTruthy(parameter.options)) {
      // Need to update to be actually boolean values
      parameter.type = "checkbox";
      parameter.options[0].value = parameter.options[0].value === "true";
      parameter.options[1].value = parameter.options[0].value === "true";
      parameter.value = parameter.value === "true";
    }
    initialValues[parameter.name] = parameter.value;
  });

  return (
    <>
      <LaunchPageHeader />
      <Form
        form={form}
        onFinish={onFinish}
        name="details"
        layout="vertical"
        initialValues={initialValues}
      >
        <LaunchDetails />
        <ParameterWithOptions options={parameterWithOptions} />
        <Button type="primary" htmlType="submit" icon={<IconLaunchPipeline />}>
          {i18n("LaunchContent.submit")}
        </Button>
      </Form>
    </>
  );
}
