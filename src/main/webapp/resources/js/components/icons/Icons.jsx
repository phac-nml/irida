import {
  ArrowLeftOutlined,
  ArrowRightOutlined,
  BellOutlined,
  BoldOutlined,
  CalendarTwoTone,
  CheckCircleOutlined,
  CheckOutlined,
  ClockCircleOutlined,
  CloseCircleOutlined,
  CloseOutlined,
  CloseSquareOutlined,
  CloudDownloadOutlined,
  CloudServerOutlined,
  CloudUploadOutlined,
  CodeOutlined,
  DatabaseOutlined,
  DeleteOutlined,
  DownloadOutlined,
  DownOutlined,
  EditOutlined,
  ExclamationCircleOutlined,
  ExperimentOutlined,
  EyeOutlined,
  FileExcelOutlined,
  FileOutlined,
  FilterOutlined,
  FlagFilled,
  FolderOutlined,
  HomeOutlined,
  InfoCircleOutlined,
  ItalicOutlined,
  LinkOutlined,
  LoadingOutlined,
  LockOutlined,
  LoginOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  OrderedListOutlined,
  PlusCircleTwoTone,
  PlusSquareOutlined,
  QuestionCircleOutlined,
  QuestionOutlined,
  RocketOutlined,
  SearchOutlined,
  SettingOutlined,
  ShareAltOutlined,
  ShoppingCartOutlined,
  SortAscendingOutlined,
  SortDescendingOutlined,
  StopOutlined,
  StrikethroughOutlined,
  SwapOutlined,
  SyncOutlined,
  TableOutlined,
  TeamOutlined,
  UnlockOutlined,
  UnorderedListOutlined,
  UploadOutlined,
  UserAddOutlined,
  UserDeleteOutlined,
  UsergroupAddOutlined,
  UsergroupDeleteOutlined,
  UserOutlined,
  WarningOutlined,
} from "@ant-design/icons";
import React from "react";
import { blue6 } from "../../styles/colors";

/**
 * @fileOverview All user interface icons used in JSX files should be loaded
 * through this file to ensure standardization and ability to quickly update
 * all icons.
 *
 * Passing `props` in and spreading them allows any attribute to be passed
 * along to the icon (e.g. classes, styles, etc...).
 */

export const IconArrowLeft = ({ ...props }) => <ArrowLeftOutlined {...props} />;

export const IconArrowRight = ({ ...props }) => (
  <ArrowRightOutlined {...props} />
);

export const IconClock = ({ ...props }) => <ClockCircleOutlined {...props} />;

export const IconCloseCircle = ({ ...props }) => (
  <CloseCircleOutlined {...props} />
);

export const IconCloseSquare = ({ ...props }) => (
  <CloseSquareOutlined {...props} />
);

export const IconTableFilter = ({ filtered, ...props }) => (
  <FilterOutlined style={{ color: filtered ? blue6 : undefined }} {...props} />
);

export const IconSearch = ({ ...props }) => <SearchOutlined {...props} />;

export const IconDownloadFile = ({ ...props }) => (
  <DownloadOutlined {...props} />
);

export const IconSyncSpin = ({ ...props }) => <SyncOutlined spin {...props} />;

export const IconCloudServer = ({ ...props }) => (
  <CloudServerOutlined {...props} />
);

export const IconCloudDownload = ({ ...props }) => (
  <CloudDownloadOutlined {...props} />
);

export const IconCloudUpload = ({ ...props }) => (
  <CloudUploadOutlined {...props} />
);

export const IconLoading = ({ ...props }) => <LoadingOutlined {...props} />;

export const IconDropDown = ({ ...props }) => <DownOutlined {...props} />;

export const IconShoppingCart = ({ ...props }) => (
  <ShoppingCartOutlined {...props} />
);

export const IconInfoCircle = ({ ...props }) => (
  <InfoCircleOutlined {...props} />
);

export const IconQuestionCircle = ({ ...props }) => (
  <QuestionCircleOutlined {...props} />
);

export const IconQuestion = ({ ...props }) => <QuestionOutlined {...props} />;

export const IconExclamationCircle = ({ ...props }) => (
  <ExclamationCircleOutlined {...props} />
);

