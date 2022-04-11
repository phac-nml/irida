import React, { lazy, Suspense } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Button, Dropdown, Menu, message, Space } from "antd";
import {
  CloseSquareOutlined,
  CloudUploadOutlined,
  DownOutlined,
  MergeCellsOutlined,
  PlusSquareOutlined,
  ShareAltOutlined,
  ShoppingCartOutlined,
} from "@ant-design/icons";
import { addToCart, reloadTable } from "../services/samplesSlice";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import {
  validateSamplesForMerge,
  validateSamplesForRemove,
} from "../services/sample.utilities";
import CreateNewSample from "./CreateNewSample";

const MergeModal = lazy(() => import("./MergeModal"));
const RemoveModal = lazy(() => import("./RemoveModal"));
const CreateModal = lazy(() => import("./CreateNewSample"));

/**
 * React element to render a row of actions that can be performed on
 * samples in the table
 * @returns {JSX.Element}
 * @constructor
 */
export default function SamplesMenu() {
  const dispatch = useDispatch();

  const { projectId, selected } = useSelector((state) => state.samples);

  const [mergeVisible, setMergeVisible] = React.useState(false);
  const [removedVisible, setRemovedVisible] = React.useState(false);
  const [createSampleVisible, setCreateSampleVisible] = React.useState(false);
  const [sorted, setSorted] = React.useState({});

  /**
   * When a merge is completed, hide the modal and ask
   * the table to reset
   */
  const onMergeComplete = () => {
    setMergeVisible(false);
    dispatch(reloadTable());
  };

  const onRemoveComplete = () => {
    setRemovedVisible(false);
    dispatch(reloadTable());
  };

  const onCreate = () => {
    setCreateSampleVisible(false);
    dispatch(reloadTable());
  };

  const onAddToCart = () => {
    dispatch(addToCart({ projectId, selected }));
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

  /**
   * Validate samples for specific modals and open the appropriate modal
   * if the right samples are available.
   * @param {string} name - which modal to open
   */
  const validateAndOpenModalFor = (name) => {
    if (name === "merge") {
      const validated = validateSamplesForMerge(selected);
      if (validated.valid.length >= 2) {
        setSorted(validated);
        setMergeVisible(true);
      } else {
        message.error("You need at least 2 unlocked samples to merge");
      }
    } else if (name === "remove") {
      const validated = validateSamplesForRemove(selected, projectId);
      if (validated.valid.length > 0) {
        setSorted(validated);
        setRemovedVisible(true);
      } else
        message.error("No selected samples can be removed from this project");
    }
  };

  const toolsMenu = React.useMemo(() => {
    return (
      <Menu>
        <Menu.Item
          key="merge-menu"
          icon={<MergeCellsOutlined />}
          onClick={() => validateAndOpenModalFor("merge")}
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
        <Menu.Item
          key="remove-menu"
          icon={<CloseSquareOutlined />}
          onClick={() => validateAndOpenModalFor("remove")}
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
          onClick={() => setCreateSampleVisible(true)}
        >
          {i18n("SamplesMenu.createSample")}
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
        <Button icon={<ShoppingCartOutlined />} onClick={onAddToCart}>
          {i18n("SampleMenu.cart")}
        </Button>
      </Space>
      {mergeVisible && (
        <Suspense fallback={<span />}>
          <MergeModal
            visible={mergeVisible}
            onComplete={onMergeComplete}
            onCancel={() => setMergeVisible(false)}
            samples={sorted}
          />
        </Suspense>
      )}
      {removedVisible && (
        <Suspense fallback={<span />}>
          <RemoveModal
            visible={removedVisible}
            onComplete={onRemoveComplete}
            onCancel={() => setRemovedVisible(false)}
            samples={sorted}
          />
        </Suspense>
      )}
      {createSampleVisible && (
        <Suspense fallback={<span />}>
          <CreateNewSample
            visible={createSampleVisible}
            onCancel={() => setCreateSampleVisible(false)}
            onCreate={onCreate}
          />
        </Suspense>
      )}
    </>
  );
}
