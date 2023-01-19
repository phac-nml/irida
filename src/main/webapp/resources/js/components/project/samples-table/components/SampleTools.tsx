import React, { useMemo } from "react";
import { Button, Dropdown, Menu, Space } from "antd";
import { useProjectSamples } from "../useProjectSamplesContext";
import { useParams } from "react-router-dom";
import { useGetProjectDetailsQuery } from "../../../../redux/endpoints/project";
import {
  CloseSquareOutlined,
  CloudUploadOutlined,
  MergeCellsOutlined,
  PlusSquareOutlined,
  ShareAltOutlined,
} from "@ant-design/icons";
import { IconDropDown } from "../../../icons/Icons";
import { CONTEXT_PATH } from "../../../../data/routes";
import MergeTrigger from "./merge/MergeTrigger";

/**
 * React component to render a dropdown list of actions that can be performed
 * on samples.
 *
 * @constructor
 */
export default function SampleTools() {
  const { projectId } = useParams();
  const { data: details = {} } = useGetProjectDetailsQuery(projectId);
  const {
    state: { selection },
  } = useProjectSamples();

  const menu = useMemo(
    () => (
      <Menu className={"t-tools-dropdown"}>
        {!details.remote ? (
          <MergeTrigger>
            <Menu.Item
              key={"merge-menu"}
              icon={<MergeCellsOutlined />}
              className={"t-merge"}
            >
              {i18n("SamplesMenu.merge")}
            </Menu.Item>
          </MergeTrigger>
        ) : null}
        <Menu.Item
          disabled={selection.count === 0}
          key="share-menu"
          icon={<ShareAltOutlined />}
          className="t-share"
        >
          {i18n("SamplesMenu.share")}
        </Menu.Item>
        <Menu.Item
          disabled={selection.count === 0}
          key="remove-menu"
          icon={<CloseSquareOutlined />}
          className="t-remove"
        >
          {i18n("SamplesMenu.remove")}
        </Menu.Item>
        <Menu.Divider />
        <Menu.Item key="import-menu" icon={<CloudUploadOutlined />}>
          <a
            href={`${CONTEXT_PATH}/projects/${projectId}/sample-metadata/upload/file`}
          >
            {i18n("SamplesMenu.import")}
          </a>
        </Menu.Item>
        <Menu.Divider />
        <Menu.Item
          key="create-menu"
          icon={<PlusSquareOutlined />}
          className="t-create-sample"
        >
          {i18n("SamplesMenu.createSample")}
        </Menu.Item>
      </Menu>
    ),
    [details.remote, projectId, selection.count]
  );

  return (
    <Dropdown overlay={menu}>
      <Button className="t-sample-tools">
        <Space size={"small"}>
          {i18n("SamplesMenu.label")}
          <IconDropDown />
        </Space>
      </Button>
    </Dropdown>
  );
}
