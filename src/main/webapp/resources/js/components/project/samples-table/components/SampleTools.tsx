import React, { useMemo } from "react";
import { Button, Dropdown, Menu } from "antd";
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
import { setBaseUrl } from "../../../../utilities/url-utilities";

export default function SampleTools() {
  const { projectId } = useParams();
  const { data: details = {} } = useGetProjectDetailsQuery(projectId);
  const { state, dispatch } = useProjectSamples();

  const menu = useMemo(
    () => (
      <Menu className={"t-tools-dropdown"}>
        {!details.remote ? (
          <Menu.Item
            disabled={state.selection.count < 2}
            key={"merge-menu"}
            icon={<MergeCellsOutlined />}
            className={"t-merge"}
          >
            {i18n("SamplesMenu.merge")}
          </Menu.Item>
        ) : null}
        <Menu.Item
          disabled={state.selection.count === 0}
          key="share-menu"
          icon={<ShareAltOutlined />}
          className="t-share"
        >
          {i18n("SamplesMenu.share")}
        </Menu.Item>
        <Menu.Item
          disabled={state.selection.count === 0}
          key="remove-menu"
          icon={<CloseSquareOutlined />}
          className="t-remove"
        >
          {i18n("SamplesMenu.remove")}
        </Menu.Item>
        <Menu.Divider />
        <Menu.Item key="import-menu" icon={<CloudUploadOutlined />}>
          <a
            href={setBaseUrl(
              `projects/${projectId}/sample-metadata/upload/file`
            )}
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
    [details.remote, projectId, state.selection.count]
  );

  return (
    <Dropdown overlay={menu}>
      <Button className="t-sample-tools">
        {i18n("SamplesMenu.label")} <IconDropDown />
      </Button>
    </Dropdown>
  );
}
