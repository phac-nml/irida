import React, { lazy, Suspense } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Button, Dropdown, Menu, message, Space } from "antd";
import {
  addToCart,
  downloadSamples,
  reloadTable,
} from "../services/samplesSlice";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import {
  validateSamplesForLinker,
  validateSamplesForMerge,
  validateSamplesForRemove,
} from "../services/sample.utilities";
import {
  IconCloseSquare,
  IconCloudDownload,
  IconCloudUpload,
  IconCode,
  IconDropDown,
  IconFile,
  IconFileExcel,
  IconPlusSquare,
  IconShare,
  IconShoppingCart,
  MergeSamplesIcon,
} from "../../../../components/icons/Icons";

const MergeModal = lazy(() => import("./MergeModal"));
const RemoveModal = lazy(() => import("./RemoveModal"));
const CreateModal = lazy(() => import("./CreateNewSample"));
const LinkerModal = lazy(() => import("./LinkerModal"));

/**
 * React element to render a row of actions that can be performed on
 * samples in the table
 * @returns {JSX.Element}
 * @constructor
 */
export default function SamplesMenu() {
  const dispatch = useDispatch();

  const { projectId, selected, selectedCount } = useSelector(
    (state) => state.samples
  );
  const { project: { canManage = false } = {} } = useSelector(
    (state) => state.user
  );

  const [mergeVisible, setMergeVisible] = React.useState(false);
  const [removedVisible, setRemovedVisible] = React.useState(false);
  const [createSampleVisible, setCreateSampleVisible] = React.useState(false);
  const [linkerVisible, setLinkerVisible] = React.useState(false);
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

  const onDownload = () => {
    dispatch(downloadSamples({ projectId, selected }));
  };

  const onNCBI = () => {
    window.location.href = setBaseUrl(
      `/projects/${projectId}/export/ncbi?ids=${Object.values(selected)
        .map((s) => s.id)
        .join(",")}`
    );
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
    } else if (name === "linker") {
      const validated = validateSamplesForLinker(selected, projectId);
      if (validated.associated.length > 0) {
        message.error(
          "You have samples form associated projects selected, they cannot be used in this linker command"
        );
      } else {
        setSorted(validated.valid);
        setLinkerVisible(true);
      }
    }
  };

  const toolsMenu = React.useMemo(() => {
    return (
      <Menu>
        <Menu.Item
          disabled={selectedCount < 2}
          key="merge-menu"
          icon={<MergeSamplesIcon />}
          onClick={() => validateAndOpenModalFor("merge")}
        >
          {i18n("SamplesMenu.merge")}
        </Menu.Item>
        <Menu.Item
          disabled={selectedCount === 0}
          key="share-menu"
          icon={<IconShare />}
          onClick={shareSamples}
        >
          {i18n("SamplesMenu.share")}
        </Menu.Item>
        <Menu.Item
          disabled={selectedCount === 0}
          key="remove-menu"
          icon={<IconCloseSquare />}
          onClick={() => validateAndOpenModalFor("remove")}
        >
          {i18n("SamplesMenu.remove")}
        </Menu.Item>
        <Menu.Divider />
        <Menu.Item key="import-menu" icon={<IconCloudUpload />}>
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
          icon={<IconPlusSquare />}
          onClick={() => setCreateSampleVisible(true)}
        >
          {i18n("SamplesMenu.createSample")}
        </Menu.Item>
      </Menu>
    );
  }, [selectedCount]);

  const exportMenu = React.useMemo(
    () => (
      <Menu>
        <Menu.Item
          disabled={selectedCount === 0}
          key="download-menu"
          icon={<IconCloudDownload />}
          onClick={onDownload}
        >
          {i18n("SampleMenu.download")}
        </Menu.Item>
        <Menu.Item
          key="linker-menu"
          icon={<IconCode />}
          onClick={() => validateAndOpenModalFor("linker")}
        >
          {i18n("SampleMenu.linker")}
        </Menu.Item>
        <Menu.Item
          disabled={selectedCount === 0}
          key="ncbi-menu"
          icon={<IconCloudUpload />}
          onClick={onNCBI}
        >
          {i18n("SampleMenu.ncbi")}
        </Menu.Item>
        <Menu.Divider />
        <Menu.Item key="menu-excel" icon={<IconFileExcel />}>
          {i18n("SampleMenu.excel")}
        </Menu.Item>
        <Menu.Item key="menu-csv" icon={<IconFile />}>
          {i18n("SampleMenu.csv")}
        </Menu.Item>
      </Menu>
    ),
    [selectedCount]
  );

  return (
    <>
      <Space>
        {canManage && (
          <Dropdown overlay={toolsMenu}>
            <Button>
              {i18n("SamplesMenu.label")} <IconDropDown />
            </Button>
          </Dropdown>
        )}
        <Dropdown overlay={exportMenu}>
          <Button>
            {i18n("SampleMenu.export")} <IconDropDown />
          </Button>
        </Dropdown>
        <Button icon={<IconShoppingCart />} onClick={onAddToCart}>
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
          <CreateModal
            visible={createSampleVisible}
            onCancel={() => setCreateSampleVisible(false)}
            onCreate={onCreate}
          />
        </Suspense>
      )}
      {linkerVisible && (
        <Suspense fallback={<span />}>
          <LinkerModal
            visible={linkerVisible}
            sampleIds={sorted}
            projectId={projectId}
            onFinish={() => setLinkerVisible(false)}
          />
        </Suspense>
      )}
    </>
  );
}