export const IconLocked = ({ ...props }) => <LockOutlined {...props} />;

export const IconUnlocked = ({ ...props }) => <UnlockOutlined {...props} />;

export const IconFlag = ({ ...props }) => <FlagFilled {...props} />;

export const IconFolder = ({ ...props }) => <FolderOutlined {...props} />;

export const IconTable = ({ ...props }) => <TableOutlined {...props} />;

export const IconPlusCircle = ({ ...props }) => (
  <PlusCircleTwoTone {...props} />
);

export const IconPlusSquare = ({ ...props }) => (
  <PlusSquareOutlined {...props} />
);

export const IconTrash = ({ ...props }) => <DeleteOutlined {...props} />;

export const IconRemove = ({ ...props }) => <CloseOutlined {...props} />;

export const IconEdit = ({ ...props }) => <EditOutlined {...props} />;

export const IconEye = ({ ...props }) => <EyeOutlined {...props} />;

export const IconStop = ({ ...props }) => <StopOutlined {...props} />;

export const IconSwap = ({ ...props }) => <SwapOutlined {...props} />;

export const IconCog = ({ ...props }) => <SettingOutlined {...props} />;

export const IconBell = ({ ...props }) => <BellOutlined {...props} />;

export const IconLaunchPipeline = ({ ...props }) => (
  <RocketOutlined {...props} />
);

export const IconWarning = ({ ...props }) => <WarningOutlined {...props} />;

/*
Editor Icons
 */

export const IconBold = ({ ...props }) => <BoldOutlined {...props} />;

export const IconItalic = ({ ...props }) => <ItalicOutlined {...props} />;

export const IconStrikeThrough = ({ ...props }) => (
  <StrikethroughOutlined {...props} />
);

export const IconUnorderedList = ({ ...props }) => (
  <UnorderedListOutlined {...props} />
);

export const IconOrderedList = ({ ...props }) => (
  <OrderedListOutlined {...props} />
);

export const IconCode = ({ ...props }) => <CodeOutlined {...props} />;

export const IconLinkOut = ({ ...props }) => <LinkOutlined {...props} />;

export const IconExperiment = ({ ...props }) => (
  <ExperimentOutlined {...props} />
);

export const IconHome = ({ ...props }) => <HomeOutlined {...props} />;

export const IconLogin = ({ ...props }) => <LoginOutlined {...props} />;

export const IconMembers = ({ ...props }) => <TeamOutlined {...props} />;

export const IconUser = ({ ...props }) => <UserOutlined {...props} />;

export const IconUserAdd = (props) => <UserAddOutlined {...props} />;

export const IconUserDelete = (props) => <UserDeleteOutlined {...props} />;

export const IconUsergroupAdd = (props) => <UsergroupAddOutlined {...props} />;

export const IconUsergroupDelete = (props) => (
  <UsergroupDeleteOutlined {...props} />
);

export const IconCalendarTwoTone = ({ ...props }) => (
  <CalendarTwoTone {...props} />
);

export const IconShare = ({ ...props }) => <ShareAltOutlined {...props} />;

/*
File Type Icons
 */

export const IconFileExcel = ({ ...props }) => <FileExcelOutlined {...props} />;

export const IconFile = ({ ...props }) => <FileOutlined {...props} />;

/*
Menu Icons
 */

export const IconMenuFold = ({ ...props }) => <MenuFoldOutlined {...props} />;

export const IconMenuUnfold = ({ ...props }) => (
  <MenuUnfoldOutlined {...props} />
);

export const IconSortAscending = ({ ...props }) => (
  <SortAscendingOutlined {...props} />
);

export const IconSortDescending = ({ ...props }) => (
  <SortDescendingOutlined {...props} />
);

export const IconCheckCircle = ({ ...props }) => (
  <CheckCircleOutlined {...props} />
);

export const IconCheck = ({ ...props }) => <CheckOutlined {...props} />;

export const IconFileUpload = ({ ...props }) => <UploadOutlined {...props} />;

export const IconDatabaseOutlined = ({ ...props }) => (
  <DatabaseOutlined {...props} />
);
