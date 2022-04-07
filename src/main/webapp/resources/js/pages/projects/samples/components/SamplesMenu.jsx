import React, { lazy, Suspense } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Button, Dropdown, Menu, Space } from "antd";
import {
  DownOutlined,
  MergeCellsOutlined,
  ShareAltOutlined,
} from "@ant-design/icons";
import { updateTable } from "../services/samplesSlice";
import { setBaseUrl } from "../../../../utilities/url-utilities";

const MergeModal = lazy(() => import("./MergeModal"));

/**
 * React element to render a row of actions that can be performed on
 * samples in the table
 * @returns {JSX.Element}
 * @constructor
 */
export default function SamplesMenu() {
  const dispatch = useDispatch();
  const {
    projectId,
    selected,
    options: { filter },
  } = useSelector((state) => state.samples);

  const [mergeVisible, setMergeVisible] = React.useState(false);

  /**
   * When a merge is completed, hide the modal and ask
   * the table to reset
   */
  const onComplete = () => {
    setMergeVisible(false);
    dispatch(updateTable());
  };

  /**
   * Format samples to share with other projects,
   * store minimal information in localStorage
   */
  const shareSamples = () => {
    if (selected.size === 0) return;

    const samples = [];
    Object.values(selected).forEach(({ id, sampleName: name, owner }) => {
      samples.push({ id, name, owner });
    });

    // Store them to window storage for later use.
    window.sessionStorage.setItem(
      "share",
      JSON.stringify({
        samples,
        projectId,
        timestamp: Date.now(),
      })
    );

    // Redirect user to share page
    window.location.href = setBaseUrl(`/projects/${projectId}/share`);
  };

  const toolsMenu = React.useMemo(() => {
    return (
      <Menu>
        <Menu.Item
          key="merge-menu"
          icon={<MergeCellsOutlined />}
          onClick={() => setMergeVisible(true)}
        >
          {i18n("SamplesMenu.merge")}
        </Menu.Item>
        <Menu.Item
          key="share-menu"
          icon={<ShareAltOutlined />}
          onClick={shareSamples}
        >
          {i18n("SamplesMenu.share")}
        </Menu.Item>
      </Menu>
    );
  }, [selected]);

  return (
    <>
      <Space>
        <Dropdown overlay={toolsMenu}>
          <Button>
            {i18n("SamplesMenu.label")} <DownOutlined />
          </Button>
        </Dropdown>
      </Space>
      {mergeVisible && (
        <Suspense fallback={<span />}>
          <MergeModal
            visible={mergeVisible}
            onComplete={onComplete}
            onCancel={() => setMergeVisible(false)}
            samples={selected}
          />
        </Suspense>
      )}
    </>
  );
}
