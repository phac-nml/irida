import {
  Alert,
  Button,
  Checkbox,
  Empty,
  Form,
  Input,
  Space,
  Table,
  Typography,
} from "antd";
import React from "react";
import { useGetCartSamplesQuery } from "../../../apis/cart/cart";
import { IconExperiment } from "../../../components/icons/Icons";
import { SampleDetailViewer } from "../../../components/samples/SampleDetailViewer";
import { blue6 } from "../../../styles/colors";

/**
 * Component to render metadata restrictions for samples that are in the cart (if any).
 * User can update the target project restrictions as required
 * @param {Object} form - Ant Design form API
 * @returns {JSX.Element}
 * @constructor
 */
export function CreateProjectMetadataRestrictions({ form }) {
  React.useEffect(() => {}, []);

  return <>Metadata Restrictions</>;
}
